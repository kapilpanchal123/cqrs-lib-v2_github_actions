package com.java.workflow.infrastructure.persistence.middlewares;

import com.java.workflow.infrastructure.core.Command;
import com.java.workflow.infrastructure.core.CommandMiddleware;
import com.java.workflow.infrastructure.persistence.domain.CommandRepository;
import org.springframework.transaction.annotation.Transactional;

public class DefaultCommandPersistenceMiddleware implements CommandMiddleware {

  private final CommandRepository commandRepository;

  public DefaultCommandPersistenceMiddleware(CommandRepository commandRepository) {
    this.commandRepository = commandRepository;
  }

  @Transactional
  @Override
  public void invoke(Command<?> command) {
    if(command == null) {
      throw new RuntimeException("Error: Command Cannot be null!");
    }
    try {
      final Long val = commandRepository.insertCommand(command);
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }
}
