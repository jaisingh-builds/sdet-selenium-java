package com.ust.sdet.data;

import java.time.LocalDate;

public class OrderBuilder {
    private String sku = "SKU-RET-101";
    private int quantity = 1;
    private long totalPaise = 129_900;
    private String status = "NEW";
    private LocalDate orderedOn = LocalDate.of(2026, 7, 8);
    private boolean refunded = false;

    private OrderBuilder() {
    }

    public static OrderBuilder anOrder() {
        return new OrderBuilder();
    }

    public OrderBuilder withSku(String sku) {
        this.sku = sku;
        return this;
    }

    public OrderBuilder withQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public OrderBuilder withTotalPaise(long totalPaise) {
        this.totalPaise = totalPaise;
        return this;
    }

    public OrderBuilder withStatus(String status) {
        this.status = status;
        return this;
    }

    public OrderBuilder orderedOn(LocalDate orderedOn) {
        this.orderedOn = orderedOn;
        return this;
    }

    public OrderBuilder refunded() {
        this.status = "REFUNDED";
        this.refunded = true;
        return this;
    }

    public Order build() {
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be at least 1");
        }
        if (totalPaise < 1) {
            throw new IllegalArgumentException("totalPaise must be positive");
        }
        return new Order(sku, quantity, totalPaise, status, orderedOn, refunded);
    }
}
