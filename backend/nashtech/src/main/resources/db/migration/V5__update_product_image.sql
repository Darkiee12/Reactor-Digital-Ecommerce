ALTER TABLE public.product_image
  -- Drop old foreign key constraints first
  DROP CONSTRAINT IF EXISTS product_image_product_id_fkey,
  DROP CONSTRAINT IF EXISTS product_image_image_id_fkey,
  
  -- Add new columns
  ADD COLUMN product_uuid uuid NOT NULL,
  ADD COLUMN image_uuid uuid NOT NULL,
  
  -- Add new foreign key constraints
  ADD CONSTRAINT product_image_product_uuid_fkey FOREIGN KEY (product_uuid) REFERENCES products(uuid) ON DELETE CASCADE,
  ADD CONSTRAINT product_image_image_uuid_fkey FOREIGN KEY (image_uuid) REFERENCES images(uuid) ON DELETE CASCADE,
  
  -- Drop old columns
  DROP COLUMN IF EXISTS product_id,
  DROP COLUMN IF EXISTS image_id;
