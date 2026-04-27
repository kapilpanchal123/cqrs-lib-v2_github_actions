package org.workflow.cqrs.transactions;

public interface CommandSavepointManager {

  void setRollbackOnly();

  void savePoint(final String savepointName);

  void rollbackToSavepoint(final String savepointName);

  void releaseSavepoint(final String savepointName);

}
