package com.java.workflow.infrastructure.persistence.data;

import com.java.workflow.infrastructure.core.Command;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

public class CommandDao implements Serializable {

  @Serial
  private final long serialVersionUID = 1L;

  private final UUID commandId;
  private final String idempotencyKey;
  private final String status;
  private final OffsetDateTime updatedAt;
  private final String tenantId;
  private final String username;
  private final String payload;

  public CommandDao(UUID commandId, String idempotencyKey, String status, OffsetDateTime updatedAt, String tenantId, String username, String payload) {
    this.commandId = commandId;
    this.idempotencyKey = idempotencyKey;
    this.status = status;
    this.updatedAt = updatedAt;
    this.tenantId = tenantId;
    this.username = username;
    this.payload = payload;
  }

  public long getSerialVersionUID() {
    return serialVersionUID;
  }

  public UUID getCommandId() {
    return commandId;
  }

  public String getIdempotencyKey() {
    return idempotencyKey;
  }

  public String getStatus() {
    return status;
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

  public String getPayload() {
    return payload;
  }

  public static CommandDao fromCommandDTO(Command<?> command, String payload) {
    return new CommandDao(command.getId(), command.getIdempotencyKey(), command.getStatus(),
        command.getUpdatedAt(), command.getTenantId(), command.getUsername(), payload);
  }
}
