package org.workflow.cqrs.failure.defaults;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.workflow.cqrs.core.Command;
import org.workflow.cqrs.core.CommandStatus;
import org.workflow.cqrs.core.CommandStore;
import org.workflow.cqrs.failure.CommandFailureStage;
import org.workflow.cqrs.failure.CommandFailureStrategy;

public class DefaultCommandPostProcessorFailure implements CommandFailureStrategy {

  private static final Logger log = LoggerFactory.getLogger(DefaultCommandPostProcessorFailure.class);

  private final CommandStore store;

  public DefaultCommandPostProcessorFailure(final CommandStore store) {
    this.store = store;
  }

  @Override
  public Boolean supports(final CommandFailureStage stage) {
    return stage == CommandFailureStage.POST_PROCESSING;
  }

  @Override
  public void onFailure(final Command<?> command, final Throwable t) {
    log.error("Post-processing failed for command: {}", command.getId(), t);
    store.update(command.getId(), CommandStatus.FAILED, t);
  }
}
