/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.policies;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;

/**
 * Abstract base implementation of {@link IPolicy} that is transactional.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractTransactionPolicy<VR> extends AbstractPolicy<VR> {

	private ITransactionalOperation operation;
	private boolean initialized;

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
	 * manipulations applied by the policy since the last {@link #init()} call.
	 * When called multiple times in sequence, only the first call will yield an
	 * operation, the subsequent calls will yield <code>null</code>.
	 *
	 * @return An {@link ITransactionalOperation} that performs all
	 *         manipulations applied by the policy since the last
	 *         {@link #init()} call.
	 */
	public ITransactionalOperation commit() {
		checkInitialized();

		// IMPORTANT: We need to locally execute the operation first to ensure
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
	 * Returns the {@link ITransactionalOperation} that is used to encapsulate
	 * the changes that are applied by this {@link AbstractTransactionPolicy}
	 * through its "work" methods.
	 *
	 * @return A new {@link ITransactionalOperation} to encapsulate all applied
	 *         changes.
	 */
	protected ITransactionalOperation getOperation() {
		return operation;
	}

	/**
	 * Initializes the policy, so that the policy's "work" methods can be used.
	 * Calling a "work" method while the policy is not initialized will result
	 * in an {@link IllegalStateException}. It is safe to call {@link #init()}
	 * multiple times in sequence.
	 */
	public void init() {
		checkUninitialized();
		initialized = true;
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
	 * Locally executes the {@link ITransactionalOperation} that is updated by
	 * this policy, i.e. not on the operation history. Maybe used in the "work"
	 * operations of subclasses.
	 *
	 */
	protected void locallyExecuteOperation() {
		try {
			// IMPORTANT: We may not skip the local execution of the operation
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
	 * Locally undoes the {@link ITransactionalOperation} that is updated by
	 * this policy, i.e. not on the operation history.
	 */
	private void locallyUndoOperation() {
		try {
			// IMPORTANT: We may not skip undo in case the operation is a
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
	 * that have been applied via the policy's work methods last {@link #init()}
	 * call.
	 */
	public void rollback() {
		// after rollback, we need to be re-initialized
		initialized = false;

		// clear operation and return current one (and formerly pushed
		// operations)
		locallyUndoOperation();
		operation = null;
	}
}
