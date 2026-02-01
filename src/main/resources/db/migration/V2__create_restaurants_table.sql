CREATE TABLE restaurants (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    address VARCHAR(200) NOT NULL,
    city VARCHAR(50) NOT NULL,
    pincode VARCHAR(10) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    owner_id BIGINT NOT NULL,
    average_rating NUMERIC(3,2) DEFAULT 0,
    total_ratings INTEGER DEFAULT 0,
    estimated_delivery_time_minutes INTEGER,
    minimum_order_amount NUMERIC(10,2),
    delivery_fee NUMERIC(10,2),
    cuisine_type VARCHAR(100),
    image_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE INDEX idx_owner_id ON restaurants(owner_id);
CREATE INDEX idx_status ON restaurants(status);
CREATE INDEX idx_city ON restaurants(city);

CREATE TABLE menu_items (
    id BIGSERIAL PRIMARY KEY,
    restaurant_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    price NUMERIC(10,2) NOT NULL,
    category VARCHAR(100),
    available BOOLEAN NOT NULL DEFAULT true,
    image_url VARCHAR(500),
    preparation_time_minutes INTEGER,
    is_vegetarian BOOLEAN DEFAULT false,
    is_spicy BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE
);

CREATE INDEX idx_restaurant_id ON menu_items(restaurant_id);
CREATE INDEX idx_category ON menu_items(category);
CREATE INDEX idx_available ON menu_items(available);
