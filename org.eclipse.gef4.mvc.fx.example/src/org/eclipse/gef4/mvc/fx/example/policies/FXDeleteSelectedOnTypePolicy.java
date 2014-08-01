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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.eclipse.gef4.mvc.parts.PartUtils;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class FXDeleteSelectedOnTypePolicy extends AbstractFXTypePolicy {

	@SuppressWarnings("rawtypes")
	public static final Class<AbstractDeleteContentChildrenPolicy> DELETE_CONTENT_CHILDREN_POLICY_KEY = AbstractDeleteContentChildrenPolicy.class;
	@SuppressWarnings("rawtypes")
	public static final Class<AbstractDetachContentAnchoragesPolicy> DETACH_CONTENT_ANCHORAGES_POLICY_KEY = AbstractDetachContentAnchoragesPolicy.class;

	protected IUndoableOperation composeDeleteOperation(IViewer<Node> viewer,
			List<IContentPart<Node>> toDelete) {
		// delete content
		IUndoableOperation contentOperations = getContentOperations(toDelete);
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
		if (contentOperations != null) {
			revOp.add(contentOperations);
		}

		return revOp;
	}

	@SuppressWarnings("unchecked")
	private IContentPart<Node> findNewFocus(Set<Object> isSelected,
			IContentPart<Node> part) {
		if (isSelected.contains(part)) {
			return null;
		}

		List<IContentPart<Node>> contentPartChildren = PartUtils.filterParts(
				part.getChildren(), IContentPart.class);
		if (contentPartChildren.isEmpty()) {
			return part;
		}

		for (IContentPart<Node> child : contentPartChildren) {
			IContentPart<Node> newFocus = findNewFocus(isSelected, child);
			if (newFocus != null) {
				return newFocus;
			}
		}

		return null;
	}

	protected ChangeFocusOperation<Node> getChangeFocusOperation(
			IViewer<Node> viewer) {
		// focus first un-selected content leaf
		Set<Object> isSelected = new HashSet<Object>(viewer.getSelectionModel()
				.getSelected());
		for (Object content : viewer.getContents()) {
			IContentPart<Node> part = viewer.getContentPartMap().get(content);
			IContentPart<Node> newFocus = findNewFocus(isSelected, part);
			if (newFocus != null) {
				return new ChangeFocusOperation<Node>(viewer, newFocus);
			}
		}
		// otherwise focus nothing
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
			AbstractDeleteContentChildrenPolicy<Node> deleteContentChildrenPolicy = parent
					.<AbstractDeleteContentChildrenPolicy<Node>> getAdapter(AdapterKey
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
				AbstractDetachContentAnchoragesPolicy<Node> deleteContentAnchoragesPolicy = anchored
						.<AbstractDetachContentAnchoragesPolicy<Node>> getAdapter(AdapterKey
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
		List<IContentPart<Node>> selected = viewer.getSelectionModel()
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
