package com.java.workflow.cqrs.implementation;

import com.java.workflow.cqrs.core.*;
import java.util.List;
import java.util.function.Supplier;

public class DefaultSynchronousCommandExecutor implements CommandExecutor {
  private final List<CommandMiddleware> middlewares;
  private final CommandRouter router;

  public DefaultSynchronousCommandExecutor(
      final List<CommandMiddleware> middlewares,
      final CommandRouter router) {
    this.middlewares = middlewares;
    this.router = router;
  }

  @Override
  public <REQ, RES> Supplier<RES> execute(final Command<REQ> command) {
    for(CommandMiddleware middleware : middlewares) {
      middleware.invoke(command);
    }
    CommandHandler<REQ,RES> handler = router.route(command);
    return(() -> handler.handle(command));
  }
}
