package com.java.workflow.cqrs.implementation.data;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestPayloadResponse implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private Map<String, Object> response = new LinkedHashMap<>();

  public TestPayloadResponse() {
  }

  public TestPayloadResponse(Map<String, Object> response) {
    this.response = response;
  }

  public Map<String, Object> getResponse() {
    return response;
  }

  public void setResponse(Map<String, Object> response) {
    this.response = response;
  }
}
