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

import javafx.scene.transform.Affine;

public class FXTransformOperation extends AbstractOperation {

	protected static boolean equals(Affine a1, Affine a2) {
		// Affine does not properly implement equals, so we have to implement
		// that here
		return a1.getMxx() == a2.getMxx() && a1.getMxy() == a2.getMxy()
				&& a1.getMxz() == a2.getMxz() && a1.getMyx() == a2.getMyx()
				&& a1.getMyy() == a2.getMyy() && a1.getMyz() == a2.getMyz()
				&& a1.getMzx() == a2.getMzx() && a1.getMzy() == a2.getMzy()
				&& a1.getMzz() == a2.getMzz() && a1.getTx() == a2.getTx()
				&& a1.getTy() == a2.getTy() && a1.getTz() == a2.getTz();
	}

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

	public boolean hasEffect() {
		return !equals(newTransform, oldTransform);
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
