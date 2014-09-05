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

import org.eclipse.gef4.mvc.models.ZoomModel;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * The AbstractZoomPolicy registers a listener on the {@link ZoomModel} and
 * notifies subclasses about zoom factor changes in order for subclasses to
 * apply the new zoom factor.
 *
 * @author wienand
 *
 * @param <VR>
 *            The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractZoomBehavior<VR> extends AbstractBehavior<VR>
		implements PropertyChangeListener {

	@Override
	public void activate() {
		super.activate();
		getHost().getRoot().getViewer().getAdapter(ZoomModel.class)
				.addPropertyChangeListener(this);
	}

	/**
	 * Applies the given zoom factor in the context of this policy. For example,
	 * you can register the policy on the root visual part and apply it to all
	 * layers.
	 *
	 * @param zoom
	 *            The factor by which to apply the zoom.
	 */
	abstract protected void applyZoom(double zoom);

	@Override
	public void deactivate() {
		getHost().getRoot().getViewer().getAdapter(ZoomModel.class)
				.removePropertyChangeListener(this);
		super.deactivate();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (ZoomModel.ZOOM_FACTOR_PROPERTY.equals(evt.getPropertyName())) {
			applyZoom((Double) evt.getNewValue());
		}
	}

}
