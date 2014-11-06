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
package org.eclipse.gef4.mvc.fx.behaviors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.models.ViewportModel;
import org.eclipse.gef4.mvc.parts.IRootPart;

public class FXViewportBehavior extends AbstractBehavior<Node> implements
		PropertyChangeListener {

	@Override
	public void activate() {
		super.activate();
		getHost().getRoot().getViewer().getAdapter(ViewportModel.class)
				.addPropertyChangeListener(this);
	}

	protected void applyViewport(double translateX, double translateY,
			double width, double height) {
		IRootPart<Node> root = getHost().getRoot();
		if (root instanceof FXRootPart) {
			FXRootPart fxRootPart = (FXRootPart) root;

			fxRootPart.getContentLayer().translateXProperty().set(translateX);
			fxRootPart.getContentLayer().translateYProperty().set(translateY);
			fxRootPart.getScrollPane().setPrefViewportWidth(width);
			fxRootPart.getScrollPane().setPrefViewportHeight(height);

		}
	}

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
