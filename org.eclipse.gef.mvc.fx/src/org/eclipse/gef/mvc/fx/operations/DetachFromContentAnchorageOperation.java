/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
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
import org.eclipse.gef.mvc.fx.parts.IContentPart;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;

import javafx.scene.Node;

/**
 * The {@link DetachFromContentAnchorageOperation} uses the {@link IContentPart}
 * API to detach an anchored from the given anchorage.
 *
 */
public class DetachFromContentAnchorageOperation extends AbstractOperation
		implements ITransactionalOperation {

	private final IContentPart<? extends Node> anchored;
	private final Object contentAnchorage;
	private final String role;

	// initial content anchorages (for no-op test)
	private SetMultimap<Object, String> initialContentAnchorages;

	/**
	 * Creates a new {@link DetachFromContentAnchorageOperation} to detach the
	 * given <i>anchored</i> {@link IContentPart} from the given
	 * <i>contentAnchorage</i> under the specified <i>role</i>, so that it will
	 * not be returned by subsequent calls to
	 * {@link IContentPart#getContentAnchoragesUnmodifiable()}.
	 *
	 * @param anchored
	 *            The {@link IContentPart} which is to be detached from the
	 *            given <i>contentAnchorage</i>.
	 * @param contentAnchorage
	 *            The content object from which the given <i>anchored</i> is to
	 *            be detached.
	 * @param role
	 *            The role under which the <i>contentAnchorage</i> is anchored.
	 */
	public DetachFromContentAnchorageOperation(
			IContentPart<? extends Node> anchored, Object contentAnchorage,
			String role) {
		super("Detach From Content Anchorage");
		this.anchored = anchored;
		this.contentAnchorage = contentAnchorage;
		this.initialContentAnchorages = ImmutableSetMultimap
				.copyOf(anchored.getContentAnchoragesUnmodifiable());
		this.role = role;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// System.out.println("EXEC detach " + anchored + " from content "
		// + contentAnchorage + " with role " + role + ".");
		if (anchored.getContent() != null
				&& anchored.getContentAnchoragesUnmodifiable()
						.containsEntry(contentAnchorage, role)) {
			anchored.detachFromContentAnchorage(contentAnchorage, role);
		}
		return Status.OK_STATUS;
	}

	@Override
	public boolean isContentRelevant() {
		return true;
	}

	@Override
	public boolean isNoOp() {
		return !initialContentAnchorages.containsEntry(contentAnchorage, role);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// System.out.println("UNDO detach " + anchored + " from content "
		// + contentAnchorage + " with role " + role + ".");
		if (anchored.getContent() != null
				&& !anchored.getContentAnchoragesUnmodifiable()
						.containsEntry(contentAnchorage, role)) {
			anchored.attachToContentAnchorage(contentAnchorage, role);
		}
		return Status.OK_STATUS;
	}

}