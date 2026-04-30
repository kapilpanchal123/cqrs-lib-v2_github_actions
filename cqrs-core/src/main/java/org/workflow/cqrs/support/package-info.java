/**
 * Default support implementations for the CQRS command processing model.
 *
 * <h2>Overview</h2>
 * <p>This package provides concrete, production-ready implementations of the
 * core CQRS abstractions defined in {@code org.workflow.cqrs.core}. These
 * implementations are designed to work out-of-the-box while remaining fully
 * extensible for advanced use cases.
 *
 * <h2>Key Responsibilities</h2>
 * <ul>
 *   <li>Orchestrating command execution via {@code DefaultCommandPipeline}</li>
 *   <li>Routing commands to appropriate handlers</li>
 *   <li>Executing middleware chains</li>
 *   <li>Handling post-processing concerns</li>
 *   <li>Integrating failure handling strategies</li>
 * </ul>
 *
 * <h2>Core Components</h2>
 * <ul>
 *   <li><b>Pipeline:</b> {@code DefaultCommandPipeline} coordinates the full
 *       command lifecycle including transactions, savepoints, and failure handling.</li>
 *   <li><b>Executor:</b> Executes middleware and resolves the appropriate handler.</li>
 *   <li><b>Router:</b> Maps commands to their corresponding handlers.</li>
 *   <li><b>Middleware:</b> Intercepts execution for cross-cutting concerns
 *       such as persistence, logging, or validation.</li>
 *   <li><b>Post-Processors:</b> Execute after successful command handling
 *       (e.g., auditing, event publishing).</li>
 * </ul>
 *
 * <h2>Execution Model</h2>
 * <p>The default pipeline follows a structured execution flow:
 * <ol>
 *   <li>Command enters the pipeline</li>
 *   <li>Middleware chain executes</li>
 *   <li>Handler is resolved and invoked</li>
 *   <li>Post-processors are executed</li>
 *   <li>Failures are handled using registered strategies</li>
 * </ol>
 *
 * <h2>Failure Semantics</h2>
 * <p>Failure handling is explicit and stage-aware:
 * <ul>
 *   <li>Execution failures (middleware/handler resolution) may use independent transactions</li>
 *   <li>Post-processing failures trigger partial rollback via savepoints</li>
 *   <li>Command state transitions (e.g., FAILED) are persisted reliably</li>
 * </ul>
 *
 * <h2>Extensibility</h2>
 * <p>Although default implementations are provided, users are encouraged to:
 * <ul>
 *   <li>Replace or extend the pipeline for custom execution semantics</li>
 *   <li>Provide custom middleware for domain-specific concerns</li>
 *   <li>Implement custom post-processors for integration with external systems</li>
 *   <li>Introduce advanced failure strategies (e.g., retry, circuit breakers)</li>
 * </ul>
 *
 * <h2>Design Principles</h2>
 * <ul>
 *   <li><b>Separation of concerns:</b> Clear distinction between execution phases</li>
 *   <li><b>Deterministic behavior:</b> Explicit control over transactions and failures</li>
 *   <li><b>Observability:</b> Designed to support tracing and auditing</li>
 *   <li><b>Safe defaults:</b> Works out-of-the-box with minimal configuration</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>Most implementations are stateless and thread-safe. Components that
 * manage shared state (e.g., persistence layers) must ensure proper
 * synchronization and transactional integrity.
 *
 * @since 1.0
 */
package org.workflow.cqrs.support;