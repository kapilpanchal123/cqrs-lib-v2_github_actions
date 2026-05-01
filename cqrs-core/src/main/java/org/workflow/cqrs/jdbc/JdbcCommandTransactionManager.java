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
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.workflow.cqrs.transactions.CommandTransactionManager;
import org.workflow.cqrs.transactions.TransactionalAction;

/**
 * JDBC-based implementation of {@link CommandTransactionManager} that manages
 * transactional execution of command processing using a {@link DataSource}.
 *
 * <p>{@code JdbcCommandTransactionManager} provides a flexible transaction model
 * supporting:
 * <ul>
 *   <li>Joining an existing transaction bound to the current thread</li>
 *   <li>Creating a new transaction when none exists</li>
 *   <li>Executing independent transactions isolated from the current context</li>
 * </ul>
 *
 * <h2>Transaction Modes</h2>
 *
 * <h3>1. Join Existing Transaction</h3>
 * <p>If a {@link Connection} is already bound to the current thread via
 * {@code JdbcConnectionHolder}, the action is executed within the existing
 * transaction context.
 *
 * <h3>2. Create New Transaction</h3>
 * <p>If no transaction is bound, a new JDBC connection is acquired,
 * auto-commit is disabled, and the transaction is committed or rolled back
 * based on execution outcome.
 *
 * <h3>3. Independent Transaction</h3>
 * <p>{@link #executeIndependent(TransactionalAction)} always creates a new
 * transaction that is not bound to the thread-local context. This is useful
 * for operations that must run in isolation (e.g., failure handling,
 * auditing, or compensation workflows).
 *
 * <h2>Execution Semantics</h2>
 * <ul>
 *   <li>Successful execution results in transaction commit</li>
 *   <li>Failures trigger rollback</li>
 *   <li>Exceptions are propagated as {@link RuntimeException}</li>
 * </ul>
 *
 * <h2>Savepoint Support</h2>
 * <p>Each transactional execution provides a {@link JdbcCommandSavepointManager}
 * to the {@link TransactionalAction}, enabling fine-grained rollback control
 * within a transaction.
 *
 * <h2>Thread Binding</h2>
 * <p>This implementation uses a thread-local mechanism ({@code JdbcConnectionHolder})
 * to bind JDBC connections for transaction propagation.
 *
 * <ul>
 *   <li>Connections are bound when a new transaction is started</li>
 *   <li>Connections are unbound after transaction completion</li>
 * </ul>
 *
 * <h2>Error Handling</h2>
 * <ul>
 *   <li>{@link SQLException} is wrapped into {@link RuntimeException}</li>
 *   <li>Rollback failures are logged but not rethrown</li>
 *   <li>Original exceptions are preserved and propagated</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>This class is thread-safe assuming the provided {@link DataSource}
 * is thread-safe. Each transaction operates on its own {@link Connection}.
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * transactionManager.execute(sp -> {
 *   // transactional logic
 *   return result;
 * });
 * }</pre>
 *
 * @see CommandTransactionManager
 * @see TransactionalAction
 * @see JdbcCommandSavepointManager
 */
public final class JdbcCommandTransactionManager implements CommandTransactionManager {
  private static final Logger log = LoggerFactory.getLogger(JdbcCommandTransactionManager.class);

  private final DataSource dataSource;

  public JdbcCommandTransactionManager(final DataSource dataSource) {
    if(dataSource == null) {
      throw new IllegalArgumentException("dataSource must not be null");
    }
    this.dataSource = dataSource;
  }

  /**
   * Executes the given {@link TransactionalAction} within a transactional context.
   *
   * <p>If a transaction is already active on the current thread, the action
   * joins the existing transaction. Otherwise, a new transaction is created.
   *
   * @param action the transactional action to execute
   * @param <T> the result type
   * @return the result of the action
   * @throws RuntimeException if execution fails
   */
  @Override
  public <T> T execute(final TransactionalAction<T> action) {
    if(JdbcConnectionHolder.isBound()) {
      log.debug("Joining existing JDBC transaction on current thread");
      return executeJoining(action);
    }
    log.debug("Opening new JDBC transaction");
    return executeNew(action);
  }

  /**
   * Executes the given {@link TransactionalAction} in a new, independent transaction.
   *
   * <p>This method always creates a new JDBC connection and does not participate
   * in any existing transaction bound to the current thread.
   *
   * <p>This is useful for:
   * <ul>
   *   <li>Failure handling strategies</li>
   *   <li>Audit logging</li>
   *   <li>Compensation or fallback operations</li>
   * </ul>
   *
   * @param action the transactional action to execute
   * @param <T> the result type
   * @return the result of the action
   * @throws RuntimeException if execution fails
   */
  @Override
  public <T> T executeIndependent(TransactionalAction<T> action) {
    log.debug("Opening independent JDBC transaction (not bound to thread-local)");
    try(final Connection connection = dataSource.getConnection()) {
      connection.setAutoCommit(false);
      try {
        final T result = action.execute(new JdbcCommandSavepointManager(connection));
        connection.commit();
        return result;
      } catch(final Throwable t) {
        silentRollback(connection);
        log.error("Independent transaction rolled back", t);
        throw (t instanceof  RuntimeException re) ? re : new RuntimeException(t);
      }
    } catch(SQLException e) {
      throw new RuntimeException("Failed to acquire JDBC connection for independent transaction", e);
    }
  }

  /**
   * Executes the action within an existing transaction bound to the current thread.
   *
   * <p>No commit or rollback is performed by this method.
   */
  private <T> T executeJoining(final TransactionalAction<T> action) {
    final Connection connection = JdbcConnectionHolder.get();
    try {
      return action.execute(new JdbcCommandSavepointManager(connection));
    } catch(final Throwable t) {
      throw(t instanceof RuntimeException re ? re : new RuntimeException(t));
    }
  }

  /**
   * Executes the action within a newly created transaction.
   *
   * <p>Handles connection acquisition, transaction demarcation,
   * commit, rollback, and thread binding.
   */
  private <T> T executeNew(final TransactionalAction<T> action) {
    try(Connection connection = dataSource.getConnection()) {
      connection.setAutoCommit(false);
      JdbcConnectionHolder.bind(connection);
      try {
        final T result = action.execute(new JdbcCommandSavepointManager(connection));
        connection.commit();
        log.debug("JDBC transaction committed");
        return result;
      } catch(final Throwable t) {
        silentRollback(connection);
        throw(t instanceof RuntimeException re ? re : new RuntimeException(t));
      } finally {
        JdbcConnectionHolder.unbind();
      }
    } catch(final SQLException e) {
      throw new RuntimeException("Failed to acquire JDBC connection", e);
    }
  }

  /**
   * Attempts to rollback the given connection, suppressing any exceptions.
   *
   * <p>Failures are logged but not propagated.
   */
  private void silentRollback(final Connection connection) {
    try {
      connection.rollback();
      log.debug("JDBC transaction rolled back");
    } catch(final SQLException e) {
      log.debug("Failed to rollback JDBC transaction; resources may be in an inconsistent state", e);
    }
  }
}
