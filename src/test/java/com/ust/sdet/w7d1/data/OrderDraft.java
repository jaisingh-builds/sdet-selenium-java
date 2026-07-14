package com.ust.sdet.w7d1.data;

public record OrderDraft(
        String sku,
        int quantity,
        long unitPricePaise,
        String address,
        String couponCode
) {
    public long subtotalPaise() {
        return unitPricePaise * quantity;
    }

    public long discountPaise() {
        return "UST10".equals(couponCode) ? subtotalPaise() / 10 : 0;
    }

    public long totalPaise() {
        return subtotalPaise() - discountPaise();
    }
}
