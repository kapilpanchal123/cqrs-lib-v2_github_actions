package org.workflow.cqrs.failure;

import org.workflow.cqrs.core.Command;

public interface CommandFailureStrategy {

  Boolean supports(final CommandFailureStage stage);

  void onFailure(final Command<?> command, final Throwable t);

}
