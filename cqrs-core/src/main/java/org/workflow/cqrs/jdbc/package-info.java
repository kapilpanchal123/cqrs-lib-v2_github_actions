/**
 * JDBC-based transaction management infrastructure for the CQRS command pipeline.
 *
 * <h2>Overview</h2>
 * <p>This package provides concrete implementations of transactional abstractions
 * defined in the CQRS framework using standard JDBC primitives such as
 * {@link java.sql.Connection} and {@link java.sql.Savepoint}.
 *
 * <p>It enables reliable execution of command workflows with support for
 * transaction propagation, partial rollbacks, and fine-grained control
 * over database interactions.
 *
 * <h2>Core Components</h2>
 * <ul>
 *   <li><b>Transaction Manager</b> – {@code JdbcCommandTransactionManager}
 *       orchestrates transaction boundaries, including creation, joining,
 *       and independent execution of transactions.</li>
 *   <li><b>Connection Holder</b> – {@code JdbcConnectionHolder} manages
 *       thread-local binding of JDBC connections to support transaction propagation.</li>
 *   <li><b>Savepoint Manager</b> – {@code JdbcCommandSavepointManager}
 *       enables partial rollback using named savepoints within a transaction.</li>
 * </ul>
 *
 * <h2>Transaction Semantics</h2>
 * <ul>
 *   <li><b>Join Existing:</b> Reuses an active transaction bound to the current thread</li>
 *   <li><b>Create New:</b> Starts a new transaction when none exists</li>
 *   <li><b>Independent:</b> Executes in an isolated transaction outside thread context</li>
 * </ul>
 *
 * <h2>Savepoint Support</h2>
 * <p>Transactions support savepoints for partial rollback scenarios, enabling:
 * <ul>
 *   <li>Fine-grained error recovery</li>
 *   <li>Rollback of specific pipeline stages (e.g., post-processing)</li>
 *   <li>Improved resilience in complex workflows</li>
 * </ul>
 *
 * <h2>Thread Model</h2>
 * <p>Transaction propagation is implemented using a thread-local mechanism:
 * <ul>
 *   <li>Each thread maintains its own JDBC connection</li>
 *   <li>Nested operations automatically join the current transaction</li>
 *   <li>Connections are bound and unbound per transaction lifecycle</li>
 * </ul>
 *
 * <h2>Error Handling</h2>
 * <ul>
 *   <li>SQL exceptions are translated into runtime exceptions</li>
 *   <li>Transaction rollbacks are performed on failure</li>
 *   <li>Rollback failures are logged but do not override the original exception</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <p>This package is typically used by the CQRS infrastructure layer and
 * should not be accessed directly by application code. Instead, applications
 * interact with higher-level abstractions such as:
 * <ul>
 *   <li>{@code CommandPipeline}</li>
 *   <li>{@code CommandExecutor}</li>
 *   <li>{@code CommandTransactionManager}</li>
 * </ul>
 *
 * <h2>Design Considerations</h2>
 * <ul>
 *   <li>Relies on JDBC-compliant {@link javax.sql.DataSource}</li>
 *   <li>Assumes transactions are managed per thread (no reactive support)</li>
 *   <li>Designed for synchronous command execution models</li>
 * </ul>
 *
 * @since 1.0
 */
package org.workflow.cqrs.jdbc;