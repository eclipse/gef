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
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public class HoverBehavior<VR> extends AbstractBehavior<VR> implements
		PropertyChangeListener {

	@Override
	public void activate() {
		super.activate();
		HoverModel<VR> hoverModel = getHoverModel();

		// register
		hoverModel.addPropertyChangeListener(this);

		// create feedback and handles if we are already hovered
		IVisualPart<VR> hover = hoverModel.getHover();
		if (hover != null) {
			onHoverChange(null, hover);
		}
	}

	@Override
	public void deactivate() {
		HoverModel<VR> hoverModel = getHoverModel();

		// remove any pending feedback and handles
		removeFeedback(Collections.singletonList(getHost()));
		removeHandles(Collections.singletonList(getHost()));

		// unregister
		hoverModel.removePropertyChangeListener(this);
		super.deactivate();
	}

	@SuppressWarnings("unchecked")
	protected HoverModel<VR> getHoverModel() {
		return getHost().getRoot().getViewer().getAdapter(HoverModel.class);
	}

	protected void onHoverChange(IVisualPart<VR> oldHovered,
			IVisualPart<VR> newHovered) {
		if (getHost() != oldHovered && getHost() == newHovered) {
			addFeedback(Collections.singletonList(getHost()));
		} else if (getHost() == oldHovered && getHost() != newHovered) {
			removeFeedback(Collections.singletonList(getHost()));
		}
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(HoverModel.HOVER_PROPERTY)) {
			IVisualPart<VR> oldHovered = (IVisualPart<VR>) event.getOldValue();
			IVisualPart<VR> newHovered = (IVisualPart<VR>) event.getNewValue();
			onHoverChange(oldHovered, newHovered);
		}
	}

}
