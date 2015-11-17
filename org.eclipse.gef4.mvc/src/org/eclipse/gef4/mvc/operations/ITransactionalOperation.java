/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.operations;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef4.mvc.policies.AbstractTransactionPolicy;

/**
 * An {@link ITransactionalOperation} is an {@link IUndoableOperation} that
 * tolerates successive calls to
 * {@link ITransactionalOperation#execute(IProgressMonitor, IAdaptable)} and
 * {@link ITransactionalOperation#undo(IProgressMonitor, IAdaptable)} and allows
 * to check whether it has an overall effect ({@link #isNoOp()}) compared to the
 * initial state upon construction.
 * <p>
 * It is used by {@link AbstractTransactionPolicy transaction policies} to
 * encapsulate their applied changes. The {@link AbstractTransactionPolicy
 * transaction policy} will potentially execute the operation locally (to
 * realize "live-feedback") before returning it in its
 * {@link AbstractTransactionPolicy#commit()} method. It will then be executed
 * on the {@link IOperationHistory}, but only if it has an overall effect that
 * needs to be undoable.
 *
 * @author mwienand
 *
 */
public interface ITransactionalOperation extends IUndoableOperation {

	/**
	 * Returns <code>true</code> if this {@link ITransactionalOperation} has no
	 * effect (in comparison to its initial state). Otherwise returns
	 * <code>false</code>.
	 *
	 * @return <code>true</code> if this {@link ITransactionalOperation} has no
	 *         effect, otherwise <code>false</code>.
	 */
	public boolean isNoOp();

}
