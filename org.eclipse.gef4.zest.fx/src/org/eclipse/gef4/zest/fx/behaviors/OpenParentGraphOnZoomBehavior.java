/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.behaviors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.models.ViewportModel;
import org.eclipse.gef4.zest.fx.parts.GraphRootPart;

// only applicable for GraphRootPart (see #getHost())
public class OpenParentGraphOnZoomBehavior extends AbstractBehavior<Node> {

	private PropertyChangeListener viewportPropertyChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			ContentModel contentModel = getHost().getViewer().getAdapter(
					ContentModel.class);
			List<? extends Object> contents = contentModel.getContents();
			if (contents.size() != 1) {
				return;
			}
			Graph graph = (Graph) contents.get(0);
			if (graph.getNestingNode() == null) {
				return;
			}

			if (ViewportModel.VIEWPORT_CONTENTS_TRANSFORM_PROPERTY.equals(evt
					.getPropertyName())) {
				AffineTransform oldTransform = (AffineTransform) evt
						.getOldValue();
				AffineTransform newTransform = (AffineTransform) evt
						.getNewValue();
				double oldScale = oldTransform.getScaleX();
				double newScale = newTransform.getScaleX();
				if (oldScale != newScale) {
					onZoomLevelChange(oldScale, newScale);
				}
			}
		}
	};

	@Override
	public void activate() {
		super.activate();
		// register viewport listener
		ViewportModel viewportModel = getHost().getRoot().getViewer()
				.getAdapter(ViewportModel.class);
		viewportModel.addPropertyChangeListener(viewportPropertyChangeListener);
	}

	@Override
	public void deactivate() {
		ViewportModel viewportModel = getHost().getRoot().getViewer()
				.getAdapter(ViewportModel.class);
		viewportModel
				.removePropertyChangeListener(viewportPropertyChangeListener);
		super.deactivate();
	}

	@Override
	public GraphRootPart getHost() {
		return (GraphRootPart) super.getHost();
	}

	protected void onZoomLevelChange(double oldScale, double newScale) {
		if (oldScale > newScale && newScale < 0.7) {
			// reset zoom level
			ViewportModel viewportModel = getHost().getRoot().getViewer()
					.getAdapter(ViewportModel.class);
			viewportModel.setContentsTransform(new AffineTransform());
			// replace contents
			ContentModel contentModel = getHost().getRoot().getViewer()
					.getAdapter(ContentModel.class);
			Graph graph = (Graph) contentModel.getContents().get(0);
			contentModel.setContents(Collections.singletonList(graph
					.getNestingNode().getGraph()));
		}
	}

}
