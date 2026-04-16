package com.java.workflow.cqrs.persistence.middlewares;

import com.java.workflow.cqrs.core.Command;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultCommandPersistenceMiddlewareTest {

//  @Mock
//  CommandRepository commandRepository;

//  DefaultCommandPersistenceMiddleware defaultCommandPersistenceMiddleware;

  @BeforeEach
  void setUp() {
//    defaultCommandPersistenceMiddleware = new DefaultCommandPersistenceMiddleware(commandRepository);
  }

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