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
 * Abstraction for managing transactional savepoints within a command execution context.
 *
 * <p>{@code CommandSavepointManager} provides fine-grained control over partial
 * rollback behavior inside a larger transaction. It is primarily used by the
 * command pipeline to isolate different phases of execution (e.g., middleware,
 * handler, post-processing) and selectively roll back changes without aborting
 * the entire transaction.
 *
 * <h2>Purpose</h2>
 * <p>This interface enables:
 * <ul>
 *   <li>Creation of named savepoints within an active transaction</li>
 *   <li>Rollback to a specific execution boundary</li>
 *   <li>Release of savepoints to free underlying resources</li>
 *   <li>Marking the entire transaction for rollback</li>
 * </ul>
 *
 * <h2>Usage in CQRS Pipeline</h2>
 * <p>In a typical command execution flow:
 * <ol>
 *   <li>Middleware may persist the command (e.g., set status to PROCESSING)</li>
 *   <li>A savepoint is created before handler execution</li>
 *   <li>Handler and post-processors execute</li>
 *   <li>If a failure occurs:
 *     <ul>
 *       <li>Rollback to the savepoint (undoing business changes)</li>
 *       <li>Preserve earlier state changes (e.g., persisted command record)</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <p>This allows the system to maintain a consistent audit trail while still
 * safely reverting partial work.
 *
 * <h2>Design Considerations</h2>
 * <ul>
 *   <li>Savepoint names should be unique within the transaction scope</li>
 *   <li>Calling rollback on a non-existent savepoint should result in an error</li>
 *   <li>Releasing a savepoint is recommended after successful execution</li>
 *   <li>Implementations should map to underlying transaction mechanisms
 *       (e.g., JDBC {@code Savepoint})</li>
 * </ul>
 *
 * <h2>Transaction Semantics</h2>
 * <ul>
 *   <li>Savepoints operate within an already active transaction</li>
 *   <li>Rollback to savepoint does <b>not</b> terminate the transaction</li>
 *   <li>{@link #setRollbackOnly()} may mark the entire transaction for rollback</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>Implementations are typically not thread-safe and are expected to be used
 * within a single-threaded transactional context.
 *
 * @see CommandTransactionManager
 */
public interface CommandSavepointManager {

  /**
   * Marks the current transaction as rollback-only.
   *
   * <p>Once invoked, the transaction should not be committed and must be rolled back.
   * The exact behavior depends on the underlying transaction implementation.
   *
   * <p>This method is typically used for unrecoverable errors where partial rollback
   * via savepoints is insufficient.
   */
  void setRollbackOnly();

  /**
   * Creates a named savepoint within the current transaction.
   *
   * <p>The savepoint acts as a checkpoint to which the transaction can later
   * be rolled back without affecting earlier operations.
   *
   * @param savepointName the unique name identifying the savepoint
   * @throws RuntimeException if the savepoint cannot be created
   */
  void savePoint(final String savepointName);

  /**
   * Rolls back the transaction to the specified savepoint.
   *
   * <p>All changes performed after the savepoint was created are undone,
   * while earlier changes remain intact.
   *
   * @param savepointName the name of the savepoint to roll back to
   * @throws IllegalArgumentException if the savepoint does not exist
   * @throws RuntimeException if the rollback operation fails
   */
  void rollbackToSavepoint(final String savepointName);

  /**
   * Releases the specified savepoint.
   *
   * <p>Releasing a savepoint frees underlying resources associated with it.
   * Once released, the savepoint cannot be used for rollback.
   *
   * <p>If the savepoint does not exist, implementations may choose to ignore
   * the request or throw an exception.
   *
   * @param savepointName the name of the savepoint to release
   * @throws RuntimeException if the release operation fails
   */
  void releaseSavepoint(final String savepointName);

}
