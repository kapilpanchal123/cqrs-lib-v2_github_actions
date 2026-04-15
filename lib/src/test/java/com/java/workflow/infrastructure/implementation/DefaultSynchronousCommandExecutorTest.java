package com.java.workflow.infrastructure.implementation;

import com.java.workflow.infrastructure.core.CommandHandler;
import com.java.workflow.infrastructure.core.CommandMiddleware;
import com.java.workflow.infrastructure.core.CommandRouter;
import com.java.workflow.infrastructure.implementation.command.UserCommand;
import com.java.workflow.infrastructure.implementation.data.TestPayloadRequest;
import com.java.workflow.infrastructure.implementation.data.TestPayloadResponse;
import java.util.Collections;
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
class DefaultSynchronousCommandExecutorTest {

  @Mock
  CommandMiddleware commandMiddleware;

  @Mock
  CommandRouter commandRouter;

  @Mock
  CommandHandler<TestPayloadRequest, TestPayloadResponse> commandHandler;

  DefaultSynchronousCommandExecutor executor;
  UserCommand userCommand;

  TestPayloadResponse testPayloadResponse;

  @BeforeEach
  void setUp() {
    executor = new DefaultSynchronousCommandExecutor(
        List.of(commandMiddleware),
        commandRouter
    );

    TestPayloadRequest payload = new TestPayloadRequest(
        "test-username1",
        "email1@email.com",
        "firstname1",
        "lastname1",
        10
    );

    userCommand = new UserCommand();
    userCommand.setId(UUID.randomUUID());
    userCommand.setPayload(payload);

    testPayloadResponse = new TestPayloadResponse();
    testPayloadResponse.setResponse(Collections.singletonMap("response", "responseVal"));
  }

  @Test
  void test_middleware_execution() {
    // given
    Mockito.when(commandRouter.route(userCommand))
        .thenAnswer(invocation -> commandHandler);

    // when
    executor.execute(userCommand);

    // then
    Mockito.verify(commandMiddleware).invoke(userCommand);
  }

  @Test
  void execute_returns_supplier_that_invokes_handler() {
    Mockito.when(commandRouter.route(userCommand))
        .thenAnswer(invocation -> commandHandler);

    Mockito.when(commandHandler.handle(userCommand))
        .thenReturn(testPayloadResponse);

    // when
    Supplier<TestPayloadResponse> supplier = executor.execute(userCommand);
    TestPayloadResponse actualResponse = supplier.get();

    // then
    Mockito.verify(commandRouter).route(userCommand);
    Mockito.verify(commandHandler).handle(userCommand);
    Assertions.assertEquals(testPayloadResponse, actualResponse);
  }
}