package com.java.workflow.cqrs.implementation;

import com.java.workflow.cqrs.core.Command;
import com.java.workflow.cqrs.core.CommandExecutor;
import com.java.workflow.cqrs.core.CommandPipeline;
import com.java.workflow.cqrs.core.CommandPostProcessor;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class DefaultCommandPipeline implements CommandPipeline {

  private final CommandExecutor executor;
  private final List<CommandPostProcessor<?>> postProcessor;

  public DefaultCommandPipeline(final CommandExecutor executor,
                                final List<CommandPostProcessor<?>> postProcessor) {
    this.executor = executor;
    this.postProcessor = postProcessor;
  }

  @Override
  public <REQ,RES> Supplier<RES> send(final Command<REQ> command) {
    Objects.requireNonNull(command, "Command Must Not be Null.");
    Supplier<RES> baseSupplier = executor.execute(command);

    // Wrap base supplier with post-processing
    return () -> {
      RES result = null;
      Throwable exception = null;

      try {
        result = baseSupplier.get();
      } catch(Throwable e) {
        exception = e;
      }

      // Execute post-processing with both result and exception
      for(CommandPostProcessor<?> postProcess : postProcessor) {
        CommandPostProcessor<REQ> typedProcessor = (CommandPostProcessor<REQ>) postProcess;
        typedProcessor.run(command);
      }

      // If exception occurred, propagate it
      if(exception != null) {
        throw new RuntimeException("Handler Execution Failed: " + exception);
      }
      return result;
    };
  }
}
