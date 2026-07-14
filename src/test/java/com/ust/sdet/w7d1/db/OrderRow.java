package com.ust.sdet.w7d1.db;

public record OrderRow(
        String status,
        long subtotalPaise,
        long discountPaise,
        long totalPaise,
        String couponCode
) {
}
