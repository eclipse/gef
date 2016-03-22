/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.mvc.parts.ITransformableContentPart;

/**
 * An {@link ITransactionalOperation} to change the transform of an
 * {@link ITransformableContentPart}.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit this
 *            {@link ITransformableContentPart} is used in, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public class TransformContentOperation<VR> extends AbstractOperation
		implements ITransactionalOperation {

	private final ITransformableContentPart<VR, ? extends VR> transformableContentPart;
	private AffineTransform finalDelta;
	private AffineTransform currentDelta;

	/**
	 * Creates a new {@link TransformContentOperation} for the given
	 * {@link ITransformableContentPart} to set the given
	 * {@link AffineTransform}.
	 *
	 * @param transformableContentPart
	 *            The part to transform.
	 * @param deltaTransform
	 *            The delta {@link AffineTransform} to apply.
	 */
	public TransformContentOperation(
			ITransformableContentPart<VR, ? extends VR> transformableContentPart,
			AffineTransform deltaTransform) {
		super("Update Content Transform");
		this.transformableContentPart = transformableContentPart;
		this.currentDelta = new AffineTransform();
		this.finalDelta = deltaTransform;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// compute delta between current transform and delta transform
		AffineTransform delta = currentDelta.getInverse()
				.preConcatenate(finalDelta);
		currentDelta = finalDelta;

		// apply delta, update current transform to new transform
		if (!delta.isIdentity()) {
			transformableContentPart.transformContent(delta);
		}
		return Status.OK_STATUS;
	}

	@Override
	public boolean isContentRelevant() {
		return true;
	}

	@Override
	public boolean isNoOp() {
		return finalDelta.isIdentity();
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
		this.finalDelta = transform;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// apply inversed delta, update current transform to new transform
		if (!currentDelta.isIdentity()) {
			transformableContentPart
					.transformContent(currentDelta.getInverse());
		}
		currentDelta = new AffineTransform();
		return Status.OK_STATUS;
	}
}