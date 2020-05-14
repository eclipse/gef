/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.mvc.fx.parts.IResizableContentPart;
import org.eclipse.gef.mvc.fx.parts.ITransformableContentPart;

import javafx.scene.Node;

/**
 * An {@link ITransactionalOperation} to change the size of an
 * {@link IResizableContentPart}.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit this
 *            {@link ITransformableContentPart} is used in, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public class ResizeContentOperation<VR> extends AbstractOperation
		implements ITransactionalOperation {

	private final IResizableContentPart<? extends Node> resizableContentPart;
	private Dimension initialSize;
	private Dimension finalSize;

	/**
	 * Creates a new {@link ResizeContentOperation} to resize the content of the
	 * given {@link IResizableContentPart}.
	 *
	 * @param resizableContentPart
	 *            The part to resize.
	 * @param initialSize
	 *            The initial size before applying the change.
	 * @param finalSize
	 *            The final size after applying the change.
	 */
	public ResizeContentOperation(
			IResizableContentPart<? extends Node> resizableContentPart,
			Dimension initialSize, Dimension finalSize) {
		super("Resize Content");
		this.resizableContentPart = resizableContentPart;
		this.initialSize = initialSize;
		this.finalSize = finalSize;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (!resizableContentPart.getContentSize().equals(finalSize)) {
			resizableContentPart.setContentSize(finalSize);
		}
		return Status.OK_STATUS;
	}

	@Override
	public boolean isContentRelevant() {
		return true;
	}

	@Override
	public boolean isNoOp() {
		return initialSize.equals(finalSize);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (!resizableContentPart.getContentSize().equals(initialSize)) {
			resizableContentPart.setContentSize(initialSize);
		}
		return Status.OK_STATUS;
	}
}