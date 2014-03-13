/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.operations;

import javafx.scene.Node;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;

public class FXResizeRelocateOperation extends AbstractOperation {

	private Node visual;
	private Point oldLocation;
	private Dimension oldSize;
	private double dx;
	private double dy;
	private double dw;
	private double dh;

	public FXResizeRelocateOperation(String label, Node visual,
			Point oldLocation, Dimension oldSize, double dx, double dy,
			double dw, double dh) {
		super(label);
		this.visual = visual;
		this.oldLocation = oldLocation;
		this.oldSize = oldSize;
		this.dx = dx;
		this.dy = dy;
		this.dw = dw;
		this.dh = dh;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (dx != 0) {
			visual.setLayoutX(oldLocation.x + dx);
		}
		if (dy != 0) {
			visual.setLayoutY(oldLocation.y + dy);
		}	
		if(dw != 0 || dh != 0){
			visual.resize(oldSize.getWidth() + dw, oldSize.getHeight() + dh);
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (dx != 0) {
			visual.setLayoutX(oldLocation.x);
		}
		if (dy != 0) {
			visual.setLayoutY(oldLocation.y);
		}	
		if(dw != 0 || dh != 0){
			visual.resize(oldSize.getWidth(), oldSize.getHeight());
		}
		return Status.OK_STATUS;
	}

}
