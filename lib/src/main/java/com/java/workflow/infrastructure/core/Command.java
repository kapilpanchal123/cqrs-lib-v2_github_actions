package com.java.workflow.infrastructure.core;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

public abstract class Command<T> implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private UUID id;
  private String idempotencyKey;
  private String status;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
  private String tenantId;
  private String username;
  private T payload;

  public Command() {}

  public Command(UUID id, String idempotencyKey, String status, OffsetDateTime createdAt, OffsetDateTime updatedAt, String tenantId, String username, T payload) {
    this.id = id;
    this.idempotencyKey = idempotencyKey;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.tenantId = tenantId;
    this.username = username;
    this.payload = payload;
  }

  public Command(Command<T> command) {
    this.idempotencyKey = command.getIdempotencyKey();
    this.status = command.getStatus();
    this.createdAt = command.getCreatedAt();
    this.updatedAt = command.getUpdatedAt();
    this.tenantId = command.getTenantId();
    this.username = command.getUsername();
    this.payload = command.getPayload();
    this.id = command.getId();
  }

  public String getIdempotencyKey() {
    return idempotencyKey;
  }

  public String getStatus() {
    return status;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public String getTenantId() {
    return tenantId;
  }

  public String getUsername() {
    return username;
  }

  public T getPayload() {
    return payload;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public void setIdempotencyKey(String idempotencyKey) {
    this.idempotencyKey = idempotencyKey;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPayload(T payload) {
    this.payload = payload;
  }
}
