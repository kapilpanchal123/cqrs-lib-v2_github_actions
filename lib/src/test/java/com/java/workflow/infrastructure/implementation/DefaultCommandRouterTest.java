package com.java.workflow.infrastructure.implementation;

import com.java.workflow.infrastructure.core.CommandHandler;
import com.java.workflow.infrastructure.implementation.command.UserCommand;
import com.java.workflow.infrastructure.implementation.data.TestPayloadRequest;
import com.java.workflow.infrastructure.implementation.data.TestPayloadResponse;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultCommandRouterTest {

  @Mock
  CommandHandler<TestPayloadRequest, TestPayloadResponse> userCommandHandler;

  List<CommandHandler<?,?>> commandHandlers;
  DefaultCommandRouter defaultCommandRouter;
  UserCommand userCommand;

  @BeforeEach
  void setUp() {
    commandHandlers = List.of(userCommandHandler);
    defaultCommandRouter = new DefaultCommandRouter(commandHandlers);

    TestPayloadRequest payload = new TestPayloadRequest(
        "test-username1",
        "email1@email.com",
        "firstname1",
        "lastname1",
        10
    );

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
  void test_whether_the_command_is_able_to_find_matching_command_handler() {
    Mockito.when(userCommandHandler.matches(userCommand)).thenReturn(true);

    CommandHandler<TestPayloadRequest, TestPayloadResponse> handler =
        defaultCommandRouter.route(userCommand);

    Assertions.assertEquals(userCommandHandler, handler);
  }

  @Test
  void test_null_command_throws_NPE() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> defaultCommandRouter.route(null));
  }
}