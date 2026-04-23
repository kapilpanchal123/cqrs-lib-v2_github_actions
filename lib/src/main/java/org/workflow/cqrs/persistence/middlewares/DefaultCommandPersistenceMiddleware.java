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
package org.workflow.cqrs.persistence.middlewares;

import org.workflow.cqrs.core.Command;
import org.workflow.cqrs.core.CommandMiddleware;
import org.workflow.cqrs.persistence.repository.CommandStore;

/**
 * Default persistence middleware for CQRS command execution.
 *
 * <p>This middleware is responsible for persisting incoming {@link Command} instances
 * before they are processed by the command execution pipeline.
 *
 * <h2>Purpose</h2>
 * <p>It acts as a cross-cutting concern in the CQRS pipeline to ensure that every command
 * is stored in a durable {@link CommandStore} for:
 * <ul>
 *   <li>Audit logging</li>
 *   <li>Replay capability</li>
 *   <li>Debugging and traceability</li>
 *   <li>Failure recovery scenarios</li>
 * </ul>
 *
 * <h2>Execution Phase</h2>
 * <p>This middleware is typically executed early in the pipeline, before the command
 * reaches its {@link org.workflow.cqrs.core.CommandHandler}.
 *
 * <h2>Behavior</h2>
 * <p>The middleware performs a simple persistence operation:
 * <ul>
 *   <li>Receives a command</li>
 *   <li>Delegates persistence to {@link CommandStore#save(Command)}</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>This class is thread-safe if the underlying {@link CommandStore} implementation
 * is thread-safe.
 *
 * <h2>Side Effects</h2>
 * <p>This middleware introduces a side effect by writing to persistent storage
 * before command execution begins.
 *
 * @see CommandMiddleware
 * @see CommandStore
 * @see Command
 */
public class DefaultCommandPersistenceMiddleware implements CommandMiddleware {

  /**
   * Storage component responsible for persisting commands.
   */
  private final CommandStore commandStore;

  /**
   * Creates a new {@code DefaultCommandPersistenceMiddleware}.
   *
   * @param commandStore the store used to persist commands
   */
  public DefaultCommandPersistenceMiddleware(final CommandStore commandStore) {
    this.commandStore = commandStore;
  }

  /**
   * Persists the given {@link Command} into the configured {@link CommandStore}.
   *
   * @param command the command to persist
   */
  @Override
  public void invoke(final Command<?> command) {
    commandStore.save(command);
  }
}
