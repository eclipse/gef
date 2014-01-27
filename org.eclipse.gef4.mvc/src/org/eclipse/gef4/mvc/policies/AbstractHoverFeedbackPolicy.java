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
 * Note: Parts of this interface have been transferred from org.eclipse.gef.EditDomain.
 * 
 *******************************************************************************/
package org.eclipse.gef4.mvc.policies;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.gef4.mvc.models.IHoverModel;
import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;

/**
 * The AbstractSelectionFeedbackPolicy is responsible for creating and removing
 * selection feedback.
 * 
 * @author anyssen
 * 
 * @param <V>
 */
public abstract class AbstractHoverFeedbackPolicy<V> extends
		AbstractFeedbackPolicy<V> implements PropertyChangeListener {

	private boolean selected = false;

	@Override
	public void activate() {
		super.activate();
		getHost().getRoot().getViewer().getSelectionModel().addPropertyChangeListener(this);
		getHost().getRoot().getViewer().getHoverModel().addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		getHost().getRoot().getViewer().getSelectionModel().removePropertyChangeListener(this);
		getHost().getRoot().getViewer().getSelectionModel().removePropertyChangeListener(this);
		super.deactivate();
	}

	@SuppressWarnings({"unchecked"})
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(ISelectionModel.SELECTION_PROPERTY)) {
			List<IContentPart<V>> newSelection = (List<IContentPart<V>>) event.getNewValue();
			selected = newSelection.contains(getHost());
			if (selected) {
				unhover();
			}
		} else if (event.getPropertyName().equals(IHoverModel.HOVER_PROPERTY)) {
			IContentPart<V> oldHovered = (IContentPart<V>) event.getOldValue();
			IContentPart<V> newHovered = (IContentPart<V>) event.getNewValue();
			if (oldHovered == getHost()) {
				unhover();
			} else if (newHovered == getHost()) {
				if (!selected) {
					hover();
				}
			}
		}
	}

	public void hover() {
		hideFeedback();
		createFeedbackVisuals();
		applyFeedbackEffect();
		showFeedback();
	}

	public abstract void applyFeedbackEffect();

	public void unhover() {
		hideFeedback();
	}

}

