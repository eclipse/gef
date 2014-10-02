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
package org.eclipse.gef4.mvc.fx.policies;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.mvc.fx.operations.FXClearInteractionModelsOperation;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.SynchronizeContentAnchoragesOperation;
import org.eclipse.gef4.mvc.operations.SynchronizeContentChildrenOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IDeleteContentChildrenPolicy;
import org.eclipse.gef4.mvc.policies.IDetachContentAnchoragesPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class FXDeleteSelectedOnTypePolicy extends AbstractFXTypePolicy {

	@SuppressWarnings("rawtypes")
	public static final Class<IDeleteContentChildrenPolicy> DELETE_CONTENT_CHILDREN_POLICY_KEY = IDeleteContentChildrenPolicy.class;
	@SuppressWarnings("rawtypes")
	public static final Class<IDetachContentAnchoragesPolicy> DETACH_CONTENT_ANCHORAGES_POLICY_KEY = IDetachContentAnchoragesPolicy.class;

	protected IUndoableOperation composeDeleteOperation(IViewer<Node> viewer,
			List<IContentPart<Node>> toDelete) {
		// delete content
		IUndoableOperation contentOperations = getContentOperations(toDelete);
		ReverseUndoCompositeOperation revOp = new FXClearInteractionModelsOperation(
				viewer);
		if (contentOperations != null) {
			revOp.add(contentOperations);
		}

		return revOp;
	}

	protected IUndoableOperation getContentOperations(
			List<IContentPart<Node>> toDelete) {
		// assemble content operations in forward-undo-operations, so that
		// synchronization is always performed after changing the content
		// model (in execute() and undo())
		ForwardUndoCompositeOperation contentChildrenOperations = new ForwardUndoCompositeOperation(
				"DeleteChildren");
		ForwardUndoCompositeOperation contentAnchoragesOperations = new ForwardUndoCompositeOperation(
				"DetachAnchorages");

		for (IContentPart<Node> part : toDelete) {
			// delete from content children
			IVisualPart<Node> parent = part.getParent();
			IDeleteContentChildrenPolicy<Node> deleteContentChildrenPolicy = parent
					.<IDeleteContentChildrenPolicy<Node>> getAdapter(AdapterKey
							.get(DELETE_CONTENT_CHILDREN_POLICY_KEY));
			if (deleteContentChildrenPolicy != null) {
				IUndoableOperation deleteOperation = deleteContentChildrenPolicy
						.getDeleteOperation(part);
				if (deleteOperation != null) {
					contentChildrenOperations.add(deleteOperation);
					contentChildrenOperations
							.add(new SynchronizeContentChildrenOperation<Node>(
									"SynchronizeChildren",
									(IContentPart<Node>) parent));
				}
			}

			// detach from content anchorages
			for (IVisualPart<Node> anchored : part.getAnchoreds()) {
				IDetachContentAnchoragesPolicy<Node> deleteContentAnchoragesPolicy = anchored
						.<IDetachContentAnchoragesPolicy<Node>> getAdapter(AdapterKey
								.get(DETACH_CONTENT_ANCHORAGES_POLICY_KEY));
				if (deleteContentAnchoragesPolicy != null) {
					boolean addedOperations = false;
					for (String r : anchored.getAnchorages().get(part)) {
						IUndoableOperation deleteOperation = deleteContentAnchoragesPolicy
								.getDeleteOperation(part, r);
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
		List<IContentPart<Node>> selected = viewer
				.<SelectionModel<Node>> getAdapter(SelectionModel.class)
				.getSelected();

		// if no parts are selected, we do not delete anything
		if (selected.isEmpty()) {
			return;
		}

		// compose complex delete operation
		IUndoableOperation fullDeleteOperation = composeDeleteOperation(viewer,
				selected);

		// execute on the stack
		executeOperation(fullDeleteOperation);
	}

	@Override
	public void released(KeyEvent event) {
	}

}
