package org.workflow.cqrs.transactions.support;

import org.workflow.cqrs.transactions.CommandSavepointManager;
import org.workflow.cqrs.transactions.CommandTransactionManager;
import org.workflow.cqrs.transactions.TransactionalAction;

/**
 * No-op implementation of {@link CommandTransactionManager}.
 *
 * <p>This implementation executes actions without any real transactional
 * boundaries. It is primarily intended for:
 * <ul>
 *   <li>Testing environments</li>
 *   <li>Non-transactional systems</li>
 *   <li>Lightweight setups where transactional guarantees are not required</li>
 * </ul>
 *
 * <h2>Behavior</h2>
 * <ul>
 *   <li>{@link #execute(TransactionalAction)} invokes the action directly</li>
 *   <li>No transaction is started, committed, or rolled back</li>
 *   <li>{@link CommandSavepointManager} operations are ignored</li>
 * </ul>
 *
 * <h2>Limitations</h2>
 * <ul>
 *   <li>No atomicity guarantees</li>
 *   <li>No rollback support (including savepoints)</li>
 *   <li>Failures may leave the system in a partially updated state</li>
 * </ul>
 *
 * <h2>Usage Considerations</h2>
 * <p>This implementation should <b>not</b> be used in production systems
 * where data consistency and transactional integrity are critical.
 *
 * <p>It is suitable for:
 * <ul>
 *   <li>Unit testing pipelines without database dependencies</li>
 *   <li>Prototyping or early-stage development</li>
 * </ul>
 *
 * <h2>Failure Semantics</h2>
 * <p>Exceptions thrown during execution are propagated directly:
 * <ul>
 *   <li>{@link RuntimeException} is rethrown as-is</li>
 *   <li>Checked exceptions are wrapped in {@link RuntimeException}</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>This class is stateless and thread-safe.
 *
 * @see CommandTransactionManager
 * @see TransactionalAction
 */
public class NoOpCommandTransactionManager implements CommandTransactionManager {

  /**
   * Executes the given action without any transactional guarantees.
   *
   * <p>The provided {@link CommandSavepointManager} is a no-op implementation,
   * meaning that savepoint operations have no effect.
   *
   * @param action the action to execute
   * @param <T> the result type
   * @return the result of the action
   * @throws RuntimeException if the action throws an exception
   */
  @Override
  public <T> T execute(final TransactionalAction<T> action) {
    try {
      return action.execute(NoOpCommandSavepointManager.INSTANCE);
    } catch (final Throwable t) {
      throw (t instanceof RuntimeException re) ? re : new RuntimeException(t);
    }
  }

  /**
   * Executes the given action independently.
   *
   * <p>Since this implementation does not support transactions, this method
   * behaves the same as {@link #execute(TransactionalAction)}.
   *
   * <p><b>Note:</b> Returning {@code null} is not recommended and may lead
   * to unexpected {@link NullPointerException}s in callers. A proper implementation
   * should delegate to {@link #execute(TransactionalAction)}.
   *
   * @param action the action to execute
   * @param <T> the result type
   * @return the result of the action
   */
  @Override
  public <T> T executeIndependent(final TransactionalAction<T> action) {
    return null;
  }

  /**
   * No-op implementation of {@link CommandSavepointManager}.
   *
   * <p>All methods are intentionally empty, as savepoints are not supported
   * in this implementation.
   *
   * <h2>Behavior</h2>
   * <ul>
   *   <li>Savepoint creation is ignored</li>
   *   <li>Rollback operations have no effect</li>
   *   <li>Release operations are no-ops</li>
   *   <li>{@link #setRollbackOnly()} does nothing</li>
   * </ul>
   *
   * <h2>Usage</h2>
   * <p>This class is used internally by {@link NoOpCommandTransactionManager}
   * and is not intended for direct use.
   */
  private static final class NoOpCommandSavepointManager implements CommandSavepointManager {

    /**
     * Singleton instance.
     */
    static final NoOpCommandSavepointManager INSTANCE = new NoOpCommandSavepointManager();

    /**
     * No-op implementation.
     */
    @Override
    public void setRollbackOnly() {

    }

    /**
     * No-op implementation.
     *
     * @param savepointName ignored
     */
    @Override
    public void savePoint(final String savepointName) {

    }

    /**
     * No-op implementation.
     *
     * @param savepointName ignored
     */
    @Override
    public void rollbackToSavepoint(final String savepointName) {

    }

    /**
     * No-op implementation.
     *
     * @param savepointName ignored
     */
    @Override
    public void releaseSavepoint(final String savepointName) {

    }
  }
}
