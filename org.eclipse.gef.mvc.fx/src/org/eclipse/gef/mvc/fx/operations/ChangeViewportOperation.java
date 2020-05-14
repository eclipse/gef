/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef.geometry.planar.AffineTransform;

import javafx.scene.transform.Affine;

/**
 * The {@link ChangeViewportOperation} can be used to alter the scroll offset
 * and the content transformation of an {@link InfiniteCanvas}. It is used by
 * scroll/pan and zoom policies.
 *
 * @author mwienand
 *
 */
// TODO: we should speak of 'final' instead of 'new'
public class ChangeViewportOperation extends AbstractOperation
		implements ITransactionalOperation {

	/**
	 * The {@link InfiniteCanvas} that is manipulated by this operation.
	 */
	private InfiniteCanvas canvas;

	/**
	 * The viewport width that is applied when undoing this operation.
	 */
	private double initialWidth;

	/**
	 * The viewport width that is applied when executing this operation.
	 */
	private double newWidth;

	/**
	 * The viewport height that is applied when undoing this operation.
	 */
	private double initialHeight;

	/**
	 * The viewport height that is applied when executing this operation.
	 */
	private double newHeight;

	/**
	 * The contents transformation that is applied when undoing this operation.
	 */
	private AffineTransform initialContentTransform;

	/**
	 * The contents transformation that is applied when executing this
	 * operation.
	 */
	private AffineTransform newContentTransform;

	/**
	 * The horizontal translation that is applied when undoing this operation.
	 */
	private double initialHorizontalScrollOffset;

	/**
	 * The horizontal translation that is applied when executing this operation.
	 */
	private double newHorizontalScrollOffset;

	/**
	 * The vertical translation that is applied when undoing this operation.
	 */
	private double initialVerticalScrollOffset;

	/**
	 * The vertical translation that is applied when executing this operation.
	 */
	private double newVerticalScrollOffset;

	/**
	 * Creates a new {@link ChangeViewportOperation} to manipulate the given
	 * {@link InfiniteCanvas}. The current viewport values are read and used
	 * when undoing this operation.
	 *
	 * @param canvas
	 *            The {@link InfiniteCanvas} which is manipulated by this
	 *            operation.
	 */
	public ChangeViewportOperation(InfiniteCanvas canvas) {
		super("Change Viewport");
		readViewport(canvas);
	}

	/**
	 * Creates a new {@link ChangeViewportOperation} to manipulate the given
	 * {@link InfiniteCanvas}. The current viewport values are read and used
	 * when undoing this operation. The given
	 * {@link java.awt.geom.AffineTransform} will be applied when executing this
	 * operation.
	 *
	 * @param canvas
	 *            The {@link InfiniteCanvas} that is manipulated.
	 * @param newContentTransform
	 *            The contents transformation which is applied when executing
	 *            this operation.
	 */
	public ChangeViewportOperation(InfiniteCanvas canvas,
			AffineTransform newContentTransform) {
		this(canvas);
		this.newContentTransform = newContentTransform;
	}

	/**
	 * Creates a new {@link ChangeViewportOperation} to manipulate the given
	 * {@link InfiniteCanvas}. The current viewport values are read and used
	 * when undoing this operation. The given translation values will be applied
	 * when executing this operation.
	 *
	 * @param canvas
	 *            The {@link InfiniteCanvas} that is manipulated.
	 * @param newHorizontalScrollOffset
	 *            The horizontal translation that is applied when executing this
	 *            operation.
	 * @param newVerticalScrollOffset
	 *            The vertical translation that is applied when executing this
	 *            operation.
	 */
	public ChangeViewportOperation(InfiniteCanvas canvas,
			double newHorizontalScrollOffset, double newVerticalScrollOffset) {
		this(canvas);
		this.newHorizontalScrollOffset = newHorizontalScrollOffset;
		this.newVerticalScrollOffset = newVerticalScrollOffset;
	}

	/**
	 * Creates a new {@link ChangeViewportOperation} to manipulate the given
	 * {@link InfiniteCanvas}. The current viewport values are read and used
	 * when undoing this operation. The given translation values and contents
	 * transformation will be applied when executing this operation.
	 *
	 * @param canvas
	 *            The {@link InfiniteCanvas} that is manipulated.
	 * @param newHorizontalScrollOffset
	 *            The horizontal translation that is applied when executing this
	 *            operation.
	 * @param newVerticalScrollOffset
	 *            The vertical translation that is applied when executing this
	 *            operation.
	 * @param newContentTransform
	 *            The contents transformation which is applied when executing
	 *            this operation.
	 */
	public ChangeViewportOperation(InfiniteCanvas canvas,
			double newHorizontalScrollOffset, double newVerticalScrollOffset,
			AffineTransform newContentTransform) {
		this(canvas);
		this.newContentTransform = newContentTransform;
		this.newHorizontalScrollOffset = newHorizontalScrollOffset;
		this.newVerticalScrollOffset = newVerticalScrollOffset;
	}

	/**
	 * Creates a new {@link ChangeViewportOperation} to manipulate the given
	 * {@link InfiniteCanvas}. The current viewport values are read and used
	 * when undoing this operation. The given translation values, dimensions,
	 * and contents transformation will be applied when executing this
	 * operation.
	 *
	 * @param canvas
	 *            The {@link InfiniteCanvas} that is manipulated.
	 * @param newHorizontalScrollOffset
	 *            The horizontal translation that is applied when executing this
	 *            operation.
	 * @param newVerticalScrollOffset
	 *            The vertical translation that is applied when executing this
	 *            operation.
	 * @param newWidth
	 *            The viewport width that is applied when executing this
	 *            operation.
	 * @param newHeight
	 *            The viewport height that is applied when executing this
	 *            operation.
	 * @param newContentTransform
	 *            The contents transformation which is applied when executing
	 *            this operation.
	 */
	public ChangeViewportOperation(InfiniteCanvas canvas,
			double newHorizontalScrollOffset, double newVerticalScrollOffset,
			double newWidth, double newHeight,
			AffineTransform newContentTransform) {
		this(canvas);
		this.newWidth = newWidth;
		this.newHeight = newHeight;
		this.newContentTransform = newContentTransform;
		this.newHorizontalScrollOffset = newHorizontalScrollOffset;
		this.newVerticalScrollOffset = newVerticalScrollOffset;
	}

	/**
	 * Concatenates the given {@link java.awt.geom.AffineTransform} to the
	 * contents transformation that will be applied when executing this
	 * operation.
	 *
	 * @param t
	 *            The {@link java.awt.geom.AffineTransform} which is
	 *            concatenated to the transformation that will be applied when
	 *            executing this operation.
	 */
	public void concatenateToNewContentTransform(AffineTransform t) {
		newContentTransform.concatenate(t);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (canvas.getPrefWidth() != newWidth) {
			canvas.setPrefWidth(newWidth);
		}
		if (canvas.getPrefHeight() != newHeight) {
			canvas.setPrefHeight(newHeight);
		}
		Affine newContentAffine = Geometry2FX.toFXAffine(newContentTransform);
		if (!canvas.getContentTransform().equals(newContentAffine)) {
			canvas.setContentTransform(newContentAffine);
		}
		if (canvas.getHorizontalScrollOffset() != newHorizontalScrollOffset) {
			canvas.setHorizontalScrollOffset(newHorizontalScrollOffset);
		}
		if (canvas.getVerticalScrollOffset() != newVerticalScrollOffset) {
			canvas.setVerticalScrollOffset(newVerticalScrollOffset);
		}
		return Status.OK_STATUS;
	}

	/**
	 * Returns the {@link InfiniteCanvas} that is manipulated by this operation.
	 *
	 * @return The {@link InfiniteCanvas} that is manipulated by this operation.
	 */
	public InfiniteCanvas getInfiniteCanvas() {
		return canvas;
	}

	/**
	 * Returns the contents transformation that will be applied when undoing
	 * this operation.
	 *
	 * @return The contents transformation that will be applied when undoing
	 *         this operation.
	 */
	public AffineTransform getInitialContentTransform() {
		return initialContentTransform;
	}

	/**
	 * Returns the viewport height that will be applied when undoing this
	 * operation.
	 *
	 * @return The viewport height that will be applied when undoing this
	 *         operation.
	 */
	public double getInitialHeight() {
		return initialHeight;
	}

	/**
	 * Returns the horizontal translation that will be applied when undoing this
	 * operation.
	 *
	 * @return The horizontal translation that will be applied when undoing this
	 *         operation.
	 */
	public double getInitialHorizontalScrollOffset() {
		return initialHorizontalScrollOffset;
	}

	/**
	 * Returns the vertical translation that will be applied when undoing this
	 * operation.
	 *
	 * @return The vertical translation that will be applied when undoing this
	 *         operation.
	 */
	public double getInitialVerticalScrollOffset() {
		return initialVerticalScrollOffset;
	}

	/**
	 * Returns the viewport width that will be applied when undoing this
	 * operation.
	 *
	 * @return The viewport width that will be applied when undoing this
	 *         operation.
	 */
	public double getInitialWidth() {
		return initialWidth;
	}

	/**
	 * Returns the contents transformation that will be applied when executing
	 * this operation.
	 *
	 * @return The contents transformation that will be applied when executing
	 *         this operation.
	 */
	public AffineTransform getNewContentTransform() {
		return newContentTransform;
	}

	/**
	 * Returns the viewport height that will be applied when executing this
	 * operation.
	 *
	 * @return The viewport height that will be applied when executing this
	 *         operation.
	 */
	public double getNewHeight() {
		return newHeight;
	}

	/**
	 * Returns the horizontal translation that will be applied when executing
	 * this operation.
	 *
	 * @return The horizontal translation that will be applied when executing
	 *         this operation.
	 */
	public double getNewHorizontalScrollOffset() {
		return newHorizontalScrollOffset;
	}

	/**
	 * Returns the vertical translation that will be applied when executing this
	 * operation.
	 *
	 * @return The vertical translation that will be applied when executing this
	 *         operation.
	 */
	public double getNewVerticalScrollOffset() {
		return newVerticalScrollOffset;
	}

	/**
	 * Returns the viewport width that will be applied when executing this
	 * operation.
	 *
	 * @return The viewport width that will be applied when executing this
	 *         operation.
	 */
	public double getNewWidth() {
		return newWidth;
	}

	@Override
	public boolean isContentRelevant() {
		return false;
	}

	@Override
	public boolean isNoOp() {
		return getNewWidth() == getInitialWidth()
				&& getNewHeight() == getInitialHeight()
				&& (getNewContentTransform() == null
						? getInitialContentTransform() == null
						: getNewContentTransform()
								.equals(getInitialContentTransform()))
				&& getNewHorizontalScrollOffset() == getInitialHorizontalScrollOffset()
				&& getNewVerticalScrollOffset() == getInitialVerticalScrollOffset();
	}

	/**
	 * Stores all relevant viewport values in fields, so that they can be
	 * restored later.
	 *
	 * @param canvas
	 *            The {@link InfiniteCanvas} from which the values are read.
	 */
	protected void readViewport(InfiniteCanvas canvas) {
		this.canvas = canvas;
		initialWidth = canvas.getWidth();
		initialHeight = canvas.getHeight();
		initialContentTransform = FX2Geometry
				.toAffineTransform(canvas.getContentTransform());
		initialHorizontalScrollOffset = canvas.getHorizontalScrollOffset();
		initialVerticalScrollOffset = canvas.getVerticalScrollOffset();
		// use old values for new values per default
		newWidth = initialWidth;
		newHeight = initialHeight;
		newContentTransform = initialContentTransform.getCopy();
		newHorizontalScrollOffset = initialHorizontalScrollOffset;
		newVerticalScrollOffset = initialVerticalScrollOffset;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	/**
	 * Sets the initial content transform before applying the new value.
	 *
	 * @param initialContentTransform
	 *            The initialContentTransform to set.
	 */
	public void setInitialContentTransform(
			AffineTransform initialContentTransform) {
		this.initialContentTransform = initialContentTransform;
	}

	/**
	 * Sets the initial height before applying the new value.
	 *
	 * @param initialHeight
	 *            The initialHeight to set.
	 */
	public void setInitialHeight(double initialHeight) {
		this.initialHeight = initialHeight;
	}

	/**
	 * Sets the initial horizontal scroll offset before applying the new value.
	 *
	 * @param initialHorizontalScrollOffset
	 *            The initialHorizontalScrollOffset to set.
	 */
	public void setInitialHorizontalScrollOffset(
			double initialHorizontalScrollOffset) {
		this.initialHorizontalScrollOffset = initialHorizontalScrollOffset;
	}

	/**
	 * Sets the initial vertical scroll offset before applying the new value.
	 *
	 * @param initialVerticalScrollOffset
	 *            The initialVerticalScrollOffset to set.
	 */
	public void setInitialVerticalScrollOffset(
			double initialVerticalScrollOffset) {
		this.initialVerticalScrollOffset = initialVerticalScrollOffset;
	}

	/**
	 * Sets the initial width before applying the new value.
	 *
	 * @param initialWidth
	 *            The initialWidth to set.
	 */
	public void setInitialWidth(double initialWidth) {
		this.initialWidth = initialWidth;
	}

	/**
	 * Sets the contents transformation that will be applied when executing this
	 * operation to the given value.
	 *
	 * @param newContentTransform
	 *            The contents transformation to apply when executing this
	 *            operation.
	 */
	public void setNewContentTransform(AffineTransform newContentTransform) {
		this.newContentTransform = newContentTransform;
	}

	/**
	 * Sets the viewport height that will be applied when executing this
	 * operation to the given value.
	 *
	 * @param newHeight
	 *            The viewport height to apply when executing this operation.
	 */
	public void setNewHeight(double newHeight) {
		this.newHeight = newHeight;
	}

	/**
	 * Sets the horizontal translation that will be applied when executing this
	 * operation to the given value.
	 *
	 * @param newHorizontalScrollOffset
	 *            The horizontal translation to apply when executing this
	 *            operation.
	 */
	public void setNewHorizontalScrollOffset(double newHorizontalScrollOffset) {
		this.newHorizontalScrollOffset = newHorizontalScrollOffset;
	}

	/**
	 * Sets the vertical translation that will be applied when executing this
	 * operation to the given value.
	 *
	 * @param newVerticalScrollOffset
	 *            The vertical translation to apply when executing this
	 *            operation.
	 */
	public void setNewVerticalScrollOffset(double newVerticalScrollOffset) {
		this.newVerticalScrollOffset = newVerticalScrollOffset;
	}

	/**
	 * Sets the viewport width that will be applied when executing this
	 * operation to the given value.
	 *
	 * @param newWidth
	 *            The viewport width to apply when executing this operation.
	 */
	public void setNewWidth(double newWidth) {
		this.newWidth = newWidth;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (canvas.getPrefWidth() != initialWidth) {
			canvas.setPrefWidth(initialWidth);
		}
		if (canvas.getPrefHeight() != initialHeight) {
			canvas.setPrefHeight(initialHeight);
		}
		Affine initialContentAffine = Geometry2FX
				.toFXAffine(initialContentTransform);
		if (!canvas.getContentTransform().equals(initialContentAffine)) {
			canvas.setContentTransform(initialContentAffine);
		}
		if (canvas
				.getHorizontalScrollOffset() != initialHorizontalScrollOffset) {
			canvas.setHorizontalScrollOffset(initialHorizontalScrollOffset);
		}
		if (canvas.getVerticalScrollOffset() != initialVerticalScrollOffset) {
			canvas.setVerticalScrollOffset(initialVerticalScrollOffset);
		}
		return Status.OK_STATUS;
	}

}
