/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.workflow.cqrs.implementation;

import org.workflow.cqrs.core.CommandHandler;
import org.workflow.cqrs.core.CommandStatus;
import org.workflow.cqrs.implementation.command.UserCommand;
import org.workflow.cqrs.implementation.data.TestPayloadRequest;
import org.workflow.cqrs.implementation.data.TestPayloadResponse;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
    userCommand.setStatus(CommandStatus.INIT);
    userCommand.setCreatedAt(OffsetDateTime.now());
    userCommand.setIdempotencyKey(UUID.randomUUID().toString());
    userCommand.setUpdatedAt(OffsetDateTime.now());
    userCommand.setTenantId("1");
    userCommand.setUsername("username1");
    userCommand.setPayload(payload);
  }

  @Disabled
  @Test
  void test_whether_the_command_is_able_to_find_matching_command_handler() {
    Mockito.when(userCommandHandler.matches(userCommand)).thenReturn(true);

    CommandHandler<TestPayloadRequest, TestPayloadResponse> handler =
        defaultCommandRouter.route(userCommand);

    Assertions.assertEquals(userCommandHandler, handler);
  }

  @Disabled
  @Test
  void test_null_command_throws_NPE() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> defaultCommandRouter.route(null));
  }

  @Disabled
  @Test
  void test_no_matching_command_handler_throws_exception() {
    // given
    Mockito.when(userCommandHandler.matches(userCommand)).thenReturn(false);

    // when + then
    IllegalArgumentException ex = Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> defaultCommandRouter.route(userCommand)
    );

    Assertions.assertTrue(
        ex.getMessage().contains("Command Handler Not Found for CommandId")
    );
  }
}