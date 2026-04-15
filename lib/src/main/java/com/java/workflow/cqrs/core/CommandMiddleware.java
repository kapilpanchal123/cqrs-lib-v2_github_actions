package com.java.workflow.cqrs.core;

public interface CommandMiddleware {
  void invoke(final Command<?> command);
}
