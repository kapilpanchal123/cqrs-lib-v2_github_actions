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
package org.workflow.cqrs.support;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import org.workflow.cqrs.core.Command;
import org.workflow.cqrs.core.CommandExecutor;
import org.workflow.cqrs.core.CommandPipeline;
import org.workflow.cqrs.core.CommandPostProcessor;
import org.workflow.cqrs.core.PipelineResult;
import org.workflow.cqrs.failure.CommandFailureStage;
import org.workflow.cqrs.failure.CommandFailureStrategy;
import org.workflow.cqrs.transactions.CommandTransactionManager;

/**
 * Default implementation of {@link CommandPipeline} that orchestrates
 * the complete lifecycle of command execution in a CQRS system.
 *
 * <h2>Overview</h2>
 * <p>{@code DefaultCommandPipeline} is the central coordination component
 * responsible for executing commands through a structured, transactional,
 * and failure-aware pipeline.
 *
 * <p>It integrates:
 * <ul>
 *   <li>{@link CommandExecutor} for middleware execution and handler resolution</li>
 *   <li>{@link CommandPostProcessor} for post-execution side effects</li>
 *   <li>{@link CommandFailureStrategy} for failure handling</li>
 *   <li>{@link CommandTransactionManager} for transaction management</li>
 * </ul>
 *
 * <h2>Execution Model</h2>
 * <p>The pipeline follows a multi-phase execution model:
 *
 * <pre>{@code
 * ┌────────────────────────────────────────────┐
 * │ Transaction Start                          │
 * ├────────────────────────────────────────────┤
 * │ Phase 1: Middleware + Handler Resolution   │
 * │   - Execute middleware chain               │
 * │   - Persist command (e.g., PROCESSING)     │
 * │   - Resolve handler                        │
 * ├────────────────────────────────────────────┤
 * │ Phase 2: Savepoint Creation                │
 * │   - Create savepoint before handler        │
 * ├────────────────────────────────────────────┤
 * │ Phase 3: Handler + Post-Processing         │
 * │   - Execute handler                        │
 * │   - Execute post-processors                │
 * ├────────────────────────────────────────────┤
 * │ Success Path                               │
 * │   - Release savepoint                      │
 * │   - Commit transaction                     │
 * ├────────────────────────────────────────────┤
 * │ Failure Path                               │
 * │   - Rollback to savepoint                  │
 * │   - Execute failure strategies             │
 * │   - Mark command as FAILED                 │
 * │   - Commit transaction                     │
 * └────────────────────────────────────────────┘
 * }</pre>
 *
 * <h2>Key Design Principles</h2>
 *
 * <h3>1. Durable State Transitions</h3>
 * <p>Command state transitions (e.g., {@code PROCESSING → FAILED}) are always
 * committed to the database, even in the presence of failures.
 *
 * <h3>2. Savepoint-Based Isolation</h3>
 * <p>A savepoint is created before handler execution to allow:
 * <ul>
 *   <li>Rolling back business logic changes</li>
 *   <li>Preserving earlier persistence (e.g., PROCESSING state)</li>
 * </ul>
 *
 * <h3>3. Failure Isolation</h3>
 * <ul>
 *   <li>Middleware failures are handled in independent transactions</li>
 *   <li>Handler/post-processing failures are handled within the same transaction</li>
 * </ul>
 *
 * <h3>4. Explicit Transaction Boundaries</h3>
 * <p>The pipeline ensures:
 * <ul>
 *   <li>Failures do not unintentionally rollback audit data</li>
 *   <li>Status updates remain durable</li>
 * </ul>
 *
 * <h2>Failure Handling Semantics</h2>
 *
 * <h3>Middleware / Execution Failures</h3>
 * <ul>
 *   <li>May occur before command persistence is guaranteed</li>
 *   <li>Handled using {@link CommandTransactionManager#executeIndependent}</li>
 *   <li>Ensures failure updates are committed regardless of outer transaction rollback</li>
 * </ul>
 *
 * <h3>Handler / Post-Processing Failures</h3>
 * <ul>
 *   <li>Rollback to savepoint (business changes undone)</li>
 *   <li>Command status updated to FAILED</li>
 *   <li>Transaction committed with failure state</li>
 * </ul>
 *
 * <h2>Supplier-Based Execution</h2>
 * <p>The {@link #send(Command)} method returns a {@link Supplier} instead of
 * executing immediately.
 *
 * <p>This design:
 * <ul>
 *   <li>Defers execution until explicitly invoked</li>
 *   <li>Allows integration with async or reactive frameworks</li>
 *   <li>Keeps pipeline construction separate from execution</li>
 * </ul>
 *
 * <h2>Usage</h2>
 *
 * <pre>{@code
 * Command<CreateOrderRequest> command = new Command<>();
 *
 * Supplier<OrderResponse> supplier = pipeline.send(command);
 *
 * OrderResponse response = supplier.get();
 * }</pre>
 *
 * <h2>Advanced Usage (Error Handling)</h2>
 *
 * <pre>{@code
 * try {
 *   pipeline.send(command).get();
 * } catch (Exception ex) {
 *   // Exception thrown AFTER transaction commit
 *   // Command status is already FAILED in DB
 * }
 * }</pre>
 *
 * <h2>Extensibility</h2>
 * <ul>
 *   <li>Add custom {@link CommandPostProcessor} for side effects</li>
 *   <li>Add custom {@link CommandFailureStrategy} for retry/circuit breaking</li>
 *   <li>Customize {@link CommandExecutor} for different execution models</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>This class is thread-safe assuming all injected dependencies are thread-safe.
 *
 * <h2>Important Guarantees</h2>
 * <ul>
 *   <li>Command status is always consistent with execution outcome</li>
 *   <li>Failures never silently rollback audit/persistence data</li>
 *   <li>Business logic and side effects are isolated via savepoints</li>
 * </ul>
 *
 * @see CommandPipeline
 * @see CommandExecutor
 * @see CommandTransactionManager
 * @see CommandFailureStrategy
 * @see CommandPostProcessor
 * @see PipelineResult
 */
public class DefaultCommandPipeline implements CommandPipeline {

  private static final String SAVEPOINT_NAME = "preHandlerSavepoint";

  private final CommandExecutor executor;
  private final List<CommandPostProcessor<?>> postProcessorList;
  private final List<CommandFailureStrategy> failureHandlerList;
  private final CommandTransactionManager commandTransactionManager;

  /**
   * Creates a new {@code DefaultCommandPipeline} with the required components.
   *
   * <p>All dependencies are mandatory and are used to orchestrate different
   * phases of command execution:
   * <ul>
   *   <li>{@link CommandExecutor} for middleware execution and handler resolution</li>
   *   <li>{@link CommandPostProcessor} for post-execution side effects</li>
   *   <li>{@link CommandFailureStrategy} for failure handling</li>
   *   <li>{@link CommandTransactionManager} for transaction control</li>
   * </ul>
   *
   * @param executor the command executor responsible for middleware and handler invocation
   * @param postProcessorList the list of post-processors executed after handler success
   * @param failureHandlerList the list of failure strategies applied on execution failure
   * @param commandTransactionManager the transaction manager used to control execution boundaries
   */
  public DefaultCommandPipeline(
      final CommandExecutor executor,
      final List<CommandPostProcessor<?>> postProcessorList,
      final List<CommandFailureStrategy> failureHandlerList,
      final CommandTransactionManager commandTransactionManager) {
    this.executor = executor;
    this.postProcessorList = postProcessorList;
    this.failureHandlerList = failureHandlerList;
    this.commandTransactionManager = commandTransactionManager;
  }

  /**
   * Dispatches the given {@link Command} through the command processing pipeline.
   *
   * <p>This method does not execute the command immediately. Instead, it returns
   * a {@link Supplier} that encapsulates the entire execution flow, including:
   * <ul>
   *   <li>Middleware execution</li>
   *   <li>Handler invocation</li>
   *   <li>Post-processing</li>
   *   <li>Failure handling</li>
   *   <li>Transaction management</li>
   * </ul>
   *
   * <h2>Execution Semantics</h2>
   * <p>When the returned {@link Supplier} is invoked:
   * <ol>
   *   <li>A transaction is started via {@link CommandTransactionManager}</li>
   *   <li>Middleware and handler resolution are executed</li>
   *   <li>A savepoint is created before handler execution</li>
   *   <li>The handler and post-processors are executed</li>
   *   <li>On success: transaction is committed</li>
   *   <li>On failure:
   *     <ul>
   *       <li>Rollback to savepoint (business changes undone)</li>
   *       <li>Failure strategies are executed</li>
   *       <li>Command status is updated to FAILED</li>
   *       <li>Transaction is committed with failure state</li>
   *     </ul>
   *   </li>
   * </ol>
   *
   * <h2>Failure Handling Behavior</h2>
   *
   * <h3>Execution Failures (Middleware / Routing)</h3>
   * <ul>
   *   <li>Handled using independent transactions</li>
   *   <li>Ensures failure state is persisted even if outer transaction rolls back</li>
   * </ul>
   *
   * <h3>Handler / Post-Processing Failures</h3>
   * <ul>
   *   <li>Rollback occurs only to the savepoint</li>
   *   <li>Failure strategies run within the same transaction</li>
   *   <li>Transaction commits with FAILED status</li>
   * </ul>
   *
   * <h2>Deferred Execution</h2>
   * <p>The use of {@link Supplier} allows:
   * <ul>
   *   <li>Lazy execution</li>
   *   <li>Integration with asynchronous frameworks</li>
   *   <li>Separation of pipeline construction from execution</li>
   * </ul>
   *
   * <h2>⚠ Critical Behavior</h2>
   * <p>Failures during handler or post-processing are <b>not thrown immediately</b>
   * within the transaction block. Instead, they are wrapped in a
   * {@link PipelineResult} and returned.
   *
   * <p>This ensures:
   * <ul>
   *   <li>The transaction commits with the FAILED status</li>
   *   <li>Audit and persistence data are not lost</li>
   * </ul>
   *
   * <p>The exception is rethrown <b>after</b> the transaction completes when
   * {@link PipelineResult#getOrThrow()} is invoked.
   *
   * @param command the command to be executed
   * @param <REQ> the type of the command payload
   * @param <RES> the type of the result produced by the command handler
   * @return a {@link Supplier} that executes the command when invoked
   * @throws RuntimeException if execution fails (thrown when supplier is invoked)
   */
  @Override
  public <REQ, RES> Supplier<RES> send(final Command<REQ> command) {
    return () -> {
      final PipelineResult<RES> result = commandTransactionManager.execute(savepointManager -> {

        // ── Phase 1: middleware + handler resolution ───────────────────────
        // executor.execute() sets status to PROCESSING, runs all CommandMiddleware
        // (including DefaultCommandPersistenceMiddleware which saves the command),
        // routes to the handler, and returns the handler as a Supplier.
        final Supplier<RES> handlerSupplier;
        try {
          handlerSupplier = executor.execute(command);
        } catch (final Throwable t) {
          // Middleware or routing failure: the PROCESSING save may or may not have
          // succeeded. We cannot update an uncertain record, so failure strategies
          // run in an independent transaction that commits even as the outer tx rolls back.
          for (final CommandFailureStrategy handler : failureHandlerList) {
            if (handler.supports(CommandFailureStage.EXECUTION)) {
              commandTransactionManager.executeIndependent(sp -> {
                handler.onFailure(command, t);
                return null;
              });
            }
          }
          throw (t instanceof RuntimeException re) ? re : new RuntimeException(t);
        }

        // ── Phase 2: savepoint before handler ─────────────────────────────
        // All changes after this point can be rolled back independently from
        // the PROCESSING save that already happened in middleware.
        savepointManager.savePoint(SAVEPOINT_NAME);

        try {
          // ── Phase 3: handler + post-processors ──────────────────────────
          final RES handlerResult = handlerSupplier.get();

          for (final CommandPostProcessor<?> postProcess : postProcessorList) {
            @SuppressWarnings("unchecked")
            final CommandPostProcessor<REQ> typed = (CommandPostProcessor<REQ>) postProcess;
            typed.run(command);
          }

          savepointManager.releaseSavepoint(SAVEPOINT_NAME);
          return PipelineResult.success(handlerResult);

        } catch (final Throwable t) {
          // Handler or post-processor failed.
          // Roll back to the savepoint: domain writes are undone, but the PROCESSING
          // save from middleware is preserved in the transaction.
          savepointManager.rollbackToSavepoint(SAVEPOINT_NAME);
          savepointManager.releaseSavepoint(SAVEPOINT_NAME);

          // Failure strategies now update the record to FAILED within the same
          // (still-open) transaction. Returning PipelineResult.failure() — rather
          // than re-throwing — ensures the outer transaction COMMITS with the
          // FAILED status, not rolling it back.
          for (final CommandFailureStrategy handler : failureHandlerList) {
            if (handler.supports(CommandFailureStage.POST_PROCESSING)) {
              handler.onFailure(command, t);
            }
          }

          final RuntimeException wrapped =
              (t instanceof RuntimeException re) ? re : new RuntimeException(t);
          return PipelineResult.<RES>failure(wrapped);
        }
      });

      // Transaction is now committed (or rolled back). Rethrow any failure AFTER
      // the transaction boundary so the FAILED status is durable.
      return result.getOrThrow();
    };
  }
}
