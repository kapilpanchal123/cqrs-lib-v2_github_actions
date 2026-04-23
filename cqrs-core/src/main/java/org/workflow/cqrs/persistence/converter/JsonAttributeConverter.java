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
package org.workflow.cqrs.persistence.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA attribute converter that serializes and deserializes {@link com.fasterxml.jackson.databind.JsonNode}
 * objects to and from their JSON string representation.
 *
 * <p>This converter enables seamless persistence of JSON structures in relational databases by
 * automatically converting {@code JsonNode} instances into a {@link String} column and vice versa.
 *
 * <h2>Purpose</h2>
 * <p>This class is used in JPA entities to:
 * <ul>
 *   <li>Persist flexible JSON structures in a single database column</li>
 *   <li>Avoid the need for custom schema changes for dynamic JSON payloads</li>
 *   <li>Ensure consistent serialization/deserialization using Jackson</li>
 * </ul>
 *
 * <h2>Serialization Behavior</h2>
 * <p>The underlying {@link com.fasterxml.jackson.databind.ObjectMapper} is configured to:
 * <ul>
 *   <li>Automatically register available Jackson modules</li>
 *   <li>Write {@code BigDecimal} values in plain format</li>
 *   <li>Use {@code BigDecimal} for floating-point deserialization to preserve precision</li>
 * </ul>
 *
 * <h2>Null Handling</h2>
 * <p>If the source value is {@code null}, both conversion methods return {@code null},
 * allowing optional JSON columns in the database.
 *
 * <h2>Error Handling</h2>
 * <p>Serialization and deserialization errors are wrapped in a {@link RuntimeException}.
 * This ensures JPA transaction rollback behavior while avoiding checked exceptions
 * in persistence layers.
 *
 * <h2>Thread Safety</h2>
 * <p>This class is thread-safe. The underlying {@link ObjectMapper} is immutable after
 * configuration and safe for concurrent use.
 *
 * @see jakarta.persistence.AttributeConverter
 * @see com.fasterxml.jackson.databind.JsonNode
 */
@Converter
public class JsonAttributeConverter implements AttributeConverter<JsonNode, String> {

  /**
   * Shared Jackson {@link com.fasterxml.jackson.databind.ObjectMapper} used for JSON
   * serialization and deserialization.
   *
   * <p>Configured to handle numeric precision and automatically register modules.
   */
  private static final ObjectMapper MAPPER = new ObjectMapper()
      .findAndRegisterModules()
      .configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)
      .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);

  /**
   * Converts a {@link JsonNode} into its JSON string representation for database storage.
   *
   * @param source the JSON node to convert
   * @return JSON string representation, or {@code null} if input is {@code null}
   * @throws RuntimeException if serialization fails
   */
  @Override
  public String convertToDatabaseColumn(final JsonNode source) {
    try {
      return source != null ? MAPPER.writeValueAsString(source) : null;
    } catch (final JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Converts a JSON string from the database into a {@link JsonNode}.
   *
   * @param source the JSON string stored in the database
   * @return parsed {@link JsonNode}, or {@code null} if input is {@code null}
   * @throws RuntimeException if deserialization fails
   */
  @Override
  public JsonNode convertToEntityAttribute(final String source) {
    try {
      return source != null ? MAPPER.readTree(source) : null;
    } catch (final JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
