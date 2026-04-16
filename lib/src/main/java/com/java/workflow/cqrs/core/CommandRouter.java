package com.java.workflow.cqrs.core;

public interface CommandRouter {
  <REQ,RES> CommandHandler<REQ,RES> route(final Command<REQ> command);
}
