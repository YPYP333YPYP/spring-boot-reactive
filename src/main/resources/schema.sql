CREATE TABLE IF NOT EXISTS warehouses
(
    id SERIAL PRIMARY KEY,
    warehouse_type VARCHAR(20) NOT NULL,
    location VARCHAR(255) NOT NULL,
    description TEXT
);

