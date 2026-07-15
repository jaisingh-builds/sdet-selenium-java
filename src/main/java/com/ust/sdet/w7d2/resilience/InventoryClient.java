package com.ust.sdet.w7d2.resilience;

@FunctionalInterface
public interface InventoryClient {
    InventoryAvailability availabilityFor(String sku);
}
