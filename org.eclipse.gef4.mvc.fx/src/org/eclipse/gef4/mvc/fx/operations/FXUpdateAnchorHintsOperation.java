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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.fx.anchors.AnchorKey;
import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;

import javafx.beans.property.ReadOnlyMapProperty;

/**
 *
 * @author mwienand
 *
 */
public class FXUpdateAnchorHintsOperation extends AbstractOperation
		implements ITransactionalOperation {

	private final Connection connection;
	private final Map<AnchorKey, Point> initialHints;
	private final Map<AnchorKey, Point> newHints;

	/**
	 * @param connection
	 *            The {@link Connection}.
	 */
	public FXUpdateAnchorHintsOperation(Connection connection) {
		super("UpdateAnchorHints()");
		this.connection = connection;
		this.initialHints = queryHints();
		this.newHints = new HashMap<>(initialHints);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (connection != null) {
			setHints(newHints);
			connection.getRouter().route(connection);
		}
		return Status.OK_STATUS;
	}

	@Override
	public boolean isContentRelevant() {
		return false;
	}

	@Override
	public boolean isNoOp() {
		return initialHints.equals(newHints);
	}

	private Map<AnchorKey, Point> queryHints() {
		ReadOnlyMapProperty<AnchorKey, Point> positionHintsProperty = connection
				.getRouter().positionHintsProperty();
		return new HashMap<>(positionHintsProperty.get());
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	private void setHints(Map<AnchorKey, Point> hints) {
		if (!connection.getRouter().positionHintsProperty().equals(hints)) {
			connection.getRouter().positionHintsProperty().clear();
			connection.getRouter().positionHintsProperty().putAll(hints);
		}
	}

	/**
	 * Clears the map of new hints are inserts all entries provided by the given
	 * map.
	 *
	 * @param newHints
	 *            A map that contains the new hints.
	 */
	public void setNewHints(Map<AnchorKey, Point> newHints) {
		this.newHints.clear();
		this.newHints.putAll(newHints);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (connection != null) {
			setHints(initialHints);
		}
		return Status.OK_STATUS;
	}

}
