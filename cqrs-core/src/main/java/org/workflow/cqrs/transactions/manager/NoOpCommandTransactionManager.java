package org.workflow.cqrs.transactions.manager;

import org.workflow.cqrs.transactions.CommandTransactionManager;
import org.workflow.cqrs.transactions.TransactionalAction;

public class NoOpCommandTransactionManager implements CommandTransactionManager {

  @Override
  public <T> T execute(final TransactionalAction<T> action) {
    try {
      return action.execute(() -> {});
    } catch(final Throwable t) {
      throw (t instanceof RuntimeException re) ? (RuntimeException) re : new RuntimeException(t);
    }
  }

  @Override
  public <T> T executeIndependent(final TransactionalAction<T> action) {
    return execute(action);
  }
}
