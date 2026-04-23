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
 * Defines standard header names used in a CQRS (Command Query Responsibility Segregation) system.
 *
 * <p>This class centralizes constant values for HTTP or messaging headers that carry
 * cross-cutting metadata such as idempotency and correlation identifiers.
 * These headers are typically set at the system boundary (e.g., API gateway,
 * filters, or interceptors) and propagated across services.
 *
 * <h2>Purpose</h2>
 * <ul>
 *   <li>Ensure consistent naming of headers across commands and queries</li>
 *   <li>Support distributed tracing and observability</li>
 *   <li>Enable idempotent command processing</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <p>These constants are commonly used in:
 * <ul>
 *   <li>HTTP filters or interceptors to read/write headers</li>
 *   <li>Command and query mappers to populate metadata fields</li>
 *   <li>Messaging systems (e.g., Kafka headers) for propagation</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>This class is immutable and thread-safe.
 *
 * <h2>Design Notes</h2>
 * <ul>
 *   <li>Header names follow a consistent {@code x-cqrs-*} naming convention</li>
 *   <li>Correlation identifiers are used for end-to-end tracing across services</li>
 *   <li>Idempotency identifiers ensure safe retries of command execution</li>
 * </ul>
 */
public final class CommandConstants {

  /**
   * Header name used to carry the idempotency key for commands.
   *
   * <p>This value is used to ensure that repeated command requests
   * (e.g., due to retries or network failures) are processed only once.
   */
  public static final String COMMAND_IDEMPOTENCY_HEADER = "x-cqrs-idempotency-id";

  /**
   * Header name used to carry the correlation identifier for commands.
   *
   * <p>This identifier is used to trace a command across multiple services,
   * logs, and processing stages within the system.
   */
  public static final String COMMAND_CORRELATION_ID_HEADER = "x-cqrs-correlation-id";

  /**
   * Header name used to carry the correlation identifier for queries.
   *
   * <p>This allows read operations to be traced independently or in conjunction
   * with commands as part of the same request flow.
   */
  public static final String QUERY_CORRELATION_ID_HEADER = "x-cqrs-query-correlation-id";

  /**
   * Private constructor to prevent instantiation.
   *
   * <p>This class is intended to be used as a static utility holder.
   */
  private CommandConstants() {
    throw new UnsupportedOperationException("Utility class should not be instantiated");
  }
}
