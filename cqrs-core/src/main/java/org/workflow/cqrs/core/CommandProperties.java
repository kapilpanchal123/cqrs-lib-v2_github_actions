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

/**
 * Configuration properties that control the behavior of the CQRS command execution pipeline.
 *
 * <p>This class defines runtime settings used by the {@code CommandPipeline} or
 * {@code CommandExecutor} implementations to determine whether command processing is enabled
 * and how commands should be executed.
 *
 * <h2>Purpose</h2>
 * <p>These properties are typically used by infrastructure layers (e.g., runtime engine,
 * Spring Boot configuration, or custom execution frameworks) to configure:
 * <ul>
 *   <li>Whether command processing is globally enabled or disabled</li>
 *   <li>The execution strategy used for processing commands</li>
 *   <li>Tuning parameters for high-performance execution modes</li>
 * </ul>
 *
 * <h2>Execution Modes</h2>
 * <p>The {@link CommandExecutorType} defines how commands are processed:
 * <ul>
 *   <li>{@code sync} – Commands are executed synchronously in the calling thread</li>
 *   <li>{@code async} – Commands are executed asynchronously using a thread-based executor</li>
 *   <li>{@code disruptor} – Commands are processed using a high-performance ring buffer model</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>This class is not inherently thread-safe. It is intended to be used as a configuration
 * object, typically initialized at startup and treated as immutable or externally synchronized.
 *
 * <h2>Serialization</h2>
 * <p>Implements {@link Serializable} to allow configuration to be transported or persisted
 * in distributed systems.
 */
public final class CommandProperties implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * Flag indicating whether command processing is enabled.
   *
   * <p>If set to {@code false}, the command pipeline should reject or bypass execution
   * of all incoming commands.
   */
  private Boolean enabled = Boolean.TRUE;

  /**
   * Defines the execution strategy used by the command pipeline.
   *
   * <p>This determines how commands are processed internally, such as synchronous execution,
   * asynchronous execution, or high-performance ring-buffer-based execution.
   */
  private CommandExecutorType executor = CommandExecutorType.SYNC;

  /**
   * Size of the ring buffer used when {@code disruptor} execution mode is enabled.
   *
   * <p>This value controls the capacity of the internal event queue used for high-throughput
   * command processing. Larger values increase throughput but consume more memory.
   */
  private Integer ringBufferSize = 1024;

  /**
   * Enumeration of supported command execution strategies.
   */
  public enum CommandExecutorType {

    /**
     * Commands are executed synchronously in the calling thread.
     */
    SYNC,

    /**
     * Commands are executed asynchronously using a thread pool or async executor.
     */
    ASYNC,

    /**
     * Commands are executed using a Disruptor-style ring buffer for high-throughput processing.
     */
    DISRUPTOR
  }

  /**
   * Creates a new instance of {@code CommandProperties} with default values.
   */
  public CommandProperties() {}

  /**
   * Returns whether command processing is enabled.
   *
   * @return {@code true} if command execution is enabled, otherwise {@code false}
   */
  public Boolean getEnabled() {
    return enabled;
  }

  /**
   * Sets whether command processing is enabled.
   *
   * @param enabled {@code true} to enable command execution, {@code false} to disable it
   */
  public void setEnabled(final Boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Returns the configured command execution strategy.
   *
   * @return the {@link CommandExecutorType} used for execution
   */
  public CommandExecutorType getExecutor() {
    return executor;
  }

  /**
   * Sets the command execution strategy.
   *
   * @param executor the execution type to use
   */
  public void setExecutor(final CommandExecutorType executor) {
    this.executor = executor;
  }

  /**
   * Returns the ring buffer size used in {@code disruptor} execution mode.
   *
   * @return the configured ring buffer size
   */
  public Integer getRingBufferSize() {
    return ringBufferSize;
  }

  /**
   * Sets the ring buffer size for {@code disruptor} execution mode.
   *
   * @param ringBufferSize the size of the internal event buffer
   */
  public void setRingBufferSize(final Integer ringBufferSize) {
    this.ringBufferSize = ringBufferSize;
  }
}
