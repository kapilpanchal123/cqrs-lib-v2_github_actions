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
package org.workflow.cqrs.defaults;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.workflow.cqrs.core.CommandExecutor;
import org.workflow.cqrs.core.CommandPostProcessor;
import org.workflow.cqrs.core.CommandStatus;
import org.workflow.cqrs.defaults.command.UserCommand;
import org.workflow.cqrs.defaults.data.TestPayloadRequest;
import java.time.OffsetDateTime;
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
class DefaultCommandPipelineTest {

  @Mock
  CommandExecutor commandExecutor;

  @Mock
  CommandPostProcessor<UserCommand> postProcess;

  DefaultCommandPipeline pipeline;
  UserCommand userCommand;

  @BeforeEach
  void setUp() {
//    pipeline = new DefaultCommandPipeline(commandExecutor, List.of(postProcess));
//
//    final TestPayloadRequest payload = new TestPayloadRequest("test-username1",
//        "email1@email.com",
//        "firstname1",
//        "lastname1",
//        10);
//
//    userCommand = new UserCommand();
//    userCommand.setId(UUID.randomUUID());
//    userCommand.setStatus(CommandStatus.INIT);
//    userCommand.setCreatedAt(OffsetDateTime.now());
//    userCommand.setIdempotencyKey(UUID.randomUUID().toString());
//    userCommand.setUpdatedAt(OffsetDateTime.now());
//    userCommand.setTenantId("1");
//    userCommand.setUsername("username1");
//    userCommand.setPayload(payload);
  }

  @Disabled
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

  @Disabled
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

  @Disabled
  @Test
  void send_null_command_throws_NPE() {
    Assertions.assertThrows(NullPointerException.class, () -> pipeline.send(null));
  }
}