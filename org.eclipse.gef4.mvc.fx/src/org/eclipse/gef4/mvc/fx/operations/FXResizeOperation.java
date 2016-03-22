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
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;

import javafx.scene.Node;

/**
 * The {@link FXResizeOperation} can be used to alter the size of a
 * {@link Node visual}.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class FXResizeOperation extends AbstractOperation
		implements ITransactionalOperation {

	private final Node visual;
	private final Dimension initialSize;
	private double dw;
	private double dh;

	/**
	 * Constructs a new {@link FXResizeOperation} for the manipulation of
	 * the given {@link Node}.
	 *
	 * @param visual
	 *            The {@link Node} that is manipulated by this operation.
	 */
	public FXResizeOperation(Node visual) {
		this(visual, 0, 0);
	}

	/**
	 * Constructs a new {@link FXResizeOperation} for the manipulation of
	 * the given {@link Node}. The given delta width and height will be applied
	 * when executing this operation.
	 *
	 * @param visual
	 *            The {@link Node} that is manipulated by this operation.
	 * @param dw
	 *            The delta width that is applied when executing this operation.
	 * @param dh
	 *            The delta height that is applied when executing this
	 *            operation.
	 */
	public FXResizeOperation(Node visual, double dw, double dh) {
		this("Resize", visual,
				new Dimension(visual.getLayoutBounds().getWidth(),
						visual.getLayoutBounds().getHeight()),
				dw, dh);
	}

	/**
	 * Constructs a new {@link FXResizeOperation} from the given values.
	 * Note that the <i>oldLocation</i> does include the layout-bounds minimum.
	 *
	 * @param label
	 *            Descriptive title for the operation.
	 * @param visual
	 *            The visual that is resized/relocated.
	 * @param initialSize
	 *            The old size of the visual.
	 * @param dw
	 *            The horizontal size difference.
	 * @param dh
	 *            The vertical size difference.
	 */
	public FXResizeOperation(String label, Node visual,
			Dimension initialSize, double dw, double dh) {
		super(label);
		this.visual = visual;

		if (initialSize.width + dw < 0) {
			throw new IllegalArgumentException("Cannot resize below zero.");
		}
		if (initialSize.height + dh < 0) {
			throw new IllegalArgumentException("Cannot resize below zero.");
		}

		this.initialSize = initialSize.getCopy();
		this.dw = dw;
		this.dh = dh;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		visual.resize(initialSize.getWidth() + dw,
				initialSize.getHeight() + dh);
		return Status.OK_STATUS;
	}

	/**
	 * Returns the delta height that is applied when executing this operation.
	 *
	 * @return The delta height that is applied when executing this operation.
	 */
	public double getDh() {
		return dh;
	}

	/**
	 * Returns the delta width that is applied when executing this operation.
	 *
	 * @return The delta width that is applied when executing this operation.
	 */
	public double getDw() {
		return dw;
	}

	/**
	 * Returns the dimensions that are applied when undoing this operation.
	 *
	 * @return The dimensions that are applied when undoing this operation.
	 */
	public Dimension getInitialSize() {
		return initialSize;
	}

	/**
	 * Returns the {@link Node} that is manipulated by this operation.
	 *
	 * @return The {@link Node} that is manipulated by this operation.
	 */
	public Node getVisual() {
		return visual;
	}

	@Override
	public boolean isContentRelevant() {
		return false;
	}

	@Override
	public boolean isNoOp() {
		return dw == 0 && dh == 0;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	/**
	 * Sets the delta height that will be applied when executing this operation
	 * to the given value.
	 *
	 * @param dh
	 *            The delta height that will be applied when executing this
	 *            operation.
	 */
	public void setDh(double dh) {
		this.dh = dh;
	}

	/**
	 * Sets the delta width that will be applied when executing this operation
	 * to the given value.
	 *
	 * @param dw
	 *            The delta width that will be applied when executing this
	 *            operation.
	 */
	public void setDw(double dw) {
		this.dw = dw;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		visual.resize(initialSize.getWidth(), initialSize.getHeight());
		return Status.OK_STATUS;
	}

}
