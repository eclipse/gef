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

public class FXResizeRelocateNodeOperation extends AbstractOperation {

	private final Node visual;
	private final Point oldLocation;
	private final Dimension oldSize;
	private double dx;
	private double dy;
	private double dw;
	private double dh;

	public FXResizeRelocateNodeOperation(Node visual) {
		this(visual, 0, 0, 0, 0);
	}

	public FXResizeRelocateNodeOperation(Node visual, double dx, double dy,
			double dw, double dh) {
		this("Resize/Relocate", visual, new Point(visual.getLayoutX()
				+ visual.getLayoutBounds().getMinX(), visual.getLayoutY()
				+ visual.getLayoutBounds().getMinY()), new Dimension(visual
				.getLayoutBounds().getWidth(), visual.getLayoutBounds()
				.getHeight()), dx, dy, dw, dh);
	}

	public FXResizeRelocateNodeOperation(String label, Node visual,
			Point oldLocation, Dimension oldSize, double dx, double dy,
			double dw, double dh) {
		super(label);
		this.visual = visual;
		this.oldLocation = oldLocation.getCopy();

		if (oldSize.width + dw < 0) {
			throw new IllegalArgumentException("Cannot resize below zero.");
		}
		if (oldSize.height + dh < 0) {
			throw new IllegalArgumentException("Cannot resize below zero.");
		}

		this.oldSize = oldSize.getCopy();
		this.dx = dx;
		this.dy = dy;
		this.dw = dw;
		this.dh = dh;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		/*
		 * IMPORTANT: We may return to the initial position or size in the
		 * course of a single resize-relocate interaction. That's why the
		 * position and size is always updated here, even if the deltas are
		 * zero.
		 */
		visual.setLayoutX(oldLocation.x + dx
				- visual.getLayoutBounds().getMinX());
		visual.setLayoutY(oldLocation.y + dy
				- visual.getLayoutBounds().getMinY());
		visual.resize(oldSize.getWidth() + dw, oldSize.getHeight() + dh);
		return Status.OK_STATUS;
	}

	public double getDh() {
		return dh;
	}

	public double getDw() {
		return dw;
	}

	public double getDx() {
		return dx;
	}

	public double getDy() {
		return dy;
	}

	public Point getOldLocation() {
		return oldLocation;
	}

	public Dimension getOldSize() {
		return oldSize;
	}

	public Node getVisual() {
		return visual;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	public void setDh(double dh) {
		this.dh = dh;
	}

	public void setDw(double dw) {
		this.dw = dw;
	}

	public void setDx(double dx) {
		this.dx = dx;
	}

	public void setDy(double dy) {
		this.dy = dy;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		visual.setLayoutX(oldLocation.x - visual.getLayoutBounds().getMinX());
		visual.setLayoutY(oldLocation.y - visual.getLayoutBounds().getMinY());
		visual.resize(oldSize.getWidth(), oldSize.getHeight());
		return Status.OK_STATUS;
	}

}
