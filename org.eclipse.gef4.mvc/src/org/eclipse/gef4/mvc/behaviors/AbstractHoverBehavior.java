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
import java.util.Map;

import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.models.IHoverModel;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.inject.Provider;

/**
 * The {@link AbstractHoverBehavior} is responsible for creating and removing
 * selection feedback.
 * 
 * @author anyssen
 * 
 * @param <VR>
 */
public abstract class AbstractHoverBehavior<VR> extends AbstractBehavior<VR>
		implements PropertyChangeListener {

	@Override
	public void activate() {
		super.activate();
		getHost().getRoot().getViewer().getHoverModel()
				.addPropertyChangeListener(this);
		
		// create feedback and handles if we are already hovered
		addFeedbackAndHandles(getHost().getRoot().getViewer().getHoverModel().getHover());
	}

	@Override
	public void deactivate() {
		// remove any pending feedback and handles
		removeFeedbackAndHandles(getHost().getRoot().getViewer().getHoverModel().getHover());
		
		getHost().getRoot().getViewer().getHoverModel()
				.removePropertyChangeListener(this);
		super.deactivate();
	}

	/**
	 * Returns an {@link IGeometry} for which visual selection feedback will be
	 * provided.
	 * @param contextMap TODO
	 * 
	 * @return an {@link IGeometry} determining feedback positions
	 */
	protected abstract IGeometry getFeedbackGeometry(Map<Object, Object> contextMap);

	@SuppressWarnings({ "unchecked" })
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(IHoverModel.HOVER_PROPERTY)) {
			IVisualPart<VR> oldHovered = (IVisualPart<VR>) event.getOldValue();
			IVisualPart<VR> newHovered = (IVisualPart<VR>) event.getNewValue();

			removeFeedbackAndHandles(oldHovered);
			addFeedbackAndHandles(newHovered);
		}
	}

	protected final void addFeedbackAndHandles(IVisualPart<VR> newHovered) {
		if (newHovered == getHost()) {
			addFeedback(Collections
					.singletonList(getHost()));
			addHandles(Collections
					.singletonList(getHost()));
		}
	}

	protected final void removeFeedbackAndHandles(IVisualPart<VR> oldHovered) {
		if (oldHovered == getHost()) {
			removeHandles(Collections
					.singletonList(getHost()));
			removeFeedback(Collections
					.singletonList(getHost()));
		}
	}

	public Provider<IGeometry> getFeedbackGeometryProvider(final Map<Object, Object> contextMap) {
		return new Provider<IGeometry>() {
			@Override
			public IGeometry get() {
				return getFeedbackGeometry(contextMap);
			}
		};
	}

}
