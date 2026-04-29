ALTER TABLE wishlist_accommodations
    DROP COLUMN IF EXISTS created_at,
    DROP COLUMN IF EXISTS updated_at;

ALTER TABLE wishlist_accommodations
    RENAME INDEX uk_wishlist_accommodation_wid_aid TO uk_wishlist_accommodations_wishlist_accommodation;
