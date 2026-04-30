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
package org.workflow.cqrs.transactions;

/**
 * Abstraction for managing transactional boundaries during command execution.
 *
 * <p>{@code CommandTransactionManager} is responsible for executing units of work
 * within a transactional context. It provides mechanisms to:
 * <ul>
 *   <li>Start new transactions</li>
 *   <li>Join existing transactions</li>
 *   <li>Execute work in isolated (independent) transactions</li>
 * </ul>
 *
 * <h2>Purpose</h2>
 * <p>This interface decouples the CQRS pipeline from any specific transaction
 * technology (e.g., JDBC, JPA, Spring Transactions), allowing flexible
 * integration with different persistence and infrastructure layers.
 *
 * <h2>Usage in CQRS Pipeline</h2>
 * <p>The transaction manager plays a critical role in ensuring correctness:
 * <ul>
 *   <li>Main pipeline execution is wrapped in {@link #execute(TransactionalAction)}</li>
 *   <li>Savepoints are managed within the transaction via {@link CommandSavepointManager}</li>
 *   <li>Failure strategies may use {@link #executeIndependent(TransactionalAction)}
 *       to guarantee durable failure state updates</li>
 * </ul>
 *
 * <h2>Execution Modes</h2>
 *
 * <h3>1. Standard Execution ({@link #execute(TransactionalAction)})</h3>
 * <ul>
 *   <li>If a transaction is already active, the action joins it</li>
 *   <li>Otherwise, a new transaction is created</li>
 *   <li>Commit/rollback is managed by the implementation</li>
 * </ul>
 *
 * <h3>2. Independent Execution ({@link #executeIndependent(TransactionalAction)})</h3>
 * <ul>
 *   <li>Always runs in a new, isolated transaction</li>
 *   <li>Does not participate in any existing transaction</li>
 *   <li>Commits or rolls back independently of the caller</li>
 * </ul>
 *
 * <p>This distinction is crucial for failure handling scenarios where:
 * <ul>
 *   <li>The main transaction may roll back</li>
 *   <li>Failure state must still be persisted (e.g., marking command as FAILED)</li>
 * </ul>
 *
 * <h2>Transaction Semantics</h2>
 * <ul>
 *   <li>Implementations must ensure proper commit/rollback behavior</li>
 *   <li>Exceptions should trigger rollback unless explicitly handled</li>
 *   <li>{@link CommandSavepointManager} should be provided to support partial rollbacks</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>Implementations are expected to be thread-safe and capable of handling
 * concurrent command executions.
 *
 * @see TransactionalAction
 * @see CommandSavepointManager
 */
public interface CommandTransactionManager {

  /**
   * Executes the given action within a transactional context.
   *
   * <p>If a transaction is already active, the action joins the existing
   * transaction. Otherwise, a new transaction is started.
   *
   * <p>The provided {@link TransactionalAction} receives a
   * {@link CommandSavepointManager} to manage savepoints and partial rollbacks.
   *
   * <p>Typical behavior:
   * <ul>
   *   <li>Begin transaction (if none exists)</li>
   *   <li>Execute action</li>
   *   <li>Commit on success</li>
   *   <li>Rollback on failure</li>
   * </ul>
   *
   * @param action the unit of work to execute within a transaction
   * @param <T> the result type
   * @return the result produced by the action
   * @throws RuntimeException if the action fails or transaction cannot be completed
   */
  <T> T execute(final TransactionalAction<T> action);

  /**
   * Executes the given action in a completely independent transaction.
   *
   * <p>This method always creates a new transaction, regardless of whether
   * a transaction is already active.
   *
   * <p>This is typically used for:
   * <ul>
   *   <li>Failure handling</li>
   *   <li>Audit logging</li>
   *   <li>Persisting critical state changes that must survive outer rollbacks</li>
   * </ul>
   *
   * <p>Behavior:
   * <ul>
   *   <li>Always starts a new transaction</li>
   *   <li>Commits independently of any outer transaction</li>
   *   <li>Rolls back only its own work on failure</li>
   * </ul>
   *
   * <p>⚠ This method should be used carefully, as it breaks transactional
   * atomicity with the calling context.
   *
   * @param action the unit of work to execute in an independent transaction
   * @param <T> the result type
   * @return the result produced by the action
   * @throws RuntimeException if the action fails or transaction cannot be completed
   */
  <T> T executeIndependent(final TransactionalAction<T> action);
}
