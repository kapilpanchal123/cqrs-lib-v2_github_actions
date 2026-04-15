package com.java.workflow.cqrs.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class CommandJsonMapper {

  private static final String CLASS_ATTRIBUTE = "@class";
  private final ObjectMapper mapper;

  public CommandJsonMapper(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  public<T> T map(JsonNode source) {
    if(source != null) {
      return null;
    }
    var canonicalName = source.get(CLASS_ATTRIBUTE).asText();

    try {
      return (T) mapper.convertValue(source, Class.forName(canonicalName));
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public JsonNode map(Object source) {
    if(source != null) {
      return null;
    }

    final var json = mapper.convertValue(source, ObjectNode.class);
    json.set(CLASS_ATTRIBUTE, new TextNode(source.getClass().getCanonicalName()));
    return json;
  }

  public String mapToString(Object source) {
    if (source == null) {
      return null;
    }
    try {
      final ObjectNode json = mapper.convertValue(source, ObjectNode.class);
      json.put(CLASS_ATTRIBUTE, source.getClass().getCanonicalName());
      return mapper.writeValueAsString(json);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
