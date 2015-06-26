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

	protected ViewportModel viewportModel;
	protected double oldWidth;
	protected double newWidth;
	protected double oldHeight;
	protected double newHeight;
	protected AffineTransform oldTransform;
	protected AffineTransform newTransform;
	protected double oldTx;
	protected double newTx;
	protected double oldTy;
	protected double newTy;

	protected FXChangeViewportOperation(ViewportModel viewportModel) {
		super("ChangeViewport");
		readViewport(viewportModel);
	}

	public FXChangeViewportOperation(ViewportModel viewportModel,
			AffineTransform newTransform) {
		this(viewportModel);
		this.newTransform = newTransform;
	}

	public FXChangeViewportOperation(ViewportModel viewportModel,
			AffineTransform newTransform, double newTx, double newTy) {
		this(viewportModel);
		this.newTransform = newTransform;
		this.newTx = newTx;
		this.newTy = newTy;
	}

	public FXChangeViewportOperation(ViewportModel viewportModel,
			AffineTransform newTransform, double newTx, double newTy,
			double newWidth, double newHeight) {
		this(viewportModel);
		this.newWidth = newWidth;
		this.newHeight = newHeight;
		this.newTransform = newTransform;
		this.newTx = newTx;
		this.newTy = newTy;
	}

	public FXChangeViewportOperation(ViewportModel viewportModel, double newTx,
			double newTy) {
		this(viewportModel);
		this.newTx = newTx;
		this.newTy = newTy;
	}

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

	public double getNewHeight() {
		return newHeight;
	}

	public AffineTransform getNewTransform() {
		return newTransform;
	}

	public double getNewTx() {
		return newTx;
	}

	public double getNewTy() {
		return newTy;
	}

	public double getNewWidth() {
		return newWidth;
	}

	public double getOldHeight() {
		return oldHeight;
	}

	public AffineTransform getOldTransform() {
		return oldTransform;
	}

	public double getOldTx() {
		return oldTx;
	}

	public double getOldTy() {
		return oldTy;
	}

	public double getOldWidth() {
		return oldWidth;
	}

	public ViewportModel getViewportModel() {
		return viewportModel;
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

	public void setNewHeight(double newHeight) {
		this.newHeight = newHeight;
	}

	public void setNewTransform(AffineTransform newTransform) {
		this.newTransform = newTransform;
	}

	public void setNewTx(double newTx) {
		this.newTx = newTx;
	}

	public void setNewTy(double newTy) {
		this.newTy = newTy;
	}

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
