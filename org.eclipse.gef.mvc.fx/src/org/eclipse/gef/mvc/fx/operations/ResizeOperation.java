/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - removed relocate functionality
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.mvc.fx.parts.IResizableContentPart;

import javafx.scene.Node;

/**
 * The {@link ResizeOperation} can be used to alter the size of a {@link Node
 * visual}.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class ResizeOperation extends AbstractOperation
		implements ITransactionalOperation {

	private final IResizableContentPart<? extends Node> resizablePart;
	private final Dimension initialSize;
	private double dw;
	private double dh;

	/**
	 * Constructs a new {@link ResizeOperation} for the manipulation of the
	 * given {@link Node}.
	 *
	 * @param resizablePart
	 *            The {@link Node} that is manipulated by this operation.
	 */
	public ResizeOperation(
			IResizableContentPart<? extends Node> resizablePart) {
		this(resizablePart, 0, 0);
	}

	/**
	 * Constructs a new {@link ResizeOperation} for the manipulation of the
	 * given {@link Node}. The given delta width and height will be applied when
	 * executing this operation.
	 *
	 * @param resizablePart
	 *            The {@link Node} that is manipulated by this operation.
	 * @param dw
	 *            The delta width that is applied when executing this operation.
	 * @param dh
	 *            The delta height that is applied when executing this
	 *            operation.
	 */
	public ResizeOperation(IResizableContentPart<? extends Node> resizablePart,
			double dw, double dh) {
		this("Resize", resizablePart, resizablePart.getVisualSize(), dw, dh);
	}

	/**
	 * Constructs a new {@link ResizeOperation} from the given values. Note that
	 * the <i>oldLocation</i> does include the layout-bounds minimum.
	 *
	 * @param label
	 *            Descriptive title for the operation.
	 * @param resizablePart
	 *            The visual that is resized/relocated.
	 * @param initialSize
	 *            The old size of the visual.
	 * @param dw
	 *            The horizontal size difference.
	 * @param dh
	 *            The vertical size difference.
	 */
	public ResizeOperation(String label,
			IResizableContentPart<? extends Node> resizablePart,
			Dimension initialSize, double dw, double dh) {
		super(label);
		this.resizablePart = resizablePart;

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
		Dimension newSize = new Dimension(initialSize.getWidth() + dw,
				initialSize.getHeight() + dh);
		if (!resizablePart.getVisualSize().equals(newSize)) {
			resizablePart.setVisualSize(newSize);
		}
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
	 * Returns the {@link IResizableContentPart} that is resized by this
	 * operation.
	 *
	 * @return The {@link IResizableContentPart} that is resized by this
	 *         operation.
	 */
	public IResizableContentPart<? extends Node> getResizablePart() {
		return resizablePart;
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
		if (!resizablePart.getVisualSize().equals(initialSize)) {
			resizablePart.setVisualSize(initialSize);
		}
		return Status.OK_STATUS;
	}

}
