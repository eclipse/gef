/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.operations;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.mvc.fx.policies.AbstractPolicy;

/**
 * An {@link ITransactionalOperation} is an {@link IUndoableOperation} that
 * tolerates successive calls to
 * {@link ITransactionalOperation#execute(IProgressMonitor, IAdaptable)} and
 * {@link ITransactionalOperation#undo(IProgressMonitor, IAdaptable)} and allows
 * to check whether it has an overall effect ({@link #isNoOp()}) compared to the
 * initial state upon construction.
 * <p>
 * It is used by {@link AbstractPolicy transaction policies} to
 * encapsulate their applied changes. The {@link AbstractPolicy
 * transaction policy} will potentially execute the operation locally (to
 * realize "live-feedback") before returning it in its
 * {@link AbstractPolicy#commit()} method. It will then be executed
 * on the {@link IOperationHistory}, but only if it has an overall effect that
 * needs to be undoable.
 *
 * @author mwienand
 *
 */
public interface ITransactionalOperation extends IUndoableOperation {

	/**
	 * Returns <code>true</code> if this {@link ITransactionalOperation} is
	 * actually changing model data (instead of only affecting the
	 * visualization). Otherwise returns <code>false</code>. The content
	 * relevance of an {@link ITransactionalOperation} can be checked to
	 * determine if the execution of the operation will affect the model, for
	 * example, to set an editor's dirty flag.
	 *
	 * @return <code>true</code> if this {@link ITransactionalOperation} is
	 *         actually changing model data, otherwise <code>false</code>.
	 */
	public boolean isContentRelevant();

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
