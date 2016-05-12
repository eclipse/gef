/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.mvc.parts.IVisualPart;

// TODO: Elaborate on the use-cases of this operation.

/**
 * A {@link SetRefreshVisualOperation} is used to set/unset the
 * {@link IVisualPart#isRefreshVisual()} flag of a specified {@link IVisualPart}
 * . This can be handy to guard other operations from model refreshes.
 *
 * @author mwienand
 *
 * @param <VR>
 *            The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public class SetRefreshVisualOperation<VR> extends AbstractOperation
		implements ITransactionalOperation {

	private IVisualPart<VR, ? extends VR> part;
	private boolean from;
	private boolean to;

	/**
	 * Creates a new {@link SetRefreshVisualOperation} for setting the
	 * {@link IVisualPart#isRefreshVisual()} flag of the given
	 * {@link IVisualPart} to the <i>to</i> value on execution and to the
	 * <i>from</i> value on undoing.
	 *
	 * @param part
	 *            The {@link IVisualPart} of which the
	 *            {@link IVisualPart#isRefreshVisual()} flag is changed.
	 * @param from
	 *            The value to which the flag is changed when undoing this
	 *            operation.
	 * @param to
	 *            The value to which the flag is changed when executing this
	 *            operation.
	 */
	public SetRefreshVisualOperation(IVisualPart<VR, ? extends VR> part,
			boolean from, boolean to) {
		super(to ? "Enable Visual Refresh" : "Disable Visual Refresh");
		this.part = part;
		this.from = from;
		this.to = to;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		part.setRefreshVisual(to);
		return Status.OK_STATUS;
	}

	@Override
	public boolean isContentRelevant() {
		return false;
	}

	@Override
	public boolean isNoOp() {
		return from == to;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		part.setRefreshVisual(from);
		return Status.OK_STATUS;
	}

}
