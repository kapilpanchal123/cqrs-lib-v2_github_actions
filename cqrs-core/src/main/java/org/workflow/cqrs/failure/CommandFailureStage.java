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
package org.workflow.cqrs.failure;

/**
 * Represents the stage in the command processing lifecycle where a failure occurred.
 *
 * <p>This enum provides a simplified failure model for CQRS command pipelines,
 * focusing on the two most critical phases of execution:
 * handler execution and post-processing.
 *
 * <h2>Purpose</h2>
 * <p>This abstraction enables failure handling strategies to:
 * <ul>
 *   <li>Differentiate between core execution failures and side-effect failures</li>
 *   <li>Apply appropriate recovery or compensation logic</li>
 *   <li>Improve observability and diagnostics</li>
 * </ul>
 *
 * <h2>Execution Model</h2>
 * <p>A command typically flows through:
 * <ol>
 *   <li>Handler execution</li>
 *   <li>Post-processing (e.g., events, auditing)</li>
 * </ol>
 *
 * <p>Failures are categorized based on which phase they occur in.
 *
 * <h2>Design Considerations</h2>
 * <ul>
 *   <li>This model intentionally focuses on core failure points to keep handling simple</li>
 *   <li>Additional stages (e.g., retry, timeout) can be modeled separately if needed</li>
 * </ul>
 *
 * @see CommandFailureStrategy
 */
public enum CommandFailureStage {

  /**
   * Failure during command handler execution.
   *
   * <p>Represents errors occurring within the core business logic or
   * underlying infrastructure (e.g., database or external service failures).
   */
  EXECUTION,

  /**
   * Failure during post-processing after successful command execution.
   *
   * <p>Typically involves side-effect operations such as event publishing,
   * logging, or auditing.
   */
  POST_PROCESSING,

}
