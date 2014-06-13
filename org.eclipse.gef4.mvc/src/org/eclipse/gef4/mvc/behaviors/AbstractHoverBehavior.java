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

import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.bindings.IProvider;
import org.eclipse.gef4.mvc.models.IHoverModel;
import org.eclipse.gef4.mvc.parts.IContentPart;

/**
 * The AbstractSelectionFeedbackPolicy is responsible for creating and removing
 * selection feedback.
 * 
 * @author anyssen
 * 
 * @param <VR>
 */
public abstract class AbstractHoverBehavior<VR> extends AbstractBehavior<VR>
		implements PropertyChangeListener {

	private IProvider<IGeometry> feedbackGeometryProvider = new IProvider<IGeometry>() {
		@Override
		public IGeometry get() {
			return getFeedbackGeometry();
		}
	};

	@Override
	public void activate() {
		super.activate();
		getHost().getRoot().getViewer().getHoverModel()
				.addPropertyChangeListener(this);
		
		// create feedback and handles if we are already hovered
		addFeedbackAndHandles((IContentPart<VR>) getHost().getRoot().getViewer().getHoverModel().getHover());
	}

	@Override
	public void deactivate() {
		// remove any pending feedback and handles
		removeFeedbackAndHandles((IContentPart<VR>) getHost().getRoot().getViewer().getHoverModel().getHover());
		
		getHost().getRoot().getViewer().getHoverModel()
				.removePropertyChangeListener(this);
		super.deactivate();
	}

	/**
	 * Returns an {@link IGeometry} for which visual selection feedback will be
	 * provided.
	 * 
	 * @return an {@link IGeometry} determining feedback positions
	 */
	protected abstract IGeometry getFeedbackGeometry();

	@SuppressWarnings({ "unchecked" })
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(IHoverModel.HOVER_PROPERTY)) {
			IContentPart<VR> oldHovered = (IContentPart<VR>) event.getOldValue();
			IContentPart<VR> newHovered = (IContentPart<VR>) event.getNewValue();

			removeFeedbackAndHandles(oldHovered);
			addFeedbackAndHandles(newHovered);
		}
	}

	protected void addFeedbackAndHandles(IContentPart<VR> newHovered) {
		if (newHovered == getHost()) {
			addFeedback(Collections
					.singletonList((IContentPart<VR>) getHost()));
			addHandles(Collections
					.singletonList((IContentPart<VR>) getHost()));
		}
	}

	protected void removeFeedbackAndHandles(IContentPart<VR> oldHovered) {
		if (oldHovered == getHost()) {
			removeHandles(Collections
					.singletonList((IContentPart<VR>) getHost()));
			removeFeedback(Collections
					.singletonList((IContentPart<VR>) getHost()));
		}
	}

	public IProvider<IGeometry> getFeedbackGeometryProvider() {
		return feedbackGeometryProvider;
	}

}
