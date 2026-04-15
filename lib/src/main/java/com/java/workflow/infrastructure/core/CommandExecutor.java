package com.java.workflow.infrastructure.core;

import java.util.function.Supplier;

public interface CommandExecutor {
  <REQ,RES> Supplier<RES> execute(Command<REQ> command);
}
