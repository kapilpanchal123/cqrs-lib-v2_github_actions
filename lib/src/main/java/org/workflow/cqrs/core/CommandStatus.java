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

/**
 * Represents the lifecycle state of a {@link Command} in a CQRS (Command Query Responsibility Segregation) system.
 *
 * <p>This enum is used to track the processing progress of a command as it moves through
 * the command execution pipeline, from creation to final outcome.
 *
 * <h2>Lifecycle Overview</h2>
 * <p>A command typically transitions through the following states:
 * <ul>
 *   <li>{@link #INIT} – Command has been created but not yet submitted for processing</li>
 *   <li>{@link #PENDING} – Command is queued and awaiting execution</li>
 *   <li>{@link #PROCESSING} – Command is currently being executed by a handler</li>
 *   <li>{@link #COMPLETED} – Command has been successfully processed</li>
 *   <li>{@link #FAILED} – Command execution failed due to an error</li>
 * </ul>
 *
 * <h2>Usage in CQRS</h2>
 * <p>This status is typically used for:
 * <ul>
 *   <li>Auditing command execution state</li>
 *   <li>Tracking asynchronous processing pipelines</li>
 *   <li>Debugging and observability of command flows</li>
 *   <li>Implementing retry or failure handling mechanisms</li>
 * </ul>
 *
 * <h2>State Transitions</h2>
 * <p>State transitions are generally managed by the command execution pipeline or handler
 * implementations and may vary depending on system design. A typical flow is:
 *
 * <pre>
 * INIT → PENDING → PROCESSING → COMPLETED
 *                           ↘ FAILED
 * </pre>
 *
 * <h2>Thread Safety</h2>
 * <p>Enum values are immutable and inherently thread-safe.
 *
 * @see Command
 */
public enum CommandStatus {

  /**
   * Initial state indicating the command has been created but not yet submitted
   * for processing.
   */
  INIT,

  /**
   * Indicates the command is queued and awaiting execution by the pipeline.
   */
  PENDING,

  /**
   * Indicates the command is currently being processed by a handler.
   */
  PROCESSING,

  /**
   * Indicates the command has been successfully processed.
   */
  COMPLETED,

  /**
   * Indicates the command execution failed due to an error or exception.
   */
  FAILED
}
