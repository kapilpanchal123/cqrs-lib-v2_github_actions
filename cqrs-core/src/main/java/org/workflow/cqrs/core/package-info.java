/**
 * Core abstractions for the CQRS (Command Query Responsibility Segregation)
 * command processing model.
 *
 * <h2>Overview</h2>
 * <p>This package defines the fundamental building blocks required to
 * construct a command processing pipeline. It focuses on modeling
 * state-changing operations and their execution lifecycle in a consistent,
 * extensible, and observable manner.
 *
 * <h2>Core Concepts</h2>
 * <ul>
 *   <li><b>Commands</b> represent state-changing operations and carry both
 *       business data and contextual metadata.</li>
 *   <li><b>Handlers</b> execute commands and contain the core business logic.</li>
 *   <li><b>Pipelines</b> orchestrate command execution and enable middleware-style
 *       processing (e.g., validation, transactions, logging).</li>
 *   <li><b>Middleware</b> intercepts and augments execution before and around handlers.</li>
 *   <li><b>Post-processors</b> execute after command handling for side effects
 *       such as auditing, event publishing, or notifications.</li>
 *   <li><b>Results</b> (e.g., {@code PipelineResult}) provide structured success
 *       or failure outcomes across pipeline stages.</li>
 * </ul>
 *
 * <h2>Execution Flow</h2>
 * <p>A typical command processing flow follows:
 * <ol>
 *   <li>Command creation and enrichment (e.g., correlationId, tenantId)</li>
 *   <li>Pipeline invocation</li>
 *   <li>Middleware pre-processing</li>
 *   <li>Command handler execution</li>
 *   <li>Middleware post-processing</li>
 *   <li>Post-processor execution</li>
 *   <li>Result propagation or failure handling</li>
 * </ol>
 *
 * <h2>Cross-Cutting Concerns</h2>
 * <ul>
 *   <li><b>Idempotency:</b> Ensures safe retries without duplicate side effects</li>
 *   <li><b>Observability:</b> Enables tracing via correlation identifiers</li>
 *   <li><b>Multi-tenancy:</b> Supports tenant isolation and routing</li>
 *   <li><b>Error Handling:</b> Centralized handling of failures across pipeline stages</li>
 * </ul>
 *
 * <h2>Extensibility</h2>
 * <p>The abstractions in this package are designed to be extended and customized:
 * <ul>
 *   <li>Custom middleware can be introduced to modify execution behavior</li>
 *   <li>Custom handlers define domain-specific logic</li>
 *   <li>Post-processors enable integration with external systems</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>Most abstractions are stateless or immutable by design. Implementations
 * should ensure thread safety when maintaining shared or mutable state.
 *
 * @since 1.0
 */
package org.workflow.cqrs.core;