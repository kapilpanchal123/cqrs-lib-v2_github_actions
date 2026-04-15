package com.java.workflow.infrastructure.core;

import com.google.common.reflect.TypeToken;

public abstract class AbstractCommandHandler<REQ,RES> implements CommandHandler<REQ,RES> {

  private final TypeToken<REQ> requestTypeToken;

  protected AbstractCommandHandler() {
    this.requestTypeToken = new TypeToken<>(getClass()) {};
  }

  @Override
  public TypeToken<REQ> requestTypeToken() {
    return requestTypeToken;
  }
}
