package org.workflow.cqrs.failure.defaults;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.workflow.cqrs.core.Command;
import org.workflow.cqrs.core.CommandStatus;
import org.workflow.cqrs.core.CommandStore;
import org.workflow.cqrs.failure.CommandFailureStage;
import org.workflow.cqrs.failure.CommandFailureStrategy;

public class DefaultCommandExecutionFailure implements CommandFailureStrategy {

  private static final Logger log = LoggerFactory.getLogger(DefaultCommandExecutionFailure.class);

  private final CommandStore store;

  public DefaultCommandExecutionFailure(final CommandStore store) {
    this.store = store;
  }

  @Override
  public Boolean supports(final CommandFailureStage stage) {
    return stage == CommandFailureStage.EXECUTION;
  }

  @Override
  public void onFailure(final Command<?> command, final Throwable t) {
    log.error("Execution failed for command: {}", command.getId(), t);
    store.save(command, CommandStatus.FAILED, t);
  }
}
