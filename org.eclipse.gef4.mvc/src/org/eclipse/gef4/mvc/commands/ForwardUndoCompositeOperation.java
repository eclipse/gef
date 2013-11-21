package org.eclipse.gef4.mvc.commands;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

public class ForwardUndoCompositeOperation extends AbstractCompositeOperation {

	public ForwardUndoCompositeOperation(String label) {
		super(label);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		IStatus status = null;
		for (IUndoableOperation operation : getOperations()) {
			status = combine(status, operation.undo(monitor, info));
		}
		return status;
	}
}
