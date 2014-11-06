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

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class DeleteContentOperation<VR> extends ReverseUndoCompositeOperation {

	protected final IContentPart<VR> toDelete;
	protected final IViewer<VR> viewer;

	public DeleteContentOperation(IViewer<VR> viewer, IContentPart<VR> toDelete) {
		super("Delete()");
		this.viewer = viewer;
		this.toDelete = toDelete;
		add(new ClearHoverFocusSelectionOperation<VR>(viewer));
		add(getContentOperations(toDelete));
	}

	protected IUndoableOperation getContentOperations(IContentPart<VR> toDelete) {
		// assemble content operations in forward-undo-operations, so that
		// synchronization is always performed after changing the content
		// model (in execute() and undo())
		ForwardUndoCompositeOperation contentChildrenOperations = new ForwardUndoCompositeOperation(
				"DeleteChildren");
		ForwardUndoCompositeOperation contentAnchoragesOperations = new ForwardUndoCompositeOperation(
				"DetachAnchorages");

		// delete from content children
		IContentPart<VR> parent = (IContentPart<VR>) toDelete.getParent();
		contentChildrenOperations.add(new RemoveContentChildOperation<VR>(
				parent, toDelete.getContent()));
		contentChildrenOperations
				.add(new SynchronizeContentChildrenOperation<VR>(
						"SynchronizeChildren", parent));

		// detach from content anchorages
		for (IVisualPart<VR> anchored : toDelete.getAnchoreds()) {
			if (!(anchored instanceof IContentPart)) {
				continue;
			}
			IContentPart<VR> cp = (IContentPart<VR>) anchored;
			for (String role : cp.getAnchorages().get(toDelete)) {
				contentAnchoragesOperations
						.add(new DetachFromContentAnchorageOperation<VR>(cp,
								toDelete.getContent(), role));
			}
			// synchronize content anchorages once per anchored
			contentAnchoragesOperations
					.add(new SynchronizeContentAnchoragesOperation<VR>(
							"SynchronizeAnchorages",
							(IContentPart<VR>) anchored));
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
