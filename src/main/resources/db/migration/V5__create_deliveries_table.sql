CREATE TABLE deliveries (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE,
    delivery_partner_id BIGINT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    pickup_address VARCHAR(500),
    delivery_address VARCHAR(500) NOT NULL,
    estimated_delivery_time TIMESTAMP,
    actual_delivery_time TIMESTAMP,
    tracking_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (delivery_partner_id) REFERENCES users(id)
);

CREATE INDEX idx_order_id ON deliveries(order_id);
CREATE INDEX idx_delivery_partner_id ON deliveries(delivery_partner_id);
CREATE INDEX idx_status ON deliveries(status);
