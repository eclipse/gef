/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.gef.fx.utils.NodeUtils;

import javafx.scene.Node;
import javafx.scene.transform.Affine;

/**
 * The {@link TransformOperation} can be used to change an {@link Affine}, for
 * example, one that is contained within the transformations list of a
 * {@link Node} to transform that {@link Node}.
 *
 * @author mwienand
 *
 */
public class TransformOperation extends AbstractOperation
		implements ITransactionalOperation {

	private final Affine nodeTransform;

	private Affine initialTransform;

	private Affine newTransform;

	/**
	 * Constructs a new {@link TransformOperation} to change the given
	 * <i>nodeTransform</i>.
	 *
	 * @param nodeTransform
	 *            The {@link Affine} that will be changed by this operation.
	 */
	public TransformOperation(Affine nodeTransform) {
		super("Transform");
		this.nodeTransform = nodeTransform;
		this.initialTransform = NodeUtils.setAffine(new Affine(),
				nodeTransform);
		this.newTransform = NodeUtils.setAffine(new Affine(), nodeTransform);
	}

	/**
	 * Constructs a new {@link TransformOperation} to change the given
	 * <i>nodeTransform</i>. The given <i>newTransform</i> will be applied to
	 * the <i>nodeTransform</i> upon execution of this operation.
	 *
	 * @param nodeTransform
	 *            The {@link Affine} that will be changed by this operation.
	 * @param newTransform
	 *            The {@link Affine} that will be applied to the
	 *            <i>nodeTransform</i> upon execution of this operation.
	 */
	public TransformOperation(Affine nodeTransform, Affine newTransform) {
		super("Transform");
		this.nodeTransform = nodeTransform;
		this.initialTransform = NodeUtils.setAffine(new Affine(),
				nodeTransform);
		this.newTransform = newTransform;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (!NodeUtils.equals(nodeTransform, newTransform)) {
			NodeUtils.setAffine(nodeTransform, newTransform);
		}
		return Status.OK_STATUS;
	}

	/**
	 * Returns the {@link Affine} that will be applied to the
	 * <i>nodeTransform</i> upon undoing of this operation.
	 *
	 * @return The {@link Affine} that will be applied to the
	 *         <i>nodeTransform</i> upon undoing of this operation.
	 */
	public Affine getInitialTransform() {
		return initialTransform;
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

	@Override
	public boolean isContentRelevant() {
		return false;
	}

	@Override
	public boolean isNoOp() {
		return NodeUtils.equals(newTransform, initialTransform);
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

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (!NodeUtils.equals(nodeTransform, initialTransform)) {
			NodeUtils.setAffine(nodeTransform, initialTransform);
		}
		return Status.OK_STATUS;
	}

}
