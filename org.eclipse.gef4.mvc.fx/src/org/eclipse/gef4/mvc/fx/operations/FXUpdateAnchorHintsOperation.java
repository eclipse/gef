/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;

/**
 * The {@link FXUpdateAnchorHintsOperation} can be used to update the
 * start-point-hint and end-point-hint of a {@link Connection}.
 */
public class FXUpdateAnchorHintsOperation extends AbstractOperation
		implements ITransactionalOperation {

	private final Connection connection;
	private final Point initialStartHint;
	private final Point initialEndHint;
	private Point newStartHint;
	private Point newEndHint;

	/**
	 * Constructs a new {@link FXUpdateAnchorHintsOperation} that can be used to
	 * update the start-point-hint and end-point-hint of the given
	 * {@link Connection}.
	 *
	 * @param connection
	 *            The {@link Connection}.
	 */
	public FXUpdateAnchorHintsOperation(Connection connection) {
		super("Update Anchor Hints");
		this.connection = connection;
		this.initialStartHint = connection.getStartPointHint() == null ? null
				: connection.getStartPointHint().getCopy();
		this.initialEndHint = connection.getEndPointHint() == null ? null
				: connection.getEndPointHint().getCopy();
		this.newStartHint = initialStartHint == null ? null
				: initialStartHint.getCopy();
		this.newEndHint = initialEndHint == null ? null
				: initialEndHint.getCopy();
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (connection != null) {
			setHints(newStartHint, newEndHint);
		}
		return Status.OK_STATUS;
	}

	@Override
	public boolean isContentRelevant() {
		return false;
	}

	@Override
	public boolean isNoOp() {
		boolean startHintEquals = initialStartHint == null
				? newStartHint == null : initialStartHint.equals(newStartHint);
		boolean endHintEquals = initialEndHint == null ? newEndHint == null
				: initialEndHint.equals(newEndHint);
		return startHintEquals && endHintEquals;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	private void setHints(Point startHint, Point endHint) {
		Point currentStartHint = connection.getStartPointHint();
		if (currentStartHint == null || !currentStartHint.equals(startHint)) {
			connection.setStartPointHint(startHint);
		}
		Point currentEndHint = connection.getEndPointHint();
		if (currentEndHint == null || !currentEndHint.equals(endHint)) {
			connection.setEndPointHint(endHint);
		}
	}

	/**
	 * Sets the new hints.
	 *
	 * @param newStartHint
	 *            The new start hint.
	 * @param newEndHint
	 *            The new end hint.
	 */
	public void setNewHints(Point newStartHint, Point newEndHint) {
		this.newStartHint = newStartHint == null ? null
				: newStartHint.getCopy();
		this.newEndHint = newEndHint == null ? null : newEndHint.getCopy();
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (connection != null) {
			setHints(initialStartHint, initialEndHint);
		}
		return Status.OK_STATUS;
	}

}
