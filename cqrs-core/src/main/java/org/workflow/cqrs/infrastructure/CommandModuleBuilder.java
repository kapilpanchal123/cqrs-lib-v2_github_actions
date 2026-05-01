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
package org.workflow.cqrs.infrastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.workflow.cqrs.core.CommandExecutor;
import org.workflow.cqrs.core.CommandHandler;
import org.workflow.cqrs.core.CommandMiddleware;
import org.workflow.cqrs.core.CommandPipeline;
import org.workflow.cqrs.core.CommandPostProcessor;
import org.workflow.cqrs.core.CommandProperties;
import org.workflow.cqrs.core.CommandRouter;
import org.workflow.cqrs.core.CommandStore;
import org.workflow.cqrs.failure.CommandFailureStrategy;
import org.workflow.cqrs.failure.defaults.DefaultCommandExecutionFailure;
import org.workflow.cqrs.failure.defaults.DefaultCommandPostProcessorFailure;
import org.workflow.cqrs.support.DefaultCommandPersistenceMiddleware;
import org.workflow.cqrs.support.DefaultCommandPipeline;
import org.workflow.cqrs.support.DefaultCommandPostProcessor;
import org.workflow.cqrs.support.DefaultCommandRouter;
import org.workflow.cqrs.support.DefaultSynchronousCommandExecutor;
import org.workflow.cqrs.transactions.CommandTransactionManager;
import org.workflow.cqrs.transactions.support.NoOpCommandTransactionManager;
import org.workflow.cqrs.utils.Ordered;

/**
 * Builder for constructing a fully configured {@link CommandModule}.
 *
 * <p>{@code CommandModuleBuilder} provides a fluent API for assembling
 * the core components of a CQRS command processing pipeline, including:
 * handlers, middleware, post-processors, failure strategies, and infrastructure
 * dependencies such as transaction management and persistence.
 *
 * <h2>Purpose</h2>
 * <p>This builder is responsible for:
 * <ul>
 *   <li>Registering command handlers and pipeline components</li>
 *   <li>Applying sensible defaults when optional components are not provided</li>
 *   <li>Wiring together router, executor, pipeline, and module</li>
 * </ul>
 *
 * <h2>Required Components</h2>
 * <ul>
 *   <li>At least one {@link CommandHandler} must be registered</li>
 * </ul>
 *
 * <p>If no handlers are provided, {@link #build()} will throw an exception.
 *
 * <h2>Optional Components</h2>
 * <p>The following components are optional and will be defaulted if not provided:
 * <ul>
 *   <li>{@link CommandMiddleware}</li>
 *   <li>{@link CommandPostProcessor}</li>
 *   <li>{@link CommandFailureStrategy}</li>
 *   <li>{@link CommandTransactionManager}</li>
 * </ul>
 *
 * <h2>Default Behavior</h2>
 * <p>If a {@link CommandStore} is configured, the builder automatically applies:
 * <ul>
 *   <li>{@link DefaultCommandPersistenceMiddleware}</li>
 *   <li>{@link DefaultCommandPostProcessor}</li>
 *   <li>{@link DefaultCommandExecutionFailure}</li>
 *   <li>{@link DefaultCommandPostProcessorFailure}</li>
 * </ul>
 *
 * <p>If no {@link CommandTransactionManager} is provided, a
 * {@link NoOpCommandTransactionManager} is used.
 *
 * <h2>Ordering</h2>
 * <p>All registered components are sorted using the {@link Ordered} interface:
 * <ul>
 *   <li>Lower values indicate higher precedence</li>
 *   <li>Components without {@link Ordered} default to lowest priority</li>
 * </ul>
 *
 * <h2>Execution Model</h2>
 * <p>The builder constructs:
 * <ol>
 *   <li>{@link CommandRouter} for handler resolution</li>
 *   <li>{@link CommandExecutor} for invoking command execution</li>
 *   <li>{@link CommandPipeline} for orchestration and lifecycle management</li>
 *   <li>{@link CommandModule} as the final aggregated container</li>
 * </ol>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * CommandModule module = CommandModuleBuilder.create()
 *     .handler(new CreateOrderHandler())
 *     .middleware(new LoggingMiddleware())
 *     .commandStore(store)
 *     .build();
 * }</pre>
 *
 * <h2>Design Considerations</h2>
 * <ul>
 *   <li>The builder is not thread-safe and should be used during application initialization</li>
 *   <li>Built {@link CommandModule} instances are immutable and thread-safe</li>
 *   <li>Defaults are applied only when corresponding components are not explicitly provided</li>
 * </ul>
 *
 * @see CommandModule
 * @see CommandHandler
 * @see CommandMiddleware
 * @see CommandPostProcessor
 * @see CommandFailureStrategy
 */
public final class CommandModuleBuilder {
  private final List<CommandHandler<?,?>> handlers = new ArrayList<>();
  private final List<CommandMiddleware> middlewares = new ArrayList<>();
  private final List<CommandPostProcessor<?>> postProcessors = new ArrayList<>();
  private final List<CommandFailureStrategy> failureStrategies = new ArrayList<>();

  private CommandTransactionManager transactionManager;
  private CommandStore commandStore;
  private CommandProperties properties = new CommandProperties();

  private CommandModuleBuilder() {
  }

  public static CommandModuleBuilder create() {
    return new CommandModuleBuilder();
  }

  /**
   * Registers a {@link CommandHandler}.
   *
   * <p>At least one handler must be registered before calling {@link #build()}.
   *
   * @param handler the command handler to register
   * @return this builder instance for chaining
   */
  public CommandModuleBuilder handler(final CommandHandler<?,?> handler) {
    this.handlers.add(Objects.requireNonNull(handler, "Handler must not be null"));
    return this;
  }

  public CommandModuleBuilder handlers(final List<? extends CommandHandler<?,?>> handlers) {
    Objects.requireNonNull(handlers, "Handler list must not be null");
    this.handlers.addAll(handlers);
    return this;
  }

  public CommandModuleBuilder middleware(final CommandMiddleware middleware) {
    this.middlewares.add(Objects.requireNonNull(middleware, "Middleware must not be null"));
    return this;
  }

  public CommandModuleBuilder middlewares(final List<? extends CommandMiddleware> middlewares) {
    Objects.requireNonNull(middlewares, "Middleware list must not be null");
    this.middlewares.addAll(middlewares);
    return this;
  }

  public CommandModuleBuilder postProcessor(final CommandPostProcessor<?> postProcessor) {
    this.postProcessors.add(Objects.requireNonNull(postProcessor, "Post processor must not be null"));
    return this;
  }

  public CommandModuleBuilder postProcessors(final List<? extends CommandPostProcessor<?>> postProcessors) {
    Objects.requireNonNull(postProcessors, "Post processor list must not be null");
    this.postProcessors.addAll(postProcessors);
    return this;
  }

  public CommandModuleBuilder failureStrategy(final CommandFailureStrategy failureStrategy) {
    this.failureStrategies.add(Objects.requireNonNull(failureStrategy, "Failure strategy must not be null"));
    return this;
  }

  public CommandModuleBuilder failureStrategies(final List<? extends CommandFailureStrategy> failureStrategies) {
    Objects.requireNonNull(failureStrategies, "Failure strategy list must not be null");
    this.failureStrategies.addAll(failureStrategies);
    return this;
  }

  public CommandModuleBuilder transactionManager(final CommandTransactionManager transactionManager) {
    this.transactionManager = Objects.requireNonNull(transactionManager, "TransactionManager must not be null");
    return this;
  }

  /**
   * Sets the {@link CommandStore} used for persistence.
   *
   * <p>If provided, default persistence middleware, post-processors,
   * and failure strategies may be automatically applied.
   *
   * @param commandStore the command store to use
   * @return this builder instance for chaining
   */
  public CommandModuleBuilder commandStore(final CommandStore commandStore) {
    this.commandStore = Objects.requireNonNull(commandStore, "Commandstore must not be null");
    return this;
  }

  public CommandModuleBuilder properties(final CommandProperties properties) {
    this.properties = Objects.requireNonNull(properties, "Properties must not be null");
    return this;
  }

  /**
   * Builds and returns a fully configured {@link CommandModule}.
   *
   * <p>This method performs the following steps:
   * <ol>
   *   <li>Validates required components (e.g., handlers)</li>
   *   <li>Applies default implementations if necessary</li>
   *   <li>Sorts all components using {@link Ordered}</li>
   *   <li>Constructs router, executor, and pipeline</li>
   *   <li>Assembles and returns the final {@link CommandModule}</li>
   * </ol>
   *
   * @return a fully initialized {@code CommandModule}
   * @throws IllegalStateException if no command handlers are registered
   */
  public CommandModule build() {
    if (handlers.isEmpty()) {
      throw new IllegalStateException("CommandModuleBuilder requires at least one CommandHandler. "
          + "Register one via .handler(...) / .handlers(...). "
          + "In Spring Boot, ensure your CommandHandler classes carry @Component "
          + "(or @Service / @Bean) so Spring injects them into the "
          + "List<CommandHandler<?,?>> parameter of your commandModule @Bean method.");
    }

    applyDefaultsIfNeeded();
    sortByOrder(handlers);
    sortByOrder(middlewares);
    sortByOrder(postProcessors);
    sortByOrder(failureStrategies);

    final CommandTransactionManager txManager = resolvedTransactionManager();
    final CommandRouter router = new DefaultCommandRouter(handlers);
    final CommandExecutor executor = buildExecutor(router);
    final CommandPipeline pipeline =
        new DefaultCommandPipeline(executor, postProcessors, failureStrategies, txManager);

    return new CommandModule(pipeline, router, executor, commandStore);
  }

  private void applyDefaultsIfNeeded() {
    if(commandStore == null) {
      return;
    }
    if(middlewares.isEmpty()) {
      middlewares.add(new DefaultCommandPersistenceMiddleware(commandStore));
    }
    if(postProcessors.isEmpty()) {
      postProcessors.add(new DefaultCommandPostProcessor<>(commandStore));
    }
    if(failureStrategies.isEmpty()) {
      failureStrategies.add(new DefaultCommandExecutionFailure(commandStore));
      failureStrategies.add(new DefaultCommandPostProcessorFailure(commandStore));
    }
  }

  private CommandTransactionManager resolvedTransactionManager() {
    return transactionManager != null ? transactionManager : new NoOpCommandTransactionManager();
  }

  private CommandExecutor buildExecutor(final CommandRouter router) {
    return switch (properties.getExecutor()) {
      case ASYNC, DISRUPTOR, SYNC ->
          new DefaultSynchronousCommandExecutor(middlewares, router);
    };
  }

  private static void sortByOrder(final List<?> list) {
    list.sort((a,b) -> {
      final int orderA = (a instanceof Ordered o) ? o.getOrder() : Integer.MAX_VALUE;
      final int orderB = (b instanceof Ordered o) ? o.getOrder() : Integer.MAX_VALUE;
      return Integer.compare(orderA, orderB);
    });
  }
}
