ALTER TABLE chatbot_histories
    ADD COLUMN metadata LONGTEXT NULL;

CREATE INDEX idx_chatbot_histories_conversation_created_at
    ON chatbot_histories (conversation_id, created_at);
