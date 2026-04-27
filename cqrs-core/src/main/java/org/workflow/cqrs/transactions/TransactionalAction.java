package org.workflow.cqrs.transactions;

public interface TransactionalAction<T> {

  T execute(final CommandSavepointManager commandSavepointManager) throws Throwable;

}
