CREATE TABLE api_usages
(
    id              BIGINT         NOT NULL AUTO_INCREMENT,
    conversation_id BIGINT         NULL,
    type            VARCHAR(40)    NOT NULL,
    model           VARCHAR(100)   NOT NULL,
    input_units     BIGINT         NOT NULL,
    output_units    BIGINT         NOT NULL,
    cost_usd        DECIMAL(14, 8) NOT NULL,
    created_at      DATETIME(6)    NOT NULL,
    updated_at      DATETIME(6)    NOT NULL,
    PRIMARY KEY (id),
    KEY idx_api_usages_conversation (conversation_id),
    CONSTRAINT fk_api_usages_conversation FOREIGN KEY (conversation_id) REFERENCES conversations (id)
);
