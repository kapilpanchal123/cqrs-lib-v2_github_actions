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

import org.workflow.cqrs.core.Command;
import org.workflow.cqrs.core.CommandMiddleware;
import org.workflow.cqrs.core.CommandStatus;
import org.workflow.cqrs.core.CommandStore;
import org.workflow.cqrs.transactions.CommandTransactionManager;

/**
 * Default persistence middleware for CQRS command execution.
 *
 * <p>This middleware persists incoming {@link Command} instances before they are processed
 * by the command execution pipeline.
 *
 * <h2>Purpose</h2>
 * <p>This acts as a cross-cutting concern in the CQRS pipeline to ensure every command
 * is stored in a durable {@link CommandStore} for:
 * <ul>
 *   <li>Audit logging</li>
 *   <li>Replay capability</li>
 *   <li>Debugging and traceability</li>
 *   <li>Failure recovery scenarios</li>
 * </ul>
 *
 * <h2>Execution Phase</h2>
 * <p>This middleware is executed early in the pipeline, before the command
 * reaches its {@link org.workflow.cqrs.core.CommandHandler}.
 *
 * <h2>Behavior</h2>
 * <p>This middleware performs persistence by delegating to:
 * {@link CommandStore#save(Command, CommandStatus)}
 *
 * <h2>Thread Safety</h2>
 * <p>This class is thread-safe if the underlying {@link CommandStore} implementation
 * is thread-safe.
 *
 * <h2>Side Effects</h2>
 * <p>This middleware writes to persistent storage before command execution begins.
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

  private final CommandTransactionManager transactionManager;

  /**
   * Creates a new {@code DefaultCommandPersistenceMiddleware}.
   *
   * @param commandStore the store used to persist commands
   */
  public DefaultCommandPersistenceMiddleware(
      final CommandStore commandStore,
      final CommandTransactionManager transactionManager) {
    this.commandStore = commandStore;
    this.transactionManager = transactionManager;
  }

  /**
   * Persists the given {@link Command} into the configured {@link CommandStore}.
   *
   * @param command the command to persist
   */
  @Override
  public void invoke(final Command<?> command) {
//    commandStore.save(command, CommandStatus.PROCESSING);
//    transactionManager.execute(txStatus -> {
      commandStore.save(command, CommandStatus.PROCESSING);
//      return null;
    });
  }
}
