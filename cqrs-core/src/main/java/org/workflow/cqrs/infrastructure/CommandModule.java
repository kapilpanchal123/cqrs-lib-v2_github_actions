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

import org.workflow.cqrs.core.CommandExecutor;
import org.workflow.cqrs.core.CommandPipeline;
import org.workflow.cqrs.core.CommandRouter;
import org.workflow.cqrs.core.CommandStore;

/**
 * Aggregates the core components required for command processing in a CQRS system.
 *
 * <p>{@code CommandModule} acts as a composition root, encapsulating the primary
 * building blocks involved in the command execution lifecycle. It provides a
 * unified access point to the configured pipeline, routing, execution, and
 * persistence mechanisms.
 *
 * <h2>Purpose</h2>
 * <p>This class is intended to:
 * <ul>
 *   <li>Group together core CQRS components</li>
 *   <li>Simplify wiring and configuration</li>
 *   <li>Provide a central entry point for command processing infrastructure</li>
 * </ul>
 *
 * <h2>Contained Components</h2>
 * <ul>
 *   <li><b>{@link CommandPipeline}</b> – orchestrates command execution and middleware flow</li>
 *   <li><b>{@link CommandRouter}</b> – resolves the appropriate handler for a command</li>
 *   <li><b>{@link CommandExecutor}</b> – executes commands using the configured pipeline</li>
 *   <li><b>{@link CommandStore}</b> – persists command state and lifecycle information</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <p>Typically constructed during application initialization and injected into
 * services or entry points responsible for dispatching commands.
 *
 * <pre>{@code
 * CommandModule module = new CommandModule(pipeline, router, executor, store);
 * module.getExecutor().execute(command);
 * }</pre>
 *
 * <h2>Design Considerations</h2>
 * <ul>
 *   <li>This class is a simple container and does not enforce lifecycle or execution rules</li>
 *   <li>All components are expected to be fully initialized and compatible</li>
 *   <li>Immutability ensures safe sharing across threads</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>This class is immutable and thread-safe, assuming the provided components
 * are themselves thread-safe.
 *
 * @see CommandPipeline
 * @see CommandRouter
 * @see CommandExecutor
 * @see CommandStore
 */
public final class CommandModule {
  private final CommandPipeline pipeline;
  private final CommandRouter router;
  private final CommandExecutor executor;
  private final CommandStore commandStore;

  /**
   * Constructs a {@code CommandModule} with the provided CQRS components.
   *
   * @param pipeline the command pipeline responsible for orchestration
   * @param router the router used to resolve command handlers
   * @param executor the executor responsible for invoking command processing
   * @param commandStore the store used for persisting command state
   */
  public CommandModule(
      final CommandPipeline pipeline,
      final CommandRouter router,
      final CommandExecutor executor,
      final CommandStore commandStore) {
    this.pipeline = pipeline;
    this.router = router;
    this.executor = executor;
    this.commandStore = commandStore;
  }

  /**
   * Returns the configured {@link CommandPipeline}.
   *
   * @return the command pipeline
   */
  public CommandPipeline getPipeline() {
    return pipeline;
  }

  /**
   * Returns the configured {@link CommandRouter}.
   *
   * @return the command router
   */
  public CommandRouter getRouter() {
    return router;
  }

  /**
   * Returns the configured {@link CommandExecutor}.
   *
   * @return the command executor
   */
  public CommandExecutor getExecutor() {
    return executor;
  }

  /**
   * Returns the configured {@link CommandStore}.
   *
   * @return the command store
   */
  public CommandStore getCommandStore() {
    return commandStore;
  }
}
