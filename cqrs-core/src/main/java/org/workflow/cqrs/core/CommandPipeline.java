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
 * Represents the entry point of a CQRS (Command Query Responsibility Segregation) command execution pipeline.
 *
 * <p>The {@code CommandPipeline} is responsible for orchestrating the full lifecycle of a {@link Command},
 * including middleware execution, handler resolution, and result production.
 *
 * <h2>Purpose</h2>
 * <p>This interface abstracts the internal mechanics of command execution and provides a unified way to:
 * <ul>
 *   <li>Dispatch commands to appropriate {@link CommandHandler} implementations</li>
 *   <li>Apply {@link CommandMiddleware} in a defined execution order</li>
 *   <li>Handle cross-cutting concerns such as logging, tracing, validation, and idempotency</li>
 * </ul>
 *
 * <h2>Execution Model</h2>
 * <p>The pipeline does not execute the command immediately. Instead, it returns a {@link Supplier}
 * representing the deferred execution of the command.
 *
 * <p>This allows:
 * <ul>
 *   <li>Lazy execution of command logic</li>
 *   <li>Composition with additional behaviors (e.g., retries, transactions)</li>
 *   <li>Explicit control over when execution occurs via {@link Supplier#get()}</li>
 * </ul>
 *
 * <h2>Processing Flow</h2>
 * <ol>
 *   <li>Command is received by the pipeline</li>
 *   <li>Middleware chain is executed (pre-processing)</li>
 *   <li>Appropriate {@link CommandHandler} is resolved using command type</li>
 *   <li>Handler processes the command</li>
 *   <li>Middleware chain may continue post-processing (if designed)</li>
 *   <li>Result is wrapped in a {@link Supplier}</li>
 * </ol>
 *
 * <h2>Type Safety</h2>
 * <p>The generic parameters ensure compile-time consistency between:
 * <ul>
 *   <li>{@code REQ} – the command payload type</li>
 *   <li>{@code RES} – the result type returned by the handler</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>Implementations should be thread-safe as pipelines are typically shared across
 * multiple concurrent command executions.
 *
 * @see Command
 * @see CommandHandler
 * @see CommandMiddleware
 * @see Supplier
 */
public interface CommandPipeline {

  /**
   * Submits a {@link Command} for processing through the CQRS execution pipeline.
   *
   * <p>The returned {@link Supplier} represents the deferred execution of the command.
   * The actual processing (middleware execution, handler invocation, and result creation)
   * occurs when {@link Supplier#get()} is called.
   *
   * @param command the command to be executed through the pipeline
   * @param <REQ> the type of the command payload
   * @param <RES> the type of the result produced by the command handler
   * @return a {@link Supplier} that executes the command and returns the result
   */
  <REQ,RES> Supplier<RES> send(final Command<REQ> command);
}
