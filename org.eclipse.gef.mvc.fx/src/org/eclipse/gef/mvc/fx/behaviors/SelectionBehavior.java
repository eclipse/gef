/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - multi selection handles in root part
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.behaviors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.common.collections.CollectionUtils;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.IHandlePartFactory;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;

/**
 * The default selection behavior is responsible for creating and removing
 * selection feedback and handles.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class SelectionBehavior extends AbstractBehavior {

	/**
	 * The adapter role for the {@link IFeedbackPartFactory} that is used to
	 * generate hover feedback parts.
	 */
	public static final String SELECTION_FEEDBACK_PART_FACTORY = "SELECTION_FEEDBACK_PART_FACTORY";

	/**
	 * The adapter role for the {@link IHandlePartFactory} that is used to
	 * generate hover handle parts.
	 */
	public static final String SELECTION_HANDLE_PART_FACTORY = "SELECTION_HANDLE_PART_FACTORY";

	private ListChangeListener<IContentPart<? extends Node>> selectionObserver = new ListChangeListener<IContentPart<? extends Node>>() {
		@Override
		public void onChanged(
				ListChangeListener.Change<? extends IContentPart<? extends Node>> c) {
			// order of selection should not be relevant for feedback and
			// handles, as such we ignore permutations
			List<IContentPart<? extends Node>> newSelection = new ArrayList<>(
					c.getList());
			List<? extends IContentPart<? extends Node>> oldSelection = CollectionUtils
					.getPreviousContents(c);
			removeFeedbackAndHandles(oldSelection);
			addFeedbackAndHandles(newSelection);
		}
	};

	/**
	 * @param selected
	 *            List of {@link IContentPart}s for which to add feedback and
	 *            handles.
	 */
	protected void addFeedbackAndHandles(
			List<? extends IContentPart<? extends Node>> selected) {
		if (!selected.isEmpty()) {
			// XXX: For a multi selection, feedback and handles are generated
			// for the whole selection at once, so the factories can decide how
			// to generate feedback.
			if (selected.size() == 1) {
				// add handles for the single selection
				addFeedback(selected.get(0));
				addHandles(selected.get(0));
			} else {
				// add handles for the whole multi selection
				addFeedback(selected);
				addHandles(selected);
			}
		}
	}

	@Override
	protected void doActivate() {
		// register
		final SelectionModel selectionModel = getSelectionModel();
		selectionModel.getSelectionUnmodifiable()
				.addListener(selectionObserver);

		// create feedback and handles if we are already selected
		addFeedbackAndHandles(selectionModel.getSelectionUnmodifiable());
	}

	@Override
	protected void doDeactivate() {
		final SelectionModel selectionModel = getSelectionModel();

		// remove any pending feedback
		removeFeedbackAndHandles(selectionModel.getSelectionUnmodifiable());

		// unregister
		selectionModel.getSelectionUnmodifiable()
				.removeListener(selectionObserver);
	}

	@Override
	protected IFeedbackPartFactory getFeedbackPartFactory(IViewer viewer) {
		return getFeedbackPartFactory(viewer, SELECTION_FEEDBACK_PART_FACTORY);
	}

	@Override
	protected IHandlePartFactory getHandlePartFactory(IViewer viewer) {
		return getHandlePartFactory(viewer, SELECTION_HANDLE_PART_FACTORY);
	}

	/**
	 * Returns the {@link SelectionModel} in the context of the
	 * {@link #getHost() host}.
	 *
	 * @return The {@link SelectionModel} in the context of the
	 *         {@link #getHost() host}.
	 */
	protected SelectionModel getSelectionModel() {
		IViewer viewer = getHost().getRoot().getViewer();
		SelectionModel selectionModel = viewer.getAdapter(SelectionModel.class);
		return selectionModel;
	}

	/**
	 * @param selected
	 *            List of {@link IContentPart}s for which to remove feedback and
	 *            handles.
	 */
	protected void removeFeedbackAndHandles(
			List<? extends IContentPart<? extends Node>> selected) {
		if (!selected.isEmpty()) {
			// XXX: For a multi selection, feedback and handles are handled for
			// the whole selection
			if (selected.size() == 1) {
				// remove handles for the single selection
				removeHandles(selected.get(0));
				removeFeedback(selected.get(0));
			} else {
				// remove handles for the multi selection
				removeHandles(selected);
				removeFeedback(selected);
			}
		}
	}
}
