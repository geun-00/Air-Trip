ALTER TABLE accommodation_details
    DROP FOREIGN KEY fk_accommodation_details_on_accommodation;

ALTER TABLE accommodation_details
    DROP KEY uk_accommodation_details_accommodation;

ALTER TABLE accommodation_details
    DROP PRIMARY KEY,
    DROP COLUMN accommodation_detail_id;

ALTER TABLE accommodation_details
    ADD CONSTRAINT pk_accommodation_details PRIMARY KEY (accommodation_id);

ALTER TABLE accommodation_details
    ADD CONSTRAINT fk_accommodation_details_on_accommodation
        FOREIGN KEY (accommodation_id) REFERENCES accommodations (accommodation_id);

ALTER TABLE member_details
    DROP FOREIGN KEY fk_member_details_on_member;

ALTER TABLE member_details
    DROP KEY uc_member_details_member;

ALTER TABLE member_details
    DROP PRIMARY KEY,
    DROP COLUMN member_detail_id;

ALTER TABLE member_details
    ADD CONSTRAINT pk_member_details PRIMARY KEY (member_id);

ALTER TABLE member_details
    ADD CONSTRAINT fk_member_details_on_member
        FOREIGN KEY (member_id) REFERENCES members (member_id);
