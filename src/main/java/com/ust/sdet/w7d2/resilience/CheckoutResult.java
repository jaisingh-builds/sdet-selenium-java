package com.ust.sdet.w7d2.resilience;

public record CheckoutResult(String status, String inventoryMessage) {
    public static CheckoutResult placed() {
        return new CheckoutResult("PLACED", "Stock confirmed");
    }

    public static CheckoutResult outOfStock() {
        return new CheckoutResult("REJECTED_OUT_OF_STOCK", "Item is out of stock");
    }

    public static CheckoutResult stockUnconfirmed() {
        return new CheckoutResult("PLACED_STOCK_UNCONFIRMED", "Stock unconfirmed");
    }
}
