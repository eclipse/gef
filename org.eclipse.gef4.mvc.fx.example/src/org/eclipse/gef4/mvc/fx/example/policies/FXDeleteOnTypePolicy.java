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
package org.eclipse.gef4.mvc.fx.example.policies;

import java.util.Collections;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXTypePolicy;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.operations.ChangeFocusOperation;
import org.eclipse.gef4.mvc.operations.ChangeHoverOperation;
import org.eclipse.gef4.mvc.operations.ChangeSelectionOperation;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.SynchronizeContentAnchoragesOperation;
import org.eclipse.gef4.mvc.operations.SynchronizeContentChildrenOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class DeleteOnTypePolicy extends AbstractFXTypePolicy {

	protected ReverseUndoCompositeOperation composeDeleteOperation(
			IViewer<Node> viewer, List<IContentPart<Node>> toDelete) {
		// delete content
		ForwardUndoCompositeOperation contentOperations = getContentOperations(toDelete);
		// clear selection
		ChangeSelectionOperation<Node> changeSelectionOperation = getChangeSelectionOperation(viewer);
		// clear hover
		ChangeHoverOperation<Node> changeHoverOperation = getChangeHoverOperation(viewer);
		// clear focus
		ChangeFocusOperation<Node> changeFocusOperation = getChangeFocusOperation(viewer);

		// assemble operations
		ReverseUndoCompositeOperation revOp = new ReverseUndoCompositeOperation(
				"Delete");
		if (changeHoverOperation != null) {
			revOp.add(changeHoverOperation);
		}
		if (changeFocusOperation != null) {
			revOp.add(changeFocusOperation);
		}
		if (changeSelectionOperation != null) {
			revOp.add(changeSelectionOperation);
		}
		// TODO: Should contentOperations == null be allowed?
		if (contentOperations != null && !contentOperations.isEmpty()) {
			revOp.add(contentOperations);
		}

		return revOp;
	}

	protected ChangeFocusOperation<Node> getChangeFocusOperation(
			IViewer<Node> viewer) {
		return new ChangeFocusOperation<Node>(viewer, null);
	}

	protected ChangeHoverOperation<Node> getChangeHoverOperation(
			IViewer<Node> viewer) {
		IVisualPart<Node> hover = viewer.getHoverModel().getHover();
		ChangeHoverOperation<Node> changeHoverOperation = null;
		if (hover == getHost()) {
			changeHoverOperation = new ChangeHoverOperation<Node>(viewer, null);
		}
		return changeHoverOperation;
	}

	protected ChangeSelectionOperation<Node> getChangeSelectionOperation(
			IViewer<Node> viewer) {
		return new ChangeSelectionOperation<Node>(viewer,
				Collections.<IContentPart<Node>> emptyList());
	}

	protected ForwardUndoCompositeOperation getContentOperations(
			List<IContentPart<Node>> toDelete) {
		// assemble content operations in a forward-undo-operation, so that
		// synchronization is always performed after changing the content
		// model (in execute() and undo())
		ForwardUndoCompositeOperation contentOps = new ForwardUndoCompositeOperation(
				"DeleteContent");

		for (IContentPart<Node> selected : toDelete) {
			// get operation to delete from content children
			IVisualPart<Node> parent = selected.getParent();
			AbstractDeleteContentChildrenPolicy deleteContentChildrenPolicy = parent
					.getAdapter(AdapterKey
							.get(AbstractDeleteContentChildrenPolicy.class));
			if (deleteContentChildrenPolicy != null) {
				IUndoableOperation deleteOperation = deleteContentChildrenPolicy
						.getDeleteOperation(selected);
				if (deleteOperation != null) {
					contentOps.add(deleteOperation);
					contentOps
							.add(new SynchronizeContentChildrenOperation<Node>(
									"SynchronizeChildren",
									(IContentPart<Node>) parent));
				}
			}

			// get operations to delete from content anchorages per anchored
			for (IVisualPart<Node> anchored : selected.getAnchoreds()) {
				AbstractDetachContentAnchoragesPolicy deleteContentAnchoragesPolicy = anchored
						.getAdapter(AdapterKey
								.get(AbstractDetachContentAnchoragesPolicy.class));
				if (deleteContentAnchoragesPolicy != null) {
					boolean addedOperations = false;
					for (String r : anchored.getAnchorages().get(selected)) {
						IUndoableOperation deleteOperation = deleteContentAnchoragesPolicy
								.getDeleteOperation(selected, r);
						if (deleteOperation != null) {
							contentOps.add(deleteOperation);
							addedOperations = true;
						}
					}
					// synchronize content anchorages once per anchored
					if (addedOperations) {
						contentOps
								.add(new SynchronizeContentAnchoragesOperation<Node>(
										"SynchronizeAnchorages",
										(IContentPart<Node>) anchored));
					}
				}
			}
		}

		return contentOps;
	}

	protected boolean isDelete(KeyEvent event) {
		// only delete on <DELETE> key
		if (event.getCode() != KeyCode.DELETE) {
			return false;
		}

		// prevent deletion when other drag policies are running
		FXClickDragTool tool = getHost().getRoot().getViewer().getDomain()
				.getAdapter(FXClickDragTool.class);
		if (tool != null && tool.isDragging()) {
			return false;
		}

		return true;
	}

	@Override
	public void pressed(KeyEvent event) {
		if (!isDelete(event)) {
			return;
		}

		// get current selection
		IViewer<Node> viewer = getHost().getRoot().getViewer();
		List<IContentPart<Node>> selected = viewer.getSelectionModel()
				.getSelected();

		// if no parts are selected, we do not delete anything
		if (selected.isEmpty()) {
			return;
		}

		// compose complex delete operation
		ReverseUndoCompositeOperation revOp = composeDeleteOperation(viewer,
				selected);

		// execute on the stack
		executeOperation(revOp);
	}

	@Override
	public void released(KeyEvent event) {
	}

}
