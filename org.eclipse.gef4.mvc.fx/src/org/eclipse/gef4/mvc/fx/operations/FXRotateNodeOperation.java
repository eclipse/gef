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
package org.eclipse.gef4.mvc.fx.operations;

import javafx.scene.Node;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class FXRotateNodeOperation extends AbstractOperation {

	private Node visual;
	private double oldDeg;
	private double newDeg;

	protected FXRotateNodeOperation(Node visual) {
		super("RotateNode");
		this.visual = visual;
		this.oldDeg = visual.getRotate();
	}

	public FXRotateNodeOperation(Node visual, double newDeg) {
		this(visual);
		this.newDeg = newDeg;
	}

	public FXRotateNodeOperation(Node visual, double oldDeg, double newDeg) {
		this(visual);
		this.oldDeg = oldDeg;
		this.newDeg = newDeg;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		visual.setRotate(newDeg);
		return Status.OK_STATUS;
	}

	public double getNewDeg() {
		return newDeg;
	}

	public double getOldDeg() {
		return oldDeg;
	}

	public Node getVisual() {
		return visual;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	public void setNewDeg(double newDeg) {
		this.newDeg = newDeg;
	}

	public void setOldDeg(double oldDeg) {
		this.oldDeg = oldDeg;
	}

	public void setVisual(Node visual) {
		this.visual = visual;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		visual.setRotate(oldDeg);
		return Status.OK_STATUS;
	}

}
