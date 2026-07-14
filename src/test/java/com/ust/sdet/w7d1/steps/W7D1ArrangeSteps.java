package com.ust.sdet.w7d1.steps;

import com.ust.sdet.w7d1.E2EContext;
import com.ust.sdet.w7d1.api.CartFactory;
import io.cucumber.java.en.Given;

import static com.ust.sdet.w7d1.data.OrderDraftBuilder.anOrder;

public class W7D1ArrangeSteps {
    private final E2EContext context;

    public W7D1ArrangeSteps(E2EContext context) {
        this.context = context;
    }

    @Given("a customer with a seeded cart")
    public void seedCart() {
        context.order = anOrder().build();
        seedFromSourceOfTruth();
    }

    @Given("a customer with a seeded cart for coupon {string}")
    public void seedCartForCoupon(String couponCode) {
        context.order = anOrder().withCoupon(couponCode).build();
        seedFromSourceOfTruth();
    }

    private void seedFromSourceOfTruth() {
        CartFactory carts = new CartFactory(
            context.runtime.apiBase(),
            context.runtime.customerEmail(),
            context.runtime.customerPassword()
        );
        context.seededCart = carts.seedCartWith(context.order);
        context.cartId = context.seededCart.cartId();
    }
}
