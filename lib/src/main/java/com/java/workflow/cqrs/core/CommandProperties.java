package com.java.workflow.cqrs.core;

import java.io.Serial;
import java.io.Serializable;

public final class CommandProperties implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private Boolean enabled = Boolean.TRUE;

  private CommandExecutorType executor = CommandExecutorType.sync;

  private Integer ringBufferSize = 1024;

  public enum CommandExecutorType {
    sync,
    async,
    disruptor
  }

  public CommandProperties() {}

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(final Boolean enabled) {
    this.enabled = enabled;
  }

  public CommandExecutorType getExecutor() {
    return executor;
  }

  public void setExecutor(final CommandExecutorType executor) {
    this.executor = executor;
  }

  public Integer getRingBufferSize() {
    return ringBufferSize;
  }

  public void setRingBufferSize(final Integer ringBufferSize) {
    this.ringBufferSize = ringBufferSize;
  }
}
