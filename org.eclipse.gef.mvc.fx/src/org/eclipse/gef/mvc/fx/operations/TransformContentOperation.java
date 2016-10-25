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
package org.eclipse.gef.mvc.fx.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.mvc.fx.parts.IBendableContentPart;
import org.eclipse.gef.mvc.fx.parts.ITransformableContentPart;

import javafx.scene.Node;

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

	private final ITransformableContentPart<? extends Node> transformableContentPart;
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
		this.initialTransform = transformableContentPart.getContentTransform()
				.getCopy();
		this.finalTransform = finalTransform;
	}

	private void applyTransform(AffineTransform transform) {
		// XXX: Cannot check content transform if bendable

		// if (!transformableContentPart.getContentTransform()
		// .equals(finalTransform)) {
		transformableContentPart.transformContent(transform);
		if (!(transformableContentPart instanceof IBendableContentPart)) {
			AffineTransform resultingTransform = transformableContentPart
					.getContentTransform();
			if (!resultingTransform.equals(transform)) {
				throw new IllegalStateException(
						"ITransformableVisualPart#transformVisual() did not transform the visual as expected. The resulting transformation should be "
								+ transform + ", but is " + resultingTransform);
			}
		}
		// }
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