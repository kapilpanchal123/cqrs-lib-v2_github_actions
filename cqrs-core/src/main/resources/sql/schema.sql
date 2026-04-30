/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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