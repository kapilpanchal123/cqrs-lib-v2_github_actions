package com.java.workflow.cqrs.core;

import java.util.function.Supplier;

public interface CommandExecutor {
  <REQ,RES> Supplier<RES> execute(final Command<REQ> command);
}
