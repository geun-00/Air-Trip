CREATE TABLE member_details
(
    member_detail_id BIGINT AUTO_INCREMENT NOT NULL,
    member_id        BIGINT                NOT NULL,
    birth_date       DATE                  NULL,
    number           VARCHAR(11)           NULL,
    profile_url      VARCHAR(255)          NULL,
    about_me         VARCHAR(500)          NULL,
    CONSTRAINT pk_member_details PRIMARY KEY (member_detail_id)
);

INSERT INTO
    member_details (member_id, birth_date, number, profile_url, about_me)
SELECT
    member_id,
    birth_date,
    number,
    profile_url,
    about_me
FROM
    members;

ALTER TABLE member_details
    ADD CONSTRAINT uc_member_details_member UNIQUE (member_id);

ALTER TABLE member_details
    ADD CONSTRAINT fk_member_details_on_member
        FOREIGN KEY (member_id) REFERENCES members (member_id);

ALTER TABLE members
    DROP COLUMN birth_date,
    DROP COLUMN number,
    DROP COLUMN profile_url,
    DROP COLUMN about_me;
