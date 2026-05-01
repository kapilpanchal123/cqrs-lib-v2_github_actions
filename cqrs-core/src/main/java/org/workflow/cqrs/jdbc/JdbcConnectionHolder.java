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

/**
 * Thread-local holder for managing JDBC {@link Connection} instances
 * within the scope of a command transaction.
 *
 * <p>{@code JdbcConnectionHolder} enables transaction propagation by binding
 * a JDBC {@link Connection} to the current thread. This allows nested
 * transactional operations to reuse the same connection without explicitly
 * passing it through method calls.
 *
 * <h2>Purpose</h2>
 * <p>This class is used internally by {@code JdbcCommandTransactionManager} to:
 * <ul>
 *   <li>Bind a connection when a new transaction is started</li>
 *   <li>Allow nested operations to join the same transaction</li>
 *   <li>Unbind the connection after transaction completion</li>
 * </ul>
 *
 * <h2>Usage Model</h2>
 * <ul>
 *   <li>A connection is bound at the start of a transaction</li>
 *   <li>Subsequent calls within the same thread reuse the bound connection</li>
 *   <li>The connection is unbound when the transaction completes</li>
 * </ul>
 *
 * <h2>Access Patterns</h2>
 * <ul>
 *   <li>{@link #get()} – retrieves the current connection or throws if none is bound</li>
 *   <li>{@link #getOrNull()} – retrieves the current connection or returns {@code null}</li>
 *   <li>{@link #isBound()} – checks if a connection is currently bound</li>
 * </ul>
 *
 * <h2>Constraints</h2>
 * <ul>
 *   <li>Only one connection may be bound per thread at a time</li>
 *   <li>Manual binding outside {@code JdbcCommandTransactionManager} is discouraged</li>
 *   <li>Nested transactional calls automatically reuse the existing connection</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>This class is thread-safe due to the use of {@link ThreadLocal}. Each thread
 * maintains its own independent connection reference.
 *
 * <h2>Lifecycle</h2>
 * <p>Connections are:
 * <ul>
 *   <li>Bound via {@link #bind(Connection)}</li>
 *   <li>Accessed during transaction execution</li>
 *   <li>Unbound via {@link #unbind()} after completion</li>
 * </ul>
 *
 * @see JdbcCommandTransactionManager
 * @see java.sql.Connection
 */
public final class JdbcConnectionHolder {
  private static final ThreadLocal<Connection> HOLDER = new ThreadLocal<>();

  private JdbcConnectionHolder() {
    throw new UnsupportedOperationException("Utility class");
  }

  /**
   * Binds the given {@link Connection} to the current thread.
   *
   * <p>This method is typically invoked at the start of a new transaction.
   *
   * @param connection the JDBC connection to bind
   * @throws IllegalStateException if a connection is already bound to the thread
   */
  public static void bind(final Connection connection) {
    if(HOLDER.get() != null) {
      throw new IllegalStateException("A JDBC connection is already bound to the current thread. "
          + "Nested calls to execute() automatically join the existing transaction; "
          + "do not bind manually.");
    }
    HOLDER.set(connection);
  }

  /**
   * Returns the {@link Connection} bound to the current thread.
   *
   * <p>This method should only be called within an active transactional context.
   *
   * @return the bound JDBC connection
   * @throws IllegalStateException if no connection is bound to the thread
   */
  public static Connection get() {
    final Connection conn = HOLDER.get();

    if(conn == null) {
      throw new IllegalStateException("No JDBC Connection is bound to the current thread. "
          + "Ensure this code runs inside JdbcCommandTransactionManager.execute().");
    }
    return conn;
  }

  /**
   * Returns the {@link Connection} bound to the current thread, if present.
   *
   * @return the bound connection, or {@code null} if none is bound
   */
  public static Connection getOrNull() {
    return HOLDER.get();
  }

  /**
   * Determines whether a {@link Connection} is currently bound to the thread.
   *
   * @return {@code true} if a connection is bound, {@code false} otherwise
   */
  public static boolean isBound() {
    return HOLDER.get() != null;
  }

  /**
   * Removes the {@link Connection} bound to the current thread.
   *
   * <p>This method is typically invoked after transaction completion to
   * clean up thread-local state.
   */
  public static void unbind() {
    HOLDER.remove();
  }
}
