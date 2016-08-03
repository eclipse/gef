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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.collections.CollectionUtils;
import org.eclipse.gef.common.reflect.Types;
import org.eclipse.gef.mvc.models.SelectionModel;
import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.mvc.parts.IFeedbackPart;
import org.eclipse.gef.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef.mvc.parts.IHandlePart;
import org.eclipse.gef.mvc.parts.IHandlePartFactory;
import org.eclipse.gef.mvc.parts.IVisualPart;
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

	private FeedbackAndHandlesDelegate<VR> feedbackAndHandlesDelegate = new FeedbackAndHandlesDelegate<>(
			this);

	@Override
	protected void addFeedback(
			List<? extends IVisualPart<VR, ? extends VR>> targets,
			List<? extends IFeedbackPart<VR, ? extends VR>> feedback) {
		throw new UnsupportedOperationException();
	}

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
				feedbackAndHandlesDelegate.addHandles(sel,
						getHandlePartFactory());
			}
			// add feedback individually for the selected parts
			for (IContentPart<VR, ? extends VR> sel : selected) {
				feedbackAndHandlesDelegate.addFeedback(sel,
						getFeedbackPartFactory());
			}
		}
	}

	@Override
	protected void addHandles(
			List<? extends IVisualPart<VR, ? extends VR>> targets,
			List<? extends IHandlePart<VR, ? extends VR>> handles) {
		throw new UnsupportedOperationException();
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
	 * Returns the {@link IFeedbackPartFactory} for selection feedback.
	 *
	 * @return The {@link IFeedbackPartFactory} for selection feedback.
	 */
	@SuppressWarnings("serial")
	protected IFeedbackPartFactory<VR> getFeedbackPartFactory() {
		IViewer<VR> viewer = getHost().getRoot().getViewer();
		return viewer.getAdapter(
				AdapterKey.get(new TypeToken<IFeedbackPartFactory<VR>>() {
				}.where(new TypeParameter<VR>() {
				}, Types.<VR> argumentOf(viewer.getClass())),
						SELECTION_FEEDBACK_PART_FACTORY));
	}

	@Override
	protected List<IFeedbackPart<VR, ? extends VR>> getFeedbackParts() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the {@link IHandlePartFactory} for selection feedback.
	 *
	 * @return The {@link IHandlePartFactory} for selection feedback.
	 */
	@SuppressWarnings("serial")
	protected IHandlePartFactory<VR> getHandlePartFactory() {
		IViewer<VR> viewer = getHost().getRoot().getViewer();
		return viewer.getAdapter(
				AdapterKey.get(new TypeToken<IHandlePartFactory<VR>>() {
				}.where(new TypeParameter<VR>() {
				}, Types.<VR> argumentOf(viewer.getClass())),
						SELECTION_HANDLE_PART_FACTORY));
	}

	@Override
	protected List<IHandlePart<VR, ? extends VR>> getHandleParts() {
		throw new UnsupportedOperationException();
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

	@Override
	protected void removeFeedback(
			List<? extends IVisualPart<VR, ? extends VR>> targets) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @param selected
	 *            List of {@link IContentPart}s for which to remove feedback and
	 *            handles.
	 */
	protected void removeFeedbackAndHandles(
			List<? extends IContentPart<VR, ? extends VR>> selected) {
		if (!selected.isEmpty()) {
			for (IContentPart<VR, ? extends VR> sel : selected) {
				feedbackAndHandlesDelegate.removeHandles(sel);
			}
			for (IContentPart<VR, ? extends VR> sel : selected) {
				feedbackAndHandlesDelegate.removeFeedback(sel);
			}
		}
	}

	@Override
	protected void removeHandles(
			List<? extends IVisualPart<VR, ? extends VR>> targets) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void switchAdaptableScopes() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Updates the handles of the given host part.
	 *
	 * @param host
	 *            The host part of the handles.
	 * @param interactedWithComparator
	 *            A {@link Comparator} that can be used to identify a new handle
	 *            at the same position as the handle that is currently
	 *            interacted with. Can be <code>null</code> if no handle should
	 *            be preserved.
	 * @param interactedWith
	 *            The {@link IHandlePart} that is interacted with, or
	 *            <code>null</code>.
	 * @return The new {@link IHandlePart} for the position of the handle part
	 *         that is interacted with so that its information can be applied to
	 *         the preserved handle part.
	 */
	public IHandlePart<VR, ? extends VR> updateHandles(
			IContentPart<VR, ? extends VR> host,
			Comparator<IHandlePart<VR, ? extends VR>> interactedWithComparator,
			IHandlePart<VR, ? extends VR> interactedWith) {
		return feedbackAndHandlesDelegate.updateHandles(host,
				Collections.singletonList(host), getHandlePartFactory(),
				interactedWithComparator, interactedWith);
	}

}
