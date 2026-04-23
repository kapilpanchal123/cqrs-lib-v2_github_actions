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

import com.google.common.reflect.TypeToken;

/**
 * Handles execution of {@link Command} instances in a CQRS
 * (Command Query Responsibility Segregation) system.
 *
 * <p>A {@code CommandHandler} is responsible for processing a specific
 * type of command payload and producing a result. It encapsulates
 * the business logic required to perform a state-changing operation.
 *
 * <h2>Role in CQRS</h2>
 * <ul>
 *   <li>Receives a {@link Command} containing a payload and metadata</li>
 *   <li>Executes domain or application logic</li>
 *   <li>Returns a result of type {@code RES}</li>
 * </ul>
 *
 * <h2>Type Resolution</h2>
 * <p>The {@link TypeToken} returned by {@link #requestTypeToken()} represents
 * the payload type ({@code REQ}) that this handler can process. It is typically
 * used by a command dispatcher or executor to route commands to the correct handler.
 *
 * <h2>Matching Strategy</h2>
 * <p>The default {@link #matches(Command)} implementation determines whether
 * this handler can process a given command by checking if the command's payload
 * type is assignable to the handler's request type.
 *
 * <p>Implementations may override this method to provide more advanced
 * matching logic if required.
 *
 * <h2>Thread Safety</h2>
 * <p>Implementations should be thread-safe, as handlers are typically
 * shared across multiple concurrent requests.
 *
 * @param <REQ> the type of the command payload handled by this handler
 * @param <RES> the type of the result returned after command processing
 *
 * @see Command
 * @see TypeToken
 */
public interface CommandHandler<REQ,RES> {

  /**
   * Returns the {@link TypeToken} representing the request payload type
   * handled by this command handler.
   *
   * <p>This is typically used by command dispatchers or registries
   * to determine the appropriate handler for a given command.
   *
   * @return the {@code TypeToken} of the request type {@code REQ}
   */
  TypeToken<REQ> requestTypeToken();

  /**
   * Processes the given {@link Command} and returns a result.
   *
   * <p>The command contains both the business payload and associated metadata.
   * Implementations are expected to perform the necessary state changes
   * and return an appropriate response.
   *
   * @param command the command to process
   * @return the result of command execution
   */
  RES handle(final Command<REQ> command);

  /**
   * Determines whether this handler can process the given {@link Command}.
   *
   * <p>The default implementation checks if the command's payload type
   * is assignable to the handler's request type.
   *
   * <p><b>Note:</b> This method performs a raw type check and does not
   * consider generic type parameters.
   *
   * @param command the command to evaluate
   * @return {@code true} if this handler can process the command,
   *         {@code false} otherwise
   */
  default boolean matches(final Command<?> command) {
    return requestTypeToken().getRawType().isAssignableFrom(command.getPayload().getClass());
  }
}
