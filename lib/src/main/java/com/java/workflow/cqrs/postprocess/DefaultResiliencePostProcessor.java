package com.java.workflow.cqrs.postprocess;

import com.java.workflow.cqrs.core.Command;
import com.java.workflow.cqrs.core.CommandPostProcessor;
import com.java.workflow.cqrs.persistence.domain.CommandStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultResiliencePostProcessor<T> implements CommandPostProcessor<T> {

  private static final String STATUS = "SUCCESSFUL";
  private static final Logger log = LoggerFactory.getLogger(DefaultResiliencePostProcessor.class);

  private final CommandStore commandStore;

  public DefaultResiliencePostProcessor(final CommandStore commandStore) {
    this.commandStore = commandStore;
  }

  @Override
  public void run(final Command<T> command) {
    commandStore.updateStatus(command.getId(), STATUS);
  }

  public void updateCommandStatusFallback(final Command<T> command, final Throwable e) {
    log.error("Retries exhausted for command {}, marking as failed in memory only. Root cause: {}", command.getId(), e.getMessage());
    throw new RuntimeException("Critical failure: Unable to update command status; DB unreachable", e);
  }
}
