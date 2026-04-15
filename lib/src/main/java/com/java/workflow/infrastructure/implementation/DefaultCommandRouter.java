package com.java.workflow.infrastructure.implementation;

import com.java.workflow.infrastructure.core.Command;
import com.java.workflow.infrastructure.core.CommandHandler;
import com.java.workflow.infrastructure.core.CommandRouter;
import java.util.List;

public class DefaultCommandRouter implements CommandRouter {
  private final List<CommandHandler<?,?>> commandHandlers;

  public DefaultCommandRouter(List<CommandHandler<?,?>> commandHandlers) {
    this.commandHandlers = commandHandlers;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <REQ,RES> CommandHandler<REQ,RES> route(Command<REQ> command) {
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
