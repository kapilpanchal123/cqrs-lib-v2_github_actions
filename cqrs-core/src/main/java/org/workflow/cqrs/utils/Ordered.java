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
package org.workflow.cqrs.utils;

/**
 * Contract for defining execution order of components within the CQRS pipeline.
 *
 * <p>This interface is used to control the ordering of various extensible
 * components such as:
 * <ul>
 *   <li>Command handlers</li>
 *   <li>Middleware</li>
 *   <li>Post-processors</li>
 *   <li>Failure strategies</li>
 * </ul>
 *
 * <h2>Purpose</h2>
 * <p>In a CQRS pipeline, multiple components may participate in processing
 * a command. The {@code Ordered} interface allows deterministic sequencing
 * of these components to ensure predictable behavior.
 *
 * <h2>Ordering Rules</h2>
 * <ul>
 *   <li>Lower values indicate higher priority (executed earlier)</li>
 *   <li>Higher values indicate lower priority (executed later)</li>
 *   <li>Components that do not implement this interface are treated as having
 *       lowest priority</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <p>Components can implement this interface to influence their position
 * in the execution chain:
 *
 * <pre>{@code
 * public class LoggingMiddleware implements CommandMiddleware, Ordered {
 *
 *   @Override
 *   public int getOrder() {
 *     return 10; // Executes early in the pipeline
 *   }
 *
 *   @Override
 *   public <T> T invoke(Command<T> command, Supplier<T> next) {
 *     // logging logic
 *     return next.get();
 *   }
 * }
 * }</pre>
 *
 * <h2>Design Considerations</h2>
 * <ul>
 *   <li>Use small integer values for high-priority components</li>
 *   <li>Avoid relying on implicit ordering when execution sequence matters</li>
 *   <li>Prefer explicit ordering for critical components such as persistence
 *       and transaction management</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>This interface does not impose any thread-safety requirements.
 * Implementations should ensure thread safety if used in concurrent contexts.
 *
 * @since 1.0
 */
public interface Ordered {

  /**
   * Returns the order value of this component.
   *
   * <p>Lower values have higher priority and are executed earlier.
   *
   * @return the order value
   */
  int getOrder();
}
