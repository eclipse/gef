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

import javafx.scene.Node;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.SynchronizeContentAnchoragesOperation;
import org.eclipse.gef4.mvc.operations.SynchronizeContentChildrenOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IDeleteContentChildrenPolicy;
import org.eclipse.gef4.mvc.policies.IDetachContentAnchoragesPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class FXDeleteOperation extends ReverseUndoCompositeOperation {

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
		IVisualPart<Node> parent = toDelete.getParent();
		IDeleteContentChildrenPolicy<Node> deleteContentChildrenPolicy = null;
		if (deleteContentChildrenPolicy != null) {
			IUndoableOperation deleteOperation = deleteContentChildrenPolicy
					.getDeleteOperation(toDelete);
			if (deleteOperation != null) {
				contentChildrenOperations.add(deleteOperation);
				contentChildrenOperations
						.add(new SynchronizeContentChildrenOperation<Node>(
								"SynchronizeChildren",
								(IContentPart<Node>) parent));
			}
		}

		// detach from content anchorages
		for (IVisualPart<Node> anchored : toDelete.getAnchoreds()) {
			IDetachContentAnchoragesPolicy<Node> deleteContentAnchoragesPolicy = null;
			if (deleteContentAnchoragesPolicy != null) {
				boolean addedOperations = false;
				for (String r : anchored.getAnchorages().get(toDelete)) {
					IUndoableOperation deleteOperation = deleteContentAnchoragesPolicy
							.getDeleteOperation(toDelete, r);
					if (deleteOperation != null) {
						contentAnchoragesOperations.add(deleteOperation);
						addedOperations = true;
					}
				}
				// synchronize content anchorages once per anchored
				if (addedOperations) {
					contentAnchoragesOperations
							.add(new SynchronizeContentAnchoragesOperation<Node>(
									"SynchronizeAnchorages",
									(IContentPart<Node>) anchored));
				}
			}
		}

		if (contentChildrenOperations.isEmpty()
				&& contentAnchoragesOperations.isEmpty()) {
			return null;
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
