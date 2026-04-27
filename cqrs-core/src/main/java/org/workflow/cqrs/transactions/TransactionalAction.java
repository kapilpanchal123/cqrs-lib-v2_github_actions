package org.workflow.cqrs.transactions;

public interface TransactionalAction<T> {

  T execute(final CommandTransactionStatus status) throws Throwable;

}
