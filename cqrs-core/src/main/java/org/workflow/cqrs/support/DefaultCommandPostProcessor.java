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
package org.workflow.cqrs.support;

import org.workflow.cqrs.core.Command;
import org.workflow.cqrs.core.CommandPostProcessor;
import org.workflow.cqrs.core.CommandStatus;
import org.workflow.cqrs.core.CommandStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link CommandPostProcessor} for CQRS command execution.
 *
 * <p>This post-processor updates the persistent state of a {@link Command} after
 * successful execution, marking it as {@link CommandStatus#COMPLETED}.
 *
 * <h2>Purpose</h2>
 * <p>This implementation ensures that command execution results are consistently
 * reflected in the {@link CommandStore}, enabling:
 * <ul>
 *   <li>Accurate lifecycle tracking of commands</li>
 *   <li>Auditability of successful executions</li>
 *   <li>Consistent state visibility across distributed systems</li>
 * </ul>
 *
 * <h2>Behavior</h2>
 * <p>Upon invocation, this post-processor:
 * <ol>
 *   <li>Receives a successfully executed {@link Command}</li>
 *   <li>Transitions its status to {@link CommandStatus#COMPLETED}</li>
 *   <li>Persists the updated state using the {@link CommandStore}</li>
 * </ol>
 *
 * <h2>Failure Handling</h2>
 * <p>This implementation assumes successful execution of the command.
 * It does not handle failure scenarios or transition the command to
 * {@link CommandStatus#FAILED}. Failure handling is expected to be
 * implemented by a dedicated post-processor or upstream component.
 *
 * <h2>Logging</h2>
 * <p>A logger is provided for observability and debugging. Subclasses
 * may extend this implementation to add structured logging or metrics.
 *
 * <h2>Thread Safety</h2>
 * <p>This class is thread-safe provided that the underlying
 * {@link CommandStore} implementation is thread-safe.
 *
 * @param <T> the type of the command payload being processed
 *
 * @see CommandPostProcessor
 * @see CommandStore
 * @see CommandStatus
 */
public class DefaultCommandPostProcessor<T> implements CommandPostProcessor<T> {

  private static final Logger log = LoggerFactory.getLogger(DefaultCommandPostProcessor.class);

  private final CommandStore commandStore;

  /**
   * Creates a new {@code DefaultCommandPostProcessor}.
   *
   * @param commandStore the store used to persist command status updates
   */
  public DefaultCommandPostProcessor(final CommandStore commandStore) {
    this.commandStore = commandStore;
  }

  /**
   * Marks the given {@link Command} as {@link CommandStatus#COMPLETED}
   * in the {@link CommandStore}.
   *
   * <p>This method should be invoked only after successful command execution.
   *
   * @param command the command that has been successfully processed
   */
  @Override
  public void run(final Command<T> command) {
      commandStore.update(command.getId(), CommandStatus.COMPLETED);
  }
}
