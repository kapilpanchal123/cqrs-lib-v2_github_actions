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
 * Abstract base implementation of the {@link CommandHandler} interface.
 *
 * <p>This class provides common functionality for command handlers by resolving
 * and exposing both the request and response generic types at runtime using
 * {@link TypeToken}.
 *
 * <h2>Purpose</h2>
 * <p>In a CQRS architecture, command handlers are responsible for processing
 * specific command types. Due to Java's type erasure, generic type information
 * is not directly available at runtime. This class captures that type
 * information to enable:
 *
 * <ul>
 *   <li>Dynamic handler registration</li>
 *   <li>Type-safe command dispatching</li>
 *   <li>Runtime validation of handler inputs and outputs</li>
 *   <li>Reflection-based routing mechanisms</li>
 * </ul>
 *
 * <h2>Type Resolution</h2>
 * <p>The {@link TypeToken} mechanism captures the actual generic type
 * parameters {@code REQ} and {@code RES} at instantiation time.
 *
 * <ul>
 *   <li>{@code REQ} is used for command-handler matching and dispatching.</li>
 *   <li>{@code RES} is primarily used for response validation and type safety.</li>
 * </ul>
 *
 * <p>This allows frameworks or dispatchers to determine which handler
 * should process a given command and optionally validate the result.
 *
 * <h2>Usage</h2>
 * <p>Concrete command handlers should extend this class and specify
 * the request and response types:
 *
 * <pre>{@code
 * public class CreateOrderHandler
 *     extends AbstractCommandHandler<CreateOrderRequest, OrderResponse> {
 *
 *   @Override
 *   public OrderResponse handle(Command<CreateOrderRequest> command) {
 *     // business logic
 *     return new OrderResponse();
 *   }
 * }
 * }</pre>
 *
 * <h2>Limitations</h2>
 * <p>This implementation relies on runtime type resolution via reflection.
 * It assumes that concrete handlers directly extend this class. Inheritance
 * chains with intermediate generic classes may prevent correct type resolution.
 *
 * <h2>Thread Safety</h2>
 * <p>This class is thread-safe provided that concrete implementations
 * are stateless or manage their state appropriately.
 *
 * @param <REQ> the type of command payload handled by this handler
 * @param <RES> the type of result returned after command processing
 *
 * @see CommandHandler
 * @see TypeToken
 */
public abstract class AbstractCommandHandler<REQ,RES> implements CommandHandler<REQ,RES> {

  /**
   * Captured {@link TypeToken} representing the generic request type {@code REQ}.
   *
   * <p>This is resolved at construction time and used for runtime type
   * inspection and command-handler matching.
   */
  private final TypeToken<REQ> requestTypeToken;

  /**
   * Captured {@link TypeToken} representing the generic response type {@code RES}.
   *
   * <p>This is primarily used for runtime validation, serialization,
   * and ensuring type consistency in command processing pipelines.
   */
  private final TypeToken<RES> responseTypeToken;

  /**
   * Constructs an {@code AbstractCommandHandler} and captures the generic
   * request and response types using {@link TypeToken}.
   *
   * <p>This enables runtime access to the actual types handled by the
   * concrete implementation, overcoming Java's type erasure limitations.
   *
   * <p><b>Note:</b> This mechanism relies on direct inheritance. If additional
   * layers of abstraction are introduced, type resolution may not behave
   * as expected.
   */
  protected AbstractCommandHandler() {
    final TypeToken<?> thisType = TypeToken.of(getClass());
    this.requestTypeToken = (TypeToken<REQ>) thisType
        .resolveType(AbstractCommandHandler.class.getTypeParameters()[0]);

    this.responseTypeToken = (TypeToken<RES>) thisType
        .resolveType(AbstractCommandHandler.class.getTypeParameters()[1]);
  }

  /**
   * Returns the {@link TypeToken} representing the request type handled
   * by this command handler.
   *
   * <p>This is typically used by command dispatchers or registries to route
   * incoming commands to the appropriate handler.
   *
   * @return the {@code TypeToken} of the request type {@code REQ}
   */
  @Override
  public TypeToken<REQ> requestTypeToken() {
    return requestTypeToken;
  }

  /**
   * Returns the {@link TypeToken} representing the response type produced
   * by this command handler.
   *
   * <p>This can be used for runtime validation, serialization,
   * or enforcing type safety in processing pipelines.
   *
   * @return the {@code TypeToken} of the response type {@code RES}
   */
  public TypeToken<RES> responseTypeToken() {
    return responseTypeToken;
  }
}
