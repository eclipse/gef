package org.eclipse.gef4.mvc.commands;

import java.util.ListIterator;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class ReverseUndoCompositeOperation extends AbstractCompositeOperation {

	public ReverseUndoCompositeOperation(String label) {
		super(label);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		IStatus status = Status.OK_STATUS;
		ListIterator<IUndoableOperation> li = getOperations().listIterator(
				getOperations().size());
		while (li.hasPrevious()) {
			status = combine(status, li.previous().undo(monitor, info));
		}
		return status;
	}

}
