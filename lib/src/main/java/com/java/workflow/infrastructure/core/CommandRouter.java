package com.java.workflow.infrastructure.core;

public interface CommandRouter {
  <REQ,RES> CommandHandler<REQ,RES> route(Command<REQ> command);
}
