CREATE TABLE IF NOT EXISTS warehouses
(
    id SERIAL PRIMARY KEY,
    warehouse_type VARCHAR(20) NOT NULL,
    location VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS products
(
    id SERIAL PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    unit VARCHAR(50) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    category VARCHAR(100),
    supplier VARCHAR(255),
    current_stock INTEGER NOT NULL DEFAULT 0,
    threshold_level INTEGER NOT NULL DEFAULT 0,
    last_used_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );