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
import org.eclipse.gef4.fx.nodes.IFXConnection;

/**
 * An {@link FXBendOperation} can be used to manipulate an {@link IFXConnection}
 * in an undo-context.
 *
 * @author mwienand
 *
 */
public class FXBendOperation extends AbstractOperation {

	private final IFXConnection connection;
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
	 */
	public FXBendOperation(IFXConnection connection) {
		super("bend");
		this.connection = connection;
		this.oldAnchors = new ArrayList<IFXAnchor>(connection.getAnchors());
		this.newAnchors = new ArrayList<IFXAnchor>(oldAnchors);
	}

	/**
	 * Constructs a new operation from the given values.
	 *
	 * @param oldAnchors
	 *            List of old {@link IFXAnchor}s.
	 * @param newAnchors
	 *            List of new {@link IFXAnchor}s.
	 */
	public FXBendOperation(String label, IFXConnection connection,
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
			// System.out.println("--- execute bending ---");
			// System.out.println("current connection:");
			// printAnchors(connection.getAnchors());
			// System.out.println("new anchors:");
			// printAnchors(newAnchors);
			connection.setAnchors(newAnchors);
			// System.out.println("new connection:");
			// printAnchors(connection.getAnchors());
			// System.out.println();
		}
		return Status.OK_STATUS;
	}

	public IFXConnection getConnection() {
		return connection;
	}

	public List<IFXAnchor> getNewAnchors() {
		return newAnchors;
	}

	public List<IFXAnchor> getOldAnchors() {
		return oldAnchors;
	}

	// private void printAnchors(List<IFXAnchor> anchors) {
	// System.out
	// .println("  start: "
	// + anchors.get(0)
	// + (anchors.get(0).isAttached(
	// connection.getStartAnchorKey()) ? " ("
	// + anchors.get(0).getPosition(
	// connection.getStartAnchorKey()) + ")"
	// : ""));
	// for (int i = 1; i < anchors.size() - 1; i++) {
	// System.out.println("  "
	// + i
	// + ". wp: "
	// + anchors.get(i)
	// + (anchors.get(i).isAttached(
	// connection.getWayAnchorKey(i - 1)) ? " ("
	// + anchors.get(i).getPosition(
	// connection.getWayAnchorKey(i - 1)) + ")"
	// : ""));
	// }
	// System.out.println("  end: "
	// + anchors.get(anchors.size() - 1)
	// + (anchors.get(anchors.size() - 1).isAttached(
	// connection.getEndAnchorKey()) ? " ("
	// + anchors.get(anchors.size() - 1).getPosition(
	// connection.getEndAnchorKey()) + ")" : ""));
	// }

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
			// System.out.println("--- undo bending ---");
			// System.out.println("current connection:");
			// printAnchors(connection.getAnchors());
			// System.out.println("new anchors:");
			// printAnchors(newAnchors);
			connection.setAnchors(oldAnchors);
			// System.out.println("new connection:");
			// printAnchors(connection.getAnchors());
			// System.out.println();
		}
		return Status.OK_STATUS;
	}

}