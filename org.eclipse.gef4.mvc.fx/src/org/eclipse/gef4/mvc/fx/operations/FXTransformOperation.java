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

import javafx.scene.transform.Affine;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class FXTransformOperation extends AbstractOperation {

	protected static Affine setAffine(Affine dst, Affine src) {
		dst.setMxx(src.getMxx());
		dst.setMxy(src.getMxy());
		dst.setMxz(src.getMxz());
		dst.setMyx(src.getMyx());
		dst.setMyy(src.getMyy());
		dst.setMyz(src.getMyz());
		dst.setMzx(src.getMzx());
		dst.setMzy(src.getMzy());
		dst.setMzz(src.getMzz());
		dst.setTx(src.getTx());
		dst.setTy(src.getTy());
		dst.setTz(src.getTz());
		return dst;
	}

	private final Affine nodeTransform;
	private Affine oldTransform;

	private Affine newTransform;

	public FXTransformOperation(Affine nodeTransform) {
		super("Transform");
		this.nodeTransform = nodeTransform;
		this.oldTransform = setAffine(new Affine(), nodeTransform);
		this.newTransform = setAffine(new Affine(), nodeTransform);
	}

	public FXTransformOperation(Affine nodeTransform, Affine newTransform) {
		super("Transform");
		this.nodeTransform = nodeTransform;
		this.oldTransform = setAffine(new Affine(), nodeTransform);
		this.newTransform = newTransform;
	}

	public FXTransformOperation(Affine nodeTransform, Affine oldTransform,
			Affine newTransform) {
		super("Transform");
		this.nodeTransform = nodeTransform;
		this.oldTransform = oldTransform;
		this.newTransform = newTransform;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		setAffine(nodeTransform, newTransform);
		return Status.OK_STATUS;
	}

	public Affine getNewTransform() {
		return newTransform;
	}

	public Affine getOldTransform() {
		return oldTransform;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	public void setNewTransform(Affine newTransform) {
		this.newTransform = newTransform;
	}

	public void setOldTransform(Affine oldTransform) {
		this.oldTransform = oldTransform;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		setAffine(nodeTransform, oldTransform);
		return Status.OK_STATUS;
	}

}
