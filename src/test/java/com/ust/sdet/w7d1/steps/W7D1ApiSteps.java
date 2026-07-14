package com.ust.sdet.w7d1.steps;

import com.ust.sdet.w7d1.E2EContext;
import com.ust.sdet.w7d1.api.OrderApi;
import io.cucumber.java.en.Then;

import static org.assertj.core.api.Assertions.assertThat;

public class W7D1ApiSteps {
    private final E2EContext context;

    public W7D1ApiSteps(E2EContext context) {
        this.context = context;
    }

    @Then("the orders API returns the placed order")
    public void apiReturnsPlacedOrder() {
        context.orderResponse = new OrderApi(context.runtime.apiBase())
            .fetch(context.orderId, context.seededCart.token());
        context.orderFetchCount++;

        assertThat(context.orderResponse.jsonPath().getString("status")).isEqualTo("PLACED");
        assertThat(context.orderResponse.jsonPath().getLong("cartId")).isEqualTo(context.cartId);
        assertThat(context.orderResponse.jsonPath().getLong("subtotalPaise"))
            .isEqualTo(context.order.subtotalPaise());
        assertThat(context.orderResponse.jsonPath().getLong("discountPaise"))
            .isEqualTo(context.order.discountPaise());
        assertThat(context.orderResponse.jsonPath().getLong("totalPaise"))
            .isEqualTo(context.order.totalPaise());
        assertThat(context.orderResponse.jsonPath().getString("couponCode"))
            .isEqualTo(context.order.couponCode());
    }
}
