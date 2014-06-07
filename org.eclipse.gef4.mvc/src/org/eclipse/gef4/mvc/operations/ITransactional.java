package org.eclipse.gef4.mvc.operations;

import org.eclipse.core.commands.operations.IUndoableOperation;

public interface ITransactional {

	public abstract void init();

	public abstract IUndoableOperation commit();

}