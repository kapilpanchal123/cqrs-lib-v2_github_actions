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
 * Represents a middleware component in a CQRS (Command Query Responsibility Segregation) system.
 *
 * <p>A {@code CommandMiddleware} allows cross-cutting concerns to be applied during
 * command processing without modifying the core {@link CommandHandler} logic.
 * It acts as a processing hook in the command execution pipeline.
 *
 * <h2>Purpose</h2>
 * <p>Middleware is typically used to implement concerns such as:
 * <ul>
 *   <li>Logging and observability</li>
 *   <li>Correlation and tracing propagation</li>
 *   <li>Authentication and authorization checks</li>
 *   <li>Validation or enrichment of commands</li>
 *   <li>Metrics and performance monitoring</li>
 * </ul>
 *
 * <h2>Execution Model</h2>
 * <p>Middleware is invoked during command processing before or after
 * handler execution, depending on the pipeline design.
 *
 * <p>It operates on the raw {@link Command} and does not return a result,
 * making it suitable for side-effect operations.
 *
 * <h2>Pipeline Behavior</h2>
 * <p>Implementations are typically chained in a sequence where each middleware
 * component may:
 * <ul>
 *   <li>Modify the command</li>
 *   <li>Enrich metadata (e.g., correlationId, tenantId)</li>
 *   <li>Reject execution by throwing exceptions</li>
 *   <li>Pass control to the next stage in the pipeline</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>Implementations should be thread-safe as middleware is typically shared
 * across multiple concurrent command executions.
 *
 * @see Command
 * @see CommandHandler
 */
public interface CommandMiddleware {

  /**
   * Invokes this middleware with the given {@link Command}.
   *
   * <p>This method is called as part of the command execution pipeline.
   * Implementations may inspect, modify, validate, or enrich the command
   * before it reaches the handler or subsequent middleware components.
   *
   * @param command the command being processed
   */
  void invoke(final Command<?> command);
}
