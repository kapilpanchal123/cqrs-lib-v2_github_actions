package com.java.workflow.infrastructure.postprocess;

import com.java.workflow.infrastructure.core.Command;
import com.java.workflow.infrastructure.implementation.data.TestPayloadRequest;
import com.java.workflow.infrastructure.persistence.domain.CommandRepository;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultResiliencePostProcessorTest {

  @Mock
  CommandRepository commandRepository;

  @Mock
  Command<TestPayloadRequest> command;

  DefaultResiliencePostProcessor<TestPayloadRequest> postProcessor;

  @BeforeEach
  void setUp() {
    postProcessor = new DefaultResiliencePostProcessor<>(commandRepository);
  }

  @Test
  void run_should_update_command_status_successfully() {
    // given
    Mockito.when(command.getId()).thenReturn(UUID.fromString("7e9fd6b4-a6a7-45bb-94e2-57b0de203608"));
    Mockito.when(commandRepository.updateCommandStatus(
            Mockito.eq("7e9fd6b4-a6a7-45bb-94e2-57b0de203608"),
            Mockito.eq("SUCCESSFUL")))
        .thenReturn(Boolean.TRUE);

    // when
    postProcessor.run(command);

    // then
    Mockito.verify(commandRepository)
        .updateCommandStatus("7e9fd6b4-a6a7-45bb-94e2-57b0de203608", "SUCCESSFUL");
  }

  @Test
  void updateCommandStatusFallback_should_throw_runtime_exception() {
    // given
    UUID commandId = UUID.fromString("7e9fd6b4-a6a7-45bb-94e2-57b0de203608");
    Throwable cause = new RuntimeException("Database Error");

    @SuppressWarnings("unchecked")
    Command<TestPayloadRequest> command = Mockito.mock(Command.class);
    Mockito.when(command.getId()).thenReturn(commandId);

    // when
    RuntimeException ex = Assertions.assertThrows(
        RuntimeException.class,
        () -> postProcessor.updateCommandStatusFallback(command, cause)
    );

    // then
    Assertions.assertTrue(ex.getMessage().contains("Critical failure"));
    Assertions.assertEquals(cause, ex.getCause());
  }
}