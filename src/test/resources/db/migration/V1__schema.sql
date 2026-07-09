CREATE TABLE order_statuses (
    code VARCHAR(20) PRIMARY KEY,
    description VARCHAR(100) NOT NULL
);

CREATE TABLE retail_orders (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(20) NOT NULL REFERENCES order_statuses(code),
    total_paise BIGINT NOT NULL CHECK (total_paise > 0),
    ordered_on DATE NOT NULL,
    refunded BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE retail_order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES retail_orders(id) ON DELETE CASCADE,
    sku VARCHAR(40) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0)
);
