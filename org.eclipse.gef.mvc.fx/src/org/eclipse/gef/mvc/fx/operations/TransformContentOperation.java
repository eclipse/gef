/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.mvc.fx.parts.ITransformableContentPart;

import javafx.scene.Node;

/**
 * An {@link ITransactionalOperation} to change the transform of an
 * {@link ITransformableContentPart}.
 *
 * @author anyssen
 *
 */
public class TransformContentOperation extends AbstractOperation
		implements ITransactionalOperation {

	private final ITransformableContentPart<? extends Node> transformableContentPart;
	// TODO: Use JavaFX Affine
	private AffineTransform finalTransform;
	private AffineTransform initialTransform;

	/**
	 * Creates a new {@link TransformContentOperation} for the given
	 * {@link ITransformableContentPart} to set the given
	 * {@link AffineTransform}.
	 *
	 * @param transformableContentPart
	 *            The part to transform.
	 * @param finalTransform
	 *            The total final {@link AffineTransform} to set.
	 */
	public TransformContentOperation(
			ITransformableContentPart<? extends Node> transformableContentPart,
			AffineTransform finalTransform) {
		super("Transform Content");
		this.transformableContentPart = transformableContentPart;
		this.initialTransform = FX2Geometry.toAffineTransform(
				transformableContentPart.getContentTransform());
		this.finalTransform = finalTransform;
	}

	private void applyTransform(AffineTransform transform) {
		if (!transformableContentPart.getContentTransform()
				.equals(finalTransform)) {
			transformableContentPart
					.setContentTransform(Geometry2FX.toFXAffine(transform));
		}
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		applyTransform(finalTransform);
		return Status.OK_STATUS;
	}

	@Override
	public boolean isContentRelevant() {
		return true;
	}

	@Override
	public boolean isNoOp() {
		return initialTransform.equals(finalTransform);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	/**
	 * Sets the given {@link AffineTransform} as the new transform to set on the
	 * content.
	 *
	 * @param transform
	 *            The new {@link AffineTransform} to set.
	 */
	public void setFinalDelta(AffineTransform transform) {
		this.finalTransform = transform;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		applyTransform(initialTransform);
		return Status.OK_STATUS;
	}
}