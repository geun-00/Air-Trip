CREATE TABLE accommodation_details
(
    accommodation_detail_id BIGINT AUTO_INCREMENT NOT NULL,
    created_at              DATETIME              NOT NULL,
    updated_at              DATETIME              NOT NULL,
    accommodation_id        BIGINT                NOT NULL,
    `description`           TEXT                  NULL,
    max_people              INT                   NULL,
    check_in                VARCHAR(255)          NULL,
    check_out               VARCHAR(255)          NULL,
    number                  VARCHAR(255)          NULL,
    refund_regulation       TEXT                  NULL,
    CONSTRAINT pk_accommodation_details PRIMARY KEY (accommodation_detail_id)
);

INSERT INTO accommodation_details (
    created_at,
    updated_at,
    accommodation_id,
    `description`,
    max_people,
    check_in,
    check_out,
    number,
    refund_regulation
)
SELECT
    created_at,
    updated_at,
    accommodation_id,
    `description`,
    max_people,
    check_in,
    check_out,
    number,
    refund_regulation
FROM accommodations;

ALTER TABLE accommodation_details
    ADD CONSTRAINT uk_accommodation_details_accommodation UNIQUE (accommodation_id);

ALTER TABLE accommodation_details
    ADD CONSTRAINT fk_accommodation_details_on_accommodation FOREIGN KEY (accommodation_id) REFERENCES accommodations (accommodation_id);

ALTER TABLE accommodations
    DROP COLUMN `description`;

ALTER TABLE accommodations
    DROP COLUMN max_people;

ALTER TABLE accommodations
    DROP COLUMN check_in;

ALTER TABLE accommodations
    DROP COLUMN check_out;

ALTER TABLE accommodations
    DROP COLUMN number;

ALTER TABLE accommodations
    DROP COLUMN refund_regulation;
