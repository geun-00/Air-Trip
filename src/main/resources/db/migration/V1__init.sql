CREATE TABLE accommodation_amenities
(
    accommodation_amenities_id BIGINT AUTO_INCREMENT NOT NULL,
    created_at                 DATETIME              NOT NULL,
    updated_at                 DATETIME              NOT NULL,
    accommodation_id           BIGINT                NOT NULL,
    amenity_id                 BIGINT                NOT NULL,
    CONSTRAINT pk_accommodation_amenities PRIMARY KEY (accommodation_amenities_id)
);

CREATE TABLE accommodation_images
(
    accommodation_image_id BIGINT AUTO_INCREMENT NOT NULL,
    created_at             DATETIME              NOT NULL,
    updated_at             DATETIME              NOT NULL,
    image_url              VARCHAR(700)          NOT NULL,
    thumbnail              BIT(1)                NOT NULL,
    accommodation_id       BIGINT                NOT NULL,
    CONSTRAINT pk_accommodation_images PRIMARY KEY (accommodation_image_id)
);

CREATE TABLE accommodation_prices
(
    accommodation_price_id BIGINT AUTO_INCREMENT NOT NULL,
    created_at             DATETIME              NOT NULL,
    updated_at             DATETIME              NOT NULL,
    accommodation_id       BIGINT                NOT NULL,
    season                 VARCHAR(255)          NOT NULL,
    day_type               VARCHAR(255)          NOT NULL,
    price                  INT                   NOT NULL,
    CONSTRAINT pk_accommodation_prices PRIMARY KEY (accommodation_price_id)
);

CREATE TABLE accommodation_stats
(
    stat_id           BIGINT AUTO_INCREMENT NOT NULL,
    accommodation_id  BIGINT                NOT NULL,
    area_code         VARCHAR(255)          NOT NULL,
    area_name         VARCHAR(255)          NOT NULL,
    title             VARCHAR(255)          NOT NULL,
    average_rating    DOUBLE                NULL,
    reservation_count INT                   NULL,
    thumbnail_url     VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_accommodation_stats PRIMARY KEY (stat_id)
);

CREATE TABLE accommodations
(
    accommodation_id  BIGINT AUTO_INCREMENT NOT NULL,
    created_at        DATETIME              NOT NULL,
    updated_at        DATETIME              NOT NULL,
    map_x             DOUBLE                NOT NULL,
    map_y             DOUBLE                NOT NULL,
    title             VARCHAR(255)          NOT NULL,
    address           VARCHAR(255)          NOT NULL,
    content_id        VARCHAR(255)          NOT NULL,
    modified_time     DATETIME              NOT NULL,
    sigungu_code      VARCHAR(255)          NOT NULL,
    `description`     TEXT                  NULL,
    max_people        INT                   NULL,
    check_in          VARCHAR(255)          NULL,
    check_out         VARCHAR(255)          NULL,
    number            VARCHAR(255)          NULL,
    refund_regulation TEXT                  NULL,
    is_embedded       BIT(1)                NULL,
    reservation_count INT                   NULL,
    average_rating    DOUBLE                NULL,
    CONSTRAINT pk_accommodations PRIMARY KEY (accommodation_id)
);

CREATE TABLE amenities
(
    amenity_id    BIGINT AUTO_INCREMENT NOT NULL,
    created_at    DATETIME              NOT NULL,
    updated_at    DATETIME              NOT NULL,
    name          VARCHAR(255)          NOT NULL,
    `description` VARCHAR(255)          NULL,
    CONSTRAINT pk_amenities PRIMARY KEY (amenity_id)
);

CREATE TABLE area_codes
(
    area_code  VARCHAR(255) NOT NULL,
    created_at DATETIME     NOT NULL,
    updated_at DATETIME     NOT NULL,
    code_name  VARCHAR(255) NOT NULL,
    CONSTRAINT pk_area_codes PRIMARY KEY (area_code)
);

CREATE TABLE chat_messages
(
    chat_message_id BIGINT AUTO_INCREMENT NOT NULL,
    created_at      DATETIME              NOT NULL,
    updated_at      DATETIME              NOT NULL,
    chat_room_id    BIGINT                NOT NULL,
    member_id       BIGINT                NOT NULL,
    content         VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_chat_messages PRIMARY KEY (chat_message_id)
);

CREATE TABLE chat_participants
(
    chat_participant_id BIGINT AUTO_INCREMENT NOT NULL,
    created_at          DATETIME              NOT NULL,
    updated_at          DATETIME              NOT NULL,
    chat_room_id        BIGINT                NOT NULL,
    member_id           BIGINT                NOT NULL,
    last_read_message   BIGINT                NULL,
    is_creator          BIT(1)                NOT NULL,
    custom_room_name    VARCHAR(255)          NOT NULL,
    is_active           BIT(1)                NOT NULL,
    left_at             DATETIME              NULL,
    last_rejoined_at    DATETIME              NULL,
    CONSTRAINT pk_chat_participants PRIMARY KEY (chat_participant_id)
);

CREATE TABLE chat_rooms
(
    chat_room_id BIGINT AUTO_INCREMENT NOT NULL,
    created_at   DATETIME              NOT NULL,
    updated_at   DATETIME              NOT NULL,
    CONSTRAINT pk_chat_rooms PRIMARY KEY (chat_room_id)
);

CREATE TABLE chatbot_histories
(
    chatbot_history_id BIGINT AUTO_INCREMENT NOT NULL,
    created_at         DATETIME              NOT NULL,
    updated_at         DATETIME              NOT NULL,
    message_type       VARCHAR(255)          NOT NULL,
    text               VARCHAR(255)          NOT NULL,
    conversation_id    VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_chatbot_histories PRIMARY KEY (chatbot_history_id)
);

CREATE TABLE members
(
    member_id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at        DATETIME              NOT NULL,
    updated_at        DATETIME              NOT NULL,
    name              VARCHAR(255)          NOT NULL,
    birth_date        DATE                  NULL,
    number            VARCHAR(11)           NULL,
    email             VARCHAR(50)           NOT NULL,
    profile_url       VARCHAR(255)          NULL,
    about_me          VARCHAR(255)          NULL,
    password          VARCHAR(255)          NOT NULL,
    social_type       VARCHAR(255)          NULL,
    is_email_verified BIT(1)                NOT NULL,
    `role`            VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_members PRIMARY KEY (member_id)
);

CREATE TABLE notifications
(
    notification_id BIGINT AUTO_INCREMENT NOT NULL,
    created_at      DATETIME              NOT NULL,
    updated_at      DATETIME              NOT NULL,
    member_id       BIGINT                NOT NULL,
    type            VARCHAR(255)          NOT NULL,
    title           VARCHAR(200)          NOT NULL,
    content         VARCHAR(500)          NOT NULL,
    reference_id    VARCHAR(100)          NULL,
    is_read         BIT(1)                NOT NULL,
    read_at         DATETIME              NULL,
    CONSTRAINT pk_notifications PRIMARY KEY (notification_id)
);

CREATE TABLE payments
(
    payment_key    VARCHAR(255) NOT NULL,
    created_at     DATETIME     NOT NULL,
    updated_at     DATETIME     NOT NULL,
    order_id       VARCHAR(255) NOT NULL,
    total_amount   INT          NOT NULL,
    payment_status VARCHAR(255) NOT NULL,
    requested_at   DATETIME     NOT NULL,
    payment_method VARCHAR(255) NULL,
    approved_at    DATETIME     NULL,
    reservation_id BIGINT       NOT NULL,
    CONSTRAINT pk_payments PRIMARY KEY (payment_key)
);

CREATE TABLE report_histories
(
    report_id        BIGINT AUTO_INCREMENT NOT NULL,
    created_at       DATETIME              NOT NULL,
    updated_at       DATETIME              NOT NULL,
    content          VARCHAR(255)          NOT NULL,
    accommodation_id BIGINT                NOT NULL,
    member_id        BIGINT                NOT NULL,
    CONSTRAINT pk_report_histories PRIMARY KEY (report_id)
);

CREATE TABLE reservations
(
    reservation_id   BIGINT AUTO_INCREMENT NOT NULL,
    created_at       DATETIME              NOT NULL,
    updated_at       DATETIME              NOT NULL,
    member_id        BIGINT                NOT NULL,
    accommodation_id BIGINT                NOT NULL,
    adults           INT                   NOT NULL,
    children         INT                   NOT NULL,
    infant           INT                   NOT NULL,
    start_date       DATETIME              NOT NULL,
    end_date         DATETIME              NOT NULL,
    status           VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_reservations PRIMARY KEY (reservation_id)
);

CREATE TABLE reviews
(
    review_id      BIGINT AUTO_INCREMENT NOT NULL,
    created_at     DATETIME              NOT NULL,
    updated_at     DATETIME              NOT NULL,
    content        VARCHAR(255)          NOT NULL,
    rating         DOUBLE                NOT NULL,
    reservation_id BIGINT                NOT NULL,
    member_id      BIGINT                NOT NULL,
    CONSTRAINT pk_reviews PRIMARY KEY (review_id)
);

CREATE TABLE sigungu_codes
(
    sigungu_code VARCHAR(255) NOT NULL,
    created_at   DATETIME     NOT NULL,
    updated_at   DATETIME     NOT NULL,
    code_name    VARCHAR(255) NOT NULL,
    area_code    VARCHAR(255) NOT NULL,
    CONSTRAINT pk_sigungu_codes PRIMARY KEY (sigungu_code)
);

CREATE TABLE view_histories
(
    view_history_id  BIGINT AUTO_INCREMENT NOT NULL,
    created_at       DATETIME              NOT NULL,
    updated_at       DATETIME              NOT NULL,
    accommodation_id BIGINT                NOT NULL,
    member_id        BIGINT                NOT NULL,
    viewed_at        DATETIME              NOT NULL,
    CONSTRAINT pk_view_histories PRIMARY KEY (view_history_id)
);

CREATE TABLE wishlist_accommodations
(
    wishlist_accommodation_id BIGINT AUTO_INCREMENT NOT NULL,
    created_at                DATETIME              NOT NULL,
    updated_at                DATETIME              NOT NULL,
    wishlist_id               BIGINT                NOT NULL,
    accommodation_id          BIGINT                NOT NULL,
    memo                      VARCHAR(250)          NULL,
    CONSTRAINT pk_wishlist_accommodations PRIMARY KEY (wishlist_accommodation_id)
);

CREATE TABLE wishlists
(
    wishlist_id BIGINT AUTO_INCREMENT NOT NULL,
    created_at  DATETIME              NOT NULL,
    updated_at  DATETIME              NOT NULL,
    member_id   BIGINT                NOT NULL,
    name        VARCHAR(50)           NOT NULL,
    CONSTRAINT pk_wishlists PRIMARY KEY (wishlist_id)
);

CREATE TABLE withdrawal_reason
(
    withdrawal_reason_id BIGINT AUTO_INCREMENT NOT NULL,
    created_at           DATETIME              NOT NULL,
    updated_at           DATETIME              NOT NULL,
    reason               VARCHAR(255)          NOT NULL,
    member_id            BIGINT                NOT NULL,
    CONSTRAINT pk_withdrawal_reason PRIMARY KEY (withdrawal_reason_id)
);

ALTER TABLE chat_participants
    ADD CONSTRAINT uc_710a0a8e71e1a884596ee9767 UNIQUE (chat_room_id, member_id);

ALTER TABLE accommodations
    ADD CONSTRAINT uc_accommodations_content UNIQUE (content_id);

ALTER TABLE chat_participants
    ADD CONSTRAINT uc_chat_participants_last_read_message UNIQUE (last_read_message);

ALTER TABLE payments
    ADD CONSTRAINT uc_payments_reservation UNIQUE (reservation_id);

ALTER TABLE reviews
    ADD CONSTRAINT uc_reviews_reservation UNIQUE (reservation_id);

ALTER TABLE withdrawal_reason
    ADD CONSTRAINT uc_withdrawal_reason_member UNIQUE (member_id);

ALTER TABLE accommodation_amenities
    ADD CONSTRAINT uk_acc_amenity UNIQUE (accommodation_id, amenity_id);

ALTER TABLE amenities
    ADD CONSTRAINT uk_amenity_name UNIQUE (name);

ALTER TABLE view_histories
    ADD CONSTRAINT uk_view_histories_member_accommodation UNIQUE (member_id, accommodation_id);

ALTER TABLE wishlist_accommodations
    ADD CONSTRAINT uk_wishlist_accommodation_wid_aid UNIQUE (wishlist_id, accommodation_id);

ALTER TABLE accommodations
    ADD CONSTRAINT fk_accommodations_on_sigungu_code FOREIGN KEY (sigungu_code) REFERENCES sigungu_codes (sigungu_code);

ALTER TABLE accommodation_amenities
    ADD CONSTRAINT fk_accommodation_amenities_on_accommodation FOREIGN KEY (accommodation_id) REFERENCES accommodations (accommodation_id);

ALTER TABLE accommodation_amenities
    ADD CONSTRAINT fk_accommodation_amenities_on_amenity FOREIGN KEY (amenity_id) REFERENCES amenities (amenity_id);

ALTER TABLE accommodation_images
    ADD CONSTRAINT fk_accommodation_images_on_accommodation FOREIGN KEY (accommodation_id) REFERENCES accommodations (accommodation_id);

ALTER TABLE accommodation_prices
    ADD CONSTRAINT fk_accommodation_prices_on_accommodation FOREIGN KEY (accommodation_id) REFERENCES accommodations (accommodation_id);

ALTER TABLE chat_messages
    ADD CONSTRAINT fk_chat_messages_on_chat_room FOREIGN KEY (chat_room_id) REFERENCES chat_rooms (chat_room_id);

ALTER TABLE chat_messages
    ADD CONSTRAINT fk_chat_messages_on_member FOREIGN KEY (member_id) REFERENCES members (member_id);

ALTER TABLE chat_participants
    ADD CONSTRAINT fk_chat_participants_on_chat_room FOREIGN KEY (chat_room_id) REFERENCES chat_rooms (chat_room_id);

ALTER TABLE chat_participants
    ADD CONSTRAINT fk_chat_participants_on_last_read_message FOREIGN KEY (last_read_message) REFERENCES chat_messages (chat_message_id);

ALTER TABLE chat_participants
    ADD CONSTRAINT fk_chat_participants_on_member FOREIGN KEY (member_id) REFERENCES members (member_id);

ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_on_member FOREIGN KEY (member_id) REFERENCES members (member_id);

ALTER TABLE payments
    ADD CONSTRAINT fk_payments_on_reservation FOREIGN KEY (reservation_id) REFERENCES reservations (reservation_id);

ALTER TABLE report_histories
    ADD CONSTRAINT fk_report_histories_on_accommodation FOREIGN KEY (accommodation_id) REFERENCES accommodations (accommodation_id);

ALTER TABLE report_histories
    ADD CONSTRAINT fk_report_histories_on_member FOREIGN KEY (member_id) REFERENCES members (member_id);

ALTER TABLE reservations
    ADD CONSTRAINT fk_reservations_on_accommodation FOREIGN KEY (accommodation_id) REFERENCES accommodations (accommodation_id);

ALTER TABLE reservations
    ADD CONSTRAINT fk_reservations_on_member FOREIGN KEY (member_id) REFERENCES members (member_id);

ALTER TABLE reviews
    ADD CONSTRAINT fk_reviews_on_member FOREIGN KEY (member_id) REFERENCES members (member_id);

ALTER TABLE reviews
    ADD CONSTRAINT fk_reviews_on_reservation FOREIGN KEY (reservation_id) REFERENCES reservations (reservation_id);

ALTER TABLE sigungu_codes
    ADD CONSTRAINT fk_sigungu_codes_on_area_code FOREIGN KEY (area_code) REFERENCES area_codes (area_code);

ALTER TABLE view_histories
    ADD CONSTRAINT fk_view_histories_on_accommodation FOREIGN KEY (accommodation_id) REFERENCES accommodations (accommodation_id);

ALTER TABLE view_histories
    ADD CONSTRAINT fk_view_histories_on_member FOREIGN KEY (member_id) REFERENCES members (member_id);

ALTER TABLE wishlists
    ADD CONSTRAINT fk_wishlists_on_member FOREIGN KEY (member_id) REFERENCES members (member_id);

ALTER TABLE wishlist_accommodations
    ADD CONSTRAINT fk_wishlist_accommodations_on_accommodation FOREIGN KEY (accommodation_id) REFERENCES accommodations (accommodation_id);

ALTER TABLE wishlist_accommodations
    ADD CONSTRAINT fk_wishlist_accommodations_on_wishlist FOREIGN KEY (wishlist_id) REFERENCES wishlists (wishlist_id);

ALTER TABLE withdrawal_reason
    ADD CONSTRAINT fk_withdrawal_reason_on_member FOREIGN KEY (member_id) REFERENCES members (member_id);