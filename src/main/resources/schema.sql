-- Warehouse (창고)
CREATE TABLE IF NOT EXISTS warehouses
(
    id SERIAL PRIMARY KEY,
    warehouse_type VARCHAR(20) NOT NULL,
    location VARCHAR(255) NOT NULL,
    description TEXT
);

-- Product (물품)
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

-- Inventory (재고)
CREATE TABLE IF NOT EXISTS inventory
(
    id SERIAL PRIMARY KEY,
    warehouse_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    location VARCHAR(255),
    quantity DECIMAL(10, 2) NOT NULL,
    minimum_threshold DECIMAL(10, 2),
    expiry_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- 외래 키 제약 조건
    CONSTRAINT fk_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- 인덱스 생성 (성능 최적화)
CREATE INDEX IF NOT EXISTS idx_inventory_warehouse ON inventory(warehouse_id);
CREATE INDEX IF NOT EXISTS idx_inventory_product ON inventory(product_id);