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
package com.java.workflow.cqrs.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class CommandJsonMapper {

  private static final String CLASS_ATTRIBUTE = "@class";
  private final ObjectMapper mapper;

  public CommandJsonMapper(final ObjectMapper mapper) {
    this.mapper = mapper;
  }

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

  public JsonNode map(final Object source) {
    if(source == null) {
      return null;
    }

    final var json = mapper.convertValue(source, ObjectNode.class);
    json.set(CLASS_ATTRIBUTE, new TextNode(source.getClass().getCanonicalName()));
    return json;
  }

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
