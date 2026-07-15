package com.ust.sdet.w7d2.resilience;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import java.util.function.Supplier;

public class ResilientCheckoutService {
    private final InventoryClient inventoryClient;
    private final CircuitBreaker circuitBreaker;

    public ResilientCheckoutService(InventoryClient inventoryClient, CircuitBreaker circuitBreaker) {
        this.inventoryClient = inventoryClient;
        this.circuitBreaker = circuitBreaker;
    }

    public CheckoutResult place(String sku) {
        Supplier<InventoryAvailability> protectedCall = CircuitBreaker.decorateSupplier(
            circuitBreaker,
            () -> inventoryClient.availabilityFor(sku)
        );
        try {
            return protectedCall.get().inStock()
                ? CheckoutResult.placed()
                : CheckoutResult.outOfStock();
        } catch (InventoryDependencyException | CallNotPermittedException error) {
            return CheckoutResult.stockUnconfirmed();
        }
    }
}
