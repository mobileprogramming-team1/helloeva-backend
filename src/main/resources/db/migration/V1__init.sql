CREATE TABLE users
(
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    nickname           VARCHAR(50)  NOT NULL,
    provider           VARCHAR(20)  NOT NULL,
    provider_id        VARCHAR(64)  NOT NULL,
    status             VARCHAR(20)  NOT NULL,
    logged_out_at      DATETIME(6)  NULL,
    withdrawn_at       DATETIME(6)  NULL,
    active_provider_id VARCHAR(64) AS (IF(status = 'ACTIVE', provider_id, NULL)) STORED,
    created_at         DATETIME(6)  NOT NULL,
    updated_at         DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_active_provider (provider, active_provider_id)
);

CREATE TABLE conversations
(
    id                BIGINT      NOT NULL AUTO_INCREMENT,
    user_id           BIGINT      NOT NULL,
    round             INT         NOT NULL,
    target_grade      VARCHAR(10) NOT NULL,
    status            VARCHAR(20) NOT NULL,
    background_survey JSON        NOT NULL,
    created_at        DATETIME(6) NOT NULL,
    updated_at        DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_conversations_user_round (user_id, round),
    CONSTRAINT fk_conversations_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE questions
(
    id                     BIGINT        NOT NULL AUTO_INCREMENT,
    conversation_id        BIGINT        NOT NULL,
    question_number        INT           NOT NULL,
    question_text          TEXT          NOT NULL,
    audio_url              VARCHAR(500)  NOT NULL,
    audio_duration_seconds INT           NOT NULL,
    replay_count           INT           NOT NULL DEFAULT 0,
    created_at             DATETIME(6)   NOT NULL,
    updated_at             DATETIME(6)   NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_questions_conversation_number (conversation_id, question_number),
    CONSTRAINT fk_questions_conversation FOREIGN KEY (conversation_id) REFERENCES conversations (id)
);

CREATE TABLE answers
(
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    question_id      BIGINT       NOT NULL,
    transcript       TEXT         NOT NULL,
    audio_url        VARCHAR(500) NOT NULL,
    duration_seconds INT          NOT NULL,
    answered_at      DATETIME(6)  NOT NULL,
    created_at       DATETIME(6)  NOT NULL,
    updated_at       DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_answers_question (question_id),
    CONSTRAINT fk_answers_question FOREIGN KEY (question_id) REFERENCES questions (id)
);

CREATE TABLE question_feedbacks
(
    id                 BIGINT      NOT NULL AUTO_INCREMENT,
    answer_id          BIGINT      NOT NULL,
    estimated_grade    VARCHAR(10) NOT NULL,
    improvement_tips   JSON        NOT NULL,
    recommended_script TEXT        NOT NULL,
    key_expressions    JSON        NOT NULL,
    created_at         DATETIME(6) NOT NULL,
    updated_at         DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_question_feedbacks_answer (answer_id),
    CONSTRAINT fk_question_feedbacks_answer FOREIGN KEY (answer_id) REFERENCES answers (id)
);

CREATE TABLE overall_feedbacks
(
    id                      BIGINT      NOT NULL AUTO_INCREMENT,
    conversation_id         BIGINT      NOT NULL,
    overall_estimated_grade VARCHAR(10) NOT NULL,
    fluency                 INT         NOT NULL,
    vocabulary              INT         NOT NULL,
    grammar                 INT         NOT NULL,
    organization            INT         NOT NULL,
    strengths               JSON        NOT NULL,
    weaknesses              JSON        NOT NULL,
    recommended_studies     JSON        NOT NULL,
    frequent_expressions    JSON        NOT NULL,
    closing_message         TEXT        NOT NULL,
    created_at              DATETIME(6) NOT NULL,
    updated_at              DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_overall_feedbacks_conversation (conversation_id),
    CONSTRAINT fk_overall_feedbacks_conversation FOREIGN KEY (conversation_id) REFERENCES conversations (id)
);
