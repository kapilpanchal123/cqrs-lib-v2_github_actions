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
package org.workflow.cqrs.utils.middlewares;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@Deprecated
@ExtendWith(MockitoExtension.class)
class DefaultCommandPersistenceMiddlewareTest {

//  @Mock
//  CommandRepository commandRepository;

//  DefaultCommandPersistenceMiddleware defaultCommandPersistenceMiddleware;

  @BeforeEach
  void setUp() {
//    defaultCommandPersistenceMiddleware = new DefaultCommandPersistenceMiddleware(commandRepository);
  }

  @Disabled
  @Test
  void test_when_command_is_valid() {
//    Command<?> command = Mockito.mock(Command.class);
//
//    Mockito.when(commandRepository.insertCommand(command))
//        .thenReturn(1L);
//
//    defaultCommandPersistenceMiddleware.invoke(command);
//
//    Mockito.verify(commandRepository, Mockito.times(1))
//        .insertCommand(command);
  }

  @Disabled
  @Test
  void test_when_command_is_null_then_exception_is_thrown() {
//    RuntimeException ex = Assertions.assertThrows(
//        RuntimeException.class,
//        () -> defaultCommandPersistenceMiddleware.invoke(null)
//    );
//
//    Assertions.assertEquals("Error: Command Cannot be null!", ex.getMessage());
//
//    Mockito.verifyNoInteractions(commandRepository);
  }

  @Disabled
  @Test
  void test_when_repository_fails_then_runtime_exception_is_thrown() {
//    Command<?> command = Mockito.mock(Command.class);
//
//    Mockito.when(commandRepository.insertCommand(command))
//        .thenThrow(new RuntimeException("Database Error"));
//
//    RuntimeException ex = Assertions.assertThrows(
//        RuntimeException.class,
//        () -> defaultCommandPersistenceMiddleware.invoke(command)
//    );
//
//    Assertions.assertTrue(ex.getCause() instanceof RuntimeException);
//    Assertions.assertEquals("Database Error", ex.getCause().getMessage());
  }
}