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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.fx.nodes.InfiniteCanvas;
import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;

/**
 * The {@link FXChangeViewportOperation} can be used to alter the scroll offset
 * and the content transformation of an {@link InfiniteCanvas}. It is used by
 * scroll/pan and zoom policies.
 *
 * @author mwienand
 *
 */
public class FXChangeViewportOperation extends AbstractOperation
		implements ITransactionalOperation {

	/**
	 * The {@link InfiniteCanvas} that is manipulated by this operation.
	 */
	protected InfiniteCanvas canvas;

	/**
	 * The viewport width that is applied when undoing this operation.
	 */
	protected double oldWidth;

	/**
	 * The viewport width that is applied when executing this operation.
	 */
	protected double newWidth;

	/**
	 * The viewport height that is applied when undoing this operation.
	 */
	protected double oldHeight;

	/**
	 * The viewport height that is applied when executing this operation.
	 */
	protected double newHeight;

	/**
	 * The contents transformation that is applied when undoing this operation.
	 */
	protected AffineTransform oldTransform;

	/**
	 * The contents transformation that is applied when executing this
	 * operation.
	 */
	protected AffineTransform newTransform;

	/**
	 * The horizontal translation that is applied when undoing this operation.
	 */
	protected double oldTx;

	/**
	 * The horizontal translation that is applied when executing this operation.
	 */
	protected double newTx;

	/**
	 * The vertical translation that is applied when undoing this operation.
	 */
	protected double oldTy;

	/**
	 * The vertical translation that is applied when executing this operation.
	 */
	protected double newTy;

	/**
	 * Creates a new {@link FXChangeViewportOperation} to manipulate the given
	 * {@link InfiniteCanvas}. The current viewport values are read and used
	 * when undoing this operation.
	 *
	 * @param canvas
	 *            The {@link InfiniteCanvas} which is manipulated by this
	 *            operation.
	 */
	protected FXChangeViewportOperation(InfiniteCanvas canvas) {
		super("Change Viewport");
		readViewport(canvas);
	}

	/**
	 * Creates a new {@link FXChangeViewportOperation} to manipulate the given
	 * {@link InfiniteCanvas}. The current viewport values are read and used
	 * when undoing this operation. The given
	 * {@link java.awt.geom.AffineTransform} will be applied when executing this
	 * operation.
	 *
	 * @param canvas
	 *            The {@link InfiniteCanvas} that is manipulated.
	 * @param newTransform
	 *            The contents transformation which is applied when executing
	 *            this operation.
	 */
	public FXChangeViewportOperation(InfiniteCanvas canvas,
			AffineTransform newTransform) {
		this(canvas);
		this.newTransform = newTransform;
	}

	/**
	 * Creates a new {@link FXChangeViewportOperation} to manipulate the given
	 * {@link InfiniteCanvas}. The current viewport values are read and used
	 * when undoing this operation. The given translation values will be applied
	 * when executing this operation.
	 *
	 * @param canvas
	 *            The {@link InfiniteCanvas} that is manipulated.
	 * @param newTx
	 *            The horizontal translation that is applied when executing this
	 *            operation.
	 * @param newTy
	 *            The vertical translation that is applied when executing this
	 *            operation.
	 */
	public FXChangeViewportOperation(InfiniteCanvas canvas, double newTx,
			double newTy) {
		this(canvas);
		this.newTx = newTx;
		this.newTy = newTy;
	}

	/**
	 * Creates a new {@link FXChangeViewportOperation} to manipulate the given
	 * {@link InfiniteCanvas}. The current viewport values are read and used
	 * when undoing this operation. The given translation values and contents
	 * transformation will be applied when executing this operation.
	 *
	 * @param canvas
	 *            The {@link InfiniteCanvas} that is manipulated.
	 * @param newTx
	 *            The horizontal translation that is applied when executing this
	 *            operation.
	 * @param newTy
	 *            The vertical translation that is applied when executing this
	 *            operation.
	 * @param newTransform
	 *            The contents transformation which is applied when executing
	 *            this operation.
	 */
	public FXChangeViewportOperation(InfiniteCanvas canvas, double newTx,
			double newTy, AffineTransform newTransform) {
		this(canvas);
		this.newTransform = newTransform;
		this.newTx = newTx;
		this.newTy = newTy;
	}

	/**
	 * Creates a new {@link FXChangeViewportOperation} to manipulate the given
	 * {@link InfiniteCanvas}. The current viewport values are read and used
	 * when undoing this operation. The given translation values, dimensions,
	 * and contents transformation will be applied when executing this
	 * operation.
	 *
	 * @param canvas
	 *            The {@link InfiniteCanvas} that is manipulated.
	 * @param newTx
	 *            The horizontal translation that is applied when executing this
	 *            operation.
	 * @param newTy
	 *            The vertical translation that is applied when executing this
	 *            operation.
	 * @param newWidth
	 *            The viewport width that is applied when executing this
	 *            operation.
	 * @param newHeight
	 *            The viewport height that is applied when executing this
	 *            operation.
	 * @param newTransform
	 *            The contents transformation which is applied when executing
	 *            this operation.
	 */
	public FXChangeViewportOperation(InfiniteCanvas canvas, double newTx,
			double newTy, double newWidth, double newHeight,
			AffineTransform newTransform) {
		this(canvas);
		this.newWidth = newWidth;
		this.newHeight = newHeight;
		this.newTransform = newTransform;
		this.newTx = newTx;
		this.newTy = newTy;
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
	public void concatenateToNewTransform(AffineTransform t) {
		newTransform.concatenate(t);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		canvas.setPrefWidth(newWidth);
		canvas.setPrefHeight(newHeight);
		canvas.setContentTransform(Geometry2JavaFX.toFXAffine(newTransform));
		canvas.setHorizontalScrollOffset(newTx);
		canvas.setVerticalScrollOffset(newTy);
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
	 * Returns the contents transformation that will be applied when executing
	 * this operation.
	 *
	 * @return The contents transformation that will be applied when executing
	 *         this operation.
	 */
	public AffineTransform getNewTransform() {
		return newTransform;
	}

	/**
	 * Returns the horizontal translation that will be applied when executing
	 * this operation.
	 *
	 * @return The horizontal translation that will be applied when executing
	 *         this operation.
	 */
	public double getNewTx() {
		return newTx;
	}

	/**
	 * Returns the vertical translation that will be applied when executing this
	 * operation.
	 *
	 * @return The vertical translation that will be applied when executing this
	 *         operation.
	 */
	public double getNewTy() {
		return newTy;
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

	/**
	 * Returns the viewport height that will be applied when undoing this
	 * operation.
	 *
	 * @return The viewport height that will be applied when undoing this
	 *         operation.
	 */
	public double getOldHeight() {
		return oldHeight;
	}

	/**
	 * Returns the contents transformation that will be applied when undoing
	 * this operation.
	 *
	 * @return The contents transformation that will be applied when undoing
	 *         this operation.
	 */
	public AffineTransform getOldTransform() {
		return oldTransform;
	}

	/**
	 * Returns the horizontal translation that will be applied when undoing this
	 * operation.
	 *
	 * @return The horizontal translation that will be applied when undoing this
	 *         operation.
	 */
	public double getOldTx() {
		return oldTx;
	}

	/**
	 * Returns the vertical translation that will be applied when undoing this
	 * operation.
	 *
	 * @return The vertical translation that will be applied when undoing this
	 *         operation.
	 */
	public double getOldTy() {
		return oldTy;
	}

	/**
	 * Returns the viewport width that will be applied when undoing this
	 * operation.
	 *
	 * @return The viewport width that will be applied when undoing this
	 *         operation.
	 */
	public double getOldWidth() {
		return oldWidth;
	}

	@Override
	public boolean isNoOp() {
		return getNewWidth() == getOldWidth()
				&& getNewHeight() == getOldHeight()
				&& (getNewTransform() == null ? getOldTransform() == null
						: getNewTransform().equals(getOldTransform()))
				&& getNewTx() == getOldTx() && getNewTy() == getOldTy();
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
		oldWidth = canvas.getWidth();
		oldHeight = canvas.getHeight();
		oldTransform = JavaFX2Geometry
				.toAffineTransform(canvas.getContentTransform());
		oldTx = canvas.getHorizontalScrollOffset();
		oldTy = canvas.getVerticalScrollOffset();
		// use old values for new values per default
		newWidth = oldWidth;
		newHeight = oldHeight;
		newTransform = oldTransform.getCopy();
		newTx = oldTx;
		newTy = oldTy;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
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
	 * Sets the contents transformation that will be applied when executing this
	 * operation to the given value.
	 *
	 * @param newTransform
	 *            The contents transformation to apply when executing this
	 *            operation.
	 */
	public void setNewTransform(AffineTransform newTransform) {
		this.newTransform = newTransform;
	}

	/**
	 * Sets the horizontal translation that will be applied when executing this
	 * operation to the given value.
	 *
	 * @param newTx
	 *            The horizontal translation to apply when executing this
	 *            operation.
	 */
	public void setNewTx(double newTx) {
		this.newTx = newTx;
	}

	/**
	 * Sets the vertical translation that will be applied when executing this
	 * operation to the given value.
	 *
	 * @param newTy
	 *            The vertical translation to apply when executing this
	 *            operation.
	 */
	public void setNewTy(double newTy) {
		this.newTy = newTy;
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
		canvas.setPrefWidth(oldWidth);
		canvas.setPrefHeight(oldHeight);
		canvas.setContentTransform(Geometry2JavaFX.toFXAffine(oldTransform));
		canvas.setHorizontalScrollOffset(oldTx);
		canvas.setVerticalScrollOffset(oldTy);
		return Status.OK_STATUS;
	}

}
