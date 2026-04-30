/**
 * Transaction management abstractions for the CQRS command processing pipeline.
 *
 * <h2>Overview</h2>
 * <p>This package defines the contracts required to manage transactional
 * boundaries during command execution. It decouples the CQRS pipeline from
 * any specific transaction technology (e.g., JDBC, JPA, or framework-managed
 * transactions), allowing flexible and pluggable implementations.
 *
 * <h2>Core Components</h2>
 * <ul>
 *   <li><b>{@link org.workflow.cqrs.transactions.CommandTransactionManager}</b> –
 *       Entry point for executing work within transactional boundaries.</li>
 *   <li><b>{@link org.workflow.cqrs.transactions.TransactionalAction}</b> –
 *       Functional abstraction representing a unit of work executed within a transaction.</li>
 *   <li><b>{@link org.workflow.cqrs.transactions.CommandSavepointManager}</b> –
 *       Provides fine-grained control over savepoints and partial rollbacks.</li>
 * </ul>
 *
 * <h2>Execution Model</h2>
 * <p>The CQRS pipeline relies on this package to enforce deterministic
 * transaction behavior:
 * <ol>
 *   <li>Command execution is wrapped inside a transaction</li>
 *   <li>Middleware may persist initial command state (e.g., PROCESSING)</li>
 *   <li>A savepoint is created before handler execution</li>
 *   <li>Handler and post-processing logic execute</li>
 *   <li>On failure:
 *     <ul>
 *       <li>Rollback to savepoint (partial rollback)</li>
 *       <li>Failure state is persisted</li>
 *     </ul>
 *   </li>
 *   <li>Transaction is committed with either success or failure state</li>
 * </ol>
 *
 * <h2>Transaction Modes</h2>
 * <ul>
 *   <li><b>Standard execution:</b> Joins an existing transaction or creates a new one</li>
 *   <li><b>Independent execution:</b> Runs in a separate transaction that commits independently</li>
 * </ul>
 *
 * <p>Independent execution is particularly important for failure handling,
 * ensuring that failure states are persisted even when the main transaction
 * is rolled back.
 *
 * <h2>Savepoint Semantics</h2>
 * <ul>
 *   <li>Savepoints allow partial rollback within a transaction</li>
 *   <li>They isolate business logic from earlier persistence operations</li>
 *   <li>Rollback to savepoint does not terminate the transaction</li>
 * </ul>
 *
 * <h2>Design Principles</h2>
 * <ul>
 *   <li><b>Separation of concerns:</b> Business logic does not manage transactions directly</li>
 *   <li><b>Deterministic behavior:</b> Explicit control over commit and rollback boundaries</li>
 *   <li><b>Extensibility:</b> Supports multiple transaction implementations</li>
 *   <li><b>Failure durability:</b> Ensures failure state is reliably persisted</li>
 * </ul>
 *
 * <h2>Implementations</h2>
 * <p>This package defines contracts only. Concrete implementations may include:
 * <ul>
 *   <li>JDBC-based transaction managers</li>
 *   <li>Framework-integrated managers (e.g., Spring)</li>
 *   <li>No-op implementations for testing</li>
 * </ul>
 *
 *
 *  <h2>Framework Transactions (@Transactional)</h2>
 *  <p>Use of framework-managed transactions (e.g., {@code @Transactional})
 *  alongside this package is generally discouraged unless carefully coordinated.
 *
 *  <p>The CQRS transaction model defined here provides:
 *  <ul>
 *    <li>Explicit control over transaction boundaries</li>
 *    <li>Fine-grained savepoint management</li>
 *    <li>Deterministic failure handling and durability guarantees</li>
 *  </ul>
 *
 *  <p>Mixing it with external transaction management mechanisms may lead to:
 *  <ul>
 *    <li>Unexpected transaction propagation behavior</li>
 *    <li>Loss of savepoint semantics</li>
 *    <li>Failure states not being persisted as intended</li>
 *  </ul>
 *
 *  <p><b>Recommendation:</b>
 *  <ul>
 *    <li>Prefer using {@link org.workflow.cqrs.transactions.CommandTransactionManager}
 *        as the single source of truth for transaction control within the CQRS pipeline</li>
 *    <li>If integrating with frameworks like Spring, ensure that transaction boundaries
 *        are not duplicated or conflicting</li>
 *  </ul>
 *
 *  <p>Advanced users may integrate with framework transactions, but must ensure
 *  that the semantics of savepoints, independent transactions, and failure
 *  persistence are preserved.</p>
 *
 *
 * <h2>Thread Safety</h2>
 * <p>Implementations must ensure thread safety and proper handling of
 * concurrent command executions, especially when using thread-bound
 * transaction contexts.
 *
 * @since 1.0
 */
package org.workflow.cqrs.transactions;