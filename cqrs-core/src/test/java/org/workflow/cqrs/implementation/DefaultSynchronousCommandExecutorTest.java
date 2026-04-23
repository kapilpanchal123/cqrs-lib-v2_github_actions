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
import org.workflow.cqrs.core.CommandMiddleware;
import org.workflow.cqrs.core.CommandRouter;
import org.workflow.cqrs.implementation.command.UserCommand;
import org.workflow.cqrs.implementation.data.TestPayloadRequest;
import org.workflow.cqrs.implementation.data.TestPayloadResponse;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

  @Disabled
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

  @Disabled
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