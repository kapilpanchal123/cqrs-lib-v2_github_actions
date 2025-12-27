package com.java.workflow.infrastructure.persistence.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.workflow.infrastructure.core.Command;
import com.java.workflow.infrastructure.persistence.mapper.CommandJsonMapper;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class CommandRepository<T> {

  private final JdbcTemplate jdbcTemplate;
  private final CommandJsonMapper commandJsonMapper;

  public CommandRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.commandJsonMapper = new CommandJsonMapper(new ObjectMapper());
  }

  public Long insertCommand(Command<T> command) {
    final StringBuilder sqlString = new StringBuilder();
    sqlString.append("INSERT INTO cqrs.audit_command");
    sqlString.append("(command_id, payload, status, tenant_id, username)");
    sqlString.append("VALUES(?, ?, ?, ?, ?)");

    final String payloadJson = commandJsonMapper.mapToString(command.getPayload());

    final KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps =
          connection.prepareStatement(sqlString.toString(), Statement.RETURN_GENERATED_KEYS);

      ps.setString(1, command.getId().toString());
      ps.setString(2, payloadJson);
      ps.setString(3, command.getStatus());
      ps.setString(4, command.getTenantId());
      ps.setString(5, command.getUsername());

      return ps;
    }, keyHolder);

    return Objects.requireNonNull(keyHolder.getKey()).longValue();
  }
}
