/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.operations;

import java.util.ListIterator;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

// TODO: init label when adding nested operations
/**
 * The {@link ReverseUndoCompositeOperation} is an
 * {@link AbstractCompositeOperation} which undoes its combined operations in
 * the reverse order of their execution.
 *
 * @author anyssen
 *
 */
public class ReverseUndoCompositeOperation extends AbstractCompositeOperation {

	/**
	 * Creates a new {@link ReverseUndoCompositeOperation} with the given label.
	 *
	 * @param label
	 *            The operation's label.
	 */
	public ReverseUndoCompositeOperation(String label) {
		super(label);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		IStatus status = Status.OK_STATUS;
		ListIterator<ITransactionalOperation> li = getOperations()
				.listIterator(getOperations().size());
		while (li.hasPrevious()) {
			status = combine(status, li.previous().undo(monitor, info));
		}
		return status;
	}

}
