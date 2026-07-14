package com.ust.sdet.w7d1.steps;

import com.ust.sdet.w7d1.E2EContext;
import com.ust.sdet.w7d1.db.OrderRow;
import io.cucumber.java.en.And;

import static org.assertj.core.api.Assertions.assertThat;

public class W7D1DatabaseSteps {
    private final E2EContext context;

    public W7D1DatabaseSteps(E2EContext context) {
        this.context = context;
    }

    @And("the order row exists in the database")
    public void orderRowExists() {
        OrderRow row = context.database.find(context.orderId);

        assertThat(row.status()).isEqualTo("PLACED");
        assertThat(row.subtotalPaise()).isEqualTo(context.order.subtotalPaise());
        assertThat(row.discountPaise()).isEqualTo(context.order.discountPaise());
        assertThat(row.totalPaise()).isEqualTo(context.order.totalPaise());
        assertThat(row.couponCode()).isEqualTo(context.order.couponCode());
    }
}
