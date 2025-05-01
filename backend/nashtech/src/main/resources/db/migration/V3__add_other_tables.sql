-- =========================
-- E-commerce Database Schema (Fixed)
-- =========================

-- Drop tables if they exist (for development reset)
DROP TABLE IF EXISTS cart_item, carts, order_item, orders, reviews, review_image, product_category, categories, product_image, images, products, brands CASCADE;

-- =========================
-- Brand Table
-- =========================
CREATE TABLE brands (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

-- =========================
-- Category Table
-- =========================
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

-- =========================
-- Product Table
-- =========================
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID UNIQUE DEFAULT gen_random_uuid() NOT NULL,
    brand_id INTEGER REFERENCES brands(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(10, 2) NOT NULL,
    stock_quantity INTEGER NOT NULL,
    specifications JSONB, -- Storing flexible specs
    type VARCHAR(50), -- Optional: Laptop, CPU, Phone, etc.
    is_deleted BOOLEAN DEFAULT FALSE, -- Soft delete
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);

-- =========================
-- Product-Category Bridge (Many-to-Many)
-- =========================
CREATE TABLE product_category (
    product_id INTEGER NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    category_id INTEGER NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    PRIMARY KEY (product_id, category_id)
);

-- =========================
-- Images Table (Storing metadata)
-- =========================
CREATE TABLE images (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID UNIQUE DEFAULT gen_random_uuid() NOT NULL,
    name VARCHAR(255) NOT NULL,  -- Original file name
    alt TEXT,  -- Alternative text for the image
    object_key VARCHAR(255) NOT NULL,  -- Object key in MinIO
    mime_type VARCHAR(255) NOT NULL,  -- e.g., 'image/jpeg'
    size INTEGER NOT NULL,  -- Size in bytes
    width INTEGER NOT NULL,  -- Width in pixels
    height INTEGER NOT NULL,  -- Height in pixels
    uploaded_at BIGINT NOT NULL
);

-- =========================
-- Product Image Table (Storing metadata)
-- =========================
CREATE TABLE product_image (
    id BIGSERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    image_id INTEGER NOT NULL REFERENCES images(id) ON DELETE CASCADE,
    created_at BIGINT NOT NULL
);

-- =========================
-- Review Table
-- =========================
CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID UNIQUE DEFAULT gen_random_uuid() NOT NULL,
    product_id INTEGER NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES "users"(id) ON DELETE CASCADE,
    rating INTEGER CHECK (rating >= 1 AND rating <= 5) NOT NULL,
    comment TEXT,
    created_at BIGINT NOT NULL
);

-- =========================
-- Review Image Table (Storing metadata)
-- =========================
CREATE TABLE review_image (
    id BIGSERIAL PRIMARY KEY,
    review_id INTEGER NOT NULL REFERENCES reviews(id) ON DELETE CASCADE,
    image_id INTEGER NOT NULL REFERENCES images(id) ON DELETE CASCADE,
    created_at BIGINT NOT NULL
);

-- =========================
-- Order Table
-- =========================
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID UNIQUE DEFAULT gen_random_uuid() NOT NULL,
    user_id INTEGER NOT NULL REFERENCES "users"(id) ON DELETE CASCADE,
    total_amount NUMERIC(10, 2) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING', -- 'PENDING', 'PAID', 'SHIPPED', 'CANCELLED', etc.
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);

-- =========================
-- Order Items Table
-- =========================
CREATE TABLE order_item (
    id BIGSERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id INTEGER NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(10, 2) NOT NULL
);

-- =========================
-- Cart Table
-- =========================
CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES "users"(id) ON DELETE CASCADE,
    created_at BIGINT NOT NULL
);

-- =========================
-- Cart Items Table
-- =========================
CREATE TABLE cart_item (
    id BIGSERIAL PRIMARY KEY,
    cart_id INTEGER NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_id INTEGER NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL
);

-- =========================
-- Indexes (for performance)
-- =========================

-- Faster lookup by product name
CREATE INDEX idx_product_name ON products(name);

-- Faster lookup by category name
CREATE INDEX idx_category_name ON categories(name);

-- Faster lookup inside specifications (e.g., cpu_brand)
CREATE INDEX idx_product_specifications ON products USING GIN (specifications);

-- Faster lookup for product brand
CREATE INDEX idx_product_brand ON products(brand_id);

-- Faster lookup for order users
CREATE INDEX idx_order_user ON orders(user_id);

-- =========================
-- End of Schema
-- =========================
