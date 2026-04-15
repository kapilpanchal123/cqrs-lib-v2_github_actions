package com.java.workflow.cqrs.implementation;

import com.java.workflow.cqrs.core.Command;
import com.java.workflow.cqrs.core.CommandHandler;
import com.java.workflow.cqrs.core.CommandRouter;
import java.util.List;

public class DefaultCommandRouter implements CommandRouter {
  private final List<CommandHandler<?,?>> commandHandlers;

  public DefaultCommandRouter(final List<CommandHandler<?,?>> commandHandlers) {
    this.commandHandlers = commandHandlers;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <REQ,RES> CommandHandler<REQ,RES> route(final Command<REQ> command) {
    if(command == null) {
      throw new IllegalArgumentException("Command must not be null");
    }

    return (CommandHandler<REQ,RES>) commandHandlers
        .stream()
        .filter(handler -> handler.matches(command))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Command Handler Not Found for CommandId = " + command.getId()));
  }
}
