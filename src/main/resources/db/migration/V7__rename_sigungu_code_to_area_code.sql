ALTER TABLE accommodations
    DROP FOREIGN KEY fk_accommodations_on_sigungu_code;

ALTER TABLE accommodations
    CHANGE COLUMN sigungu_code area_code VARCHAR(255) NOT NULL;

ALTER TABLE accommodations
    ADD CONSTRAINT fk_accommodations_on_child_area_code
        FOREIGN KEY (area_code) REFERENCES area_codes (area_code);
