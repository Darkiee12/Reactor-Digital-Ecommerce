-- Ensure products.uuid is unique
ALTER TABLE products
ADD CONSTRAINT unique_product_uuid UNIQUE (uuid);

-- Ensure accounts.uuid is unique
ALTER TABLE accounts
ADD CONSTRAINT unique_account_uuid UNIQUE (uuid);


-- 1. Add new UUID columns
ALTER TABLE reviews ADD COLUMN product_uuid UUID;
ALTER TABLE reviews ADD COLUMN account_uuid UUID;

-- 2. Populate the new UUID columns based on current integer IDs
UPDATE reviews
SET product_uuid = products.uuid
FROM products
WHERE reviews.product_uuid = products.uuid;

UPDATE reviews
SET account_uuid = "accounts".uuid
FROM "accounts"
WHERE reviews.account_uuid = "accounts".uuid;

-- 3. Set NOT NULL constraint (only after verifying the updates are successful)
ALTER TABLE reviews ALTER COLUMN product_uuid SET NOT NULL;
ALTER TABLE reviews ALTER COLUMN account_uuid SET NOT NULL;

-- 4. Add foreign key constraints
ALTER TABLE reviews
ADD CONSTRAINT fk_product_uuid FOREIGN KEY (product_uuid) REFERENCES products(uuid) ON DELETE CASCADE;

ALTER TABLE reviews
ADD CONSTRAINT fk_account_uuid FOREIGN KEY (account_uuid) REFERENCES "accounts"(uuid) ON DELETE CASCADE;

-- 5. Optional: drop the old integer FK columns
ALTER TABLE reviews DROP COLUMN product_id;
ALTER TABLE reviews DROP COLUMN user_id;
