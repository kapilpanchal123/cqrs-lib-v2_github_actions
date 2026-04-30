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
 * Represents the result of a pipeline execution in a CQRS system.
 *
 * <p>{@code PipelineResult} models the outcome of processing within a command
 * pipeline, encapsulating either a successful result or a failure without
 * immediately throwing exceptions.
 *
 * <h2>Purpose</h2>
 * <p>This abstraction enables:
 * <ul>
 *   <li>Controlled propagation of execution results across pipeline stages</li>
 *   <li>Separation of business logic from error-handling flow</li>
 *   <li>Deferred exception handling</li>
 *   <li>Improved composability of middleware and execution steps</li>
 * </ul>
 *
 * <h2>Design</h2>
 * <p>This is a sealed interface with two possible outcomes:
 * <ul>
 *   <li>{@link Success} — represents a successful execution containing a value</li>
 *   <li>{@link Failure} — represents a failed execution containing a {@link RuntimeException}</li>
 * </ul>
 *
 * <p>This design is conceptually similar to functional constructs such as
 * {@code Either} or {@code Result}, but tailored for command pipeline execution.
 *
 * <h2>Usage in Pipeline</h2>
 * <p>Pipeline components (e.g., middleware, handlers) may return a
 * {@code PipelineResult} instead of throwing exceptions directly. This allows:
 * <ul>
 *   <li>Centralized failure handling</li>
 *   <li>Consistent error propagation</li>
 *   <li>Optional recovery or fallback strategies</li>
 * </ul>
 *
 * <h2>Exception Handling</h2>
 * <p>The {@link #getOrThrow()} method provides a convenient way to:
 * <ul>
 *   <li>Extract the successful value</li>
 *   <li>Re-throw the underlying exception if execution failed</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>This type is immutable and inherently thread-safe.
 *
 * @param <T> the type of value produced on successful execution
 */
public sealed interface PipelineResult<T> permits PipelineResult.Success, PipelineResult.Failure {

  /**
   * Represents a successful pipeline execution.
   *
   * @param <T> the type of the result value
   * @param value the result produced by the pipeline
   */
  record Success<T> (T value) implements PipelineResult<T> {}

  /**
   * Represents a failed pipeline execution.
   *
   * @param <T> the type parameter (unused but preserved for type consistency)
   * @param cause the exception that caused the failure
   */
  record Failure<T> (RuntimeException cause) implements PipelineResult<T> {}

  /**
   * Creates a successful {@link PipelineResult}.
   *
   * @param value the result value
   * @param <T> the type of the result
   * @return a {@link Success} instance containing the value
   */
  static <T> PipelineResult<T> success(final T value) {
    return new Success<>(value);
  }

  /**
   * Creates a failed {@link PipelineResult}.
   *
   * @param cause the exception representing the failure
   * @param <T> the type of the result
   * @return a {@link Failure} instance containing the exception
   */
  static <T> PipelineResult<T> failure(final RuntimeException cause) {
    return new Failure<>(cause);
  }

  /**
   * Returns the result value if execution was successful, otherwise throws
   * the underlying exception.
   *
   * <p>This method provides a bridge between functional-style result handling
   * and traditional exception-based control flow.
   *
   * @return the successful result value
   * @throws RuntimeException if this result represents a failure
   */
  default T getOrThrow() {
    return switch(this) {
      case Success<T> s -> s.value();
      case Failure<T> f -> throw f.cause();
    };
  }
}
