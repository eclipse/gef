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

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.fx.nodes.ScrollPaneEx;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ViewportModel;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;
import org.eclipse.gef4.zest.fx.policies.NavigationPolicy;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Transform;

// only applicable for NodeContentPart (see #getHost())
// TODO: refactor into policy -> directly react on zoom level change
public class OpenNestedGraphOnZoomBehavior extends AbstractBehavior<Node> {

	protected double zoomLevel;

	private PropertyChangeListener viewportPropertyChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			/*
			 * The PropertyChangeEvent could have been processed already and
			 * could have deactivated our host, in which case we should not do
			 * anything.
			 */
			if (!isActive()) {
				// TODO: Enhance property change mechanism to allow for easier
				// decoupling of behaviors. For example, an event could be
				// consumed to not notify any more listeners.
				return;
			}
			if (ViewportModel.VIEWPORT_CONTENTS_TRANSFORM_PROPERTY.equals(evt.getPropertyName())) {
				Transform localToSceneTransform = getHost().getVisual().getLocalToSceneTransform();
				AffineTransform transform = JavaFX2Geometry.toAffineTransform(localToSceneTransform);
				double lastZoomLevel = zoomLevel;
				zoomLevel = transform.getScaleX();
				onZoomLevelChange(lastZoomLevel, zoomLevel);
			}
		}
	};

	@Override
	public void activate() {
		super.activate();
		// only for nodes with nested graphs
		Graph nestedGraph = getHost().getContent().getNestedGraph();
		if (nestedGraph != null) {
			// determine initial zoom level
			zoomLevel = JavaFX2Geometry.toAffineTransform(getHost().getVisual().getLocalToSceneTransform()).getScaleX();
			// register viewport listener
			ViewportModel viewportModel = getHost().getRoot().getViewer().getAdapter(ViewportModel.class);
			viewportModel.addPropertyChangeListener(viewportPropertyChangeListener);
		}
	}

	@Override
	public void deactivate() {
		ViewportModel viewportModel = getHost().getRoot().getViewer().getAdapter(ViewportModel.class);
		viewportModel.removePropertyChangeListener(viewportPropertyChangeListener);
		super.deactivate();
	}

	@Override
	public NodeContentPart getHost() {
		return (NodeContentPart) super.getHost();
	}

	protected NavigationPolicy getSemanticZoomPolicy() {
		return getHost().getRoot().getAdapter(NavigationPolicy.class);
	}

	protected void onZoomLevelChange(double oldScale, double newScale) {
		if (oldScale < newScale && newScale > 3) {
			// determine bounds of host visual
			Group hostVisual = getHost().getVisual();
			Bounds boundsInScene = hostVisual.localToScene(hostVisual.getLayoutBounds());
			// transform into the viewport coordinate system
			ScrollPaneEx scrollPane = ((FXViewer) getHost().getRoot().getViewer()).getScrollPane();
			org.eclipse.gef4.geometry.planar.Rectangle boundsInViewport = JavaFX2Geometry
					.toRectangle(scrollPane.sceneToLocal(boundsInScene));
			// compute intersection with the viewport
			org.eclipse.gef4.geometry.planar.Rectangle viewportBounds = new org.eclipse.gef4.geometry.planar.Rectangle(
					0, 0, scrollPane.getWidth(), scrollPane.getHeight());
			org.eclipse.gef4.geometry.planar.Rectangle intersected = boundsInViewport.getIntersected(viewportBounds);
			// only open nested graph if we fill at least the half of the
			// viewport
			if (intersected.getArea() > viewportBounds.getArea() / 2) {
				final Graph nestedGraph = getHost().getContent().getNestedGraph();
				if (nestedGraph != null) {
					NavigationPolicy semanticZoomPolicy = getSemanticZoomPolicy();
					semanticZoomPolicy.init();
					semanticZoomPolicy.openNestedGraph(nestedGraph);
					IUndoableOperation commit = semanticZoomPolicy.commit();
					if (commit != null) {
						getHost().getRoot().getViewer().getDomain().execute(commit);
					}
				}
			}
		}
	}
}
