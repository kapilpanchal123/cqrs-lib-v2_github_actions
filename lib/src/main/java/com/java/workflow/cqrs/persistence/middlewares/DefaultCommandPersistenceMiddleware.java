package com.java.workflow.cqrs.persistence.middlewares;

import com.java.workflow.cqrs.core.Command;
import com.java.workflow.cqrs.core.CommandMiddleware;
import com.java.workflow.cqrs.persistence.domain.CommandStore;

public class DefaultCommandPersistenceMiddleware implements CommandMiddleware {

  private final CommandStore commandStore;

  public DefaultCommandPersistenceMiddleware(final CommandStore commandStore) {
    this.commandStore = commandStore;
  }

  @Override
  public void invoke(final Command<?> command) {
    commandStore.save(command);
  }
}
