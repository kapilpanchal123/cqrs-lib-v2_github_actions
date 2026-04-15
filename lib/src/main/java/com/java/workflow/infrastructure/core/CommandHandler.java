package com.java.workflow.infrastructure.core;

import com.google.common.reflect.TypeToken;

public interface CommandHandler<REQ,RES> {

  TypeToken<REQ> requestTypeToken();

  RES handle(Command<REQ> command);

  default boolean matches(Command<?> command) {
    return requestTypeToken().getRawType().isAssignableFrom(command.getPayload().getClass());
  }
}
