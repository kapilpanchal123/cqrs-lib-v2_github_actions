package com.java.workflow.infrastructure.implementation;

import com.java.workflow.infrastructure.core.*;
import java.util.List;
import java.util.function.Supplier;

public class DefaultSynchronousCommandExecutor implements CommandExecutor {
  private final List<CommandMiddleware> middlewares;
  private final CommandRouter router;

  public DefaultSynchronousCommandExecutor(List<CommandMiddleware> middlewares, CommandRouter router) {
    this.middlewares = middlewares;
    this.router = router;
  }

  @Override
  public <REQ, RES> Supplier<RES> execute(Command<REQ> command) {
    for(CommandMiddleware middleware : middlewares) {
      middleware.invoke(command);
    }
    CommandHandler<REQ,RES> handler = router.route(command);
    return(() -> handler.handle(command));
  }
}
