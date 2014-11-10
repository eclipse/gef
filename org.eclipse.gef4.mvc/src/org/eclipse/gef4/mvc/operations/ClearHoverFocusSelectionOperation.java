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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.models.HoverModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.parts.PartUtils;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class ClearHoverFocusSelectionOperation<VR> extends
		ReverseUndoCompositeOperation {

	public ClearHoverFocusSelectionOperation(IViewer<VR> viewer) {
		super("Clear Hover, Focus, Selection");

		// clear hover first
		ChangeHoverOperation<VR> changeHoverOperation = getChangeHoverOperation(viewer);
		if (changeHoverOperation != null) {
			add(changeHoverOperation);
		}

		// then focus
		ChangeFocusOperation<VR> changeFocusOperation = getChangeFocusOperation(viewer);
		if (changeFocusOperation != null) {
			add(changeFocusOperation);
		}

		// selection last
		ChangeSelectionOperation<VR> changeSelectionOperation = getChangeSelectionOperation(viewer);
		if (changeSelectionOperation != null) {
			add(changeSelectionOperation);
		}
	}

	@SuppressWarnings("unchecked")
	private IContentPart<VR> findNewFocus(
			Collection<IContentPart<VR>> isSelected, IContentPart<VR> part) {
		if (isSelected.contains(part)) {
			return null;
		}

		List<IContentPart<VR>> contentPartChildren = PartUtils.filterParts(
				part.getChildren(), IContentPart.class);
		if (contentPartChildren.isEmpty()) {
			return part;
		}

		for (IContentPart<VR> child : contentPartChildren) {
			IContentPart<VR> newFocus = findNewFocus(isSelected, child);
			if (newFocus != null) {
				return newFocus;
			}
		}

		return null;
	}

	protected ChangeFocusOperation<VR> getChangeFocusOperation(
			IViewer<VR> viewer) {
		// focus first un-selected content leaf
		List<IContentPart<VR>> isSelected = viewer
				.<SelectionModel<VR>> getAdapter(SelectionModel.class)
				.getSelected();
		for (Object content : viewer.getAdapter(ContentModel.class)
				.getContents()) {
			IContentPart<VR> part = viewer.getContentPartMap().get(content);
			IContentPart<VR> newFocus = findNewFocus(isSelected, part);
			if (newFocus != null) {
				return new ChangeFocusOperation<VR>(viewer, newFocus);
			}
		}
		// otherwise focus nothing
		return new ChangeFocusOperation<VR>(viewer, null);
	}

	protected ChangeHoverOperation<VR> getChangeHoverOperation(
			IViewer<VR> viewer) {
		IVisualPart<VR> hover = viewer.<HoverModel<VR>> getAdapter(
				HoverModel.class).getHover();
		ChangeHoverOperation<VR> changeHoverOperation = null;
		if (hover != null) {
			changeHoverOperation = new ChangeHoverOperation<VR>(viewer, null);
		}
		return changeHoverOperation;
	}

	protected ChangeSelectionOperation<VR> getChangeSelectionOperation(
			IViewer<VR> viewer) {
		return new ChangeSelectionOperation<VR>(viewer,
				Collections.<IContentPart<VR>> emptyList());
	}

}
