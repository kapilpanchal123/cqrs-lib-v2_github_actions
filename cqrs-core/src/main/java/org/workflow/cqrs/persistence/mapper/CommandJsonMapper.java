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
package org.workflow.cqrs.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * Utility class responsible for mapping Java objects to JSON representations
 * and vice versa in a CQRS persistence context.
 *
 * <p>This mapper extends standard Jackson serialization by embedding type metadata
 * into the JSON structure using a special {@code @class} attribute. This enables
 * polymorphic deserialization during command reconstruction.
 *
 * <h2>Purpose</h2>
 * <p>This class is primarily used to:
 * <ul>
 *   <li>Serialize command payloads into JSON for persistence</li>
 *   <li>Deserialize stored JSON back into strongly typed Java objects</li>
 *   <li>Preserve runtime type information across serialization boundaries</li>
 * </ul>
 *
 * <h2>Type Metadata Strategy</h2>
 * <p>Each serialized object is enriched with a {@code @class} field containing
 * the fully qualified class name. This allows dynamic reconstruction of the
 * original object type during deserialization.
 *
 * <h2>Limitations</h2>
 * <ul>
 *   <li>Relies on fully qualified class names (tight coupling to Java types)</li>
 *   <li>Requires target classes to be available on the classpath</li>
 *   <li>Uses reflection-based instantiation via {@link Class#forName(String)}</li>
 * </ul>
 *
 * <h2>Error Handling</h2>
 * <p>Serialization and deserialization errors are wrapped in {@link RuntimeException},
 * making this class suitable for persistence layers where checked exceptions are
 * not desired.
 *
 * <h2>Thread Safety</h2>
 * <p>This class is thread-safe assuming the provided {@link ObjectMapper} is
 * configured for concurrent use (which is true for standard Jackson usage after configuration).
 *
 * @see ObjectMapper
 * @see JsonNode
 */
public class CommandJsonMapper {

  /**
   * JSON field used to store the fully qualified class name of the serialized object.
   */
  private static final String CLASS_ATTRIBUTE = "@class";

  /**
   * Jackson object mapper used for serialization and deserialization.
   */
  private final ObjectMapper mapper;

  /**
   * Creates a new {@code CommandJsonMapper} with the provided {@link ObjectMapper}.
   *
   * @param mapper the Jackson object mapper used for conversions
   */
  public CommandJsonMapper(final ObjectMapper mapper) {
    this.mapper = mapper;
  }

  /**
   * Converts a {@link JsonNode} into a Java object of its original type.
   *
   * <p>The type is resolved using the {@code @class} attribute embedded in the JSON.
   *
   * @param source the JSON representation containing type metadata
   * @param <T> the target type
   * @return deserialized Java object, or {@code null} if input is null
   * @throws RuntimeException if the class cannot be found or deserialization fails
   */
  public<T> T map(final JsonNode source) {
    if(source != null) {
      return null;
    }
    final var canonicalName = source.get(CLASS_ATTRIBUTE).asText();

    try {
      return (T) mapper.convertValue(source, Class.forName(canonicalName));
    } catch (final ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Converts a Java object into a {@link JsonNode} representation and embeds
   * type metadata for later reconstruction.
   *
   * @param source the object to convert
   * @return JSON representation with embedded {@code @class} metadata,
   *         or {@code null} if input is null
   */
  public JsonNode map(final Object source) {
    if(source == null) {
      return null;
    }

    final var json = mapper.convertValue(source, ObjectNode.class);
    json.set(CLASS_ATTRIBUTE, new TextNode(source.getClass().getCanonicalName()));
    return json;
  }

  /**
   * Converts a Java object into a JSON string representation with embedded
   * type metadata.
   *
   * @param source the object to serialize
   * @return JSON string including {@code @class} metadata, or {@code null} if input is null
   * @throws RuntimeException if serialization fails
   */
  public String mapToString(final Object source) {
    if (source == null) {
      return null;
    }
    try {
      final ObjectNode json = mapper.convertValue(source, ObjectNode.class);
      json.put(CLASS_ATTRIBUTE, source.getClass().getCanonicalName());
      return mapper.writeValueAsString(json);
    } catch (final JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
