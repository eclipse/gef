/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.policies;

import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import javafx.scene.Node;

/**
 * Abstract base implementation of {@link IPolicy} that is transactional.
 *
 * @author anyssen
 *
 */
public abstract class AbstractPolicy extends
		IAdaptable.Bound.Impl<IVisualPart<? extends Node>> implements IPolicy {

	private ITransactionalOperation operation;
	private boolean initialized;

	/**
	 * Checks whether this {@link AbstractPolicy} is initialized and throws an
	 * IllegalStateException if not.
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
	@Override
	public ITransactionalOperation commit() {
		checkInitialized();
		// XXX: We need to locally execute the operation first to ensure
		// the visuals and content reflect the target state of the operation.
		// After this, we may safely omit the operation as commit operation in
		// case its a no-op.
		ITransactionalOperation commit = null;
		try {
			locallyExecuteOperation();
			// clear operation and return current one (and formerly pushed
			// operations)
			commit = getOperation();
		} catch (Exception e) {
			throw e;
		} finally {
			// after commit, we need to be re-initialized
			initialized = false;
			operation = null;
		}
		if (commit != null) {
			if (!commit.isNoOp()) {
				return commit;
			}
		}
		return null;
	}

	/**
	 * Creates an {@link ITransactionalOperation} that is used to encapsulate
	 * the changes that are applied by this {@link AbstractPolicy} through its
	 * "work" methods. The created operation should allow for
	 * {@link #locallyExecuteOperation() local execution} at each time.
	 *
	 * @return A new {@link ITransactionalOperation} to encapsulate all applied
	 *         changes.
	 */
	protected abstract ITransactionalOperation createOperation();

	/**
	 * Returns the {@link ITransactionalOperation} that is used to encapsulate
	 * the changes that are applied by this {@link AbstractPolicy} through its
	 * "work" methods.
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
	@Override
	public void init() {
		checkUninitialized();
		initialized = true;
		operation = createOperation();
	}

	/**
	 * Returns whether this {@link AbstractPolicy} is initialized or not.
	 *
	 * @return <code>true</code> if this {@link AbstractPolicy} is initialized,
	 *         <code>false</code> otherwise.
	 */
	protected boolean isInitialized() {
		return initialized;
	}

	/**
	 * Locally executes the {@link ITransactionalOperation} that is updated by
	 * this policy, i.e. not on the operation history. Maybe used in the "work"
	 * operations of subclasses.
	 */
	protected void locallyExecuteOperation() {
		try {
			// XXX: We may not skip the local execution of the operation
			// if it is a no-op, because the visual or content
			// might already be in a state that is diverse from the initial
			// state of the operation (so execute might have an actual affect).
			if (operation != null) {
				operation.execute(null, null);
			}
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Locally undoes the {@link ITransactionalOperation} that is updated by
	 * this policy, i.e. not on the operation history.
	 */
	private void locallyUndoOperation() {
		try {
			// XXX: We may not skip undo in case the operation is a
			// no-op when executing it locally, because the visual or content
			// might already be in a state that is diverse from the initial
			// state (so undo might have an actual affect).
			if (operation != null) {
				operation.undo(null, null);
			}
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Puts back this policy into an uninitialized state, reverting any changes
	 * that have been applied via the policy's work methods since the preceding
	 * {@link #init()} call.
	 */
	@Override
	public void rollback() {
		// after rollback, we need to be re-initialized
		initialized = false;
		// clear operation and return current one (and formerly pushed
		// operations)
		try {
			locallyUndoOperation();
		} catch (Exception e) {
			throw e;
		} finally {
			operation = null;
		}
	}
}
