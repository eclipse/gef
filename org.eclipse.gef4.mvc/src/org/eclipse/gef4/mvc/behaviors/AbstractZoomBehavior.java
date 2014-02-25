/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.behaviors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef4.mvc.models.IZoomModel;

/**
 * The AbstractZoomPolicy registers a listener on the {@link IZoomModel} and
 * notifies subclasses about zoom factor changes in order for subclasses to
 * apply the new zoom factor.
 * 
 * @author wienand
 * 
 */
public abstract class AbstractZoomBehavior<V> extends AbstractBehavior<V>
		implements PropertyChangeListener {

	@Override
	public void activate() {
		super.activate();
		getHost().getRoot().getViewer().getZoomModel().addPropertyChangeListener(this);
	}
	
	@Override
	public void deactivate() {
		getHost().getRoot().getViewer().getZoomModel().removePropertyChangeListener(this);
		super.deactivate();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (IZoomModel.ZOOM_FACTOR_PROPERTY.equals(evt.getPropertyName())) {
			applyZoomFactor((Double) evt.getNewValue());
		}
	}

	/**
	 * Applies the given zoom factor in the context of this policy. For example,
	 * you can register the policy on the root visual part and apply it to all
	 * layers.
	 * 
	 * @param zoomFactor
	 */
	abstract protected void applyZoomFactor(Double zoomFactor);

}
