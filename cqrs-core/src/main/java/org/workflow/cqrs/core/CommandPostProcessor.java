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

/**
 * Represents a post-processing hook in a CQRS (Command Query Responsibility Segregation) system.
 *
 * <p>A {@code CommandPostProcessor} is executed after a {@link Command} has been processed
 * by its corresponding {@link CommandHandler}. It is used to perform side effects or
 * follow-up actions that are not part of the core business logic.
 *
 * <h2>Purpose</h2>
 * <p>This functional interface enables separation of concerns by allowing post-execution
 * logic to be handled independently of the command handler. Typical use cases include:
 *
 * <ul>
 *   <li>Audit logging</li>
 *   <li>Metrics collection</li>
 *   <li>Event publishing</li>
 *   <li>Notification triggers</li>
 *   <li>Cleanup or enrichment of persisted state</li>
 * </ul>
 *
 * <h2>Execution Timing</h2>
 * <p>Post-processors are invoked after successful (or optionally failed) command execution,
 * depending on pipeline configuration.
 *
 * <h2>Functional Interface</h2>
 * <p>This interface is marked as a {@link FunctionalInterface}, allowing it to be used
 * with lambda expressions or method references.
 *
 * <h2>Thread Safety</h2>
 * <p>Implementations should be thread-safe as they may be executed concurrently
 * in multi-threaded command processing pipelines.
 *
 * @param <T> the type of the command payload being processed
 *
 * @see Command
 * @see CommandHandler
 */
@FunctionalInterface
public interface CommandPostProcessor<T> {

  /**
   * Executes post-processing logic for the given {@link Command}.
   *
   * <p>This method is invoked after the command has been processed by its handler,
   * allowing implementations to perform side effects such as logging, auditing,
   * or event publishing.
   *
   * @param command the command that has been processed
   */
  void run(final Command<T> command);
}
