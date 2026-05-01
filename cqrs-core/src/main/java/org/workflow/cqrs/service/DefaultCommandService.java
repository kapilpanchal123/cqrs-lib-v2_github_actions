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
package org.workflow.cqrs.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.workflow.cqrs.core.Command;
import org.workflow.cqrs.core.CommandStatus;
import org.workflow.cqrs.core.CommandStore;
import org.workflow.cqrs.utils.mapper.CommandJsonMapper;

/**
 * Default JDBC-based implementation of {@link CommandStore} responsible for
 * persisting and updating {@link Command} lifecycle state.
 *
 * <p>{@code DefaultCommandService} provides a straightforward persistence mechanism
 * for CQRS command tracking using a relational database table (e.g., {@code cqrs_master}).
 * It serializes command payloads and stores metadata required for auditing,
 * observability, and failure diagnostics.
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Persisting incoming commands before execution</li>
 *   <li>Updating command status during lifecycle transitions</li>
 *   <li>Recording failure details for diagnostics and auditing</li>
 * </ul>
 *
 * <h2>Persistence Model</h2>
 * <p>This implementation writes to a database table with fields such as:
 * <ul>
 *   <li>Command identifiers ({@code id}, {@code idempotency_key})</li>
 *   <li>Status ({@link CommandStatus})</li>
 *   <li>Contextual metadata (tenant, user, correlationId, etc.)</li>
 *   <li>Serialized payload (JSON)</li>
 *   <li>Error details (stack trace)</li>
 *   <li>Timestamps (createdAt, updatedAt)</li>
 * </ul>
 *
 * <h2>Serialization</h2>
 * <p>The command payload is converted into JSON using {@link CommandJsonMapper}
 * before being persisted. The resulting JSON is stored as a string in the database.
 *
 * <h2>Transaction Behavior</h2>
 * <ul>
 *   <li>Each operation uses a new JDBC {@link Connection}</li>
 *   <li>Auto-commit is disabled and transactions are explicitly committed</li>
 *   <li>Failures trigger rollback (savepoint-based for inserts)</li>
 * </ul>
 *
 * <p><b>Note:</b> This implementation manages its own transactions and does not
 * participate in external transaction contexts (e.g., {@code JdbcCommandTransactionManager}).
 *
 * <h2>Error Handling</h2>
 * <ul>
 *   <li>SQL and mapping exceptions are wrapped in {@link RuntimeException}</li>
 *   <li>Failures during persistence are logged with command identifiers</li>
 *   <li>Stack traces are captured and stored for failed updates</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>This class is thread-safe assuming the provided {@link DataSource}
 * and {@link CommandJsonMapper} are thread-safe.
 *
 * <h2>Limitations</h2>
 * <ul>
 *   <li>No batching or bulk operations</li>
 *   <li>No retry or resilience mechanisms</li>
 *   <li>No optimistic locking or version control</li>
 *   <li>No idempotency enforcement at the database level</li>
 * </ul>
 *
 * <h2>Extensibility</h2>
 * <p>Applications are encouraged to provide custom {@link CommandStore}
 * implementations for:
 * <ul>
 *   <li>Advanced transaction management</li>
 *   <li>Integration with ORM frameworks (e.g., JPA, Hibernate)</li>
 *   <li>Resilience patterns (e.g., retries, circuit breakers)</li>
 *   <li>Custom storage backends</li>
 * </ul>
 *
 * @see CommandStore
 * @see Command
 * @see CommandStatus
 * @see CommandJsonMapper
 */
public final class DefaultCommandService implements CommandStore {
  private static final Logger log = LoggerFactory.getLogger(DefaultCommandService.class);

  private final CommandJsonMapper mapper;
  private final DataSource dataSource;

  public DefaultCommandService(
      final CommandJsonMapper mapper,
      final DataSource dataSource) {
    this.mapper = mapper;
    this.dataSource = dataSource;
  }

  /**
   * Persists a new {@link Command} record in the database.
   *
   * <p>The command payload is serialized to JSON and stored along with
   * metadata and timestamps.
   *
   * <p>This method uses a savepoint to allow rollback in case of failure
   * during insert execution.
   *
   * @param command the command to persist
   * @param commandStatus the initial status to associate with the command
   * @throws RuntimeException if persistence fails
   */
  @Override
  public void save(final Command<?> command, final CommandStatus commandStatus) {
    final JsonNode payloadJson = mapper.map(command.getPayload());
    try (final Connection conn = dataSource.getConnection()) {
      conn.setAutoCommit(false);

      final Savepoint commandSavepoint = conn.setSavepoint("commandSavepoint");

      try (PreparedStatement preparedStatement = conn.prepareStatement(
          "INSERT INTO public.cqrs_master" +
              "(id, " +
              "idempotency_key, " +
              "status, " +
              "tenant_id, " +
              "username, " +
              "request_url, " +
              "class_name, " +
              "api_version, " +
              "correlation_id, " +
              "error, " +
              "payload, " +
              "created_at, " +
              "updated_at) " +
              "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?);"
      )) {
        preparedStatement.setObject(1, command.getId());
        preparedStatement.setString(2, command.getIdempotencyKey());
        preparedStatement.setString(3, command.getStatus().name());
        preparedStatement.setString(4, command.getTenantId());
        preparedStatement.setString(5, command.getUsername());
        preparedStatement.setString(6, command.getRequestUrl());
        preparedStatement.setString(7, command.getClassName());
        preparedStatement.setString(8, command.getApiVersion());
        preparedStatement.setString(9, command.getCorrelationId());
        preparedStatement.setString(10, "");
        preparedStatement.setString(11, payloadJson.toString());
        preparedStatement.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));
        preparedStatement.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));
        preparedStatement.executeUpdate();

        conn.commit();
        log.info("Saved Command with id {}, to the database", command.getId());
      } catch (final Exception e) {
        log.error("Failed to save command {}", command.getId(), e);
        conn.rollback(commandSavepoint);
        throw new RuntimeException(e);
      }
    } catch (final SQLException e) {
      log.error("Failed to save command {}", command.getId(), e);
      throw new RuntimeException(e);
    }
  }

  /**
   * Updates the status of an existing command.
   *
   * <p>This method modifies only the {@code status} field of the command record.
   *
   * @param commandId the unique identifier of the command
   * @param commandStatus the new status to set
   * @throws RuntimeException if the update operation fails
   */
  @Override
  public void update(final UUID commandId, final CommandStatus commandStatus) {
    log.info("Updating command with id {}", commandId);

    try(final Connection conn = dataSource.getConnection()) {
      conn.setAutoCommit(false);
      try (final PreparedStatement preparedStatement = conn.prepareStatement(
          "UPDATE public.cqrs_master SET status=? WHERE id=?;"
      )) {
        preparedStatement.setString(1, commandStatus.name());
        preparedStatement.setObject(2, commandId);
        preparedStatement.executeUpdate();
      }
      conn.commit();
    } catch (final Exception e) {
      log.error("Failed to update command {}", commandId, e);
      throw new RuntimeException(e);
    }
  }

  /**
   * Updates the status and error details of an existing command.
   *
   * <p>The provided {@link Throwable} is converted into a full stack trace
   * string and stored in the {@code error} field.
   *
   * @param commandId the unique identifier of the command
   * @param commandStatus the new status to set
   * @param t the exception associated with the failure
   * @throws RuntimeException if the update operation fails
   */
  @Override
  public void update(final UUID commandId, final CommandStatus commandStatus, final Throwable t) {
    log.info("Updating command master with id {} with status as {}", commandId, commandStatus);
    final String error = getStackTrace(t);

    try(final Connection conn = dataSource.getConnection()) {
      conn.setAutoCommit(false);
      try (final PreparedStatement preparedStatement = conn.prepareStatement(
          "UPDATE public.cqrs_master SET status=?,error=? WHERE id=?;"
      )) {
        preparedStatement.setString(1, commandStatus.name());
        preparedStatement.setString(2, error);
        preparedStatement.setObject(3, commandId);
        preparedStatement.executeUpdate();
      }
      conn.commit();
    } catch (final Exception e) {
      log.error("Failed to update command {}", commandId, e);
      throw new RuntimeException(e);
    }
  }

  private String getStackTrace(final Throwable t) {
    final StringWriter stringWriter = new StringWriter();
    final PrintWriter printWriter = new PrintWriter(stringWriter);
    t.printStackTrace(printWriter);

    printWriter.flush();
    return stringWriter.toString();
  }
}
