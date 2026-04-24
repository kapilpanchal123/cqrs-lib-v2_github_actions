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
package org.workflow.cqrs.defaults;

import java.util.List;
import java.util.function.Supplier;
import org.workflow.cqrs.core.Command;
import org.workflow.cqrs.core.CommandExecutor;
import org.workflow.cqrs.core.CommandHandler;
import org.workflow.cqrs.core.CommandMiddleware;
import org.workflow.cqrs.core.CommandRouter;
import org.workflow.cqrs.core.CommandStatus;

/**
 * Default synchronous implementation of the {@link CommandExecutor} used in a
 * CQRS (Command Query Responsibility Segregation) system.
 *
 * <p>This executor processes commands in a blocking (synchronous) manner by:
 * <ul>
 *   <li>Applying configured {@link CommandMiddleware} instances</li>
 *   <li>Routing the command to an appropriate {@link CommandHandler}</li>
 *   <li>Returning a {@link Supplier} that executes the handler</li>
 * </ul>
 *
 * <h2>Execution Model</h2>
 * <p>This implementation performs middleware execution immediately when
 * {@link #execute(Command)} is called, but defers actual command handling
 * until the returned {@link Supplier#get()} is invoked.
 *
 * <h2>Processing Flow</h2>
 * <ol>
 *   <li>Command status is set to {@link CommandStatus#PROCESSING}</li>
 *   <li>All registered {@link CommandMiddleware} instances are invoked sequentially</li>
 *   <li>The {@link CommandRouter} resolves the appropriate handler</li>
 *   <li>A {@link Supplier} is returned that executes the handler</li>
 * </ol>
 *
 * <h2>Middleware Execution</h2>
 * <p>Middleware components are executed in the order they are provided. They are typically used for:
 * <ul>
 *   <li>Logging and tracing</li>
 *   <li>Validation</li>
 *   <li>Security checks</li>
 *   <li>Metadata enrichment</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>This class is thread-safe provided that:
 * <ul>
 *   <li>The injected {@link CommandRouter} is thread-safe</li>
 *   <li>The provided middleware list is immutable or externally synchronized</li>
 * </ul>
 *
 * <h2>Limitations</h2>
 * <ul>
 *   <li>Execution is strictly synchronous (no async or parallel processing)</li>
 *   <li>Middleware cannot prevent handler resolution once execution proceeds</li>
 * </ul>
 *
 * @see CommandExecutor
 * @see CommandMiddleware
 * @see CommandRouter
 * @see CommandHandler
 */
public class DefaultSynchronousCommandExecutor implements CommandExecutor {
  private final List<CommandMiddleware> middlewares;
  private final CommandRouter router;

  /**
   * Creates a new synchronous command executor.
   *
   * @param middlewares the list of middleware components applied before execution
   * @param router the router responsible for resolving the appropriate handler
   */
  public DefaultSynchronousCommandExecutor(
      final List<CommandMiddleware> middlewares,
      final CommandRouter router) {
    this.middlewares = middlewares;
    this.router = router;
  }

  /**
   * Executes the given {@link Command} in a synchronous pipeline.
   *
   * <p>Middleware is executed immediately, and the actual command handling is deferred
   * until the returned {@link Supplier} is invoked.
   *
   * @param command the command to execute
   * @param <REQ> the type of the command payload
   * @param <RES> the type of the result returned by the handler
   * @return a {@link Supplier} that executes the command handler when invoked
   */
  @Override
  public <REQ, RES> Supplier<RES> execute(final Command<REQ> command) {
    command.setStatus(CommandStatus.PROCESSING);
    for(final CommandMiddleware middleware : middlewares) {
      middleware.invoke(command);
    }
    final CommandHandler<REQ,RES> handler = router.route(command);
    return(() -> handler.handle(command));
  }
}
