ALTER TABLE accommodations
    ADD COLUMN review_count INT NOT NULL DEFAULT 0;

UPDATE accommodations a
SET a.review_count = COALESCE((
    SELECT COUNT(*)
    FROM reviews rv
    JOIN reservations rs ON rv.reservation_id = rs.reservation_id
    WHERE rs.accommodation_id = a.accommodation_id
), 0);
