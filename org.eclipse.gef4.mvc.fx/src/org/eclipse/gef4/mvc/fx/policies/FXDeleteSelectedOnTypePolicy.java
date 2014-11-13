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

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.operations.ClearHoverFocusSelectionOperation;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.policies.ContentPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class FXDeleteSelectedOnTypePolicy extends AbstractFXTypePolicy {

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

		// unestablish anchor relations
		ReverseUndoCompositeOperation rev = new ReverseUndoCompositeOperation(
				"Unestablish Anchor Relations");
		for (IContentPart<Node> p : new ArrayList<IContentPart<Node>>(selected)) {
			ContentPolicy<Node> policy = p
					.<ContentPolicy<Node>> getAdapter(ContentPolicy.class);
			if (policy != null) {
				policy.init();
				policy.detachAllContentAnchoreds();
				policy.detachFromAllContentAnchorages();
				IUndoableOperation detachOperation = policy.commit();
				if (detachOperation != null) {
					rev.add(detachOperation);
				}
			}
		}

		// remove content from parent
		for (IContentPart<Node> p : new ArrayList<IContentPart<Node>>(selected)) {
			ContentPolicy<Node> policy = p
					.<ContentPolicy<Node>> getAdapter(ContentPolicy.class);
			if (policy != null) {
				policy.init();
				policy.removeFromParent();
				IUndoableOperation removeOperation = policy.commit();
				if (removeOperation != null) {
					rev.add(removeOperation);
				}
			}
		}

		/*
		 * FIXME: Refactor so that users can chain more operations to the delete
		 * operation, such as clearing the intersection models, which has to be
		 * provided by the user, because we cannot know which interaction models
		 * need to be cleared.
		 *
		 * Therefore, when this is refactored, the following Clear*Operation
		 * should be chained within the example.
		 */

		// clear interaction models
		ForwardUndoCompositeOperation fwd = new ForwardUndoCompositeOperation(
				"Delete Selected");
		fwd.add(rev);
		fwd.add(new ClearHoverFocusSelectionOperation<Node>(getHost().getRoot()
				.getViewer()));

		// execute composite operation
		getHost().getRoot().getViewer().getDomain().execute(fwd);
	}

	@Override
	public void released(KeyEvent event) {
	}

}
