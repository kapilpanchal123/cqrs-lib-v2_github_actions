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
package org.workflow.cqrs.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.LinkedHashMap;
import java.util.Map;
import org.workflow.cqrs.transactions.CommandSavepointManager;

/**
 * JDBC-based implementation of {@link CommandSavepointManager} that provides
 * fine-grained transaction control using database savepoints.
 *
 * <p>{@code JdbcCommandSavepointManager} allows command processing workflows
 * to create, manage, and rollback to named savepoints within an active JDBC
 * transaction. This enables partial rollback scenarios without aborting the
 * entire transaction.
 *
 * <h2>Purpose</h2>
 * <p>This implementation is responsible for:
 * <ul>
 *   <li>Creating named savepoints within a JDBC transaction</li>
 *   <li>Rolling back to specific savepoints</li>
 *   <li>Releasing savepoints when no longer needed</li>
 *   <li>Supporting full rollback via {@link #setRollbackOnly()}</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <p>This class is typically used within a transactional context managed by
 * a {@code CommandTransactionManager}. It assumes that:
 * <ul>
 *   <li>The provided {@link Connection} is active and transactional</li>
 *   <li>Auto-commit is disabled</li>
 * </ul>
 *
 * <pre>{@code
 * savepointManager.savePoint("before-post-processing");
 * try {
 *   // execute step
 * } catch (Exception e) {
 *   savepointManager.rollbackToSavepoint("before-post-processing");
 * }
 * }</pre>
 *
 * <h2>Savepoint Semantics</h2>
 * <ul>
 *   <li>Savepoints are identified by unique string names</li>
 *   <li>Each savepoint is stored internally and reused for rollback operations</li>
 *   <li>Releasing a savepoint removes it from the internal registry</li>
 * </ul>
 *
 * <h2>Error Handling</h2>
 * <ul>
 *   <li>{@link SQLException} is wrapped into {@link RuntimeException}</li>
 *   <li>Rolling back to a non-existent savepoint results in {@link IllegalArgumentException}</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>This class is <b>not thread-safe</b> and is intended to be used within
 * a single transactional context (typically one thread per request).
 *
 * <h2>Lifecycle</h2>
 * <p>Instances of this class should be short-lived and scoped to a single
 * transaction. They should not be reused across multiple transactions.
 *
 * @see CommandSavepointManager
 * @see java.sql.Connection
 * @see java.sql.Savepoint
 */
public final class JdbcCommandSavepointManager implements CommandSavepointManager {
  private final Connection connection;

  private final Map<String, Savepoint> savepoints = new LinkedHashMap<>();

  public JdbcCommandSavepointManager(final Connection connection) {
    this.connection = connection;
  }

  /**
   * Marks the current transaction for rollback by invoking
   * {@link Connection#rollback()}.
   *
   * <p>This results in a full transaction rollback, discarding all changes
   * made since the transaction began.
   *
   * @throws RuntimeException if the rollback operation fails
   */
  @Override
  public void setRollbackOnly() {
    try {
      connection.rollback();
    } catch(final SQLException e) {
      throw new RuntimeException("Failed to mark transaction for rollback", e);
    }
  }

  /**
   * Creates a named savepoint within the current transaction.
   *
   * <p>If a savepoint with the same name already exists, it is not overwritten.
   *
   * @param savepointName the unique name of the savepoint
   * @throws RuntimeException if the savepoint cannot be created
   */
  @Override
  public void savePoint(final String savepointName) {
    try {
      final Savepoint savepoint = connection.setSavepoint(savepointName);
      savepoints.putIfAbsent(savepointName, savepoint);
    } catch(final SQLException e) {
      throw new RuntimeException("Failed to set savepoint '" + savepointName + "'", e);
    }
  }

  /**
   * Rolls back the transaction state to the specified savepoint.
   *
   * <p>All changes made after the savepoint was created are discarded,
   * while earlier changes remain intact.
   *
   * @param savepointName the name of the savepoint to rollback to
   * @throws IllegalArgumentException if no savepoint exists with the given name
   * @throws RuntimeException if the rollback operation fails
   */
  @Override
  public void rollbackToSavepoint(final String savepointName) {
    final Savepoint savepoint = savepoints.get(savepointName);
    if(savepoint == null) {
      throw new IllegalArgumentException("No savepoint found with name '" + savepointName + "'. "
          + "Available savepoints: " + savepoints.keySet());
    }
    try {
      connection.rollback(savepoint);
    } catch (final SQLException e) {
      throw new RuntimeException("Failed to rollback to savepoint '" + savepointName + "'", e);
    }
  }

  /**
   * Releases the specified savepoint from the transaction.
   *
   * <p>If the savepoint does not exist, this method performs no action.
   *
   * @param savepointName the name of the savepoint to release
   * @throws RuntimeException if releasing the savepoint fails
   */
  @Override
  public void releaseSavepoint(final String savepointName) {
    final Savepoint savepoint = savepoints.remove(savepointName);
    if(savepoint == null) {
      return;
    }
    try {
      connection.releaseSavepoint(savepoint);
    } catch(final SQLException e) {
      throw new RuntimeException("Failed to release savepoint '" + savepointName + "'", e);
    }
  }
}
