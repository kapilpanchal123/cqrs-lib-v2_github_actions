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
 * auditing, tracing, observability, idempotency, and multi-tenancy.
 *
 * <h2>Key Characteristics</h2>
 * <ul>
 *   <li><b>Write Intent:</b> Commands represent operations that mutate system state.</li>
 *   <li><b>Idempotency:</b> The {@link #idempotencyKey} ensures safe retries without
 *       duplicate side effects.</li>
 *   <li><b>Traceability:</b> The {@link #correlationId} enables end-to-end tracking across
 *       distributed systems, while {@link #id} uniquely identifies this command instance.</li>
 *   <li><b>Multi-tenancy:</b> The {@link #tenantId} allows isolation across tenants.</li>
 *   <li><b>Error Visibility:</b> The {@link #error} captures failure details for debugging
 *       and observability.</li>
 *   <li><b>Version Awareness:</b> {@link #apiVersion} enables backward compatibility
 *       and API evolution.</li>
 * </ul>
 *
 * <h2>Lifecycle</h2>
 * <p>A typical command lifecycle includes:
 * <ol>
 *   <li>Creation by a client or API layer</li>
 *   <li>Validation and enrichment (e.g., setting metadata such as correlationId)</li>
 *   <li>Dispatch to a command handler</li>
 *   <li>Processing and status transitions</li>
 *   <li>Persistence for auditing, replay, or debugging</li>
 * </ol>
 *
 * <h2>Correlation &amp; Observability</h2>
 * <ul>
 *   <li>{@link #correlationId} is typically generated at the request entry point
 *       (e.g., HTTP filter or gateway) and propagated across services.</li>
 *   <li>It enables linking command records, logs, and downstream operations.</li>
 *   <li>{@link #requestUrl} and {@link #className} provide additional context
 *       about the origin of the command.</li>
 * </ul>
 *
 * <h2>Error Handling</h2>
 * <p>If command processing fails, {@link #error} may contain a descriptive message
 * or serialized exception details. This enables:
 * <ul>
 *   <li>Failure auditing</li>
 *   <li>Retry diagnostics</li>
 *   <li>Dead-letter queue analysis</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>This class is <b>not thread-safe</b>. Instances are expected to be confined
 * to a single request-processing thread or externally synchronized if shared.
 *
 * <h2>Serialization</h2>
 * <p>Implements {@link java.io.Serializable} to support transmission across
 * distributed systems (e.g., messaging queues, event logs, or persistence layers).
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
   * Correlation identifier used to group related commands, events,
   * or operations across distributed services.
   *
   * <p>Typically propagated across service boundaries to enable
   * end-to-end tracing of a request lifecycle.
   */
  private String correlationId;

  /**
   * Error message or diagnostic information captured if command processing fails.
   *
   * <p>May contain a human-readable message or serialized exception details.
   * This field is primarily intended for logging, debugging, and audit purposes.
   */
  private String error;

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

  /**
   * Default constructor.
   *
   * <p>Creates an empty {@code Command} instance. Fields are expected
   * to be populated via setters or mapping frameworks (e.g., deserialization,
   * DTO mappers, or builders).
   */
  public Command() {
  }

  /**
   * Constructs a fully initialized {@code Command} instance with all fields.
   *
   * <p>This constructor is useful for manual instantiation, testing,
   * or scenarios where complete control over the command state is required.
   *
   * @param id unique identifier for this command instance
   * @param idempotencyKey key used to ensure idempotent processing
   * @param status current processing status of the command
   * @param tenantId identifier of the tenant in a multi-tenant system
   * @param username user or principal initiating the command
   * @param requestUrl originating request URL for tracing and debugging
   * @param className fully qualified class name of the originating component
   * @param apiVersion API version used to create the command
   * @param payload business payload associated with the command
   * @param correlationId identifier used for distributed tracing across systems
   * @param error error message or diagnostic details if processing failed
   * @param createdAt timestamp when the command was created
   * @param updatedAt timestamp when the command was last updated
   */
  public Command(
      final UUID id,
      final String idempotencyKey,
      final CommandStatus status,
      final String tenantId,
      final String username,
      final String requestUrl,
      final String className,
      final String apiVersion,
      final T payload,
      final String correlationId,
      final String error,
      final OffsetDateTime createdAt,
      final OffsetDateTime updatedAt) {
    this.id = id;
    this.idempotencyKey = idempotencyKey;
    this.status = status;
    this.tenantId = tenantId;
    this.username = username;
    this.requestUrl = requestUrl;
    this.className = className;
    this.apiVersion = apiVersion;
    this.payload = payload;
    this.correlationId = correlationId;
    this.error = error;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  /**
   * Returns the unique identifier of this command.
   *
   * @return the command ID
   */
  public UUID getId() {
    return id;
  }

  /**
   * Sets the unique identifier for this command.
   *
   * @param id the command ID to set
   */
  public void setId(final UUID id) {
    this.id = id;
  }

  /**
   * Returns the idempotency key associated with this command.
   *
   * @return the idempotency key
   */
  public String getIdempotencyKey() {
    return idempotencyKey;
  }

  /**
   * Sets the idempotency key for this command.
   *
   * <p>This key is used to ensure that duplicate commands are not processed
   * multiple times.
   *
   * @param idempotencyKey the idempotency key to set
   */
  public void setIdempotencyKey(final String idempotencyKey) {
    this.idempotencyKey = idempotencyKey;
  }

  /**
   * Returns the current processing status of this command.
   *
   * @return the command status
   */
  public CommandStatus getStatus() {
    return status;
  }

  /**
   * Sets the processing status of this command.
   *
   * <p>If {@code null} is provided, the status defaults to {@link CommandStatus#INIT}.
   *
   * @param status the command status to set
   */
  public void setStatus(final CommandStatus status) {
    this.status = (status != null) ? status : CommandStatus.INIT;
  }

  /**
   * Returns the tenant identifier associated with this command.
   *
   * @return the tenant ID
   */
  public String getTenantId() {
    return tenantId;
  }

  /**
   * Sets the tenant identifier for this command.
   *
   * @param tenantId the tenant ID to set
   */
  public void setTenantId(final String tenantId) {
    this.tenantId = tenantId;
  }

  /**
   * Returns the username or principal that initiated this command.
   *
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the username or principal that initiated this command.
   *
   * @param username the username to set
   */
  public void setUsername(final String username) {
    this.username = username;
  }

  /**
   * Returns the request URL from which this command originated.
   *
   * @return the request URL
   */
  public String getRequestUrl() {
    return requestUrl;
  }

  /**
   * Sets the originating request URL for this command.
   *
   * @param requestUrl the request URL to set
   */
  public void setRequestUrl(final String requestUrl) {
    this.requestUrl = requestUrl;
  }

  /**
   * Returns the fully qualified class name associated with this command.
   *
   * @return the class name
   */
  public String getClassName() {
    return className;
  }

  /**
   * Sets the fully qualified class name associated with this command.
   *
   * @param className the class name to set
   */
  public void setClassName(final String className) {
    this.className = className;
  }

  /**
   * Returns the API version used to create this command.
   *
   * @return the API version
   */
  public String getApiVersion() {
    return apiVersion;
  }

  /**
   * Sets the API version for this command.
   *
   * @param apiVersion the API version to set
   */
  public void setApiVersion(final String apiVersion) {
    this.apiVersion = apiVersion;
  }

  /**
   * Returns the business payload associated with this command.
   *
   * @return the payload
   */
  public T getPayload() {
    return payload;
  }

  /**
   * Sets the business payload for this command.
   *
   * @param payload the payload to set
   */
  public void setPayload(final T payload) {
    this.payload = payload;
  }

  /**
   * Returns the correlation identifier associated with this command.
   *
   * @return the correlation ID
   */
  public String getCorrelationId() {
    return correlationId;
  }

  /**
   * Sets the correlation identifier for this command.
   *
   * <p>This ID is used to trace the command across distributed systems,
   * logs, and related operations.
   *
   * @param correlationId the correlation ID to set
   */
  public void setCorrelationId(final String correlationId) {
    this.correlationId = correlationId;
  }

  /**
   * Returns the error message or diagnostic information associated with this command.
   *
   * @return the error message, or {@code null} if no error occurred
   */
  public String getError() {
    return error;
  }

  /**
   * Sets the error message or diagnostic information for this command.
   *
   * <p>This is typically populated when command processing fails.
   *
   * @param error the error message to set
   */
  public void setError(final String error) {
    this.error = error;
  }

  /**
   * Returns the timestamp when this command was created.
   *
   * @return the creation timestamp
   */
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   * Sets the creation timestamp for this command.
   *
   * <p>This value should ideally be set once and remain immutable.
   *
   * @param createdAt the creation timestamp to set
   */
  public void setCreatedAt(final OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  /**
   * Returns the timestamp when this command was last updated.
   *
   * @return the last updated timestamp
   */
  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  /**
   * Sets the last updated timestamp for this command.
   *
   * <p>This value should be updated whenever the command state changes.
   *
   * @param updatedAt the updated timestamp to set
   */
  public void setUpdatedAt(final OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
