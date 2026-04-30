package org.workflow.cqrs.transactions;

public interface CommandTransactionManager {

  <T> T execute(final TransactionalAction<T> action);

  <T> T executeIndependent(final TransactionalAction<T> action);

}
