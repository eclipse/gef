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

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.fx.nodes.IFXConnection;
import org.eclipse.gef4.geometry.planar.Point;

public class FXChangeWayPointsOperation extends AbstractOperation {

	private final IFXConnection connection;
	private final List<Point> oldWayPoints;
	private final List<Point> newWayPoints;

	public FXChangeWayPointsOperation(String label, IFXConnection connection,
			List<Point> oldWayPoints, List<Point> newWayPoints) {
		super(label);
		this.connection = connection;
		this.oldWayPoints = oldWayPoints;
		this.newWayPoints = newWayPoints;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		connection.setWayPoints(newWayPoints);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public String toString() {
		String str = "ChangeWayPoints:\n  from:\n";
		for (int i = 0; i < oldWayPoints.size(); i++) {
			str = str + "   - " + oldWayPoints.get(i) + "\n";
		}
		str = str + "  to:\n";
		for (int i = 0; i < newWayPoints.size(); i++) {
			str = str + "   - " + newWayPoints.get(i) + "\n";
		}
		return str;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		connection.setWayPoints(oldWayPoints);
		return Status.OK_STATUS;
	}

}
