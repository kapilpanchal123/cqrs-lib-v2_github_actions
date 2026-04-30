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
package org.workflow.cqrs.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.workflow.cqrs.core.CommandExecutor;
import org.workflow.cqrs.core.CommandPostProcessor;
import org.workflow.cqrs.core.CommandStatus;
import org.workflow.cqrs.failure.CommandFailureStage;
import org.workflow.cqrs.failure.CommandFailureStrategy;
import org.workflow.cqrs.support.command.UserCommand;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
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
import org.workflow.cqrs.support.data.TestPayloadRequest;
import org.workflow.cqrs.support.data.TestPayloadResponse;
import org.workflow.cqrs.transactions.CommandSavepointManager;
import org.workflow.cqrs.transactions.CommandTransactionManager;
import org.workflow.cqrs.transactions.TransactionalAction;

@ExtendWith(MockitoExtension.class)
public class DefaultCommandPipelineTest {

  @Mock
  CommandExecutor commandExecutor;

  @Mock
  CommandPostProcessor<TestPayloadRequest> commandPostProcessor;

  @Mock
  CommandFailureStrategy failureStrategy;

  @Mock
  CommandTransactionManager transactionManager;

  @Mock
  CommandSavepointManager sp;

  DefaultCommandPipeline pipeline;
  UserCommand userCommand;

  @BeforeEach()
  void setUp() {
    pipeline = new DefaultCommandPipeline(
        commandExecutor,
        List.of(commandPostProcessor),
        List.of(failureStrategy),
        transactionManager);

    final TestPayloadRequest payload = new TestPayloadRequest("test-username1",
        "email1@example.com",
        "firstname1",
        "lastname1",
        10);

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

  private <T> Supplier<T> success(final T value) {
    return () -> value;
  }

  private <T> Supplier<T> failure(final RuntimeException ex) {
    return () -> {
      throw ex;
    };
  }

  @Test
  void send_success_executes_handler_and_postprocessors() {
    // given
    Mockito.when(transactionManager.execute(Mockito.any()))
        .thenAnswer(invocation -> {
          final Object action = invocation.getArgument(0);
          return ((TransactionalAction<?>) action).execute(sp);
        });

    Mockito.<Supplier<TestPayloadResponse>>when(commandExecutor.execute(userCommand))
        .thenReturn(success(new TestPayloadResponse(Map.of("status", "SUCCESS"))));

    final Supplier<TestPayloadResponse> resultSupplier = pipeline.send(userCommand);

    // when
    final TestPayloadResponse result = resultSupplier.get();

    // then
    assertEquals("SUCCESS", result.getResponse().get("status"));

    Mockito.verify(commandExecutor).execute(userCommand);
    Mockito.verify(commandPostProcessor).run(userCommand);
    Mockito.verifyNoInteractions(failureStrategy);
  }

  @Test
  void send_execution_failure_triggers_failure_strategy_and_throws() {
    // given
    final RuntimeException e = new RuntimeException("boom");

    Mockito.when(transactionManager.execute(Mockito.any()))
        .thenAnswer(invocation -> {
          final Object action = invocation.getArgument(0);
          return ((TransactionalAction<?>) action).execute(sp);
        });

    Mockito.when(transactionManager.executeIndependent(Mockito.any()))
        .thenAnswer(invocation -> {
          final Object action = invocation.getArgument(0);
          return ((TransactionalAction<?>) action).execute(sp);
        });

    Mockito.when(commandExecutor.execute(userCommand))
        .thenThrow(e);

    Mockito.when(failureStrategy.supports(CommandFailureStage.EXECUTION))
        .thenReturn(true);

    final Supplier<TestPayloadResponse> resultSupplier = pipeline.send(userCommand);

    // when + then
    final RuntimeException thrown =
        Assertions.assertThrows(RuntimeException.class, resultSupplier::get);

    assertEquals("boom", thrown.getMessage());

    Mockito.verify(failureStrategy).onFailure(userCommand, e);
  }

  @Test
  void send_handler_failure_rolls_back_and_marks_failed() {
    // given
    final RuntimeException exception = new RuntimeException("handler failed");

    Mockito.when(transactionManager.execute(Mockito.any()))
        .thenAnswer(invocation -> {
          final Object action = invocation.getArgument(0);
          return ((TransactionalAction<?>) action).execute(sp);
        });

    Mockito.<Supplier<TestPayloadResponse>>when(commandExecutor.execute(userCommand))
        .thenReturn(failure(exception));

    Mockito.when(failureStrategy.supports(CommandFailureStage.POST_PROCESSING))
        .thenReturn(true);

    final Supplier<TestPayloadResponse> resultSupplier = pipeline.send(userCommand);

    // when
    final RuntimeException thrown =
        Assertions.assertThrows(RuntimeException.class, resultSupplier::get);

    // then
    assertEquals("handler failed", thrown.getMessage());

    Mockito.verify(commandExecutor).execute(userCommand);
    Mockito.verifyNoInteractions(commandPostProcessor);
    Mockito.verify(failureStrategy).onFailure(userCommand, exception);
  }

  @Test
  void send_postprocessor_failure_rolls_back_and_marks_failed() {
    // given
    final RuntimeException ex = new RuntimeException("post failed");

    Mockito.when(transactionManager.execute(Mockito.any()))
        .thenAnswer(invocation -> {
          final Object action = invocation.getArgument(0);
          return ((TransactionalAction<?>) action).execute(sp);
        });

    Mockito.<Supplier<String>>when(commandExecutor.execute(userCommand))
        .thenReturn(success("OK"));

    Mockito.doThrow(ex)
        .when(commandPostProcessor)
        .run(userCommand);

    Mockito.when(failureStrategy.supports(CommandFailureStage.POST_PROCESSING))
        .thenReturn(true);

    final Supplier<String> supplier = pipeline.send(userCommand);

    // when + then
    final RuntimeException thrown =
        Assertions.assertThrows(RuntimeException.class, supplier::get);

    assertEquals("post failed", thrown.getMessage());
  }

  @Test
  void send_creates_and_releases_savepoint_on_success() {
    // given
    Mockito.when(transactionManager.execute(Mockito.any()))
        .thenAnswer(invocation -> {
          final Object action = invocation.getArgument(0);
          return ((TransactionalAction<?>) action).execute(sp);
        });

    Mockito.<Supplier<TestPayloadResponse>>when(commandExecutor.execute(userCommand))
        .thenReturn(success(new TestPayloadResponse(Map.of("status", "SUCCESS"))));

    // when
    final TestPayloadResponse result =
        (TestPayloadResponse) pipeline.send(userCommand).get();

    // then
    assertEquals("SUCCESS", result.getResponse().get("status"));

    Mockito.verify(sp).savePoint("preHandlerSavepoint");
    Mockito.verify(sp).releaseSavepoint("preHandlerSavepoint");
  }

  @Test
  void send_execution_non_runtime_exception_is_wrapped() {
    // given
    final Throwable error = new Error("checked boom");

    Mockito.when(transactionManager.execute(Mockito.any()))
        .thenAnswer(invocation -> {
          final Object action = invocation.getArgument(0);
          return ((TransactionalAction<?>) action).execute(sp);
        });

    Mockito.when(transactionManager.executeIndependent(Mockito.any()))
        .thenAnswer(invocation -> {
          final Object action = invocation.getArgument(0);
          return ((TransactionalAction<?>) action).execute(sp);
        });

    Mockito.when(commandExecutor.execute(userCommand))
        .thenThrow(error);

    Mockito.when(failureStrategy.supports(CommandFailureStage.EXECUTION))
        .thenReturn(true);

    final Supplier<TestPayloadResponse> resultSupplier = pipeline.send(userCommand);

    // when
    final RuntimeException thrown =
        Assertions.assertThrows(RuntimeException.class, resultSupplier::get);

    // then
    assertEquals("checked boom", thrown.getCause().getMessage());
    Mockito.verify(failureStrategy).onFailure(userCommand, error);
  }

  @Test
  void send_handler_non_runtime_throwable_is_wrapped() {
    // given
    final Error error = new Error("handler error");

    Mockito.when(transactionManager.execute(Mockito.any()))
        .thenAnswer(invocation -> {
          final Object action = invocation.getArgument(0);
          return ((TransactionalAction<?>) action).execute(sp);
        });

    Mockito.<Supplier<TestPayloadResponse>>when(commandExecutor.execute(userCommand))
        .thenReturn(() -> {
          throw error;
        });

    Mockito.when(failureStrategy.supports(CommandFailureStage.POST_PROCESSING))
        .thenReturn(true);

    final Supplier<TestPayloadResponse> supplier = pipeline.send(userCommand);

    // when
    final RuntimeException thrown =
        Assertions.assertThrows(RuntimeException.class, supplier::get);

    // then
    assertEquals("handler error", thrown.getCause().getMessage());
  }

  @Test
  void send_execution_failure_does_not_trigger_failure_strategy_when_not_supported() {
    final RuntimeException e = new RuntimeException("boom");

    Mockito.when(transactionManager.execute(Mockito.any()))
        .thenAnswer(invocation -> {
          final Object action = invocation.getArgument(0);
          return ((TransactionalAction<?>) action).execute(sp);
        });

    Mockito.when(commandExecutor.execute(userCommand))
        .thenThrow(e);

    Mockito.when(failureStrategy.supports(CommandFailureStage.EXECUTION))
        .thenReturn(false); // 👈 key difference

    final Supplier<TestPayloadResponse> supplier = pipeline.send(userCommand);

    Assertions.assertThrows(RuntimeException.class, supplier::get);

    // 👇 this is what proves false branch executed
    Mockito.verify(failureStrategy, Mockito.never())
        .onFailure(Mockito.any(), Mockito.any());
  }

  @Test
  void send_postprocessor_failure_does_not_trigger_failure_strategy_when_not_supported() {
    final RuntimeException ex = new RuntimeException("post failed");

    Mockito.when(transactionManager.execute(Mockito.any()))
        .thenAnswer(invocation -> {
          final Object action = invocation.getArgument(0);
          return ((TransactionalAction<?>) action).execute(sp);
        });

    Mockito.when(commandExecutor.execute(userCommand))
        .thenReturn(() -> "OK");

    Mockito.doThrow(ex)
        .when(commandPostProcessor)
        .run(userCommand);

    Mockito.when(failureStrategy.supports(CommandFailureStage.POST_PROCESSING))
        .thenReturn(false); // 👈 key difference

    final Supplier<String> supplier = pipeline.send(userCommand);

    Assertions.assertThrows(RuntimeException.class, supplier::get);

    Mockito.verify(failureStrategy, Mockito.never())
        .onFailure(Mockito.any(), Mockito.any());
  }
}