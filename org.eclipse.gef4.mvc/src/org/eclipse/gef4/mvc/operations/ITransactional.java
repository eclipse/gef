package org.eclipse.gef4.mvc.operations;

import org.eclipse.core.commands.operations.IUndoableOperation;

public interface ITransactional {

	public abstract IUndoableOperation commit();

	public abstract void init();

}