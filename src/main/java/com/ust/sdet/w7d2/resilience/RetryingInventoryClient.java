package com.ust.sdet.w7d2.resilience;

public class RetryingInventoryClient implements InventoryClient {
    private final InventoryClient delegate;
    private final int maxAttempts;

    public RetryingInventoryClient(InventoryClient delegate, int maxAttempts) {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be at least one");
        }
        this.delegate = delegate;
        this.maxAttempts = maxAttempts;
    }

    @Override
    public InventoryAvailability availabilityFor(String sku) {
        InventoryDependencyException lastFailure = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return delegate.availabilityFor(sku);
            } catch (InventoryDependencyException error) {
                lastFailure = error;
            }
        }
        throw lastFailure;
    }
}
