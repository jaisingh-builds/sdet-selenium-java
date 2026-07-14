package com.ust.sdet.w7d1.api;

public record SeededCart(
        long cartId,
        String token,
        long customerId,
        String email,
        String displayName
) {
}
