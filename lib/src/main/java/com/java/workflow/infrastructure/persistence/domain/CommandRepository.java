package com.java.workflow.infrastructure.persistence.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.workflow.infrastructure.core.Command;
import com.java.workflow.infrastructure.persistence.data.CommandDao;
import com.java.workflow.infrastructure.persistence.mapper.CommandJsonMapper;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Objects;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class CommandRepository {

  private final JdbcTemplate jdbcTemplate;
  private final CommandJsonMapper commandJsonMapper;

  public CommandRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.commandJsonMapper = new CommandJsonMapper(new ObjectMapper());
  }

  public Long insertCommand(Command<?> command) {
    final String payloadJson = commandJsonMapper.mapToString(command.getPayload());
    final CommandDao commandDao = CommandDao.fromCommandDTO(command, payloadJson);

    final StringBuilder sqlString = new StringBuilder();
    sqlString.append("INSERT INTO cqrs.audit_command");
    sqlString.append("(command_id, payload, status, tenant_id, username)");
    sqlString.append("VALUES(?, ?, ?, ?, ?)");

    final KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      final PreparedStatement ps =
          connection.prepareStatement(sqlString.toString(), Statement.RETURN_GENERATED_KEYS);

      ps.setString(1, commandDao.getCommandId().toString());
      ps.setString(2, commandDao.getPayload());
      ps.setString(3, commandDao.getStatus());
      ps.setString(4, commandDao.getTenantId());
      ps.setString(5, commandDao.getUsername());

      return ps;
    }, keyHolder);

    return Objects.requireNonNull(keyHolder.getKey()).longValue();
  }

  public Boolean updateCommandStatus(String commandId, String status) {
    final StringBuilder sqlString = new StringBuilder();
    sqlString.append("UPDATE cqrs.audit_command SET status=? WHERE command_id=?;");

    final int result = jdbcTemplate.update(sqlString.toString(), status, commandId);

    return result == 0 ? Boolean.FALSE : Boolean.TRUE;
  }
}
