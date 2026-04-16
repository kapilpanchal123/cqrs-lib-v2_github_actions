package com.java.workflow.cqrs.core;

import java.util.function.Supplier;

public interface CommandPipeline {
  <REQ,RES> Supplier<RES> send(final Command<REQ> command);
}
