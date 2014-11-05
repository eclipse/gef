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

import org.eclipse.gef4.mvc.models.ViewportModel;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractViewportBehavior<VR> extends AbstractBehavior<VR>
		implements PropertyChangeListener {

	@Override
	public void activate() {
		super.activate();
		getHost().getRoot().getViewer().getAdapter(ViewportModel.class)
				.addPropertyChangeListener(this);
	}

	protected abstract void applyViewport(double translateX, double translateY,
			double width, double height);

	@Override
	public void deactivate() {
		getHost().getRoot().getViewer().getAdapter(ViewportModel.class)
				.removePropertyChangeListener(this);
		super.deactivate();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (ViewportModel.VIEWPORT_TRANSLATE_X_PROPERTY.equals(evt
				.getPropertyName())
				|| ViewportModel.VIEWPORT_TRANSLATE_Y_PROPERTY.equals(evt
						.getPropertyName())
				|| ViewportModel.VIEWPORT_WIDTH_PROPERTY.equals(evt
						.getPropertyName())
				|| ViewportModel.VIEWPORT_HEIGHT_PROPERTY.equals(evt
						.getPropertyName())) {
			ViewportModel viewportModel = getHost().getRoot().getViewer()
					.getAdapter(ViewportModel.class);
			applyViewport(viewportModel.getTranslateX(),
					viewportModel.getTranslateY(), viewportModel.getWidth(),
					viewportModel.getHeight());
		}
	}

}