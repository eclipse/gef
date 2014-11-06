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
package org.eclipse.gef4.mvc.fx.behaviors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.scene.Node;
import javafx.scene.transform.Scale;

import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.models.ZoomModel;
import org.eclipse.gef4.mvc.parts.IRootPart;

public class FXZoomBehavior extends AbstractBehavior<Node> implements
		PropertyChangeListener {

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
	 * @param zoomFactor
	 *            The factor by which to apply the zoom.
	 */
	protected void applyZoom(double zoomFactor) {
		if (zoomFactor <= 0) {
			throw new IllegalArgumentException(
					"Expected: positive double. Given: <" + zoomFactor + ">.");
		}

		IRootPart<Node> root = getHost().getRoot();
		if (root instanceof FXRootPart) {
			FXRootPart fxRootPart = (FXRootPart) root;
			fxRootPart.zoomProperty().set(new Scale(zoomFactor, zoomFactor));
			// TODO: set zoom on content layer rather than via zoomProperty() on
			// FXRootPart
			// also set it on grid layer in case zoom grid is enabled??
		}
	}

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
