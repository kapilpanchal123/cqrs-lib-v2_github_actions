package com.java.workflow.cqrs.core;

@FunctionalInterface
public interface CommandPostProcessor<T> {
  void run(final Command<T> command);
}
