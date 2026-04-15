package com.java.workflow.infrastructure.core;

public interface CommandMiddleware {
  void invoke(Command<?> command);
}
