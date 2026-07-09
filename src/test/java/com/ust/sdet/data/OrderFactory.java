package com.ust.sdet.data;

public class OrderFactory {
    private final OrderRepository repository;

    public OrderFactory(OrderRepository repository) {
        this.repository = repository;
    }

    public long persisted(OrderBuilder builder) {
        return repository.save(builder.build());
    }
}
