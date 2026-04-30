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
package org.workflow.cqrs.failure;

import org.workflow.cqrs.core.Command;

/**
 * Strategy interface for handling failures occurring during command processing
 * in a CQRS pipeline.
 *
 * <p>{@code CommandFailureStrategy} enables pluggable, stage-aware handling of
 * failures by allowing implementations to react differently depending on where
 * in the execution lifecycle the failure occurred.
 *
 * <h2>Purpose</h2>
 * <p>This abstraction allows:
 * <ul>
 *   <li>Decoupling failure handling from core execution logic</li>
 *   <li>Custom recovery, compensation, or logging strategies</li>
 *   <li>Selective handling based on failure stage</li>
 * </ul>
 *
 * <h2>Execution Model</h2>
 * <p>When a failure occurs, the pipeline:
 * <ol>
 *   <li>Determines the {@link CommandFailureStage}</li>
 *   <li>Invokes all strategies where {@link #supports(CommandFailureStage)} returns {@code true}</li>
 *   <li>Executes {@link #onFailure(Command, Throwable)} for each matching strategy</li>
 * </ol>
 *
 * <p>Multiple strategies may be invoked for a single failure.
 *
 * <h2>Typical Responsibilities</h2>
 * <ul>
 *   <li>Logging and monitoring</li>
 *   <li>Updating command state (e.g., marking as FAILED)</li>
 *   <li>Triggering retries or fallback mechanisms</li>
 *   <li>Publishing failure events</li>
 * </ul>
 *
 * <h2>Design Considerations</h2>
 * <ul>
 *   <li>Implementations should be idempotent, as failures may be retried</li>
 *   <li>Strategies should avoid throwing exceptions unless escalation is intended</li>
 *   <li>Execution order may not be guaranteed unless explicitly controlled by the pipeline</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>Implementations should be thread-safe as they may be invoked concurrently.
 *
 * @see Command
 * @see CommandFailureStage
 */
public interface CommandFailureStrategy {

  /**
   * Determines whether this strategy supports handling failures
   * occurring at the given {@link CommandFailureStage}.
   *
   * <p>This method is used by the pipeline to filter applicable strategies
   * before invoking {@link #onFailure(Command, Throwable)}.
   *
   * @param stage the stage at which the failure occurred
   * @return {@code true} if this strategy should handle the failure,
   *         {@code false} otherwise
   */
  boolean supports(final CommandFailureStage stage);

  /**
   * Handles a failure for the given {@link Command}.
   *
   * <p>This method is invoked when a failure occurs and this strategy
   * has indicated support for the corresponding stage.
   *
   * <p>Implementations may perform actions such as:
   * <ul>
   *   <li>Logging the failure</li>
   *   <li>Updating command state</li>
   *   <li>Triggering retries or compensating actions</li>
   * </ul>
   *
   * <p>The provided {@link Throwable} represents the cause of the failure
   * and should be used for diagnostics or recovery logic.
   *
   * @param command the command being processed when the failure occurred
   * @param t the exception or error that caused the failure
   */
  void onFailure(final Command<?> command, final Throwable t);
}
