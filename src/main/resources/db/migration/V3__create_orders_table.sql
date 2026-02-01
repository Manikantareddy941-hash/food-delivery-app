CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    restaurant_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PLACED',
    subtotal NUMERIC(10,2) NOT NULL,
    delivery_fee NUMERIC(10,2),
    tax NUMERIC(10,2),
    total_amount NUMERIC(10,2) NOT NULL,
    delivery_address VARCHAR(500) NOT NULL,
    delivery_city VARCHAR(50) NOT NULL,
    delivery_pincode VARCHAR(10) NOT NULL,
    delivery_phone VARCHAR(20) NOT NULL,
    special_instructions VARCHAR(1000),
    delivery_partner_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (customer_id) REFERENCES users(id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id),
    FOREIGN KEY (delivery_partner_id) REFERENCES users(id)
);

CREATE INDEX idx_customer_id ON orders(customer_id);
CREATE INDEX idx_restaurant_id ON orders(restaurant_id);
CREATE INDEX idx_status ON orders(status);
CREATE INDEX idx_created_at ON orders(created_at);

CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    menu_item_id BIGINT NOT NULL,
    menu_item_name VARCHAR(200) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(10,2) NOT NULL,
    total_price NUMERIC(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

CREATE INDEX idx_order_id ON order_items(order_id);
CREATE INDEX idx_menu_item_id ON order_items(menu_item_id);
