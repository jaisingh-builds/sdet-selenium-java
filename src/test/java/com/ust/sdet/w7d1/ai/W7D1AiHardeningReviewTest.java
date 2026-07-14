package com.ust.sdet.w7d1.ai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class W7D1AiHardeningReviewTest {

    @Test
    @DisplayName("AI draft exposes the five defects from the deck")
    void draftContainsFiveReviewTargets() throws IOException {
        String draft = Files.readString(
            Path.of("src/test/resources/ai-drafts/w7d1-coupon-flow-before.java.txt")
        );

        assertThat(draft).contains(
            "By.id(\"promo\")",
            "Thread.sleep(5000)",
            "get(\"/orders/latest\")",
            "assertThat(response).isNotNull()",
            "equalTo(899)"
        );
    }

    @Test
    @DisplayName("Hardened flow uses the real page object, condition wait, captured id and source data")
    void hardenedFlowRemovesHallucinations() throws IOException {
        String page = Files.readString(
            Path.of("src/test/java/com/ust/sdet/w7d1/pages/ShopKartCheckoutPage.java")
        );
        String uiSteps = Files.readString(
            Path.of("src/test/java/com/ust/sdet/w7d1/steps/W7D1UiSteps.java")
        );
        String apiSteps = Files.readString(
            Path.of("src/test/java/com/ust/sdet/w7d1/steps/W7D1ApiSteps.java")
        );

        assertThat(page).contains(
            "By.id(\"coupon\")",
            "new WebDriverWait",
            "expectedTotalPaise"
        );
        assertThat(uiSteps).contains(
            "context.order.couponCode()",
            "context.order.totalPaise()",
            "context.orderId = checkout.placeOrder"
        );
        assertThat(apiSteps).contains(
            "fetch(context.orderId",
            "context.order.totalPaise()"
        );

        assertThat(page + uiSteps + apiSteps).doesNotContain(
            "By.id(\"promo\")",
            "Thread.sleep",
            "/orders/latest",
            "isNotNull()",
            "equalTo(899)"
        );
    }
}
