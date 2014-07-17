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
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * The AbstractZoomPolicy registers a listener on the {@link IZoomModel} and
 * notifies subclasses about zoom factor changes in order for subclasses to
 * apply the new zoom factor.
 * 
 * @author wienand
 * 
 * @param <VR> The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractZoomBehavior<VR> extends AbstractBehavior<VR>
		implements PropertyChangeListener {

	@Override
	public void activate() {
		super.activate();
		getHost().getRoot().getViewer().getZoomModel()
				.addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		getHost().getRoot().getViewer().getZoomModel()
				.removePropertyChangeListener(this);
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
	 *            The factor by which to apply the zoom.
	 */
	abstract protected void applyZoomFactor(Double zoomFactor);

}
