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

import org.eclipse.gef4.fx.nodes.InfiniteCanvas;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;
import org.eclipse.gef4.zest.fx.policies.NavigationPolicy;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;

/**
 * The {@link OpenNestedGraphOnZoomBehavior} handles the navigation to a nested
 * graph when the user zooms into the nesting node.
 *
 * @author mwienand
 *
 */
// only applicable for NodeContentPart (see #getHost())
// TODO: refactor into policy -> directly react on zoom level change
public class OpenNestedGraphOnZoomBehavior extends AbstractBehavior<Node> {

	private ChangeListener<? super Number> scaleXListener = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			onZoomLevelChange(oldValue.doubleValue(), newValue.doubleValue());
		}
	};

	@Override
	public void activate() {
		super.activate();
		// only for nodes with nested graphs
		Graph nestedGraph = getHost().getContent().getNestedGraph();
		if (nestedGraph != null) {
			// register viewport listener
			((FXViewer) getHost().getRoot().getViewer()).getCanvas().getContentTransform().mxxProperty()
					.addListener(scaleXListener);
		}
	}

	@Override
	public void deactivate() {
		// unregister viewport listener
		((FXViewer) getHost().getRoot().getViewer()).getCanvas().getContentTransform().mxxProperty()
				.removeListener(scaleXListener);
		super.deactivate();
	}

	@Override
	public NodeContentPart getHost() {
		return (NodeContentPart) super.getHost();
	}

	/**
	 * Returns the {@link NavigationPolicy} that is installed on the
	 * {@link IRootPart} of the {@link #getHost() host}.
	 *
	 * @return The {@link NavigationPolicy} that is installed on the
	 *         {@link IRootPart} of the {@link #getHost() host}.
	 */
	protected NavigationPolicy getSemanticZoomPolicy() {
		return getHost().getRoot().getAdapter(NavigationPolicy.class);
	}

	/**
	 * Called upon zoom level changes. If the {@link #getHost() host} is nesting
	 * a {@link Graph}, the zoom level is changed beyond <code>3</code>, and the
	 * {@link #getHost() host} claims at least half of the viewport area, then
	 * the nested {@link Graph} is opened.
	 *
	 * @param oldScale
	 *            The previous zoom level.
	 * @param newScale
	 *            The new zoom level.
	 */
	// TODO: make zoom threshold configurable
	// TODO: loosen the viewport area rule and make it configurable
	protected void onZoomLevelChange(double oldScale, double newScale) {
		if (oldScale < newScale && newScale > 3) {
			// determine bounds of host visual
			Group hostVisual = getHost().getVisual();
			Bounds boundsInScene = hostVisual.localToScene(hostVisual.getLayoutBounds());
			// transform into the viewport coordinate system
			InfiniteCanvas infiniteCanvas = ((FXViewer) getHost().getRoot().getViewer()).getCanvas();
			org.eclipse.gef4.geometry.planar.Rectangle boundsInViewport = JavaFX2Geometry
					.toRectangle(infiniteCanvas.sceneToLocal(boundsInScene));
			// compute intersection with the viewport
			org.eclipse.gef4.geometry.planar.Rectangle viewportBounds = new org.eclipse.gef4.geometry.planar.Rectangle(
					0, 0, infiniteCanvas.getWidth(), infiniteCanvas.getHeight());
			org.eclipse.gef4.geometry.planar.Rectangle intersected = boundsInViewport.getIntersected(viewportBounds);
			// only open nested graph if we fill at least the half of the
			// viewport
			if (intersected.getArea() > viewportBounds.getArea() / 2) {
				final Graph nestedGraph = getHost().getContent().getNestedGraph();
				if (nestedGraph != null) {
					NavigationPolicy semanticZoomPolicy = getSemanticZoomPolicy();
					semanticZoomPolicy.init();
					semanticZoomPolicy.openNestedGraph(nestedGraph);
					ITransactionalOperation commit = semanticZoomPolicy.commit();
					if (commit != null) {
						getHost().getRoot().getViewer().getDomain().execute(commit);
					}
				}
			}
		}
	}

}
