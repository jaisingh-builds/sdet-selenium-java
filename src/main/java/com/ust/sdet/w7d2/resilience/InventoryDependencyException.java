package com.ust.sdet.w7d2.resilience;

public class InventoryDependencyException extends RuntimeException {
    public InventoryDependencyException(String message) {
        super(message);
    }

    public InventoryDependencyException(String message, Throwable cause) {
        super(message, cause);
    }
}
