/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
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
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.mvc.fx.parts.ITransformableContentPart;

import javafx.scene.Node;
import javafx.scene.transform.Affine;

/**
 * The {@link TransformVisualOperation} can be used to change an {@link Affine},
 * for example, one that is contained within the transformations list of a
 * {@link Node} to transform that {@link Node}.
 *
 * @author mwienand
 *
 */
public class TransformVisualOperation extends AbstractOperation
		implements ITransactionalOperation {

	private ITransformableContentPart<? extends Node> transformablePart;
	private Affine initialTransform;
	private Affine finalTransform;

	/**
	 * Constructs a new {@link TransformVisualOperation} to change the given
	 * <i>nodeTransform</i>.
	 *
	 * @param transformablePart
	 *            The {@link ITransformableContentPart} that will be transformed
	 *            by this operation.
	 */
	public TransformVisualOperation(
			ITransformableContentPart<? extends Node> transformablePart) {
		this(transformablePart, transformablePart.getVisualTransform());
	}

	/**
	 * Constructs a new {@link TransformVisualOperation} to change the given
	 * <i>nodeTransform</i>. The given <i>newTransform</i> will be applied to
	 * the <i>nodeTransform</i> upon execution of this operation.
	 *
	 * @param transformablePart
	 *            The {@link ITransformableContentPart} that will be transformed
	 *            by this operation.
	 * @param newTransform
	 *            The {@link Affine} that will be set as the visual
	 *            transformation for the given {@link ITransformableContentPart}
	 *            upon execution of this operation.
	 */
	public TransformVisualOperation(
			ITransformableContentPart<? extends Node> transformablePart,
			Affine newTransform) {
		super("Transform");
		this.transformablePart = transformablePart;
		this.initialTransform = NodeUtils.setAffine(new Affine(),
				transformablePart.getVisualTransform());
		this.finalTransform = NodeUtils.setAffine(new Affine(), newTransform);
	}

	/**
	 * Sets the visual transformation to the given {@link Affine}.
	 *
	 * @param transform
	 *            The {@link Affine} that is to be set as the total visual
	 *            transformation for the {@link ITransformableContentPart}.
	 */
	protected void applyTransform(Affine transform) {
		if (!NodeUtils.equals(transformablePart.getVisualTransform(),
				transform)) {
			transformablePart.setVisualTransform(transform);
		}
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		applyTransform(finalTransform);
		return Status.OK_STATUS;
	}

	/**
	 * Returns the initial {@link Affine} of the
	 * {@link ITransformableContentPart}.
	 *
	 * @return The initial {@link Affine} of the
	 *         {@link ITransformableContentPart}.
	 */
	public Affine getInitialTransform() {
		return initialTransform;
	}

	/**
	 * Returns the {@link Affine} that will be set as the transformation matrix
	 * of the {@link ITransformableContentPart}.
	 *
	 * @return The {@link Affine} that will be set as the transformation matrix
	 *         of the {@link ITransformableContentPart}.
	 */
	public Affine getNewTransform() {
		return finalTransform;
	}

	@Override
	public boolean isContentRelevant() {
		return false;
	}

	@Override
	public boolean isNoOp() {
		return NodeUtils.equals(initialTransform, finalTransform);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	/**
	 * Sets the {@link Affine} that will be set as the transformation matrix of
	 * the {@link ITransformableContentPart}.
	 *
	 * @param newTransform
	 *            The {@link Affine} that will be set as the transformation
	 *            matrix of the {@link ITransformableContentPart}.
	 */
	public void setFinalTransform(Affine newTransform) {
		this.finalTransform = NodeUtils.setAffine(new Affine(), newTransform);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		applyTransform(initialTransform);
		return Status.OK_STATUS;
	}
}
