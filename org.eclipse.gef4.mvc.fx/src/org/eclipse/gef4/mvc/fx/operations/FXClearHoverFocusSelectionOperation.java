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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.models.HoverModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.operations.ChangeFocusOperation;
import org.eclipse.gef4.mvc.operations.ChangeHoverOperation;
import org.eclipse.gef4.mvc.operations.ChangeSelectionOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.parts.PartUtils;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class FXClearHoverFocusSelectionOperation extends
		ReverseUndoCompositeOperation {

	public FXClearHoverFocusSelectionOperation(IViewer<Node> viewer) {
		super("clear-interaction-models");

		// clear hover first
		ChangeHoverOperation<Node> changeHoverOperation = getChangeHoverOperation(viewer);
		if (changeHoverOperation != null) {
			add(changeHoverOperation);
		}

		// then focus
		ChangeFocusOperation<Node> changeFocusOperation = getChangeFocusOperation(viewer);
		if (changeFocusOperation != null) {
			add(changeFocusOperation);
		}

		// selection last
		ChangeSelectionOperation<Node> changeSelectionOperation = getChangeSelectionOperation(viewer);
		if (changeSelectionOperation != null) {
			add(changeSelectionOperation);
		}
	}

	@SuppressWarnings("unchecked")
	private IContentPart<Node> findNewFocus(
			Collection<IContentPart<Node>> isSelected, IContentPart<Node> part) {
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
		List<IContentPart<Node>> isSelected = viewer
				.<SelectionModel<Node>> getAdapter(SelectionModel.class)
				.getSelected();
		for (Object content : viewer.getAdapter(ContentModel.class)
				.getContents()) {
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
		IVisualPart<Node> hover = viewer.<HoverModel<Node>> getAdapter(
				HoverModel.class).getHover();
		ChangeHoverOperation<Node> changeHoverOperation = null;
		if (hover != null) {
			changeHoverOperation = new ChangeHoverOperation<Node>(viewer, null);
		}
		return changeHoverOperation;
	}

	protected ChangeSelectionOperation<Node> getChangeSelectionOperation(
			IViewer<Node> viewer) {
		return new ChangeSelectionOperation<Node>(viewer,
				Collections.<IContentPart<Node>> emptyList());
	}

}
