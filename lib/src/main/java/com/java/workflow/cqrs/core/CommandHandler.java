package com.java.workflow.cqrs.core;

import com.google.common.reflect.TypeToken;

public interface CommandHandler<REQ,RES> {

  TypeToken<REQ> requestTypeToken();

  RES handle(final Command<REQ> command);

  default boolean matches(final Command<?> command) {
    return requestTypeToken().getRawType().isAssignableFrom(command.getPayload().getClass());
  }
}
