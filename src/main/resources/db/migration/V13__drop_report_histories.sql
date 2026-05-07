ALTER TABLE report_histories
    DROP FOREIGN KEY fk_report_histories_on_accommodation;

ALTER TABLE report_histories
    DROP FOREIGN KEY fk_report_histories_on_member;

DROP TABLE report_histories;
