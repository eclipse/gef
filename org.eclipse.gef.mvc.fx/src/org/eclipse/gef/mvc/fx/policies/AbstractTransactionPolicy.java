/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.policies;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;

/**
 * Abstract base implementation of {@link IPolicy} that is transactional.
 *
 * @author anyssen
 *
 */
public abstract class AbstractTransactionPolicy extends AbstractPolicy {

	private ITransactionalOperation operation;
	private boolean initialized;
	private boolean isLocallyExecuteOperation = false;

	/**
	 * Checks whether this {@link AbstractTransactionPolicy} is initialized and
	 * throws an IllegalStateException if not.
	 */
	protected void checkInitialized() {
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}
	}

	private void checkUninitialized() {
		if (initialized) {
			throw new IllegalStateException("Already initialized");
		}
	}

	/**
	 * Returns an {@link ITransactionalOperation} that performs all
	 * manipulations applied by the policy since the previous {@link #init()}
	 * call.
	 *
	 * @return An {@link ITransactionalOperation} that performs all
	 *         manipulations applied by the policy since the last
	 *         {@link #init()} call.
	 */
	public ITransactionalOperation commit() {
		checkInitialized();
		// XXX: We need to locally execute the operation first to ensure
		// the visuals and content reflect the target state of the operation.
		// After this, we may safely omit the operation as commit operation in
		// case its a no-op.
		locallyExecuteOperation();
		// after commit, we need to be re-initialized
		initialized = false;
		// clear operation and return current one (and formerly pushed
		// operations)
		ITransactionalOperation commit = getOperation();
		operation = null;
		if (commit != null) {
			if (!commit.isNoOp()) {
				return commit;
			}
		}
		return null;
	}

	/**
	 * Creates an {@link ITransactionalOperation} that is used to encapsulate
	 * the changes that are applied by this {@link AbstractTransactionPolicy}
	 * through its "work" methods. The created operation should allow for
	 * {@link #locallyExecuteOperation() local execution} at each time.
	 *
	 * @return A new {@link ITransactionalOperation} to encapsulate all applied
	 *         changes.
	 */
	protected abstract ITransactionalOperation createOperation();

	/**
	 * Locally executes the {@link ITransactionalOperation} that is updated by
	 * this policy, i.e. not on the operation history. Maybe used in the "work"
	 * operations of subclasses.
	 */
	protected void doLocallyExecuteOperation() {
		try {
			// XXX: We may not skip the local execution of the operation
			// if it is a no-op, because the visual or content
			// might already be in a state that is diverse from the initial
			// state of the operation (so execute might have an actual affect).
			if (operation != null) {
				operation.execute(null, null);
			}
		} catch (ExecutionException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Returns the {@link ITransactionalOperation} that is used to encapsulate
	 * the changes that are applied by this {@link AbstractTransactionPolicy}
	 * through its "work" methods.
	 *
	 * @return A new {@link ITransactionalOperation} to encapsulate all applied
	 *         changes.
	 */
	protected final ITransactionalOperation getOperation() {
		return operation;
	}

	/**
	 * Initializes the policy, so that the policy's "work" methods can be used.
	 * Calling a "work" method while the policy is not initialized will result
	 * in an {@link IllegalStateException}, as well as re-initializing before
	 * committing or rolling back.
	 */
	public void init() {
		checkUninitialized();
		initialized = true;
		setLocallyExecuteOperation(true);
		operation = createOperation();
	}

	/**
	 * Returns whether this {@link AbstractTransactionPolicy} is initialized or
	 * not.
	 *
	 * @return <code>true</code> if this {@link AbstractTransactionPolicy} is
	 *         initialized, <code>false</code> otherwise.
	 */
	protected boolean isInitialized() {
		return initialized;
	}

	/**
	 * Returns <code>true</code> if the local execution of operations is enabled
	 * for this {@link AbstractTransactionPolicy}. Otherwise returns
	 * <code>false</code>.
	 *
	 * @return <code>true</code> if the local execution of operations is enabled
	 *         for this {@link AbstractTransactionPolicy}, <code>false</code>
	 *         otherwise.
	 */
	public boolean isLocallyExecuteOperation() {
		return isLocallyExecuteOperation;
	}

	/**
	 * Locally executes the {@link ITransactionalOperation} that is updated by
	 * this policy, i.e. not on the operation history. Maybe used in the "work"
	 * operations of subclasses.
	 *
	 */
	protected final void locallyExecuteOperation() {
		if (isLocallyExecuteOperation()) {
			doLocallyExecuteOperation();
		}
	}

	/**
	 * Locally undoes the {@link ITransactionalOperation} that is updated by
	 * this policy, i.e. not on the operation history.
	 */
	private void locallyUndoOperation() {
		if (!isLocallyExecuteOperation()) {
			return;
		}
		try {
			// XXX: We may not skip undo in case the operation is a
			// no-op when executing it locally, because the visual or content
			// might already be in a state that is diverse from the initial
			// state (so undo might have an actual affect).
			if (operation != null) {
				operation.undo(null, null);
			}
		} catch (ExecutionException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Puts back this policy into an uninitialized state, reverting any changes
	 * that have been applied via the policy's work methods since the preceding
	 * {@link #init()} call.
	 */
	public void rollback() {
		// after rollback, we need to be re-initialized
		initialized = false;
		// clear operation and return current one (and formerly pushed
		// operations)
		locallyUndoOperation();
		operation = null;
	}

	/**
	 * Enables or disables the local execution of operations for this
	 * {@link AbstractTransactionPolicy} depending on the given flag.
	 *
	 * @param isLocallyExecuteOperation
	 *            <code>true</code> in order to enable the local execution of
	 *            operations, <code>false</code> in order to disable the local
	 *            execution of operations.
	 */
	public void setLocallyExecuteOperation(boolean isLocallyExecuteOperation) {
		this.isLocallyExecuteOperation = isLocallyExecuteOperation;
	}

}
