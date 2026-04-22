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
package com.java.workflow.cqrs.postprocess;

import com.java.workflow.cqrs.core.Command;
import com.java.workflow.cqrs.implementation.data.TestPayloadRequest;
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
class DefaultResiliencePostProcessorTest {

//  @Mock
//  CommandRepository commandRepository;

//  @Mock
//  Command<TestPayloadRequest> command;

//  DefaultResiliencePostProcessor<TestPayloadRequest> postProcessor;

  @BeforeEach
  void setUp() {
//    postProcessor = new DefaultResiliencePostProcessor<>(commandRepository);
  }

  @Disabled
  @Test
  void run_should_update_command_status_successfully() {
//    // given
//    Mockito.when(command.getId()).thenReturn(UUID.fromString("7e9fd6b4-a6a7-45bb-94e2-57b0de203608"));
//    Mockito.when(commandRepository.updateCommandStatus(
//            Mockito.eq("7e9fd6b4-a6a7-45bb-94e2-57b0de203608"),
//            Mockito.eq("SUCCESSFUL")))
//        .thenReturn(Boolean.TRUE);
//
//    // when
//    postProcessor.run(command);
//
//    // then
//    Mockito.verify(commandRepository)
//        .updateCommandStatus("7e9fd6b4-a6a7-45bb-94e2-57b0de203608", "SUCCESSFUL");
  }

  @Disabled
  @Test
  void updateCommandStatusFallback_should_throw_runtime_exception() {
//    // given
//    UUID commandId = UUID.fromString("7e9fd6b4-a6a7-45bb-94e2-57b0de203608");
//    Throwable cause = new RuntimeException("Database Error");
//
//    @SuppressWarnings("unchecked")
//    Command<TestPayloadRequest> command = Mockito.mock(Command.class);
//    Mockito.when(command.getId()).thenReturn(commandId);
//
//    // when
//    RuntimeException ex = Assertions.assertThrows(
//        RuntimeException.class,
//        () -> postProcessor.updateCommandStatusFallback(command, cause)
//    );
//
//    // then
//    Assertions.assertTrue(ex.getMessage().contains("Critical failure"));
//    Assertions.assertEquals(cause, ex.getCause());
  }
}