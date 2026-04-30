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
 * Represents a unit of work to be executed within a transactional context.
 *
 * <p>{@code TransactionalAction} is a functional abstraction used by
 * {@link CommandTransactionManager} to execute logic within a transaction.
 * It provides access to a {@link CommandSavepointManager}, enabling fine-grained
 * control over savepoints and partial rollback behavior.
 *
 * <h2>Purpose</h2>
 * <p>This interface allows the CQRS pipeline to:
 * <ul>
 *   <li>Encapsulate transactional logic in a reusable and composable manner</li>
 *   <li>Separate transaction management from business logic</li>
 *   <li>Support advanced scenarios such as partial rollbacks using savepoints</li>
 * </ul>
 *
 * <h2>Usage in CQRS Pipeline</h2>
 * <p>Implementations typically:
 * <ul>
 *   <li>Execute middleware and handler logic</li>
 *   <li>Create savepoints before critical phases</li>
 *   <li>Rollback selectively on failure</li>
 *   <li>Return a result or propagate an error</li>
 * </ul>
 *
 * <h2>Exception Handling</h2>
 * <p>This method is allowed to throw {@link Throwable} to support both checked
 * and unchecked exceptions.
 *
 * <ul>
 *   <li>Unchecked exceptions are typically propagated as-is</li>
 *   <li>Checked exceptions may be wrapped by the transaction manager</li>
 *   <li>Thrown exceptions generally trigger transaction rollback unless handled</li>
 * </ul>
 *
 * <h2>Transaction Semantics</h2>
 * <p>The lifecycle of the transaction (begin, commit, rollback) is fully managed
 * by the {@link CommandTransactionManager}. Implementations should:
 * <ul>
 *   <li>Focus only on business or orchestration logic</li>
 *   <li>Use {@link CommandSavepointManager} for partial rollback when needed</li>
 *   <li>Avoid directly interacting with low-level transaction APIs</li>
 * </ul>
 *
 * <h2>Functional Usage</h2>
 * <p>This is a functional interface and is typically used with lambda expressions:
 *
 * <pre>{@code
 * transactionManager.execute(savepointManager -> {
 *     savepointManager.savePoint("beforeHandler");
 *
 *     try {
 *         // business logic
 *         return result;
 *     } catch (Exception ex) {
 *         savepointManager.rollbackToSavepoint("beforeHandler");
 *         throw ex;
 *     }
 * });
 * }</pre>
 *
 * <h2>Thread Safety</h2>
 * <p>Implementations are expected to be executed within a single thread-bound
 * transactional context and should not assume thread safety.
 *
 * @param <T> the result type returned after execution
 *
 * @see CommandTransactionManager
 * @see CommandSavepointManager
 */
public interface TransactionalAction<T> {

  /**
   * Executes the action within a transactional context.
   *
   * <p>The provided {@link CommandSavepointManager} allows creation and
   * management of savepoints for partial rollback.
   *
   * @param commandSavepointManager the savepoint manager associated with the current transaction
   * @return the result of the execution
   * @throws Throwable if execution fails; may trigger transaction rollback depending on handling
   */
  T execute(final CommandSavepointManager commandSavepointManager) throws Throwable;
}
