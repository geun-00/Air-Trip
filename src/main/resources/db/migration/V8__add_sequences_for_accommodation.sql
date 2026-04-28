DELIMITER //
CREATE OR REPLACE PROCEDURE sync_sequence(IN seq_name VARCHAR(64), IN table_name VARCHAR(64), IN col_name VARCHAR(64))
BEGIN
    SET @get_max = CONCAT('SELECT COALESCE(MAX(', col_name, '), 0) INTO @current_max FROM ', table_name);
    PREPARE stmt1 FROM @get_max; EXECUTE stmt1; DEALLOCATE PREPARE stmt1;

    SET @set_seq = CONCAT('SELECT SETVAL(', seq_name, ', ', @current_max, ')');
    PREPARE stmt2 FROM @set_seq; EXECUTE stmt2; DEALLOCATE PREPARE stmt2;
END //
DELIMITER ;

CALL sync_sequence('accommodations_seq', 'accommodations', 'accommodation_id');
CALL sync_sequence('accommodation_images_seq', 'accommodation_images', 'accommodation_image_id');
CALL sync_sequence('accommodation_prices_seq', 'accommodation_prices', 'accommodation_price_id');
CALL sync_sequence('accommodation_amenities_seq', 'accommodation_amenities', 'accommodation_amenities_id');

DROP PROCEDURE sync_sequence;
