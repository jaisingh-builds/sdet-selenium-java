package com.ust.sdet.w7d1.steps;

import com.ust.sdet.w7d1.E2EContext;
import com.ust.sdet.w7d1.pages.ShopKartCheckoutPage;
import io.cucumber.java.en.When;

public class W7D1UiSteps {
    private final E2EContext context;

    public W7D1UiSteps(E2EContext context) {
        this.context = context;
    }

    @When("they check out in the UI")
    public void checkoutInUi() {
        ShopKartCheckoutPage checkout = openSeededCheckout();
        context.orderId = checkout.placeOrder(context.order.address(), context.order.totalPaise());
    }

    @When("they apply the valid coupon and check out in the UI")
    public void applyCouponAndCheckoutInUi() {
        ShopKartCheckoutPage checkout = openSeededCheckout();
        checkout.applyCoupon(context.order.couponCode(), context.order.totalPaise());
        context.orderId = checkout.placeOrder(context.order.address(), context.order.totalPaise());
    }

    private ShopKartCheckoutPage openSeededCheckout() {
        return new ShopKartCheckoutPage(context.driver, context.runtime.baseUrl())
            .openSeededCart(context.seededCart, context.order.subtotalPaise());
    }
}
