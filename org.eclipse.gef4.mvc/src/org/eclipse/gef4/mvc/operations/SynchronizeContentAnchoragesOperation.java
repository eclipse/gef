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
import org.eclipse.gef4.mvc.behaviors.ContentBehavior;
import org.eclipse.gef4.mvc.parts.IContentPart;

/**
 * The {@link SynchronizeContentAnchoragesOperation} can be used to trigger the
 * synchronization of anchorages and content anchorages using the
 * {@link ContentBehavior} of a specified {@link IContentPart}.
 *
 * @author mwienand
 *
 * @param <VR>
 *            The visual root node of the UI toolkit, e.g. javafx.scene.Node in
 *            case of JavaFX.
 */
public class SynchronizeContentAnchoragesOperation<VR>
		extends AbstractOperation {

	private IContentPart<VR, ? extends VR> anchored;

	/**
	 * Creates a new {@link SynchronizeContentAnchoragesOperation} for the
	 * synchronization of anchorages and content anchorages of the given
	 * {@link IContentPart}.
	 *
	 * @param label
	 *            The operation's label.
	 * @param anchored
	 *            The {@link IContentPart} for which the content anchorages
	 *            synchronization is performed.
	 */
	public SynchronizeContentAnchoragesOperation(String label,
			IContentPart<VR, ? extends VR> anchored) {
		super(label);
		this.anchored = anchored;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// System.out.println("EXEC sync content anchorages for " + anchored);
		ContentBehavior<?> contentBehavior = anchored
				.getAdapter(ContentBehavior.class);
		contentBehavior
				.synchronizeContentAnchorages(anchored.getContentAnchorages());
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
		// System.out.println("UNDO sync content anchorages for " + anchored);
		ContentBehavior<?> contentBehavior = anchored
				.getAdapter(ContentBehavior.class);
		contentBehavior
				.synchronizeContentAnchorages(anchored.getContentAnchorages());
		return Status.OK_STATUS;
	}

}
