/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
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
package org.eclipse.gef.mvc.behaviors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.common.collections.CollectionUtils;
import org.eclipse.gef.common.reflect.Types;
import org.eclipse.gef.mvc.models.SelectionModel;
import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef.mvc.parts.IHandlePartFactory;
import org.eclipse.gef.mvc.viewer.IViewer;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

import javafx.collections.ListChangeListener;

/**
 * The default selection behavior is responsible for creating and removing
 * selection feedback and handles.
 *
 * @author anyssen
 * @author mwienand
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public class SelectionBehavior<VR> extends AbstractBehavior<VR> {

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

	private ListChangeListener<IContentPart<VR, ? extends VR>> selectionObserver = new ListChangeListener<IContentPart<VR, ? extends VR>>() {
		@Override
		public void onChanged(
				ListChangeListener.Change<? extends IContentPart<VR, ? extends VR>> c) {
			// order of selection should not be relevant for feedback and
			// handles, as such we ignore permutations
			List<IContentPart<VR, ? extends VR>> newSelection = new ArrayList<>(
					c.getList());
			List<? extends IContentPart<VR, ? extends VR>> oldSelection = CollectionUtils
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
			List<? extends IContentPart<VR, ? extends VR>> selected) {
		if (!selected.isEmpty()) {
			// reveal primary selection
			getHost().getRoot().getViewer().reveal(selected.get(0));
			// add feedback individually for the selected parts
			for (IContentPart<VR, ? extends VR> sel : selected) {
				addFeedback(sel);
			}
			// XXX: For a multi selection, handles are generated for the whole
			// selection and not for each part individually. For a single
			// selection, handles are generated for the only selected part.
			if (selected.size() == 1) {
				// add handles for the single selection
				addHandles(selected.get(0));
			} else {
				// add handles for the whole multi selection
				addHandles(selected);
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

	@Override
	protected String getFeedbackPartFactoryRole() {
		return SELECTION_FEEDBACK_PART_FACTORY;
	}

	@Override
	protected String getHandlePartFactoryRole() {
		return SELECTION_HANDLE_PART_FACTORY;
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
	 * @param selected
	 *            List of {@link IContentPart}s for which to remove feedback and
	 *            handles.
	 */
	protected void removeFeedbackAndHandles(
			List<? extends IContentPart<VR, ? extends VR>> selected) {
		if (!selected.isEmpty()) {
			// remove feedback individually for all parts
			for (IContentPart<VR, ? extends VR> sel : selected) {
				removeFeedback(sel);
			}
			// XXX: For a multi selection, handles are generated for the whole
			// selection and not for each part individually. For a single
			// selection, handles are generated for the only selected part.
			if (selected.size() == 1) {
				// remove handles for the single selection
				removeHandles(selected.get(0));
			} else {
				// remove handles for the multi selection
				removeHandles(selected);
			}
		}
	}

}
