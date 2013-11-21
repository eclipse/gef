package org.eclipse.gef4.mvc.commands;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

public class UnexecutableOperation extends AbstractOperation {

	public UnexecutableOperation(String label) {
		super(label);
	}
	
	@Override
	public boolean canExecute() {
		return false;
	}
	
	@Override
	public boolean canRedo() {
		return false;
	}
	
	@Override
	public boolean canUndo() {
		return false;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return null;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return null;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return null;
	}

}
