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
package org.workflow.cqrs.defaults;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import org.workflow.cqrs.core.Command;
import org.workflow.cqrs.core.CommandExecutor;
import org.workflow.cqrs.core.CommandPipeline;
import org.workflow.cqrs.core.CommandPostProcessor;
import org.workflow.cqrs.failure.CommandFailureStage;
import org.workflow.cqrs.failure.CommandFailureStrategy;
import org.workflow.cqrs.transactions.CommandTransactionManager;

/**
 * Default implementation of the {@link CommandPipeline} responsible for orchestrating
 * command execution and post-processing in a CQRS (Command Query Responsibility Segregation) system.
 *
 * <p>This class delegates core command execution to a {@link CommandExecutor} and applies
 * a sequence of {@link CommandPostProcessor} instances after execution completes.
 *
 * <h2>Responsibilities</h2>
 * <p>The pipeline is responsible for:
 * <ul>
 *   <li>Validating incoming commands</li>
 *   <li>Delegating execution to the underlying {@link CommandExecutor}</li>
 *   <li>Wrapping execution in a {@link Supplier} for deferred execution</li>
 *   <li>Invoking post-processors after command execution</li>
 *   <li>Handling exceptions and ensuring post-processing is still executed</li>
 * </ul>
 *
 * <h2>Execution Flow</h2>
 * <ol>
 *   <li>Command is validated for null safety</li>
 *   <li>{@link CommandExecutor} produces a base execution {@link Supplier}</li>
 *   <li>The supplier is wrapped to intercept execution</li>
 *   <li>Command is executed when {@link Supplier#get()} is called</li>
 *   <li>Post-processors are invoked after execution (success or failure)</li>
 *   <li>Exceptions are propagated after post-processing</li>
 * </ol>
 *
 * <h2>Post-Processing</h2>
 * <p>All registered {@link CommandPostProcessor} instances are executed after command
 * execution. These are typically used for:
 * <ul>
 *   <li>Auditing</li>
 *   <li>Logging</li>
 *   <li>Metrics collection</li>
 *   <li>Event publishing</li>
 * </ul>
 *
 * <p><b>Note:</b> Post-processors are invoked regardless of whether execution succeeds or fails.
 *
 * <h2>Type Safety</h2>
 * <p>This implementation performs runtime casting of post-processors due to type erasure.
 * Care must be taken to ensure that post-processors are compatible with the command payload type.
 *
 * <h2>Thread Safety</h2>
 * <p>This class is thread-safe provided that:
 * <ul>
 *   <li>The injected {@link CommandExecutor} is thread-safe</li>
 *   <li>The provided list of {@link CommandPostProcessor} instances is immutable or externally synchronized</li>
 * </ul>
 *
 * <h2>Limitations</h2>
 * <p>This implementation assumes that post-processing does not alter execution results.
 * It also performs unchecked casts due to Java's type erasure limitations.
 *
 * @see CommandPipeline
 * @see CommandExecutor
 * @see CommandPostProcessor
 */
public class DefaultCommandPipeline implements CommandPipeline {

  private final CommandExecutor executor;
  private final List<CommandPostProcessor<?>> postProcessors;
  private final List<CommandFailureStrategy> failureHandlers;
  private final CommandTransactionManager transactionManager;

  /**
   * Creates a new {@code DefaultCommandPipeline}.
   *
   * @param executor the command executor responsible for executing commands
   * @param postProcessors list of post-processors executed after command execution
   * @param failureHandlers list of failureHandlers executed after command execution
   */
  public DefaultCommandPipeline(
      final CommandExecutor executor,
      final List<CommandPostProcessor<?>> postProcessors,
      final List<CommandFailureStrategy> failureHandlers,
      final CommandTransactionManager transactionManager) {
    this.executor = executor;
    this.postProcessors = postProcessors;
    this.failureHandlers = failureHandlers;
    this.transactionManager = transactionManager;
  }

  /**
   * Submits a {@link Command} for execution through the pipeline.
   *
   * <p>The returned {@link Supplier} encapsulates the full execution lifecycle,
   * including command handling and post-processing.
   *
   * <p>Execution is deferred until {@link Supplier#get()} is invoked.
   *
   * @param command the command to execute
   * @param <REQ> the type of the command payload
   * @param <RES> the type of the result returned by the handler
   * @return a {@link Supplier} that executes the command and returns the result
   * @throws NullPointerException if the command is {@code null}
   */
  @Override
  public <REQ,RES> Supplier<RES> send(final Command<REQ> command) {

    return transactionManager.execute(txManager -> {
      Objects.requireNonNull(command, "Command Must Not be Null.");
//      txManager.savepoint("payloadsavepoint");
      Supplier<RES> baseSupplier;
      try {
        baseSupplier = executor.execute(command);
      } catch (Throwable e) {
//        txManager.rollbackToSavepoint("payloadsavepoint");
        for (final CommandFailureStrategy handler : failureHandlers) {
          if (handler.supports(CommandFailureStage.EXECUTION)) {
            handler.onFailure(command, e);
          }
        }

        if (e instanceof RuntimeException) {
          throw (RuntimeException) e;
        }
        throw new RuntimeException(e);
      }

      // Return Supplier<RES> → this becomes T
      return () -> {
        try {
          RES result = baseSupplier.get();

          for (final CommandPostProcessor<?> postProcessor : postProcessors) {
            final CommandPostProcessor<REQ> typedProcessor =
                (CommandPostProcessor<REQ>) postProcessor;
            typedProcessor.run(command);
          }

          return result;
        } catch (Throwable e) {
          for (final CommandFailureStrategy handler : failureHandlers) {
            if (handler.supports(CommandFailureStage.POST_PROCESSING)) {
              handler.onFailure(command, e);
            }
          }

          if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
          }
          throw new RuntimeException(e);
        }
      };
    });
  }
}
