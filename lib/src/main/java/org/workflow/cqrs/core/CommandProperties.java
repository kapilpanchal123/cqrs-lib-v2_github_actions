/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.workflow.cqrs.core;

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
