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
import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.models.HoverModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.parts.PartUtils;
import org.eclipse.gef4.mvc.viewer.IViewer;

/**
 * The {@link ClearHoverFocusSelectionOperation} can be used to clear the
 * {@link HoverModel}, {@link FocusModel}, and {@link SelectionModel} within an
 * {@link IViewer}.
 *
 * @author mwienand
 *
 * @param <VR>
 *            The visual root node of the UI toolkit, e.g. javafx.scene.Node in
 *            case of JavaFX.
 */
public class ClearHoverFocusSelectionOperation<VR>
		extends ReverseUndoCompositeOperation {

	/**
	 * Creates a new {@link ClearHoverFocusSelectionOperation} for the given
	 * {@link IViewer}.
	 *
	 * @param viewer
	 *            The {@link IViewer} of which the {@link HoverModel},
	 *            {@link FocusModel}, and {@link SelectionModel} are cleared.
	 */
	public ClearHoverFocusSelectionOperation(IViewer<VR> viewer) {
		super("Clear Hover, Focus, Selection");

		// clear hover first
		ChangeHoverOperation<VR> changeHoverOperation = getChangeHoverOperation(
				viewer);
		if (changeHoverOperation != null) {
			add(changeHoverOperation);
		}

		// then focus
		ChangeFocusOperation<VR> changeFocusOperation = getChangeFocusOperation(
				viewer);
		if (changeFocusOperation != null) {
			add(changeFocusOperation);
		}

		// selection last
		ChangeSelectionOperation<VR> changeSelectionOperation = getChangeSelectionOperation(
				viewer);
		if (changeSelectionOperation != null) {
			add(changeSelectionOperation);
		}
	}

	@SuppressWarnings("unchecked")
	private IContentPart<VR, ? extends VR> findNewFocus(
			Collection<IContentPart<VR, ? extends VR>> isSelected,
			IContentPart<VR, ? extends VR> part) {
		if (isSelected.contains(part)) {
			return null;
		}

		List<IContentPart<VR, ? extends VR>> contentPartChildren = PartUtils
				.filterParts(part.getChildren(), IContentPart.class);
		if (contentPartChildren.isEmpty()) {
			return part;
		}

		for (IContentPart<VR, ? extends VR> child : contentPartChildren) {
			IContentPart<VR, ? extends VR> newFocus = findNewFocus(isSelected,
					child);
			if (newFocus != null) {
				return newFocus;
			}
		}

		return null;
	}

	/**
	 * Returns a {@link ChangeFocusOperation} to clear the {@link FocusModel} of
	 * the given {@link IViewer}.
	 *
	 * @param viewer
	 *            The {@link IViewer} of which the {@link FocusModel} is
	 *            cleared.
	 * @return A {@link ChangeFocusOperation} to clear the {@link FocusModel} of
	 *         the given {@link IViewer}.
	 */
	protected ChangeFocusOperation<VR> getChangeFocusOperation(
			IViewer<VR> viewer) {
		// focus first un-selected content leaf
		List<IContentPart<VR, ? extends VR>> isSelected = viewer
				.<SelectionModel<VR>> getAdapter(SelectionModel.class)
				.getSelected();
		for (Object content : viewer.getAdapter(ContentModel.class)
				.getContents()) {
			IContentPart<VR, ? extends VR> part = viewer.getContentPartMap()
					.get(content);
			IContentPart<VR, ? extends VR> newFocus = findNewFocus(isSelected,
					part);
			if (newFocus != null) {
				return new ChangeFocusOperation<VR>(viewer, newFocus);
			}
		}
		// otherwise focus nothing
		return new ChangeFocusOperation<VR>(viewer, null);
	}

	/**
	 * Returns a {@link ChangeHoverOperation} to clear the {@link HoverModel} of
	 * the given {@link IViewer}.
	 *
	 * @param viewer
	 *            The {@link IViewer} of which the {@link HoverModel} is
	 *            cleared.
	 * @return A {@link ChangeHoverOperation} to clear the {@link HoverModel} of
	 *         the given {@link IViewer}.
	 */
	protected ChangeHoverOperation<VR> getChangeHoverOperation(
			IViewer<VR> viewer) {
		IVisualPart<VR, ? extends VR> hover = viewer
				.<HoverModel<VR>> getAdapter(HoverModel.class).getHover();
		ChangeHoverOperation<VR> changeHoverOperation = null;
		if (hover != null) {
			changeHoverOperation = new ChangeHoverOperation<VR>(viewer, null);
		}
		return changeHoverOperation;
	}

	/**
	 * Returns a {@link ChangeSelectionOperation} to clear the
	 * {@link SelectionModel} of the given {@link IViewer}.
	 *
	 * @param viewer
	 *            The {@link IViewer} of which the {@link SelectionModel} is
	 *            cleared.
	 * @return A {@link ChangeSelectionOperation} to clear the
	 *         {@link SelectionModel} of the given {@link IViewer}.
	 */
	protected ChangeSelectionOperation<VR> getChangeSelectionOperation(
			IViewer<VR> viewer) {
		return new ChangeSelectionOperation<VR>(viewer,
				Collections.<IContentPart<VR, ? extends VR>> emptyList());
	}

}
