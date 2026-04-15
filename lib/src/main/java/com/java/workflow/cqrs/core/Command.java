package com.java.workflow.cqrs.core;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Command<T> implements Serializable {

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

  public Command(
      final UUID id,
      final String idempotencyKey,
      final String status,
      final OffsetDateTime createdAt,
      final OffsetDateTime updatedAt,
      final String tenantId,
      final String username,
      final T payload) {
    this.id = id;
    this.idempotencyKey = idempotencyKey;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.tenantId = tenantId;
    this.username = username;
    this.payload = payload;
  }

  public Command(final Command<T> command) {
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

  public void setId(final UUID id) {
    this.id = id;
  }

  public void setIdempotencyKey(final String idempotencyKey) {
    this.idempotencyKey = idempotencyKey;
  }

  public void setStatus(final String status) {
    this.status = status;
  }

  public void setCreatedAt(final OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(final OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public void setTenantId(final String tenantId) {
    this.tenantId = tenantId;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public void setPayload(final T payload) {
    this.payload = payload;
  }
}
