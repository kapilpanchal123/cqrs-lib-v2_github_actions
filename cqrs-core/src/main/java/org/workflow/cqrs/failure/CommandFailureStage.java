package org.workflow.cqrs.failure;

public enum CommandFailureStage {
  INITIALIZATION,
  EXECUTION,
  POST_PROCESSING,
  RETRY,
  FALLBACK,
  TIMEOUT
}
