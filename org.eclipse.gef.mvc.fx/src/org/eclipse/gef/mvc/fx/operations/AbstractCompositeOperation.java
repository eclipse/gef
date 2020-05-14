/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
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
package org.eclipse.gef.mvc.fx.operations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.ICompositeOperation;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.mvc.fx.MvcFxBundle;

/**
 * The {@link AbstractCompositeOperation} is an abstract implementation of the
 * {@link ICompositeOperation} interface. The individual operations are stored
 * in a {@link List}. They are executed/redone/undone in forward order.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public abstract class AbstractCompositeOperation extends AbstractOperation
		implements ICompositeOperation, ITransactionalOperation {

	/**
	 * The list containing the {@link ITransactionalOperation}s which are
	 * combined in this composite operation.
	 */
	List<ITransactionalOperation> operations = new ArrayList<>();

	/**
	 * Creates a new {@link AbstractCompositeOperation} with the given label.
	 *
	 * @param label
	 *            The label for this {@link AbstractCompositeOperation}.
	 */
	public AbstractCompositeOperation(String label) {
		super(label);
	}

	@Override
	public void add(IUndoableOperation operation) {
		if (operation instanceof ITransactionalOperation) {
			operations.add((ITransactionalOperation) operation);
		} else {
			throw new IllegalArgumentException(
					"The given operation may not be null and must implement ITransactionalOperation.");
		}
	}

	/**
	 * Adds the given {@link IUndoableOperation}s to this composite operation.
	 *
	 * @param operations
	 *            The {@link IUndoableOperation}s which are added to this
	 *            composite operation.
	 */
	public void addAll(List<ITransactionalOperation> operations) {
		/*
		 * Do not use <code>operations.addAll()</code> because we need to check
		 * for <code>null</code> operations.
		 */
		for (ITransactionalOperation op : operations) {
			add(op);
		}
	}

	@Override
	public void addContext(IUndoContext context) {
		super.addContext(context);
	}

	@Override
	public boolean canExecute() {
		for (ITransactionalOperation operation : operations) {
			if (!operation.canExecute()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canRedo() {
		for (ITransactionalOperation operation : operations) {
			if (!operation.canRedo()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canUndo() {
		for (ITransactionalOperation operation : operations) {
			if (!operation.canUndo()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return an {@link IStatus} representing the merge of the given first and
	 * second {@link IStatus}s.
	 *
	 * @param s1
	 *            The first {@link IStatus}.
	 * @param s2
	 *            The second {@link IStatus}.
	 * @return The merge of the first and second {@link IStatus}.
	 */
	protected IStatus combine(IStatus s1, IStatus s2) {
		MultiStatus status = new MultiStatus(MvcFxBundle.PLUGIN_ID, IStatus.OK,
				null, null);
		status.merge(s1);
		status.merge(s2);
		return status;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		IStatus status = Status.OK_STATUS;
		for (ITransactionalOperation operation : operations) {
			combine(status, operation.execute(monitor, info));
		}
		return status;
	}

	/**
	 * Returns the list of {@link ITransactionalOperation}s which are combined
	 * in this composite operation.
	 *
	 * @return The list of {@link ITransactionalOperation}s which are combined
	 *         in this composite operation.
	 */
	public List<ITransactionalOperation> getOperations() {
		return operations;
	}

	@Override
	public boolean isContentRelevant() {
		for (ITransactionalOperation op : operations) {
			if (op.isContentRelevant()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns <code>true</code> if no operations are currently combined in this
	 * composite operation. Otherwise returns <code>false</code>.
	 *
	 * @return <code>true</code> if no operations are currently combined in this
	 *         composite operation, otherwise <code>false</code>.
	 */
	public boolean isEmpty() {
		return operations.isEmpty();
	}

	@Override
	public boolean isNoOp() {
		for (ITransactionalOperation op : operations) {
			if (!op.isNoOp()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		IStatus status = Status.OK_STATUS;
		for (ITransactionalOperation operation : operations) {
			combine(status, operation.redo(monitor, info));
		}
		return status;
	}

	@Override
	public void remove(IUndoableOperation operation) {
		operations.remove(operation);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		IStatus status = Status.OK_STATUS;
		for (ITransactionalOperation operation : operations) {
			combine(status, operation.undo(monitor, info));
		}
		return status;
	}

	/**
	 * Simplifies this composite operation if possible and returns the
	 * simplified operation. When this composite operation does not contain any
	 * operations, <code>null</code> is returned. When this composite operation
	 * contains exactly one operation, that one operation is returned.
	 * Otherwise, this composite operation is returned.
	 *
	 * @param filterNoOps
	 *            <code>true</code> if no-ops (see
	 *            {@link ITransactionalOperation#isNoOp()}) should be removed
	 *            from the list of operations, otherwise <code>false</code>.
	 * @return <code>null</code> when no operations are contained, the one
	 *         operation when only one operation is contained, this composite
	 *         when multiple operations are contained.
	 */
	public ITransactionalOperation unwrap(boolean filterNoOps) {
		if (filterNoOps) {
			// remove no-op operations
			for (int i = operations.size() - 1; i >= 0; i--) {
				ITransactionalOperation op = operations.get(i);
				if (op.isNoOp()) {
					operations.remove(i);
				} else if (op instanceof AbstractCompositeOperation) {
					// unwrap recursively
					ITransactionalOperation unwrapped = ((AbstractCompositeOperation) operations
							.get(i)).unwrap(filterNoOps);
					operations.remove(i);
					operations.add(i, unwrapped);
				}
			}
		}
		// reduce operation
		if (operations.size() == 0) {
			return null;
		} else if (operations.size() == 1) {
			return operations.get(0);
		} else {
			return this;
		}
	}

}
