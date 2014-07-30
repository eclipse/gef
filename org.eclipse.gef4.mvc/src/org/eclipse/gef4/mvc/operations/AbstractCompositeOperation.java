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

public abstract class AbstractCompositeOperation extends AbstractOperation
		implements ICompositeOperation {

	List<IUndoableOperation> operations = new ArrayList<IUndoableOperation>();

	public AbstractCompositeOperation(String label) {
		super(label);
	}

	@Override
	public void add(IUndoableOperation operation) {
		operations.add(operation);
	}

	@Override
	public void addContext(IUndoContext context) {
		super.addContext(context);
	}

	public void allAll(List<IUndoableOperation> operations) {
		this.operations.addAll(operations);
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

	protected List<IUndoableOperation> getOperations() {
		return operations;
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

	public IUndoableOperation unwrap() {
		if (operations.size() == 1) {
			return operations.get(0);
		} else {
			return this;
		}
	}

}
