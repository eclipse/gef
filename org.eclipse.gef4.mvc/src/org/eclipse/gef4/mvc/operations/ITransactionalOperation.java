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

import org.eclipse.core.commands.operations.IUndoableOperation;

/**
 * The {@link ITransactionalOperation} interface is an extension to
 * {@link IUndoableOperation}. It specified a {@link #isNoOp()} method that can
 * be used to determine if an operation has an effect. If an operation does not
 * have an effect, there is no need to execute it (on the operation history).
 *
 * @author mwienand
 *
 */
public interface ITransactionalOperation extends IUndoableOperation {

	/**
	 * Returns <code>true</code> if this {@link ITransactionalOperation} has no
	 * effect. Otherwise returns <code>false</code>.
	 *
	 * @return <code>true</code> if this {@link ITransactionalOperation} has no
	 *         effect, otherwise <code>false</code>.
	 */
	public boolean isNoOp();

}
