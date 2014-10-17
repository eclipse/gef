/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.behaviors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;

import org.eclipse.gef4.mvc.models.HoverModel;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * The {@link HoverBehavior} is responsible for creating and removing selection
 * feedback.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public class HoverBehavior<VR> extends AbstractBehavior<VR> implements
		PropertyChangeListener {

	@Override
	public void activate() {
		super.activate();
		HoverModel hoverModel = getHost().getRoot().getViewer()
				.getAdapter(HoverModel.class);

		// register
		hoverModel.addPropertyChangeListener(this);

		// create feedback and handles if we are already hovered
		addFeedbackAndHandles(hoverModel.getHover());
	}

	protected final void addFeedbackAndHandles(IVisualPart<VR> newHovered) {
		if (newHovered == getHost()) {
			addFeedback(Collections.singletonList(getHost()));
			addHandles(Collections.singletonList(getHost()));
		}
	}

	@Override
	public void deactivate() {
		HoverModel hoverModel = getHost().getRoot().getViewer()
				.getAdapter(HoverModel.class);

		// remove any pending feedback and handles
		removeFeedbackAndHandles(hoverModel.getHover());

		// unregister
		hoverModel.removePropertyChangeListener(this);
		super.deactivate();
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(HoverModel.HOVER_PROPERTY)) {
			IVisualPart<VR> oldHovered = (IVisualPart<VR>) event.getOldValue();
			IVisualPart<VR> newHovered = (IVisualPart<VR>) event.getNewValue();

			removeFeedbackAndHandles(oldHovered);
			addFeedbackAndHandles(newHovered);
		}
	}

	protected final void removeFeedbackAndHandles(IVisualPart<VR> oldHovered) {
		if (oldHovered == getHost()) {
			removeHandles(Collections.singletonList(getHost()));
			removeFeedback(Collections.singletonList(getHost()));
		}
	}

}
