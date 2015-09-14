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
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;

import javafx.scene.Node;
import javafx.scene.transform.Affine;

/**
 * The {@link FXTransformOperation} can be used to change an {@link Affine}, for
 * example, one that is contained within the transformations list of a
 * {@link Node} to transform that {@link Node}.
 *
 * @author mwienand
 *
 */
public class FXTransformOperation extends AbstractOperation
		implements ITransactionalOperation {

	/**
	 * Returns <code>true</code> if the given {@link Affine}s are equal.
	 * Otherwise returns <code>false</code>.
	 *
	 * @param a1
	 *            The first operand.
	 * @param a2
	 *            The second operand.
	 * @return <code>true</code> if the given {@link Affine}s are equal,
	 *         otherwise <code>false</code>.
	 */
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

	/**
	 * Assigns the transformation values of the <i>src</i> {@link Affine} to the
	 * <i>dst</i> {@link Affine}.
	 *
	 * @param dst
	 *            The destination {@link Affine}.
	 * @param src
	 *            The source {@link Affine}.
	 * @return The destination {@link Affine} for convenience.
	 */
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

	/**
	 * Constructs a new {@link FXTransformOperation} to change the given
	 * <i>nodeTransform</i>.
	 *
	 * @param nodeTransform
	 *            The {@link Affine} that will be changed by this operation.
	 */
	public FXTransformOperation(Affine nodeTransform) {
		super("Transform");
		this.nodeTransform = nodeTransform;
		this.oldTransform = setAffine(new Affine(), nodeTransform);
		this.newTransform = setAffine(new Affine(), nodeTransform);
	}

	/**
	 * Constructs a new {@link FXTransformOperation} to change the given
	 * <i>nodeTransform</i>. The given <i>newTransform</i> will be applied to
	 * the <i>nodeTransform</i> upon execution of this operation.
	 *
	 * @param nodeTransform
	 *            The {@link Affine} that will be changed by this operation.
	 * @param newTransform
	 *            The {@link Affine} that will be applied to the
	 *            <i>nodeTransform</i> upon execution of this operation.
	 */
	public FXTransformOperation(Affine nodeTransform, Affine newTransform) {
		super("Transform");
		this.nodeTransform = nodeTransform;
		this.oldTransform = setAffine(new Affine(), nodeTransform);
		this.newTransform = newTransform;
	}

	/**
	 * Constructs a new {@link FXTransformOperation} to change the given
	 * <i>nodeTransform</i>. The given <i>oldTransform</i> will be applied to
	 * the <i>nodeTransform</i> upon undoing of this operation. The given
	 * <i>newTransform</i> will be applied to the <i>nodeTransform</i> upon
	 * execution of this operation.
	 *
	 * @param nodeTransform
	 *            The {@link Affine} that will be changed by this operation.
	 * @param oldTransform
	 *            The {@link Affine} that will be applied to the
	 *            <i>nodeTransform</i> upon undoing of this operation.
	 * @param newTransform
	 *            The {@link Affine} that will be applied to the
	 *            <i>nodeTransform</i> upon execution of this operation.
	 */
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

	/**
	 * Returns the {@link Affine} that will be applied to the
	 * <i>nodeTransform</i> upon execution of this operation.
	 *
	 * @return The {@link Affine} that will be applied to the
	 *         <i>nodeTransform</i> upon execution of this operation.
	 */
	public Affine getNewTransform() {
		return newTransform;
	}

	/**
	 * Returns the {@link Affine} that will be applied to the
	 * <i>nodeTransform</i> upon undoing of this operation.
	 *
	 * @return The {@link Affine} that will be applied to the
	 *         <i>nodeTransform</i> upon undoing of this operation.
	 */
	public Affine getOldTransform() {
		return oldTransform;
	}

	@Override
	public boolean isNoOp() {
		return equals(newTransform, oldTransform);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	/**
	 * Sets the {@link Affine} that will be applied to the <i>nodeTransform</i>
	 * upon execution of this operation to the given value.
	 *
	 * @param newTransform
	 *            The {@link Affine} that will be applied upon execution of this
	 *            operation.
	 */
	public void setNewTransform(Affine newTransform) {
		this.newTransform = newTransform;
	}

	/**
	 * Sets the {@link Affine} that will be applied to the <i>nodeTransform</i>
	 * upon undoing of this operation to the given value.
	 *
	 * @param oldTransform
	 *            The {@link Affine} that will be applied upon undoing of this
	 *            operation.
	 */
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
