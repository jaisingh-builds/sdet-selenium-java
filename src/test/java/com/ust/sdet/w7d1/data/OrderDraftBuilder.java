package com.ust.sdet.w7d1.data;

public final class OrderDraftBuilder {
    private String sku = "SKU-BAG";
    private int quantity = 2;
    private long unitPricePaise = 49_900;
    private String address = "UST Campus, Technopark, Trivandrum";
    private String couponCode;

    private OrderDraftBuilder() {
    }

    public static OrderDraftBuilder anOrder() {
        return new OrderDraftBuilder();
    }

    public OrderDraftBuilder withQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public OrderDraftBuilder withCoupon(String couponCode) {
        this.couponCode = couponCode == null ? null : couponCode.trim().toUpperCase();
        return this;
    }

    public OrderDraft build() {
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be at least 1");
        }
        if (unitPricePaise < 1) {
            throw new IllegalArgumentException("unit price must be positive");
        }
        if (couponCode != null && !"UST10".equals(couponCode)) {
            throw new IllegalArgumentException("the W7D1 fixture supports coupon UST10 only");
        }
        return new OrderDraft(sku, quantity, unitPricePaise, address, couponCode);
    }
}
