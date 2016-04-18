/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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
import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.layout.ILayoutAlgorithm;
import org.eclipse.gef4.layout.ILayoutFilter;
import org.eclipse.gef4.layout.LayoutContext;
import org.eclipse.gef4.layout.LayoutProperties;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.models.HidingModel;
import org.eclipse.gef4.zest.fx.models.NavigationModel;
import org.eclipse.gef4.zest.fx.models.NavigationModel.ViewportState;
import org.eclipse.gef4.zest.fx.parts.GraphPart;
import org.eclipse.gef4.zest.fx.parts.NodePart;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Parent;

/**
 * The {@link GraphLayoutBehavior} is responsible for initiating layout passes.
 * It is only applicable to {@link GraphPart}.
 *
 * @author mwienand
 *
 */
// only applicable for GraphPart (see #getHost())
public class GraphLayoutBehavior extends AbstractLayoutBehavior {

	private Parent nestingVisual;

	private ChangeListener<? super Bounds> nestingVisualLayoutBoundsChangeListener = new ChangeListener<Bounds>() {
		@Override
		public void changed(ObservableValue<? extends Bounds> observable, Bounds oldLayoutBounds,
				Bounds newLayoutBounds) {
			updateBounds();
		}
	};

	private ChangeListener<? super Bounds> viewportBoundsChangeListener = new ChangeListener<Bounds>() {
		@Override
		public void changed(ObservableValue<? extends Bounds> observable, Bounds oldLayoutBounds,
				Bounds newLayoutBounds) {
			updateBounds();
		}
	};

	/**
	 * Performs one layout pass using the static layout algorithm that is
	 * configured for the layout context.
	 *
	 * @param clean
	 *            Whether to fully re-compute the layout or not.
	 */
	public void applyLayout(boolean clean) {
		Graph graph = getHost().getContent();

		// update layout algorithm (apply layout will depend on it)
		LayoutContext layoutContext = getLayoutContext();
		ILayoutAlgorithm layoutAlgorithm = ZestProperties.getLayoutAlgorithm(graph);
		if (layoutAlgorithm != null) {
			if (layoutContext.getLayoutAlgorithm() != layoutAlgorithm) {
				layoutContext.setLayoutAlgorithm(layoutAlgorithm);
			}
		} else {
			if (layoutContext.getLayoutAlgorithm() != null) {
				layoutContext.setLayoutAlgorithm(null);
			}
		}

		// update the graph
		if (layoutContext.getGraph() != graph) {
			layoutContext.setGraph(graph);
		}

		// apply layout (if no algorithm is set, will be a no-op)
		layoutContext.applyLayout(true);
	}

	/**
	 * Determines the layout bounds for the graph.
	 *
	 * @return The bounds used to layout the graph.
	 */
	protected Rectangle computeLayoutBounds() {
		Rectangle newBounds = new Rectangle();
		if (nestingVisual != null) {
			// nested graph uses layout bounds of nesting node
			Bounds layoutBounds = nestingVisual.getLayoutBounds();
			newBounds = new Rectangle(0, 0, layoutBounds.getWidth() / NodePart.DEFAULT_NESTED_CHILDREN_ZOOM_FACTOR,
					layoutBounds.getHeight() / NodePart.DEFAULT_NESTED_CHILDREN_ZOOM_FACTOR);
		} else {
			// root graph uses infinite canvas bounds
			InfiniteCanvas canvas = getInfiniteCanvas();
			newBounds = new Rectangle(0, 0, canvas.getWidth(), canvas.getHeight());
		}
		return newBounds;
	}

	@Override
	protected void doActivate() {
		super.doActivate();

		// register listener for bounds changes
		if (getHost().getParent() == getHost().getRoot()) {
			/*
			 * Our graph is the root graph, therefore we listen to viewport
			 * changes to update the layout bounds in the context accordingly.
			 */
			getInfiniteCanvas().scrollableBoundsProperty().addListener(viewportBoundsChangeListener);
		} else {
			/*
			 * Our graph is nested inside a node of another graph, therefore we
			 * listen to changes of that node's layout-bounds.
			 */
			nestingVisual = getHost().getVisual().getParent();
			nestingVisual.layoutBoundsProperty().addListener(nestingVisualLayoutBoundsChangeListener);
		}

		// add layout filter for hidden/layout irrelevant elements
		final HidingModel hidingModel = getHost().getRoot().getViewer().getAdapter(HidingModel.class);
		if (hidingModel != null) {
			getLayoutContext().addLayoutFilter(new ILayoutFilter() {
				@Override
				public boolean isLayoutIrrelevant(Edge edge) {
					return Boolean.TRUE.equals(ZestProperties.getLayoutIrrelevant(edge))
							|| isLayoutIrrelevant(edge.getSource()) || isLayoutIrrelevant(edge.getTarget());
				}

				@Override
				public boolean isLayoutIrrelevant(org.eclipse.gef4.graph.Node node) {
					return Boolean.TRUE.equals(ZestProperties.getLayoutIrrelevant(node)) || hidingModel.isHidden(node);
				}
			});
		}

		// initially apply layout if no viewport state is saved for this graph,
		// or we are nested inside a node, or the saved viewport is outdated
		NavigationModel navigationModel = getHost().getRoot().getViewer().getAdapter(NavigationModel.class);
		ViewportState savedViewport = navigationModel == null ? null
				: navigationModel.getViewportState(getHost().getContent());
		InfiniteCanvas canvas = ((FXViewer) getHost().getRoot().getViewer()).getCanvas();
		boolean isNested = getNestingPart() != null;
		boolean isViewportChanged = savedViewport != null
				&& (savedViewport.getWidth() != canvas.getWidth() || savedViewport.getHeight() != canvas.getHeight());
		if (savedViewport == null || isNested || isViewportChanged) {
			LayoutProperties.setBounds(getHost().getContent(), computeLayoutBounds());
			applyLayout(true);
		}

	}

	@Override
	protected void doDeactivate() {
		super.doDeactivate();

		// store the viewport state (in case navigation is supported)
		Rectangle bounds = LayoutProperties.getBounds(getHost().getContent());
		NavigationModel navigationModel = getHost().getRoot().getViewer().getAdapter(NavigationModel.class);
		if (navigationModel != null) {
			navigationModel.setViewportState(getHost().getContent(), new ViewportState(0, 0, bounds.getWidth(),
					bounds.getHeight(), FX2Geometry.toAffineTransform(getInfiniteCanvas().getContentTransform())));
		}

		if (nestingVisual != null) {
			// remove layout change listener from nesting visual
			nestingVisual.layoutBoundsProperty().removeListener(nestingVisualLayoutBoundsChangeListener);
		} else {
			// remove change listener from infinite canvas
			getInfiniteCanvas().scrollableBoundsProperty().removeListener(viewportBoundsChangeListener);
		}
		nestingVisual = null;
	}

	@Override
	public GraphPart getHost() {
		return (GraphPart) super.getHost();
	}

	/**
	 * Returns the {@link InfiniteCanvas} of the {@link IViewer} of the
	 * {@link #getHost() host}.
	 *
	 * @return The {@link InfiniteCanvas} of the {@link IViewer} of the
	 *         {@link #getHost() host}.
	 */
	protected InfiniteCanvas getInfiniteCanvas() {
		return ((FXViewer) getHost().getRoot().getViewer()).getCanvas();
	}

	@Override
	protected LayoutContext getLayoutContext() {
		return getHost().getAdapter(LayoutContext.class);
	}

	/**
	 * Returns the {@link NodePart} that contains the nested graph to which the
	 * behavior corresponds, if this behavior is related to a nested graph.
	 *
	 * @return The {@link NodePart} that contains the nested graph to which the
	 *         behavior corresponds.
	 */
	protected NodePart getNestingPart() {
		if (getHost().getParent() instanceof NodePart) {
			return (NodePart) getHost().getParent();
		}
		return null;
	}

	@Override
	protected void postLayout() {
	}

	@Override
	protected void preLayout() {
	}

	/**
	 * Updates the bounds property from the visual (viewport or nesting node)
	 */
	protected void updateBounds() {
		Rectangle newBounds = computeLayoutBounds();
		Rectangle oldBounds = LayoutProperties.getBounds(getHost().getContent());
		if (oldBounds != newBounds && (oldBounds == null || !oldBounds.equals(newBounds))) {
			LayoutProperties.setBounds(getHost().getContent(), newBounds);
			applyLayout(true);
		}
	}
}
