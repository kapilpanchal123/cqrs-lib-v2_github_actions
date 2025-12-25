package com.java.workflow.infrastructure.core;

import com.google.common.reflect.TypeToken;

public interface CommandHandler<REQ,RES> {
  RES handle(Command<REQ> command);

  default boolean matches(Command<?> command) {
    TypeToken<REQ> handlerType = new TypeToken<REQ>() {};
    return handlerType.getRawType().isAssignableFrom(command.getPayload().getClass());
  }
}
