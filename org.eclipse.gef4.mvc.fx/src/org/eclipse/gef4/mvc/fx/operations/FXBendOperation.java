/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.FXConnection;

/**
 * An {@link FXBendOperation} can be used to manipulate an {@link FXConnection}
 * in an undo-context.
 *
 * @author mwienand
 *
 */
public class FXBendOperation extends AbstractOperation {

	private final FXConnection connection;
	private List<IFXAnchor> oldAnchors;
	private List<IFXAnchor> newAnchors;

	/**
	 * Constructs an "empty" operation which will not change anything.
	 */
	public FXBendOperation() {
		this("no-op", null, null, null);
	}

	/**
	 * Constructs a new operation from the given connection. The lists of old
	 * and new {@link IFXAnchor}s are initialized based on the connection.
	 *
	 * @param connection
	 *            The {@link FXConnection} which will be modified by this
	 *            operation.
	 */
	public FXBendOperation(FXConnection connection) {
		super("bend");
		this.connection = connection;
		this.oldAnchors = new ArrayList<IFXAnchor>(connection.getAnchors());
		this.newAnchors = new ArrayList<IFXAnchor>(oldAnchors);
	}

	/**
	 * Constructs a new operation from the given values.
	 *
	 * @param label
	 *            The description for this operation.
	 * @param connection
	 *            The {@link FXConnection} which will be modified by this
	 *            operation.
	 * @param oldAnchors
	 *            List of old {@link IFXAnchor}s.
	 * @param newAnchors
	 *            List of new {@link IFXAnchor}s.
	 */
	public FXBendOperation(String label, FXConnection connection,
			List<IFXAnchor> oldAnchors, List<IFXAnchor> newAnchors) {
		super(label);
		this.connection = connection;
		if (oldAnchors == null) {
			this.oldAnchors = new ArrayList<IFXAnchor>();
		} else {
			this.oldAnchors = new ArrayList<IFXAnchor>(oldAnchors);
		}
		if (newAnchors == null) {
			this.newAnchors = new ArrayList<IFXAnchor>();
		} else {
			this.newAnchors = new ArrayList<IFXAnchor>(newAnchors);
		}
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (connection != null) {
			connection.setAnchors(newAnchors);
		}
		return Status.OK_STATUS;
	}

	public FXConnection getConnection() {
		return connection;
	}

	public List<IFXAnchor> getNewAnchors() {
		return newAnchors;
	}

	public List<IFXAnchor> getOldAnchors() {
		return oldAnchors;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	public void setNewAnchors(List<IFXAnchor> newAnchors) {
		this.newAnchors = newAnchors;
	}

	@Override
	public String toString() {
		return "FXBendOperation";
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (connection != null) {
			connection.setAnchors(oldAnchors);
		}
		return Status.OK_STATUS;
	}

}