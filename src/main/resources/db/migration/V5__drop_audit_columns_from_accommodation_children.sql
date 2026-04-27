ALTER TABLE accommodation_amenities
    DROP COLUMN IF EXISTS created_at,
    DROP COLUMN IF EXISTS updated_at;

ALTER TABLE accommodation_details
    DROP COLUMN IF EXISTS created_at,
    DROP COLUMN IF EXISTS updated_at;

ALTER TABLE accommodation_images
    DROP COLUMN IF EXISTS created_at,
    DROP COLUMN IF EXISTS updated_at;

ALTER TABLE accommodation_prices
    DROP COLUMN IF EXISTS created_at,
    DROP COLUMN IF EXISTS updated_at;
