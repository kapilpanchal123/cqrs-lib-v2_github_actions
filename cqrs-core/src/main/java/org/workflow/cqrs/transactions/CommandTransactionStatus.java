package org.workflow.cqrs.transactions;

public interface CommandTransactionStatus {

  void setRollbackOnly();

}
