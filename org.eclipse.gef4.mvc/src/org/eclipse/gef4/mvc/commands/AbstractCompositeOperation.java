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
package org.eclipse.gef4.mvc.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.ICompositeOperation;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.mvc.Activator;

public abstract class AbstractCompositeOperation extends AbstractOperation implements ICompositeOperation {

	public AbstractCompositeOperation(String label) {
		super(label);
	}

	List<IUndoableOperation> operations = new ArrayList<IUndoableOperation>();
	
	protected List<IUndoableOperation> getOperations() {
		return operations;
	}
	
	@Override
	public void add(IUndoableOperation operation) {
		operations.add(operation);
		
	}

	@Override
	public void remove(IUndoableOperation operation) {
		operations.remove(operation);
	}
	
	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		IStatus status = Status.OK_STATUS;
		for(IUndoableOperation operation : operations){
			combine(status, operation.execute(monitor, info));
		}
		return status;
	}
	
	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		IStatus status = Status.OK_STATUS;
		for(IUndoableOperation operation : operations){
			combine(status, operation.redo(monitor, info));
		}
		return status;
	}
	
	@Override
	public boolean canExecute() {
		for(IUndoableOperation operation : operations){
			if(!operation.canExecute()){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean canUndo() {
		for(IUndoableOperation operation : operations){
			if(!operation.canUndo()){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean canRedo() {
		for(IUndoableOperation operation : operations){
			if(!operation.canRedo()){
				return false;
			}
		}
		return true;
	}
	
	protected IStatus combine(IStatus s1, IStatus s2){
		MultiStatus status = new MultiStatus(Activator.PLUGIN_ID, IStatus.OK, null, null);
		status.merge(s1);
		status.merge(s2);
		return status;
	}

}
