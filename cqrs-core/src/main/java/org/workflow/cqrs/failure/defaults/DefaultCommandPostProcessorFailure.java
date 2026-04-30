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
package org.workflow.cqrs.failure.defaults;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.workflow.cqrs.core.Command;
import org.workflow.cqrs.core.CommandStatus;
import org.workflow.cqrs.core.CommandStore;
import org.workflow.cqrs.failure.CommandFailureStage;
import org.workflow.cqrs.failure.CommandFailureStrategy;

/**
 * Default {@link CommandFailureStrategy} for handling failures during
 * post-processing in a CQRS command pipeline.
 *
 * <p>This implementation is responsible for:
 * <ul>
 *   <li>Logging post-processing failures for observability</li>
 *   <li>Updating the command state to {@link CommandStatus#FAILED}</li>
 *   <li>Persisting failure details via {@link CommandStore}</li>
 * </ul>
 *
 * <h2>Scope</h2>
 * <p>This strategy applies only to failures occurring during the
 * {@link CommandFailureStage#POST_PROCESSING} phase.
 *
 * <h2>Behavior</h2>
 * <ul>
 *   <li>Logs the failure along with the command identifier</li>
 *   <li>Marks the command as {@code FAILED} in the underlying store</li>
 *   <li>Captures the associated {@link Throwable} for diagnostics</li>
 * </ul>
 *
 * <h2>Post-Processing Semantics</h2>
 * <p>Post-processing occurs after successful command execution and typically
 * involves side effects such as event publishing, auditing, or notifications.
 *
 * <p>A failure at this stage indicates that:
 * <ul>
 *   <li>The core business logic has already completed successfully</li>
 *   <li>One or more side-effect operations have failed</li>
 * </ul>
 *
 * <p>Implementations may choose to combine this strategy with retry or
 * compensation mechanisms, depending on the criticality of post-processing steps.
 *
 * <h2>Design Considerations</h2>
 * <ul>
 *   <li>This strategy performs terminal failure handling and does not attempt recovery</li>
 *   <li>It should typically be combined with retry or alerting strategies</li>
 *   <li>Marking the command as {@code FAILED} may be application-specific for this stage</li>
 * </ul>
 *
 *  <h2>Retry and Resilience</h2>
 *  <p>This default implementation does not perform retries and treats
 *  post-processing failures as terminal.
 *
 *  <p>Applications that require resilient behavior (e.g., retries,
 *  backoff, circuit breaking) are encouraged to provide custom
 *  {@link CommandFailureStrategy} implementations.
 *
 *  <p>Such implementations may integrate with resilience libraries
 *  (e.g., Resilience4j) to:
 *  <ul>
 *    <li>Retry failed post-processing operations</li>
 *    <li>Apply exponential backoff strategies</li>
 *    <li>Introduce circuit breakers for external dependencies</li>
 *  </ul>
 *
 *  <p>Care should be taken to ensure that retry logic is idempotent and
 *  does not introduce duplicate side effects.
 *
 * <h2>Thread Safety</h2>
 * <p>This class is thread-safe assuming the provided {@link CommandStore}
 * implementation is thread-safe.
 *
 * @see CommandFailureStrategy
 * @see CommandFailureStage
 * @see CommandStore
 */
public final class DefaultCommandPostProcessorFailure implements CommandFailureStrategy {

  private static final Logger log = LoggerFactory.getLogger(DefaultCommandPostProcessorFailure.class);

  private final CommandStore store;

  /**
   * Creates a new post-processing failure strategy backed by the given {@link CommandStore}.
   *
   * @param store the command store used to persist failure state and diagnostics
   */
  public DefaultCommandPostProcessorFailure(final CommandStore store) {
    this.store = store;
  }

  /**
   * Determines whether this strategy supports the given failure stage.
   *
   * <p>This implementation only supports {@link CommandFailureStage#POST_PROCESSING}.
   *
   * @param stage the failure stage
   * @return {@code true} if the stage is {@code POST_PROCESSING}, {@code false} otherwise
   */
  @Override
  public boolean supports(final CommandFailureStage stage) {
    return stage == CommandFailureStage.POST_PROCESSING;
  }

  /**
   * Handles a failure that occurred during post-processing.
   *
   * <p>This method logs the failure and updates the command status to
   * {@link CommandStatus#FAILED}, including the associated exception details.
   *
   * @param command the command being processed when the failure occurred
   * @param t the exception or error that caused the failure
   */
  @Override
  public void onFailure(final Command<?> command, final Throwable t) {
    log.error("Post-processing failed for command: {}", command.getId(), t);
    store.update(command.getId(), CommandStatus.FAILED, t);
  }
}
