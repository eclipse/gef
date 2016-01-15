/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - multi selection handles in root part
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.behaviors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.common.reflect.Types;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

import javafx.collections.ListChangeListener;

/**
 * The default selection behavior is responsible for creating and removing
 * selection feedback and handles.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public class SelectionBehavior<VR> extends AbstractBehavior<VR> {

	private ListChangeListener<IContentPart<VR, ? extends VR>> selectionObserver = new ListChangeListener<IContentPart<VR, ? extends VR>>() {

		@Override
		public void onChanged(
				javafx.collections.ListChangeListener.Change<? extends IContentPart<VR, ? extends VR>> c) {
			// order of selection should not be relevant for feedback and
			// handles, as such we ignore permutations
			List<IContentPart<VR, ? extends VR>> newSelection = new ArrayList<>(
					c.getList());
			List<IContentPart<VR, ? extends VR>> oldSelection = new ArrayList<>(
					newSelection);
			while (c.next()) {
				// We need to reconstruct the old selection from the changes. As
				// a setAll() is internally decomposed into a clear() and
				// addAll(), we need to first remove the added elements, then
				// re-add the removed ones to reconstruct the old selection.
				// TODO: ideally we would only remove and add feedback for those
				// elements that changed; the current implementation is however
				// not capable of handling this, so we have to compute the old
				// and new selection; again we ignore the ordering here
				if (c.wasAdded()) {
					oldSelection.removeAll(c.getAddedSubList());
				}
				if (c.wasRemoved()) {
					oldSelection.addAll(c.getRemoved());
				}
			}
			removeFeedbackAndHandles(oldSelection);
			addFeedbackAndHandles(newSelection);
		}
	};

	/**
	 * Creates feedback parts and handle parts for the given list of (selected)
	 * {@link IContentPart}s.
	 *
	 * @param selected
	 *            The list of {@link IContentPart}s for which feedback and
	 *            handles are created.
	 */
	protected void addFeedbackAndHandles(
			List<? extends IContentPart<VR, ? extends VR>> selected) {
		// root is responsible for multi selection
		if (getHost() instanceof IRootPart && selected.size() > 1) {
			addFeedback(selected);
			addHandles(selected);
			// TODO: optimize performance (generating feedback and handles) as
			// this seems to slow down marquee selection
		} else if (selected.contains(getHost())) {
			// reveal the clicked part
			getHost().getRoot().getViewer().reveal(getHost());
			// add feedback and handles
			addFeedback(Collections.singletonList(getHost()));
			if (selected.get(0) == getHost() && selected.size() <= 1) {
				addHandles(Collections.singletonList(getHost()));
			}
		}
	}

	@Override
	protected void doActivate() {
		// register
		final SelectionModel<VR> selectionModel = getSelectionModel();
		selectionModel.getSelectionUnmodifiable()
				.addListener(selectionObserver);

		// create feedback and handles if we are already selected
		addFeedbackAndHandles(selectionModel.getSelectionUnmodifiable());
	}

	@Override
	protected void doDeactivate() {
		final SelectionModel<VR> selectionModel = getSelectionModel();

		// remove any pending feedback
		removeFeedbackAndHandles(selectionModel.getSelectionUnmodifiable());

		// unregister
		selectionModel.getSelectionUnmodifiable()
				.removeListener(selectionObserver);
	}

	/**
	 * Returns the {@link SelectionModel} in the context of the
	 * {@link #getHost() host}.
	 *
	 * @return The {@link SelectionModel} in the context of the
	 *         {@link #getHost() host}.
	 */
	@SuppressWarnings("serial")
	protected SelectionModel<VR> getSelectionModel() {
		IViewer<VR> viewer = getHost().getRoot().getViewer();
		SelectionModel<VR> selectionModel = viewer
				.getAdapter(new TypeToken<SelectionModel<VR>>() {
				}.where(new TypeParameter<VR>() {
				}, Types.<VR> argumentOf(viewer.getClass())));
		return selectionModel;
	}

	/**
	 * Removes feedback parts and handle parts for the given list of (selected)
	 * {@link IContentPart}s.
	 *
	 * @param selected
	 *            The list of {@link IContentPart}s for which feedback and
	 *            handles are removed.
	 */
	protected void removeFeedbackAndHandles(
			List<? extends IContentPart<VR, ? extends VR>> selected) {
		// root is responsible for multi selection
		if (getHost() instanceof IRootPart && selected.size() > 1) {
			removeHandles(selected);
			removeFeedback(selected);
		} else if (selected.contains(getHost())) {
			removeHandles(Collections.singletonList(getHost()));
			removeFeedback(Collections.singletonList(getHost()));
		}
	}
}
