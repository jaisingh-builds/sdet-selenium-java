package com.ust.sdet.w7d2.resilience;

import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.resetAllRequests;
import static com.github.tomakehurst.wiremock.client.WireMock.serviceUnavailable;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static io.github.resilience4j.circuitbreaker.CircuitBreaker.State.CLOSED;
import static io.github.resilience4j.circuitbreaker.CircuitBreaker.State.HALF_OPEN;
import static io.github.resilience4j.circuitbreaker.CircuitBreaker.State.OPEN;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@WireMockTest
class W7D2ResilienceTest {
    private static final String SKU = "SKU-1";
    private static final List<String> EVIDENCE = new CopyOnWriteArrayList<>();

    @BeforeAll
    static void resetEvidence() {
        EVIDENCE.clear();
    }

    @AfterAll
    static void writeEvidence() throws IOException {
        Path output = Path.of("target/w7d2-resilience-evidence.md");
        Files.createDirectories(output.getParent());
        Files.writeString(
            output,
            "# W7D2 Resilience Evidence\n\n"
                + "| Fault | Breaker transition | SUT behaviour | Dependency calls |\n"
                + "| --- | --- | --- | --- |\n"
                + EVIDENCE.stream().sorted().reduce("", (left, right) -> left + right + "\n"),
            StandardCharsets.UTF_8
        );
    }

    @Test
    void healthyDependencyKeepsCheckoutAndBreakerHealthy(WireMockRuntimeInfo wireMock) {
        healthyInventory();
        CircuitBreaker breaker = testBreaker();
        ResilientCheckoutService checkout = checkout(wireMock, breaker, Duration.ofMillis(250));

        CheckoutResult result = checkout.place(SKU);

        assertThat(result.status()).isEqualTo("PLACED");
        assertThat(result.inventoryMessage()).isEqualTo("Stock confirmed");
        assertThat(breaker.getState()).isEqualTo(CLOSED);
        verify(exactly(1), getRequestedFor(urlEqualTo("/inventory/" + SKU)));
        EVIDENCE.add("| 200 baseline | CLOSED -> CLOSED | PLACED | 1 |");
    }

    @Test
    void outageOpensBreakerAndShortCircuitsWithoutBreakingCheckout(WireMockRuntimeInfo wireMock) {
        outage();
        CircuitBreaker breaker = testBreaker();
        ResilientCheckoutService checkout = checkout(wireMock, breaker, Duration.ofMillis(250));

        CheckoutResult first = checkout.place(SKU);
        CheckoutResult second = checkout.place(SKU);
        CheckoutResult shortCircuited = checkout.place(SKU);

        assertThat(first.status()).isEqualTo("PLACED_STOCK_UNCONFIRMED");
        assertThat(second.status()).isEqualTo("PLACED_STOCK_UNCONFIRMED");
        assertThat(shortCircuited.inventoryMessage()).isEqualTo("Stock unconfirmed");
        assertThat(breaker.getState()).isEqualTo(OPEN);
        verify(exactly(2), getRequestedFor(urlEqualTo("/inventory/" + SKU)));
        EVIDENCE.add("| 503 outage | CLOSED -> OPEN | PLACED_STOCK_UNCONFIRMED | 2; next call short-circuited |");
    }

    @Test
    void healedDependencyMovesBreakerHalfOpenThenClosedWithoutRestart(WireMockRuntimeInfo wireMock) {
        outage();
        CircuitBreaker breaker = testBreaker();
        ResilientCheckoutService checkout = checkout(wireMock, breaker, Duration.ofMillis(250));
        checkout.place(SKU);
        checkout.place(SKU);
        assertThat(breaker.getState()).isEqualTo(OPEN);

        resetAllRequests();
        healthyInventory();
        await().atMost(3, SECONDS).until(() -> breaker.getState() == HALF_OPEN);

        CheckoutResult recovered = checkout.place(SKU);

        assertThat(recovered.status()).isEqualTo("PLACED");
        await().atMost(2, SECONDS).until(() -> breaker.getState() == CLOSED);
        verify(exactly(1), getRequestedFor(urlEqualTo("/inventory/" + SKU)));
        EVIDENCE.add("| dependency healed | OPEN -> HALF_OPEN -> CLOSED | PLACED | 1 trial call |");
    }

    @Test
    void latencyLongerThanClientTimeoutFallsBackAndOpensBreaker(WireMockRuntimeInfo wireMock) {
        stubFor(get(urlEqualTo("/inventory/" + SKU))
            .willReturn(okJson("{\"inStock\":true}").withFixedDelay(600)));
        CircuitBreaker breaker = testBreaker();
        ResilientCheckoutService checkout = checkout(wireMock, breaker, Duration.ofMillis(100));

        assertThat(checkout.place(SKU).status()).isEqualTo("PLACED_STOCK_UNCONFIRMED");
        assertThat(checkout.place(SKU).status()).isEqualTo("PLACED_STOCK_UNCONFIRMED");

        assertThat(breaker.getState()).isEqualTo(OPEN);
        verify(exactly(2), getRequestedFor(urlEqualTo("/inventory/" + SKU)));
        EVIDENCE.add("| 600 ms latency with 100 ms timeout | CLOSED -> OPEN | PLACED_STOCK_UNCONFIRMED | 2 timed-out calls |");
    }

    @Test
    void wrongTypedPayloadIsRejectedAndFallsBack(WireMockRuntimeInfo wireMock) {
        stubFor(get(urlEqualTo("/inventory/" + SKU))
            .willReturn(okJson("{\"inStock\":\"not-a-boolean\"}")));
        CircuitBreaker breaker = testBreaker();
        ResilientCheckoutService checkout = checkout(wireMock, breaker, Duration.ofMillis(250));

        assertThat(checkout.place(SKU).status()).isEqualTo("PLACED_STOCK_UNCONFIRMED");
        assertThat(checkout.place(SKU).status()).isEqualTo("PLACED_STOCK_UNCONFIRMED");

        assertThat(breaker.getState()).isEqualTo(OPEN);
        verify(exactly(2), getRequestedFor(urlEqualTo("/inventory/" + SKU)));
        EVIDENCE.add("| wrong-typed JSON | CLOSED -> OPEN | PLACED_STOCK_UNCONFIRMED | 2 rejected responses |");
    }

    @Test
    void connectionResetRetriesOnceThenFallsBack(WireMockRuntimeInfo wireMock) {
        stubFor(get(urlEqualTo("/inventory/" + SKU))
            .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));
        CircuitBreaker breaker = testBreaker();
        InventoryClient http = new HttpInventoryClient(wireMock.getHttpBaseUrl(), Duration.ofMillis(250));
        ResilientCheckoutService checkout = new ResilientCheckoutService(
            new RetryingInventoryClient(http, 2),
            breaker
        );

        CheckoutResult result = checkout.place(SKU);

        assertThat(result.status()).isEqualTo("PLACED_STOCK_UNCONFIRMED");
        verify(exactly(2), getRequestedFor(urlEqualTo("/inventory/" + SKU)));
        EVIDENCE.add("| connection reset | CLOSED -> CLOSED | PLACED_STOCK_UNCONFIRMED | 2 attempts; retry once |");
    }

    private static ResilientCheckoutService checkout(
            WireMockRuntimeInfo wireMock,
            CircuitBreaker breaker,
            Duration timeout
    ) {
        return new ResilientCheckoutService(
            new HttpInventoryClient(wireMock.getHttpBaseUrl(), timeout),
            breaker
        );
    }

    private static CircuitBreaker testBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .slidingWindowSize(2)
            .minimumNumberOfCalls(2)
            .failureRateThreshold(50)
            .waitDurationInOpenState(Duration.ofMillis(400))
            .permittedNumberOfCallsInHalfOpenState(1)
            .automaticTransitionFromOpenToHalfOpenEnabled(true)
            .recordExceptions(InventoryDependencyException.class)
            .build();
        return CircuitBreaker.of("inventory-" + System.nanoTime(), config);
    }

    private static void healthyInventory() {
        stubFor(get(urlEqualTo("/inventory/" + SKU))
            .willReturn(okJson("{\"inStock\":true}")));
    }

    private static void outage() {
        stubFor(get(urlEqualTo("/inventory/" + SKU)).willReturn(serviceUnavailable()));
    }
}
