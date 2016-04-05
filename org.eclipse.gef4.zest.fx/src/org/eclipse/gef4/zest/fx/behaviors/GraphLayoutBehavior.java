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
import org.eclipse.gef4.mvc.parts.IContentPart;
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
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * The {@link GraphLayoutBehavior} is responsible for initiating layout passes.
 * It is only applicable to {@link GraphPart}.
 *
 * @author mwienand
 *
 */
// only applicable for GraphPart (see #getHost())
public class GraphLayoutBehavior extends AbstractLayoutBehavior {

	private LayoutContext layoutContext;
	private Pane nestingVisual;

	private ChangeListener<? super Bounds> nestingVisualLayoutBoundsChangeListener = new ChangeListener<Bounds>() {
		@Override
		public void changed(ObservableValue<? extends Bounds> observable, Bounds oldLayoutBounds,
				Bounds newLayoutBounds) {
			onNestingVisualLayoutBoundsChange(oldLayoutBounds, newLayoutBounds);
		}
	};

	private ChangeListener<? super Bounds> viewportBoundsChangeListener = new ChangeListener<Bounds>() {
		@Override
		public void changed(ObservableValue<? extends Bounds> observable, Bounds oldLayoutBounds,
				Bounds newLayoutBounds) {
			onViewportModelPropertyChange(oldLayoutBounds, newLayoutBounds);
		}
	};

	@Override
	protected void adaptFromLayout() {
	}

	/**
	 * Performs one layout pass using the static layout algorithm that is
	 * configured for the layout context.
	 *
	 * @param clean
	 *            Whether to fully re-compute the layout or not.
	 */
	public void applyLayout(boolean clean) {
		InfiniteCanvas canvas = ((FXViewer) getHost().getRoot().getViewer()).getCanvas();

		Graph graph = getHost().getContent();

		// TODO: move this into provideToLayout?
		Rectangle bounds = LayoutProperties.getBounds(graph);

		getHost().getRoot().getViewer().getAdapter(NavigationModel.class).setViewportState(layoutContext.getGraph(),
				new ViewportState(0, 0, bounds.getWidth(), bounds.getHeight(),
						FX2Geometry.toAffineTransform(canvas.getContentTransform())));

		// update layout algorithm (apply layout will depend on it)
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
		layoutContext.flushChanges();
		// update label positions
	}

	@Override
	protected void doActivate() {
		super.doActivate();

		// register listener for bounds changes
		Rectangle initialBounds = new Rectangle();
		Graph graph = getHost().getContent();
		if (getHost().getParent() == getHost().getRoot()) {
			/*
			 * Our graph is the root graph, therefore we listen to viewport
			 * changes to update the layout bounds in the context accordingly.
			 */
			getInfiniteCanvas().scrollableBoundsProperty().addListener(viewportBoundsChangeListener);
			// read initial bounds
			FXViewer fxViewer = (FXViewer) getHost().getRoot().getViewer();
			initialBounds.setX(0);
			initialBounds.setY(0);
			initialBounds.setWidth(fxViewer.getCanvas().getWidth());
			initialBounds.setHeight(fxViewer.getCanvas().getHeight());
		} else if (graph.getNestingNode() != null) {
			/*
			 * Our graph is nested inside a node of another graph, therefore we
			 * listen to changes of that node's layout-bounds.
			 */
			nestingVisual = getNestingPart().getNestedChildrenPane();
			nestingVisual.layoutBoundsProperty().addListener(nestingVisualLayoutBoundsChangeListener);
			Bounds layoutBounds = nestingVisual.getLayoutBounds();
			// read initial bounds
			initialBounds.setWidth(layoutBounds.getWidth() / NodePart.DEFAULT_NESTED_CHILDREN_ZOOM_FACTOR);
			initialBounds.setHeight(layoutBounds.getHeight() / NodePart.DEFAULT_NESTED_CHILDREN_ZOOM_FACTOR);
		} else {
			throw new IllegalStateException("Graph is neither nested nor root?!");
		}

		// retrieve layout context
		layoutContext = getLayoutContext();

		// add layout filter for hidden/layout irrelevant elements
		final HidingModel hidingModel = getHost().getRoot().getViewer().getAdapter(HidingModel.class);
		layoutContext.addLayoutFilter(new ILayoutFilter() {
			@Override
			public boolean isLayoutIrrelevant(Edge edge) {
				return ZestProperties.getLayoutIrrelevant(edge, true) || isLayoutIrrelevant(edge.getSource())
						|| isLayoutIrrelevant(edge.getTarget());
			}

			@Override
			public boolean isLayoutIrrelevant(org.eclipse.gef4.graph.Node node) {
				return ZestProperties.getLayoutIrrelevant(node, true) || hidingModel.isHidden(node);
			}
		});

		// set initial bounds on the context
		LayoutProperties.setBounds(graph, initialBounds);
	}

	@Override
	protected void doDeactivate() {
		super.doDeactivate();

		if (nestingVisual == null) {
			// remove change listener from viewport
			getInfiniteCanvas().scrollableBoundsProperty().removeListener(viewportBoundsChangeListener);
		} else {
			nestingVisual.layoutBoundsProperty().removeListener(nestingVisualLayoutBoundsChangeListener);
		}
		// nullify variables
		layoutContext = null;
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
	 * behavior corresponds.
	 *
	 * @return The {@link NodePart} that contains the nested graph to which the
	 *         behavior corresponds.
	 */
	protected NodePart getNestingPart() {
		org.eclipse.gef4.graph.Node nestingNode = getHost().getContent().getNestingNode();
		IContentPart<Node, ? extends Node> nestingNodePart = getHost().getRoot().getViewer().getContentPartMap()
				.get(nestingNode);
		return (NodePart) nestingNodePart;
	}

	/**
	 * Sets the layout bounds on the layout context for nested graphs.
	 *
	 * @param oldLayoutBounds
	 *            The previous nesting node's bounds.
	 * @param newLayoutBounds
	 *            The current nesting node's bounds.
	 */
	protected void onNestingVisualLayoutBoundsChange(Bounds oldLayoutBounds, Bounds newLayoutBounds) {
		// update layout bounds to match the nesting visual layout bounds
		double width = newLayoutBounds.getWidth() / NodePart.DEFAULT_NESTED_CHILDREN_ZOOM_FACTOR;
		double height = newLayoutBounds.getHeight() / NodePart.DEFAULT_NESTED_CHILDREN_ZOOM_FACTOR;
		Rectangle newBounds = new Rectangle(0, 0, width, height);
		LayoutProperties.setBounds(getHost().getContent(), newBounds);
	}

	/**
	 * Called upon viewport bounds changes to update the layout bounds.
	 *
	 * @param oldScrollableBounds
	 *            The old {@link Bounds}.
	 * @param newScrollableBounds
	 *            The new {@link Bounds}.
	 */
	protected void onViewportModelPropertyChange(Bounds oldScrollableBounds, Bounds newScrollableBounds) {
		InfiniteCanvas canvas = getInfiniteCanvas();
		Rectangle newBounds = new Rectangle(0, 0, canvas.getWidth(), canvas.getHeight());
		LayoutProperties.setBounds(getHost().getContent(), newBounds);
	}

	@Override
	protected void provideToLayout() {
	}

}
