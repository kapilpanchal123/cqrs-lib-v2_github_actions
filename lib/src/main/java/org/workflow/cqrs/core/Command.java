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
package org.workflow.cqrs.core;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Represents a generic Command in a CQRS (Command Query Responsibility Segregation) system.
 *
 * <p>A {@code Command} encapsulates an intention to perform a state-changing operation.
 * It carries both the business payload and contextual metadata required for processing,
 * auditing, tracing, idempotency, and multi-tenancy.
 *
 * <h2>Key Characteristics</h2>
 * <ul>
 *   <li><b>Write Intent:</b> Commands represent operations that mutate system state.</li>
 *   <li><b>Idempotency:</b> The {@link #idempotencyKey} ensures safe retries without
 *       duplicate side effects.</li>
 *   <li><b>Traceability:</b> Metadata such as {@link #id}, {@link #username}, and
 *       {@link #requestUrl} enable tracking across distributed systems.</li>
 *   <li><b>Multi-tenancy:</b> The {@link #tenantId} allows isolation across tenants.</li>
 *   <li><b>Version Awareness:</b> {@link #apiVersion} enables backward compatibility
 *       and evolution of APIs.</li>
 * </ul>
 *
 * <h2>Lifecycle</h2>
 * <p>A typical command lifecycle includes:
 * <ol>
 *   <li>Creation by a client or API layer</li>
 *   <li>Validation and enrichment (e.g., setting metadata)</li>
 *   <li>Dispatch to a command handler</li>
 *   <li>Processing and status updates</li>
 *   <li>Persistence for auditing or replay</li>
 * </ol>
 *
 * <h2>Thread Safety</h2>
 * <p>This class is <b>not thread-safe</b>. Instances are expected to be confined
 * to a single request-processing thread or properly synchronized externally.
 *
 * <h2>Serialization</h2>
 * <p>Implements {@link java.io.Serializable} to allow transmission across
 * distributed systems (e.g., messaging queues, event logs).
 *
 * @param <T> the type of payload associated with the command
 *
 * @see java.util.UUID
 * @see java.time.OffsetDateTime
 */
public class Command<T> implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * Unique identifier for this command instance.
   *
   * <p>Typically generated at creation time and used for tracing,
   * correlation, and persistence.
   */
  private UUID id;

  /**
   * Key used to guarantee idempotent processing.
   *
   * <p>If multiple commands are received with the same key,
   * the system should ensure that only one is processed.
   */
  private String idempotencyKey;

  /**
   * Current processing status of the command.
   *
   * <p>Common values include {@code PENDING}, {@code PROCESSING},
   * {@code COMPLETED}, and {@code FAILED}.
   *
   * <p>The exact lifecycle is application-specific.
   */
  private CommandStatus status = CommandStatus.INIT;

  /**
   * Identifier for the tenant in a multi-tenant system.
   *
   * <p>This value is used to enforce data isolation and routing.
   */
  private String tenantId;

  /**
   * Username or principal that initiated the command.
   *
   * <p>Useful for auditing and security tracing.
   */
  private String username;

  /**
   * Full request URL from which this command originated.
   *
   * <p>Mainly used for debugging, tracing, and observability.
   */
  private String requestUrl;

  /**
   * Fully qualified class name of the originating component
   * or handler associated with this command.
   */
  private String className;

  /**
   * Version of the API used to create this command.
   *
   * <p>Helps support backward compatibility in evolving systems.
   */
  private String apiVersion;

  /**
   * Business payload associated with the command.
   *
   * <p>Contains the actual data required to perform the operation.
   */
  private T payload;

  /**
   * Timestamp when this command was created.
   *
   * <p>Should be set once and remain immutable.
   */
  private OffsetDateTime createdAt;

  /**
   * Timestamp when this command was last modified.
   *
   * <p>Updated whenever the command state changes.
   */
  private OffsetDateTime updatedAt;

  public Command() {
  }

  public Command(UUID id, String idempotencyKey, CommandStatus status, String tenantId, String username, String requestUrl, String className, String apiVersion, T payload, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    this.id = id;
    this.idempotencyKey = idempotencyKey;
    this.status = (status != null) ? status : CommandStatus.INIT;
    this.tenantId = tenantId;
    this.username = username;
    this.requestUrl = requestUrl;
    this.className = className;
    this.apiVersion = apiVersion;
    this.payload = payload;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getIdempotencyKey() {
    return idempotencyKey;
  }

  public void setIdempotencyKey(String idempotencyKey) {
    this.idempotencyKey = idempotencyKey;
  }

  public CommandStatus getStatus() {
    return status;
  }

  public void setStatus(CommandStatus status) {
    this.status = (status != null) ? status : CommandStatus.INIT;
  }

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getRequestUrl() {
    return requestUrl;
  }

  public void setRequestUrl(String requestUrl) {
    this.requestUrl = requestUrl;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public T getPayload() {
    return payload;
  }

  public void setPayload(T payload) {
    this.payload = payload;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
