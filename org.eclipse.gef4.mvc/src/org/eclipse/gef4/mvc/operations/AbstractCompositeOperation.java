/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.operations;

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
import org.eclipse.gef4.mvc.MvcBundle;

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
		implements ICompositeOperation {

	/**
	 * The list containing the {@link IUndoableOperation}s which are combined in
	 * this composite operation.
	 */
	List<IUndoableOperation> operations = new ArrayList<IUndoableOperation>();

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
		if (operation == null) {
			throw new IllegalArgumentException(
					"The given operation may not be null.");
		}
		operations.add(operation);
	}

	/**
	 * Adds the given {@link IUndoableOperation}s to this composite operation.
	 *
	 * @param operations
	 *            The {@link IUndoableOperation}s which are added to this
	 *            composite operation.
	 */
	public void addAll(List<IUndoableOperation> operations) {
		/*
		 * Do not use <code>operations.addAll()</code> because we need to check
		 * for <code>null</code> operations.
		 */
		for (IUndoableOperation op : operations) {
			add(op);
		}
	}

	@Override
	public void addContext(IUndoContext context) {
		super.addContext(context);
	}

	@Override
	public boolean canExecute() {
		for (IUndoableOperation operation : operations) {
			if (!operation.canExecute()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canRedo() {
		for (IUndoableOperation operation : operations) {
			if (!operation.canRedo()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canUndo() {
		for (IUndoableOperation operation : operations) {
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
		MultiStatus status = new MultiStatus(MvcBundle.PLUGIN_ID, IStatus.OK,
				null, null);
		status.merge(s1);
		status.merge(s2);
		return status;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		IStatus status = Status.OK_STATUS;
		for (IUndoableOperation operation : operations) {
			combine(status, operation.execute(monitor, info));
		}
		return status;
	}

	/**
	 * Returns the list of operations which are combined in this composite
	 * operation.
	 *
	 * @return The list of operations which are combined in this composite
	 *         operation.
	 */
	public List<IUndoableOperation> getOperations() {
		return operations;
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
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		IStatus status = Status.OK_STATUS;
		for (IUndoableOperation operation : operations) {
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
		for (IUndoableOperation operation : operations) {
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
	 * @return <code>null</code> when no operations are contained, the one
	 *         operation when only one operation is contained, this composite
	 *         when multiple operations are contained.
	 */
	public IUndoableOperation unwrap() {
		if (operations.size() == 0) {
			return null;
		} else if (operations.size() == 1) {
			return operations.get(0);
		} else {
			return this;
		}
	}

}
