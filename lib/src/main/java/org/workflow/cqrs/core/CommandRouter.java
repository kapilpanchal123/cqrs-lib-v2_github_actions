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
 * Responsible for resolving the appropriate {@link CommandHandler} for a given {@link Command}
 * in a CQRS (Command Query Responsibility Segregation) system.
 *
 * <p>The {@code CommandRouter} acts as a lookup and dispatch strategy component that determines
 * which handler should process an incoming command based on its payload type or other routing
 * metadata.
 *
 * <h2>Purpose</h2>
 * <p>This interface enables decoupling between command execution and handler selection by:
 * <ul>
 *   <li>Mapping commands to their corresponding handlers</li>
 *   <li>Supporting dynamic or runtime-based handler resolution</li>
 *   <li>Enabling extensible routing strategies (e.g., type-based, annotation-based, or custom rules)</li>
 * </ul>
 *
 * <h2>Routing Strategy</h2>
 * <p>Typical implementations use one or more of the following mechanisms:
 * <ul>
 *   <li>Request payload type matching via {@link com.google.common.reflect.TypeToken}</li>
 *   <li>Explicit handler registry mapping</li>
 *   <li>Custom routing rules based on command metadata</li>
 * </ul>
 *
 * <h2>Role in CQRS Pipeline</h2>
 * <p>The router is typically used internally by a {@code CommandPipeline} or {@code CommandExecutor}
 * to locate the correct handler before invoking command processing.
 *
 * <h2>Thread Safety</h2>
 * <p>Implementations should be thread-safe as routing is typically performed concurrently
 * for multiple incoming commands.
 *
 * @see Command
 * @see CommandHandler
 */
public interface CommandRouter {

  /**
   * Resolves the appropriate {@link CommandHandler} capable of processing the given {@link Command}.
   *
   * <p>This method selects a handler based on the command's payload type or other routing criteria
   * defined by the implementation.
   *
   * @param command the command for which a handler must be resolved
   * @param <REQ> the type of the command payload
   * @param <RES> the type of the result produced by the handler
   * @return a matching {@link CommandHandler} capable of processing the command
   * @throws IllegalStateException if no suitable handler can be found
   */
  <REQ,RES> CommandHandler<REQ,RES> route(final Command<REQ> command);
}
