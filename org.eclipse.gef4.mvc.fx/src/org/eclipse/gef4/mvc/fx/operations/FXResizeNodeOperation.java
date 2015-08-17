/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - removed relocate functionality
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.geometry.planar.Dimension;

import javafx.scene.Node;

public class FXResizeNodeOperation extends AbstractOperation {

	private final Node visual;
	private final Dimension oldSize;
	private double dw;
	private double dh;

	public FXResizeNodeOperation(Node visual) {
		this(visual, 0, 0);
	}

	public FXResizeNodeOperation(Node visual, double dw, double dh) {
		this("Resize", visual,
				new Dimension(visual.getLayoutBounds().getWidth(),
						visual.getLayoutBounds().getHeight()),
				dw, dh);
	}

	/**
	 * Constructs a new {@link FXResizeNodeOperation} from the given values.
	 * Note that the <i>oldLocation</i> does include the layout-bounds minimum.
	 *
	 * @param label
	 *            Descriptive title for the operation.
	 * @param visual
	 *            The visual that is resized/relocated.
	 * @param oldSize
	 *            The old size of the visual.
	 * @param dw
	 *            The horizontal size difference.
	 * @param dh
	 *            The vertical size difference.
	 */
	public FXResizeNodeOperation(String label, Node visual, Dimension oldSize,
			double dw, double dh) {
		super(label);
		this.visual = visual;

		if (oldSize.width + dw < 0) {
			throw new IllegalArgumentException("Cannot resize below zero.");
		}
		if (oldSize.height + dh < 0) {
			throw new IllegalArgumentException("Cannot resize below zero.");
		}

		this.oldSize = oldSize.getCopy();
		this.dw = dw;
		this.dh = dh;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		visual.resize(oldSize.getWidth() + dw, oldSize.getHeight() + dh);
		return Status.OK_STATUS;
	}

	public double getDh() {
		return dh;
	}

	public double getDw() {
		return dw;
	}

	public Dimension getOldSize() {
		return oldSize;
	}

	public Node getVisual() {
		return visual;
	}

	public boolean hasEffect() {
		return dw != 0 && dh != 0;
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

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		visual.resize(oldSize.getWidth(), oldSize.getHeight());
		return Status.OK_STATUS;
	}

}
