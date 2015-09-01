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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IRootPart;

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
public class SelectionBehavior<VR> extends AbstractBehavior<VR> implements
		PropertyChangeListener {

	@Override
	public void activate() {
		super.activate();
		SelectionModel<VR> selectionModel = getHost().getRoot().getViewer()
				.<SelectionModel<VR>> getAdapter(SelectionModel.class);

		// register
		selectionModel.addPropertyChangeListener(this);

		// create feedback and handles if we are already selected
		addFeedbackAndHandles(selectionModel.getSelected());
	}

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
	public void deactivate() {
		// remove any pending feedback
		SelectionModel<VR> selectionModel = getHost().getRoot().getViewer()
				.<SelectionModel<VR>> getAdapter(SelectionModel.class);
		removeFeedbackAndHandles(selectionModel.getSelected());

		selectionModel.removePropertyChangeListener(this);
		super.deactivate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(SelectionModel.SELECTION_PROPERTY)) {
			List<IContentPart<VR, ? extends VR>> oldSelection = (List<IContentPart<VR, ? extends VR>>) event
					.getOldValue();
			List<IContentPart<VR, ? extends VR>> newSelection = (List<IContentPart<VR, ? extends VR>>) event
					.getNewValue();

			removeFeedbackAndHandles(oldSelection);
			addFeedbackAndHandles(newSelection);
		}
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
