/**
 * Default service implementations for CQRS command persistence and lifecycle management.
 *
 * <h2>Overview</h2>
 * <p>This package provides concrete implementations of core CQRS service
 * abstractions, primarily focused on persisting and updating command state.
 *
 * <p>The classes in this package act as the default bridge between the
 * command processing pipeline and the underlying data store.
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Persisting commands before execution</li>
 *   <li>Updating command lifecycle status (e.g., INIT, PROCESSING, COMPLETED, FAILED)</li>
 *   <li>Capturing error details for failed executions</li>
 *   <li>Providing auditability and traceability of command processing</li>
 * </ul>
 *
 * <h2>Persistence Model</h2>
 * <p>Implementations typically store:
 * <ul>
 *   <li>Command metadata (id, tenant, correlationId, etc.)</li>
 *   <li>Serialized payloads (e.g., JSON)</li>
 *   <li>Execution status and timestamps</li>
 *   <li>Error details for diagnostics</li>
 * </ul>
 *
 * <h2>Transaction Behavior</h2>
 * <p>Default implementations in this package:
 * <ul>
 *   <li>Use independent JDBC transactions per operation</li>
 *   <li>Do not participate in external transaction managers</li>
 *   <li>Explicitly commit or rollback database operations</li>
 * </ul>
 *
 * <p><b>Note:</b> This design keeps persistence concerns isolated but may
 * result in separate transaction boundaries from the command execution pipeline.
 *
 * <h2>Extensibility</h2>
 * <p>Applications are encouraged to provide custom implementations of
 * {@link org.workflow.cqrs.core.CommandStore} for:
 * <ul>
 *   <li>Integration with ORM frameworks (e.g., JPA, Hibernate)</li>
 *   <li>Advanced transaction coordination</li>
 *   <li>Database-specific optimizations</li>
 *   <li>Resilience patterns (e.g., retries, circuit breakers)</li>
 * </ul>
 *
 * <h2>Design Considerations</h2>
 * <ul>
 *   <li>Simple and explicit JDBC-based implementation</li>
 *   <li>Optimized for clarity over abstraction</li>
 *   <li>Intended as a reference or baseline implementation</li>
 * </ul>
 *
 * @since 1.0
 */
package org.workflow.cqrs.service;