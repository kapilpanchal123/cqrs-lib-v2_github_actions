package org.workflow.cqrs.transactions;

@Deprecated
public interface CommandTransactionStatus {

  void setRollbackOnly();

}
