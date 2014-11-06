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
package org.eclipse.gef4.mvc.fx.operations;

import java.util.HashSet;
import java.util.Set;

import javafx.scene.Node;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.SynchronizeContentAnchoragesOperation;
import org.eclipse.gef4.mvc.operations.SynchronizeContentChildrenOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class FXDeleteOperation extends ReverseUndoCompositeOperation {

	/**
	 * The {@link FXDeleteContentOperation} uses the {@link IContentPart} API to
	 * remove a content object from an {@link IContentPart}.
	 */
	public static class FXDeleteContentOperation extends AbstractOperation {
		private final IContentPart<Node> parent;
		private final Object contentChild;

		public FXDeleteContentOperation(IContentPart<Node> parent,
				Object contentChild) {
			super("deleteContent");
			this.parent = parent;
			this.contentChild = contentChild;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			parent.removeContentChild(contentChild);
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
			parent.addContentChild(contentChild);
			return Status.OK_STATUS;
		}
	}

	/**
	 * The {@link FXDetachContentOperation} uses the {@link IContentPart} API to
	 * detach a content object as an anchorage from all of its anchoreds.
	 */
	public static class FXDetachContentOperation extends AbstractOperation {
		private final IContentPart<Node> contentPart;
		private final IContentPart<Node> anchored;
		private final Set<String> roles = new HashSet<String>();

		public FXDetachContentOperation(IContentPart<Node> anchored,
				IContentPart<Node> contentPart) {
			super("deleteContent");
			this.anchored = anchored;
			this.contentPart = contentPart;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			roles.clear();
			for (String role : anchored.getAnchorages().get(contentPart)) {
				roles.add(role); // remember for undo
				anchored.detachFromContentAnchorage(contentPart.getContent(),
						role);
			}
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
			for (String role : roles) {
				anchored.attachToContentAnchorage(contentPart.getContent(),
						role);
			}
			return Status.OK_STATUS;
		}
	}

	protected final IContentPart<Node> toDelete;
	protected final IViewer<Node> viewer;

	public FXDeleteOperation(IViewer<Node> viewer, IContentPart<Node> toDelete) {
		super("Delete()");
		this.viewer = viewer;
		this.toDelete = toDelete;
		add(new FXClearHoverFocusSelectionOperation(viewer));
		add(getContentOperations(toDelete));
	}

	protected IUndoableOperation getContentOperations(
			IContentPart<Node> toDelete) {
		// assemble content operations in forward-undo-operations, so that
		// synchronization is always performed after changing the content
		// model (in execute() and undo())
		ForwardUndoCompositeOperation contentChildrenOperations = new ForwardUndoCompositeOperation(
				"DeleteChildren");
		ForwardUndoCompositeOperation contentAnchoragesOperations = new ForwardUndoCompositeOperation(
				"DetachAnchorages");

		// delete from content children
		IContentPart<Node> parent = (IContentPart<Node>) toDelete.getParent();
		contentChildrenOperations.add(new FXDeleteContentOperation(parent,
				toDelete.getContent()));
		contentChildrenOperations
				.add(new SynchronizeContentChildrenOperation<Node>(
						"SynchronizeChildren", parent));

		// detach from content anchorages
		for (IVisualPart<Node> anchored : toDelete.getAnchoreds()) {
			if (!(anchored instanceof IContentPart)) {
				continue;
			}
			IContentPart<Node> cp = (IContentPart<Node>) anchored;
			contentAnchoragesOperations.add(new FXDetachContentOperation(cp,
					toDelete));
			// synchronize content anchorages once per anchored
			contentAnchoragesOperations
					.add(new SynchronizeContentAnchoragesOperation<Node>(
							"SynchronizeAnchorages",
							(IContentPart<Node>) anchored));
		}

		ReverseUndoCompositeOperation revOp = new ReverseUndoCompositeOperation(
				"DeleteContent");
		if (!contentAnchoragesOperations.isEmpty()) {
			revOp.add(contentAnchoragesOperations);
		}
		if (!contentChildrenOperations.isEmpty()) {
			revOp.add(contentChildrenOperations);
		}

		return revOp;
	}

}
