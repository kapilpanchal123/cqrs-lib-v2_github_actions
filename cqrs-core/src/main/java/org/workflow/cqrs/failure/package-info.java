/**
 * Default implementations of {@link org.workflow.cqrs.failure.CommandFailureStrategy}
 * for handling failures in a CQRS command processing pipeline.
 *
 * <h2>Overview</h2>
 * <p>This package provides out-of-the-box failure handling strategies for common
 * stages of command execution. These implementations focus on ensuring that
 * failures are consistently logged and persisted for observability and auditing.
 *
 * <h2>Provided Strategies</h2>
 * <ul>
 *   <li>Execution failure handling – marks commands as failed when core processing fails</li>
 *   <li>Post-processing failure handling – captures failures in side-effect operations</li>
 * </ul>
 *
 * <h2>Design Philosophy</h2>
 * <p>The default strategies are intentionally simple and act as terminal handlers:
 * <ul>
 *   <li>No retry or recovery logic is performed</li>
 *   <li>Failures are logged and persisted</li>
 *   <li>Execution is not retried automatically</li>
 * </ul>
 *
 * <h2>Extensibility</h2>
 * <p>Applications are encouraged to provide custom implementations of
 * {@link org.workflow.cqrs.failure.CommandFailureStrategy} for advanced scenarios such as:
 * <ul>
 *   <li>Retry mechanisms with backoff strategies</li>
 *   <li>Circuit breaking for external dependencies</li>
 *   <li>Compensation or fallback workflows</li>
 * </ul>
 *
 * <p>Such implementations may integrate with resilience libraries
 * (e.g., Resilience4j) or custom retry frameworks, depending on application needs.
 *
 * <h2>Usage</h2>
 * <p>These strategies are typically registered as part of the command pipeline
 * and may be combined with other custom strategies to achieve the desired
 * failure handling behavior.
 *
 * <h2>Thread Safety</h2>
 * <p>All implementations are expected to be thread-safe. Thread safety depends
 * on the underlying components (e.g., {@code CommandStore}).
 *
 * @since 1.0
 */
package org.workflow.cqrs.failure;