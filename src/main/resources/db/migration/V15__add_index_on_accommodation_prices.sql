CREATE INDEX idx_accommodation_prices_covering
    ON accommodation_prices (accommodation_id, season, day_type, price);
