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
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.mvc.models.ViewportModel;

/**
 * The {@link FXChangeViewportOperation} can be used to alter a
 * {@link ViewportModel}. It is used by scroll/pan and zoom policies.
 *
 * @author mwienand
 *
 */
public class FXChangeViewportOperation extends AbstractOperation {

	/**
	 * The {@link ViewportModel} that is manipulated by this operation.
	 */
	protected ViewportModel viewportModel;

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
	 * {@link ViewportModel}. The current viewport values are read and used when
	 * undoing this operation.
	 *
	 * @param viewportModel
	 *            The {@link ViewportModel} which is manipulated by this
	 *            operation.
	 */
	protected FXChangeViewportOperation(ViewportModel viewportModel) {
		super("Change Viewport");
		readViewport(viewportModel);
	}

	/**
	 * Creates a new {@link FXChangeViewportOperation} to manipulate the given
	 * {@link ViewportModel}. The current viewport values are read and used when
	 * undoing this operation. The given {@link java.awt.geom.AffineTransform}
	 * will be applied when executing this operation.
	 *
	 * @param viewportModel
	 *            The {@link ViewportModel} that is manipulated.
	 * @param newTransform
	 *            The contents transformation which is applied when executing
	 *            this operation.
	 */
	public FXChangeViewportOperation(ViewportModel viewportModel,
			AffineTransform newTransform) {
		this(viewportModel);
		this.newTransform = newTransform;
	}

	/**
	 * Creates a new {@link FXChangeViewportOperation} to manipulate the given
	 * {@link ViewportModel}. The current viewport values are read and used when
	 * undoing this operation. The given translation values will be applied when
	 * executing this operation.
	 *
	 * @param viewportModel
	 *            The {@link ViewportModel} that is manipulated.
	 * @param newTx
	 *            The horizontal translation that is applied when executing this
	 *            operation.
	 * @param newTy
	 *            The vertical translation that is applied when executing this
	 *            operation.
	 */
	public FXChangeViewportOperation(ViewportModel viewportModel, double newTx,
			double newTy) {
		this(viewportModel);
		this.newTx = newTx;
		this.newTy = newTy;
	}

	/**
	 * Creates a new {@link FXChangeViewportOperation} to manipulate the given
	 * {@link ViewportModel}. The current viewport values are read and used when
	 * undoing this operation. The given translation values and contents
	 * transformation will be applied when executing this operation.
	 *
	 * @param viewportModel
	 *            The {@link ViewportModel} that is manipulated.
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
	public FXChangeViewportOperation(ViewportModel viewportModel, double newTx,
			double newTy, AffineTransform newTransform) {
		this(viewportModel);
		this.newTransform = newTransform;
		this.newTx = newTx;
		this.newTy = newTy;
	}

	/**
	 * Creates a new {@link FXChangeViewportOperation} to manipulate the given
	 * {@link ViewportModel}. The current viewport values are read and used when
	 * undoing this operation. The given translation values, dimensions, and
	 * contents transformation will be applied when executing this operation.
	 *
	 * @param viewportModel
	 *            The {@link ViewportModel} that is manipulated.
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
	public FXChangeViewportOperation(ViewportModel viewportModel, double newTx,
			double newTy, double newWidth, double newHeight,
			AffineTransform newTransform) {
		this(viewportModel);
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
		viewportModel.setWidth(newWidth);
		viewportModel.setHeight(newHeight);
		viewportModel.setContentsTransform(newTransform);
		viewportModel.setTranslateX(newTx);
		viewportModel.setTranslateY(newTy);
		return Status.OK_STATUS;
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

	/**
	 * Returns the {@link ViewportModel} which will be manipulated by this
	 * operation.
	 *
	 * @return The {@link ViewportModel} which will be manipulated by this
	 *         operation.
	 */
	public ViewportModel getViewportModel() {
		return viewportModel;
	}

	/**
	 * Returns <code>true</code> if the execution of this operation will result
	 * in a manipulation, i.e. if it will have an effect. Otherwise
	 * <code>false</code> is returned.
	 *
	 * @return <code>true</code> if the execution of this operation will result
	 *         in a manipulation, otherwise <code>false</code>.
	 */
	public boolean hasEffect() {
		if (getNewWidth() == getOldWidth() && getNewHeight() == getOldHeight()
				&& (getNewTransform() == null ? getOldTransform() == null
						: getNewTransform().equals(getOldTransform()))
				&& getNewTx() == getOldTx() && getNewTy() == getOldTy()) {
			return false;
		}
		return true;
	}

	/**
	 * Stores all relevant values in fields, so that they can be restored later.
	 *
	 * @param viewportModel
	 *            The {@link ViewportModel} from which the values are read.
	 */
	protected void readViewport(ViewportModel viewportModel) {
		this.viewportModel = viewportModel;
		oldWidth = viewportModel.getWidth();
		oldHeight = viewportModel.getHeight();
		oldTransform = viewportModel.getContentsTransform();
		oldTx = viewportModel.getTranslateX();
		oldTy = viewportModel.getTranslateY();
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
		viewportModel.setWidth(oldWidth);
		viewportModel.setHeight(oldHeight);
		viewportModel.setContentsTransform(oldTransform);
		viewportModel.setTranslateX(oldTx);
		viewportModel.setTranslateY(oldTy);
		return Status.OK_STATUS;
	}

}
