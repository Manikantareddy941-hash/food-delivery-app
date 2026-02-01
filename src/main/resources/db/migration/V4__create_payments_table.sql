CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    transaction_id VARCHAR(100) UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(20) NOT NULL,
    amount NUMERIC(10,2) NOT NULL,
    gateway_response VARCHAR(1000),
    failure_reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE INDEX idx_order_id ON payments(order_id);
CREATE INDEX idx_status ON payments(status);
CREATE INDEX idx_transaction_id ON payments(transaction_id);
