/**
 * Infrastructure components for assembling and configuring the CQRS
 * command processing module.
 *
 * <h2>Overview</h2>
 * <p>This package provides the building blocks required to construct and
 * wire together a fully functional CQRS command processing pipeline.
 * It acts as the composition layer that brings together core abstractions,
 * default implementations, and configuration into a cohesive module.
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Aggregating core CQRS components into a single module</li>
 *   <li>Providing a fluent builder API for configuration</li>
 *   <li>Applying sensible defaults for middleware, post-processing, and failure handling</li>
 *   <li>Wiring together routing, execution, pipeline orchestration, and persistence</li>
 * </ul>
 *
 * <h2>Key Components</h2>
 * <ul>
 *   <li><b>{@code CommandModule}</b> – container for the assembled CQRS components</li>
 *   <li><b>{@code CommandModuleBuilder}</b> – fluent builder for configuring and creating the module</li>
 * </ul>
 *
 * <h2>Configuration Model</h2>
 * <p>The infrastructure layer allows applications to:
 * <ul>
 *   <li>Register command handlers and middleware</li>
 *   <li>Customize execution behavior</li>
 *   <li>Plug in persistence and transaction management</li>
 *   <li>Define failure handling strategies</li>
 * </ul>
 *
 * <h2>Default Behavior</h2>
 * <p>When certain components are not explicitly configured, the builder may
 * provide default implementations (e.g., persistence middleware, failure strategies),
 * depending on the presence of required dependencies such as a command store.
 *
 * <h2>Usage</h2>
 * <p>This package is typically used during application initialization:
 *
 * <pre>{@code
 * CommandModule module = CommandModuleBuilder.create()
 *     .handler(new CreateOrderHandler())
 *     .commandStore(store)
 *     .build();
 * }</pre>
 *
 * <h2>Design Considerations</h2>
 * <ul>
 *   <li>This layer is focused on configuration and composition, not business logic</li>
 *   <li>Components produced by this package are intended to be reused and shared</li>
 *   <li>The builder is not thread-safe and should be used during startup only</li>
 * </ul>
 *
 * <h2>Extensibility</h2>
 * <p>Applications can customize behavior by:
 * <ul>
 *   <li>Providing custom middleware and post-processors</li>
 *   <li>Defining custom failure strategies</li>
 *   <li>Replacing default components with specialized implementations</li>
 * </ul>
 *
 * @since 1.0
 */
package org.workflow.cqrs.infrastructure;