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

import java.util.function.Supplier;

/**
 * Executes {@link Command} instances within a CQRS (Command Query Responsibility Segregation) system.
 *
 * <p>The {@code CommandExecutor} acts as an entry point for dispatching commands
 * to their corresponding {@link CommandHandler} implementations. It abstracts
 * the underlying routing, execution, and optional middleware (e.g., logging,
 * validation, transaction management).
 *
 * <h2>Execution Model</h2>
 * <p>This interface returns a {@link Supplier} instead of executing the command eagerly.
 * This enables:
 *
 * <ul>
 *   <li><b>Lazy execution:</b> Command handling can be deferred until explicitly invoked.</li>
 *   <li><b>Pipeline composition:</b> Callers can wrap execution with additional behavior
 *       such as retries, transactions, or monitoring.</li>
 *   <li><b>Separation of concerns:</b> Dispatching and execution timing are decoupled.</li>
 * </ul>
 *
 * <h2>Typical Flow</h2>
 * <ol>
 *   <li>A {@link Command} is created and enriched with metadata.</li>
 *   <li>The command is passed to {@code execute(...)}.</li>
 *   <li>A {@link Supplier} is returned representing the execution logic.</li>
 *   <li>The caller invokes {@link Supplier#get()} to trigger processing.</li>
 * </ol>
 *
 * <h2>Type Safety</h2>
 * <p>The generic parameters ensure compile-time consistency:
 * <ul>
 *   <li>{@code REQ} – the type of the command payload</li>
 *   <li>{@code RES} – the type of the result produced by the handler</li>
 * </ul>
 *
 * <h2>Responsibilities</h2>
 * <p>Implementations typically:
 * <ul>
 *   <li>Locate the appropriate {@link CommandHandler} based on the command type</li>
 *   <li>Apply cross-cutting concerns (e.g., logging, correlation, validation)</li>
 *   <li>Invoke the handler and return the result</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>Implementations should be thread-safe as they may be shared across
 * multiple concurrent requests.
 *
 * @see Command
 * @see CommandHandler
 * @see Supplier
 */
public interface CommandExecutor {
  /**
   * Prepares execution of the given {@link Command}.
   *
   * <p>This method does not immediately execute the command. Instead,
   * it returns a {@link Supplier} that encapsulates the execution logic.
   * The caller is responsible for invoking {@link Supplier#get()} to
   * trigger processing.
   *
   * @param command the command to be executed
   * @param <REQ> the type of the command payload
   * @param <RES> the type of the result produced by the command handler
   * @return a {@link Supplier} that, when invoked, executes the command
   *         and returns the result
   */
  <REQ,RES> Supplier<RES> execute(final Command<REQ> command);
}
