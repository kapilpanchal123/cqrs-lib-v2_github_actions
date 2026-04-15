package com.java.workflow.infrastructure.core;

@FunctionalInterface
public interface CommandPostProcessor<T> {
  void run(Command<T> command);
}
