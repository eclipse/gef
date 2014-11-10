/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.mvc.parts.IContentPart;

/**
 * The {@link AttachToContentAnchorageOperation} uses the {@link IContentPart}
 * API to detach an anchored from the given anchorage.
 *
 * @param <VR>
 *            The visual root node of the UI toolkit, e.g. javafx.scene.Node in
 *            case of JavaFX.
 */
public class AttachToContentAnchorageOperation<VR> extends AbstractOperation {

	private final IContentPart<VR> anchored;
	private final Object contentAnchorage;
	private final String role;

	public AttachToContentAnchorageOperation(IContentPart<VR> anchored,
			Object contentAnchorage, String role) {
		super("Attach To Content Anchorage");
		this.anchored = anchored;
		this.contentAnchorage = contentAnchorage;
		this.role = role;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		anchored.attachToContentAnchorage(contentAnchorage, role);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		anchored.detachFromContentAnchorage(contentAnchorage, role);
		return Status.OK_STATUS;
	}
}