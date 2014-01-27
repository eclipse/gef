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
public abstract class AbstractSelectionFeedbackPolicy<V> extends
		AbstractFeedbackPolicy<V> implements PropertyChangeListener {

	@Override
	public void activate() {
		super.activate();
		getHost().getRoot().getViewer().getSelectionModel()
				.addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		getHost().getRoot().getViewer().getSelectionModel()
				.removePropertyChangeListener(this);
		super.deactivate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(ISelectionModel.SELECTION_PROPERTY)) {
			List<IContentPart<V>> oldSelection = (List<IContentPart<V>>) event
					.getOldValue();
			List<IContentPart<V>> newSelection = (List<IContentPart<V>>) event
					.getNewValue();

			boolean inOld = oldSelection.contains(getHost());
			boolean inNew = newSelection.contains(getHost());
			if (inOld && !inNew) {
				deselect();
			} else if (!inOld && inNew) {
				if (newSelection.get(0) == getHost()) {
					selectPrimary();
				} else {
					selectSecondary();
				}
			}
		}
	}

	public void selectSecondary() {
	}

	public void deselect() {
		hideFeedback();
	}

	public void selectPrimary() {
		hideFeedback();
		createFeedbackVisuals();
		applyFeedbackEffect();
		showFeedback();
	}

	/**
	 * <pre>feedback.setEffect(dropShadow);</pre>
	 */
	public abstract void applyFeedbackEffect();

}
