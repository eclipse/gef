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
import java.util.List;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.models.ViewportModel;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.zest.fx.parts.GraphRootPart;
import org.eclipse.gef4.zest.fx.policies.NavigationPolicy;

import javafx.scene.Node;

/**
 * The {@link OpenParentGraphOnZoomBehavior} handles the navigation to a parent
 * graph when the user zooms out of a nested graph.
 *
 * @author mwienand
 *
 */
// only applicable for GraphRootPart (see #getHost())
// TODO: refactor into policy -> directly react on zoom level change
public class OpenParentGraphOnZoomBehavior extends AbstractBehavior<Node> {

	private PropertyChangeListener viewportPropertyChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			ContentModel contentModel = getHost().getViewer().getAdapter(ContentModel.class);
			List<? extends Object> contents = contentModel.getContents();
			if (contents.size() != 1) {
				return;
			}
			Graph graph = (Graph) contents.get(0);
			if (graph.getNestingNode() == null) {
				return;
			}

			if (ViewportModel.VIEWPORT_CONTENTS_TRANSFORM_PROPERTY.equals(evt.getPropertyName())) {
				AffineTransform oldTransform = (AffineTransform) evt.getOldValue();
				AffineTransform newTransform = (AffineTransform) evt.getNewValue();
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
		ViewportModel viewportModel = getHost().getRoot().getViewer().getAdapter(ViewportModel.class);
		viewportModel.addPropertyChangeListener(viewportPropertyChangeListener);
	}

	@Override
	public void deactivate() {
		ViewportModel viewportModel = getHost().getRoot().getViewer().getAdapter(ViewportModel.class);
		viewportModel.removePropertyChangeListener(viewportPropertyChangeListener);
		super.deactivate();
	}

	@Override
	public GraphRootPart getHost() {
		return (GraphRootPart) super.getHost();
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
	 * Called upon zoom level changes (reported by the {@link ViewportModel}).
	 * If the {@link #getHost() host} is nested inside a {@link Node} and the
	 * zoom level is changed below <code>0.7</code>, then the {@link Graph} to
	 * which the nesting {@link Node} belongs is opened.
	 *
	 * @param oldScale
	 *            The previous zoom level.
	 * @param newScale
	 *            The new zoom level.
	 */
	protected void onZoomLevelChange(double oldScale, double newScale) {
		if (oldScale > newScale && newScale < 0.7) {
			ContentModel contentModel = getHost().getRoot().getViewer().getAdapter(ContentModel.class);
			if (contentModel == null) {
				throw new IllegalArgumentException("ContentModel could not be obtained!");
			}

			final Graph currentGraph = (Graph) contentModel.getContents().get(0);
			final Graph nestingGraph = currentGraph.getNestingNode() != null ? currentGraph.getNestingNode().getGraph()
					: null;

			if (nestingGraph != null) {
				NavigationPolicy semanticZoomPolicy = getSemanticZoomPolicy();
				semanticZoomPolicy.init();
				semanticZoomPolicy.openNestingGraph(nestingGraph);
				IUndoableOperation commit = semanticZoomPolicy.commit();
				if (commit != null) {
					getHost().getRoot().getViewer().getDomain().execute(commit);
				}
			}
		}
	}

}
