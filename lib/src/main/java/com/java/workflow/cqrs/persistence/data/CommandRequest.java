package com.java.workflow.cqrs.persistence.data;

import com.java.workflow.cqrs.core.Command;
import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

public class CommandRequest implements Serializable {

  @Serial
  private final long serialVersionUID = 1L;

  private final UUID commandId;
  private final String idempotencyKey;
  private final String status;
  private final OffsetDateTime updatedAt;
  private final String tenantId;
  private final String username;
  private final String payload;

  public CommandRequest(
      final UUID commandId,
      final String idempotencyKey,
      final String status,
      final OffsetDateTime updatedAt,
      final String tenantId,
      final String username,
      final String payload) {
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

  public static CommandRequest from(final Command<?> command, final String payload) {
    return new CommandRequest(
        command.getId(),
        command.getIdempotencyKey(),
        command.getStatus(),
        command.getUpdatedAt(),
        command.getTenantId(),
        command.getUsername(),
        payload);
  }

  public static Command<?> to(final CommandRequest commandDao) {
    return new Command<Object>(
        commandDao.getCommandId(),
        commandDao.getIdempotencyKey(),
        commandDao.getStatus(),
        null,
        commandDao.getUpdatedAt(),
        commandDao.getTenantId(),
        commandDao.getUsername(),
        commandDao.getPayload());
  }
}
