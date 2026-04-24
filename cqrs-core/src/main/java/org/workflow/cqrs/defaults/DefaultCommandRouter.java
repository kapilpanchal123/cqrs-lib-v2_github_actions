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
import org.workflow.cqrs.core.Command;
import org.workflow.cqrs.core.CommandHandler;
import org.workflow.cqrs.core.CommandRouter;

/**
 * Default implementation of the {@link CommandRouter} responsible for resolving
 * appropriate {@link CommandHandler} instances for incoming {@link Command}s
 * in a CQRS (Command Query Responsibility Segregation) system.
 *
 * <p>This router maintains a registry of available command handlers and selects
 * the first matching handler based on the command's payload type using
 * {@link CommandHandler#matches(Command)}.
 *
 * <h2>Purpose</h2>
 * <p>The primary responsibility of this class is to:
 * <ul>
 *   <li>Maintain a collection of registered {@link CommandHandler} instances</li>
 *   <li>Resolve the correct handler for a given command at runtime</li>
 *   <li>Support dynamic dispatching based on command type</li>
 * </ul>
 *
 * <h2>Routing Strategy</h2>
 * <p>Handler resolution is performed using a linear search over the registered
 * handlers. The first handler that returns {@code true} from
 * {@link CommandHandler#matches(Command)} is selected.
 *
 * <p><b>Note:</b> This implementation assumes that only one handler exists per
 * command type. If multiple handlers match, only the first one is used.
 *
 * <h2>Thread Safety</h2>
 * <p>This class is thread-safe if the provided list of handlers is immutable
 * or externally synchronized. The internal state is not modified after construction.
 *
 * <h2>Limitations</h2>
 * <ul>
 *   <li>Uses linear search for handler resolution (O(n) complexity)</li>
 *   <li>Relies on runtime type matching via {@link CommandHandler#matches(Command)}</li>
 *   <li>Does not enforce uniqueness of handlers per command type</li>
 * </ul>
 *
 * <h2>Error Handling</h2>
 * <p>If no matching handler is found, an {@link IllegalArgumentException} is thrown.
 *
 * @see CommandRouter
 * @see CommandHandler
 * @see Command
 */
public class DefaultCommandRouter implements CommandRouter {
  private final List<CommandHandler<?,?>> commandHandlers;

  /**
   * Creates a new {@code DefaultCommandRouter} with the given list of command handlers.
   *
   * @param commandHandlers the list of registered command handlers used for routing
   */
  public DefaultCommandRouter(final List<CommandHandler<?,?>> commandHandlers) {
    this.commandHandlers = commandHandlers;
  }

  /**
   * Resolves the appropriate {@link CommandHandler} for the given {@link Command}.
   *
   * <p>The router scans through the registered handlers and selects the first handler
   * whose {@link CommandHandler#matches(Command)} method returns {@code true}.
   *
   * @param command the command for which a handler must be resolved
   * @param <REQ> the type of the command payload
   * @param <RES> the type of the result produced by the handler
   * @return a matching {@link CommandHandler} capable of processing the command
   * @throws IllegalArgumentException if the command is {@code null}
   * @throws IllegalArgumentException if no matching handler is found
   */
  @SuppressWarnings("unchecked")
  @Override
  public <REQ,RES> CommandHandler<REQ,RES> route(final Command<REQ> command) {
    if(command == null) {
      throw new IllegalArgumentException("Command must not be null");
    }

    return (CommandHandler<REQ,RES>) commandHandlers
        .stream()
        .filter(handler -> handler.matches(command))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Command Handler Not Found for CommandId = " + command.getId()));
  }
}
