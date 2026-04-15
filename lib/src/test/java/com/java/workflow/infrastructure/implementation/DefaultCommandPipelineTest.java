package com.java.workflow.infrastructure.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.java.workflow.infrastructure.core.CommandExecutor;
import com.java.workflow.infrastructure.core.CommandPostProcessor;
import com.java.workflow.infrastructure.implementation.command.UserCommand;
import com.java.workflow.infrastructure.implementation.data.TestPayloadRequest;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultCommandPipelineTest {

  @Mock
  CommandExecutor commandExecutor;

  @Mock
  CommandPostProcessor<UserCommand> postProcess;

  DefaultCommandPipeline pipeline;
  UserCommand userCommand;

  @BeforeEach
  void setUp() {
    pipeline = new DefaultCommandPipeline(commandExecutor, List.of(postProcess));

    final TestPayloadRequest payload = new TestPayloadRequest("test-username1",
        "email1@email.com",
        "firstname1",
        "lastname1",
        10);

    userCommand = new UserCommand();
    userCommand.setId(UUID.randomUUID());
    userCommand.setStatus("INIT");
    userCommand.setCreatedAt(OffsetDateTime.now());
    userCommand.setIdempotencyKey(UUID.randomUUID().toString());
    userCommand.setUpdatedAt(OffsetDateTime.now());
    userCommand.setTenantId("1");
    userCommand.setUsername("username1");
    userCommand.setPayload(payload);
  }

  @Test
  void send_success_executes_postprocessor_and_returns_result() {

    // given
    final Supplier<String> supplier = () -> "SUCCESS";
    Mockito.when(commandExecutor.execute(userCommand))
        .thenAnswer(invocation -> supplier);
    // when
    final Supplier<String> resultsSupplier = pipeline.send(userCommand);
    final String result = resultsSupplier.get();

    // then
    assertEquals("SUCCESS", result);
    Mockito.verify(commandExecutor).execute(userCommand);
    Mockito.verify(postProcess).run(Mockito.any());
    Mockito.verifyNoMoreInteractions(commandExecutor, postProcess);
  }

  @Test
  void send_failure_executes_postprocessor_and_throws_exception() {

    // given
    final Supplier<String> failingSupplier = () -> {
      throw new IllegalStateException("Error");
    };

    // when
    Mockito.when(commandExecutor.execute(userCommand)).thenAnswer(invocation -> failingSupplier);

    final Supplier<String> resultSupplier = pipeline.send(userCommand);

    // then
    final RuntimeException e = Assertions.assertThrows(RuntimeException.class, resultSupplier::get);
    assertTrue(e.getMessage().contains("Handler Execution Failed"));

    Mockito.verify(commandExecutor).execute(userCommand);
    Mockito.verify(postProcess).run(Mockito.any());
  }

  @Test
  void send_null_command_throws_NPE() {
    Assertions.assertThrows(NullPointerException.class, () -> pipeline.send(null));
  }
}