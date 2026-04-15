package com.java.workflow.cqrs.persistence.domain;

import com.java.workflow.cqrs.core.Command;
import java.util.UUID;

public interface CommandStore {

  void save(final Command<?> command);

  void updateStatus(final UUID commandId, final String status);

}
