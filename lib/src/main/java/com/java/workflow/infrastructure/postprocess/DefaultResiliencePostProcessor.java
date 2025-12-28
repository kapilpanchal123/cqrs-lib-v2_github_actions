package com.java.workflow.infrastructure.postprocess;

import com.java.workflow.infrastructure.core.Command;
import com.java.workflow.infrastructure.core.CommandPostProcessor;
import com.java.workflow.infrastructure.persistence.domain.CommandRepository;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class DefaultResiliencePostProcessor<T> implements CommandPostProcessor<T> {

  private static final String STATUS = "SUCCESSFUL";
  private static final Logger log = LoggerFactory.getLogger(DefaultResiliencePostProcessor.class);

  private final CommandRepository commandRepository;

  public DefaultResiliencePostProcessor(CommandRepository commandRepository) {
    this.commandRepository = commandRepository;
  }

  @Retry(name = "run", fallbackMethod = "updateCommandStatusFallback")
  @Transactional
  @Override
  public void run(Command<T> command) {
    Boolean results = commandRepository.updateCommandStatus(command.getId().toString(), STATUS);
  }

  public void updateCommandStatusFallback(UUID commandId, Throwable e) {
    log.error("Retries exhausted for command {}, marking as failed in memory only. Root cause: {}", commandId, e.getMessage());
    throw new RuntimeException("Critical failure: Unable to update command status; DB unreachable", e);
  }
}
