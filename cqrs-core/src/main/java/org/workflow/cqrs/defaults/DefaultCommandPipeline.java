package org.workflow.cqrs.defaults;

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

public class DefaultCommandPipeline implements CommandPipeline {

  private final CommandExecutor executor;
  private final List<CommandPostProcessor<?>> postProcessors;
  private final List<CommandFailureStrategy> failureHandlers;
  private final CommandTransactionManager transactionManager;

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

  @Override
  public <REQ,RES> Supplier<RES> send(final Command<REQ> command) {
    Objects.requireNonNull(command, "Command Must Not be Null.");

    return () -> {
      final PipelineResult<RES> pipelineResult = transactionManager.execute(sp -> {
        final Supplier<RES> baseSupplier;
        try {
          baseSupplier = executor.execute(command);
        } catch(final Throwable t) {
          for(final CommandFailureStrategy h :  failureHandlers) {
            if(h.supports(CommandFailureStage.EXECUTION)) {
              h.onFailure(command, t);
            }
          }
          throw (t instanceof RuntimeException re) ? re : new RuntimeException(t);
        }
        sp.savePoint("beforeHandlerSavepoint1");

        try {
          final RES result = baseSupplier.get();
          for(final CommandPostProcessor<?> postProcessor : postProcessors) {
            CommandPostProcessor<REQ> typed = (CommandPostProcessor<REQ>) postProcessor;
            typed.run(command);
          }
          sp.releaseSavepoint("beforeHandlerSavepoint1");
          return PipelineResult.success(result);
        } catch(final Throwable t) {
          sp.rollbackToSavepoint("beforeHandlerSavepoint1");
          sp.releaseSavepoint("beforeHandlerSavepoint1");

          for(final CommandFailureStrategy h : failureHandlers) {
            if(h.supports(CommandFailureStage.EXECUTION)) {
              h.onFailure(command, t);
            }
          }
          RuntimeException wrapped = (t instanceof RuntimeException re) ? re : new RuntimeException(t);
          return PipelineResult.<RES>failure(wrapped);
        }
      });
      return pipelineResult.getOrThrow();
    };
  }

//    @Override
//    public <REQ,RES> Supplier<RES> send(final Command<REQ> command) {
//
//      return transactionManager.execute(savepointManager -> {
//        Objects.requireNonNull(command, "Command Must Not be Null.");
////      savepointManager.savePoint("payloadSavepoint1");
//        Supplier<RES> baseSupplier;
//        try {
//          baseSupplier = executor.execute(command);
//        } catch (Throwable e) {
////        savepointManager.rollbackToSavepoint("payloadSavepoint1");
//          for (final CommandFailureStrategy handler : failureHandlers) {
//            if (handler.supports(CommandFailureStage.EXECUTION)) {
//              handler.onFailure(command, e);
//            }
//          }
//
//          if (e instanceof RuntimeException) {
//            throw (RuntimeException) e;
//          }
//          throw new RuntimeException(e);
//        }
//
//        // Return Supplier<RES> → this becomes T
//        return () -> {
//          try {
//            RES result = baseSupplier.get();
//
//            for (final CommandPostProcessor<?> postProcessor : postProcessors) {
//              final CommandPostProcessor<REQ> typedProcessor =
//                  (CommandPostProcessor<REQ>) postProcessor;
//              typedProcessor.run(command);
//            }
//
//            return result;
//          } catch (Throwable e) {
//            for (final CommandFailureStrategy handler : failureHandlers) {
//              if (handler.supports(CommandFailureStage.POST_PROCESSING)) {
//                handler.onFailure(command, e);
//              }
//            }
//
//            if (e instanceof RuntimeException) {
//              throw (RuntimeException) e;
//            }
//            throw new RuntimeException(e);
//          }
//        };
//      });
//  }
}
