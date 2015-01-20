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

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class FXRotateNodeOperation extends AbstractOperation {

	private Node visual;
	private double oldDeg;
	private double oldPivotX;
	private double oldPivotY;
	private double newDeg;
	private double newPivotX;
	private double newPivotY;

	public FXRotateNodeOperation(Node visual) {
		super("RotateNode");
		this.visual = visual;
		oldDeg = 0;
		oldPivotX = 0;
		oldPivotY = 0;
		// read old from current transforms
		ObservableList<Transform> transforms = visual.getTransforms();
		if (transforms.size() > 0) {
			Transform lastTx = transforms.get(transforms.size() - 1);
			if (lastTx instanceof Rotate) {
				Rotate rot = (Rotate) lastTx;
				oldDeg = rot.getAngle();
				oldPivotX = rot.getPivotX();
				oldPivotY = rot.getPivotY();
			}
		}
		this.newDeg = oldDeg;
		this.newPivotX = oldPivotX;
		this.newPivotY = oldPivotY;
	}

	public FXRotateNodeOperation(Node visual, double newDeg, double newPivotX,
			double newPivotY) {
		this(visual);
		this.newDeg = newDeg;
		this.newPivotX = newPivotX;
		this.newPivotY = newPivotY;
	}

	public FXRotateNodeOperation(Node visual, double oldDeg, double oldPivotX,
			double oldPivotY, double newDeg, double newPivotX, double newPivotY) {
		super("RotateNode");
		this.visual = visual;
		this.oldDeg = oldDeg;
		this.oldPivotX = oldPivotX;
		this.oldPivotY = oldPivotY;
		this.newDeg = newDeg;
		this.newPivotX = newPivotX;
		this.newPivotY = newPivotY;
	}

	private void applyRotation(double angle, double pivotX, double pivotY) {
		Rotate rot = null;
		ObservableList<Transform> transforms = visual.getTransforms();
		if (transforms.size() > 0) {
			Transform lastTx = transforms.get(transforms.size() - 1);
			if (lastTx instanceof Rotate) {
				rot = (Rotate) lastTx;
			}
		}
		if (rot == null) {
			rot = new Rotate();
			transforms.add(rot);
		}
		rot.setAngle(angle);
		rot.setPivotX(pivotX);
		rot.setPivotY(pivotY);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		applyRotation(newDeg, newPivotX, newPivotY);
		return Status.OK_STATUS;
	}

	public double getNewDeg() {
		return newDeg;
	}

	public double getNewPivotX() {
		return newPivotX;
	}

	public double getNewPivotY() {
		return newPivotY;
	}

	public double getOldDeg() {
		return oldDeg;
	}

	public double getOldPivotX() {
		return oldPivotX;
	}

	public double getOldPivotY() {
		return oldPivotY;
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

	public void setNewPivotX(double newPivotX) {
		this.newPivotX = newPivotX;
	}

	public void setNewPivotY(double newPivotY) {
		this.newPivotY = newPivotY;
	}

	public void setOldDeg(double oldDeg) {
		this.oldDeg = oldDeg;
	}

	public void setOldPivotX(double oldPivotX) {
		this.oldPivotX = oldPivotX;
	}

	public void setOldPivotY(double oldPivotY) {
		this.oldPivotY = oldPivotY;
	}

	public void setVisual(Node visual) {
		this.visual = visual;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		applyRotation(oldDeg, oldPivotX, oldPivotY);
		return Status.OK_STATUS;
	}

}