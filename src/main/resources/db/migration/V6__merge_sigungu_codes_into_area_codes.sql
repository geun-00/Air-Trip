ALTER TABLE area_codes
    ADD COLUMN IF NOT EXISTS parent_code VARCHAR(255) NULL;

INSERT INTO area_codes (area_code, created_at, updated_at, code_name, parent_code)
SELECT
    CASE
        WHEN sigungu_code LIKE '%-%' THEN sigungu_code
        ELSE CONCAT(area_code, '-', sigungu_code)
    END,
    created_at,
    updated_at,
    code_name,
    area_code
FROM sigungu_codes;

ALTER TABLE area_codes
    ADD CONSTRAINT fk_area_codes_on_parent_code
        FOREIGN KEY (parent_code) REFERENCES area_codes (area_code);

ALTER TABLE accommodations
    DROP FOREIGN KEY fk_accommodations_on_sigungu_code;

ALTER TABLE accommodations
    ADD CONSTRAINT fk_accommodations_on_sigungu_code
        FOREIGN KEY (sigungu_code) REFERENCES area_codes (area_code);

DROP TABLE sigungu_codes;
