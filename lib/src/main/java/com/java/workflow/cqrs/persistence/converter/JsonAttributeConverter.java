package com.java.workflow.cqrs.persistence.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class JsonAttributeConverter implements AttributeConverter<JsonNode, String> {

  private final ObjectMapper mapper;

  public JsonAttributeConverter(final ObjectMapper mapper) {
    this.mapper = mapper;
    mapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
    mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
  }

  @Override
  public String convertToDatabaseColumn(final JsonNode source) {
    try {
      return source != null ? mapper.writeValueAsString(source) : null;
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public JsonNode convertToEntityAttribute(final String source) {
    try {
      return source != null ? mapper.readTree(source) : null;
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
