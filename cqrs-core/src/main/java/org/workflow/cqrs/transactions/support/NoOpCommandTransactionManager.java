package org.workflow.cqrs.transactions.support;

import org.workflow.cqrs.transactions.CommandSavepointManager;
import org.workflow.cqrs.transactions.CommandTransactionManager;
import org.workflow.cqrs.transactions.TransactionalAction;

public class NoOpCommandTransactionManager implements CommandTransactionManager {

  @Override
  public <T> T execute(final TransactionalAction<T> action) {
    try {
      return action.execute(NoOpCommandSavepointManager.INSTANCE);
    } catch (final Throwable t) {
      throw (t instanceof RuntimeException re) ? re : new RuntimeException(t);
    }
  }

  @Override
  public <T> T executeIndependent(final TransactionalAction<T> action) {
    return null;
  }

  private static final class NoOpCommandSavepointManager implements CommandSavepointManager {

    static final NoOpCommandSavepointManager INSTANCE = new NoOpCommandSavepointManager();

    @Override
    public void setRollbackOnly() {

    }

    @Override
    public void savePoint(String savepointName) {

    }

    @Override
    public void rollbackToSavepoint(String savepointName) {

    }

    @Override
    public void releaseSavepoint(String savepointName) {

    }
  }
}
