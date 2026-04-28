ALTER TABLE view_histories DROP CONSTRAINT IF EXISTS fk_view_histories_on_accommodation;
ALTER TABLE view_histories DROP CONSTRAINT IF EXISTS fk_view_histories_on_member;
ALTER TABLE view_histories DROP CONSTRAINT IF EXISTS uk_view_histories_member_accommodation;

DROP TABLE IF EXISTS view_histories;
