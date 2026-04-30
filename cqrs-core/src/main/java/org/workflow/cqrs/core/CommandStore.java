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
package org.workflow.cqrs.core;

import java.util.UUID;

/**
 * Persistence abstraction for storing and updating {@link Command} lifecycle information
 * in a CQRS (Command Query Responsibility Segregation) system.
 *
 * <p>The {@code CommandStore} is responsible for maintaining a durable record of commands
 * for auditing, traceability, and recovery purposes.
 *
 * <h2>Purpose</h2>
 * <p>This interface defines the contract for:
 * <ul>
 *   <li>Persisting newly received commands</li>
 *   <li>Updating the execution status of existing commands</li>
 * </ul>
 *
 * <h2>Usage in CQRS Pipeline</h2>
 * <p>Implementations of this interface are typically used by:
 * <ul>
 *   <li>Persistence middleware to store incoming commands</li>
 *   <li>Execution components to update command lifecycle state</li>
 *   <li>Monitoring and auditing systems</li>
 * </ul>
 *
 * <h2>Command Lifecycle Tracking</h2>
 * <p>The store is expected to persist and manage transitions of {@link CommandStatus}
 * such as INIT, PENDING, PROCESSING, COMPLETED, and FAILED.
 *
 * <h2>Thread Safety</h2>
 * <p>Implementations should be thread-safe as they are expected to be accessed
 * concurrently in distributed or multi-threaded environments.
 *
 * @see Command
 * @see CommandStatus
 */
public interface CommandStore {

  /**
   * Persists a new {@link Command} into the storage system.
   *
   * <p>This method is typically invoked before command execution begins to ensure
   * durability and traceability.
   *
   * @param command the command to be persisted
   */
  void save(final Command<?> command, final CommandStatus status);

  void save(final Command<?> command, final CommandStatus status, final Throwable t);

  /**
   * Updates the execution status of an existing command.
   *
   * <p>This method is used to track lifecycle transitions of a command during
   * or after execution.
   *
   * @param commandId the unique identifier of the command
   * @param status the new {@link CommandStatus} to be applied
   */
  void update(final UUID commandId, final CommandStatus status);

  void update(final UUID commandId, final CommandStatus status, final Throwable t);

}
