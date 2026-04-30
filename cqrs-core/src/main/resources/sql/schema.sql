CREATE TABLE cqrs_master
(
    id              UUID PRIMARY KEY,
    idempotency_key VARCHAR(64),
    status          VARCHAR(255),
    tenant_id       VARCHAR(64),
    username        VARCHAR(255),
    request_url     TEXT,
    class_name      VARCHAR(255),
    api_version     VARCHAR(32),
    correlation_id  VARCHAR(255),
    error           TEXT,
    payload         TEXT,
    created_at      TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_idempotency_key_correlation_id UNIQUE(idempotency_key, correlation_id),
    CONSTRAINT chk_status CHECK (status IN ("INIT", "PENDING", "PROCESSING", "COMPLETED", "FAILED"))
);