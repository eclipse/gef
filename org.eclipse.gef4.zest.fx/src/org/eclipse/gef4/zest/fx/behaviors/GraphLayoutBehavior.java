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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.fx.nodes.InfiniteCanvas;
import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.layout.IEdgeLayout;
import org.eclipse.gef4.layout.ILayoutAlgorithm;
import org.eclipse.gef4.layout.ILayoutFilter;
import org.eclipse.gef4.layout.INodeLayout;
import org.eclipse.gef4.layout.LayoutProperties;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.layout.GraphEdgeLayout;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.layout.GraphNodeLayout;
import org.eclipse.gef4.zest.fx.models.HidingModel;
import org.eclipse.gef4.zest.fx.models.NavigationModel;
import org.eclipse.gef4.zest.fx.models.NavigationModel.ViewportState;
import org.eclipse.gef4.zest.fx.parts.GraphPart;
import org.eclipse.gef4.zest.fx.parts.NodePart;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
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

	private MapChangeListener<String, Object> layoutContextAttributesObserver = new MapChangeListener<String, Object>() {

		@Override
		public void onChanged(MapChangeListener.Change<? extends String, ? extends Object> change) {
			if (LayoutProperties.BOUNDS_PROPERTY.equals(change.getKey())
					&& change.getMap().get(change.getKey()) != null) {
				applyLayout(true);
			}
		}
	};

	private GraphLayoutContext layoutContext;
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

	private List<Runnable> provideLayoutRunnables = new ArrayList<>();
	private Runnable provideLayoutRunnable = new Runnable() {
		@Override
		public void run() {
			for (Runnable r : provideLayoutRunnables) {
				r.run();
			}
		}
	};

	private List<Runnable> adaptToLayoutRunnables = new ArrayList<>();
	private Runnable adaptToLayoutRunnable = new Runnable() {
		@Override
		public void run() {
			for (Runnable r : adaptToLayoutRunnables) {
				r.run();
			}
		}
	};

	private List<Runnable> updateLabelsRunnables = new ArrayList<>();
	private Runnable updateLabelsRunnable = new Runnable() {
		@Override
		public void run() {
			for (Runnable r : updateLabelsRunnables) {
				r.run();
			}
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
		Rectangle bounds = LayoutProperties.getBounds(layoutContext);
		getHost().getRoot().getViewer().getAdapter(NavigationModel.class).setViewportState(layoutContext.getGraph(),
				new ViewportState(0, 0, bounds.getWidth(), bounds.getHeight(),
						FX2Geometry.toAffineTransform(canvas.getContentTransform())));
		// update layout algorithm (apply layout will depend on it)
		Object layoutAlgorithmValue = getHost().getContent().attributesProperty()
				.get(ZestProperties.GRAPH_LAYOUT_ALGORITHM);
		if (layoutAlgorithmValue != null) {
			ILayoutAlgorithm layoutAlgorithm = (ILayoutAlgorithm) layoutAlgorithmValue;
			if (layoutContext.getLayoutAlgorithm() == null) {
				layoutContext.setLayoutAlgorithm(layoutAlgorithm);
			}
		} else {
			if (layoutContext.getLayoutAlgorithm() != null) {
				layoutContext.setLayoutAlgorithm(null);
			}
		}
		// apply layout (if no algorithm is set, will be a no-op)
		layoutContext.applyLayout(true);
		layoutContext.flushChanges();
		// update label positions

	}

	@Override
	protected void doActivate() {
		// register listener for bounds changes
		Rectangle initialBounds = new Rectangle();
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
		} else if (getHost().getContent().getNestingNode() != null) {
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
		layoutContext = getGraphLayoutContext();

		// add layout filter for hidden/layout irrelevant elements
		final HidingModel hidingModel = getHost().getRoot().getViewer().getAdapter(HidingModel.class);
		layoutContext.addLayoutFilter(new ILayoutFilter() {
			@Override
			public boolean isLayoutIrrelevant(IEdgeLayout connectionLayout) {
				return ZestProperties.getLayoutIrrelevant(((GraphEdgeLayout) connectionLayout).getEdge(), true)
						|| isLayoutIrrelevant(connectionLayout.getSource())
						|| isLayoutIrrelevant(connectionLayout.getTarget());
			}

			@Override
			public boolean isLayoutIrrelevant(INodeLayout nodeLayout) {
				org.eclipse.gef4.graph.Node node = ((GraphNodeLayout) nodeLayout).getNode();
				return ZestProperties.getLayoutIrrelevant(node, true) || hidingModel.isHidden(node);
			}
		});

		// set initial bounds on the context
		LayoutProperties.setBounds(layoutContext, initialBounds);

		// register listener for layout context property changes after setting
		// the initial layout properties, so that this listener will not be
		// called for the initial layout properties
		layoutContext.attributesProperty().addListener(layoutContextAttributesObserver);

		// schedule pre/post layout runnables
		layoutContext.schedulePreLayoutPass(provideLayoutRunnable);
		layoutContext.schedulePostLayoutPass(adaptToLayoutRunnable);
		layoutContext.schedulePostLayoutPass(updateLabelsRunnable);
	}

	@Override
	protected void doDeactivate() {
		// remove property change listener from context
		if (layoutContext != null) {
			layoutContext.attributesProperty().removeListener(layoutContextAttributesObserver);
		}
		if (nestingVisual == null) {
			// remove change listener from viewport
			getInfiniteCanvas().scrollableBoundsProperty().removeListener(viewportBoundsChangeListener);
		} else {
			nestingVisual.layoutBoundsProperty().removeListener(nestingVisualLayoutBoundsChangeListener);
		}
		// unschedule pre/post layout runnables
		layoutContext.unschedulePreLayoutPass(provideLayoutRunnable);
		layoutContext.unschedulePostLayoutPass(adaptToLayoutRunnable);
		layoutContext.unschedulePostLayoutPass(updateLabelsRunnable);
		// nullify variables
		layoutContext = null;
		nestingVisual = null;
	}

	@Override
	protected GraphLayoutBehavior getGraphLayoutBehavior() {
		return this;
	}

	/**
	 * Returns the {@link GraphLayoutContext} that corresponds to the
	 * {@link #getHost() host}.
	 *
	 * @return The {@link GraphLayoutContext} that corresponds to the
	 *         {@link #getHost() host}.
	 */
	protected GraphLayoutContext getGraphLayoutContext() {
		return getHost().getAdapter(GraphLayoutContext.class);
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

	/**
	 * Returns the {@link NodePart} that contains the nested graph to which the
	 * {@link #getGraphLayoutBehavior()} corresponds.
	 *
	 * @return The {@link NodePart} that contains the nested graph to which the
	 *         {@link #getGraphLayoutBehavior()} corresponds.
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
		LayoutProperties.setBounds(layoutContext, newBounds);
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
		LayoutProperties.setBounds(layoutContext, newBounds);
	}

	@Override
	protected void provideToLayout() {
	}

	/**
	 * Schedules the given "adapt to layout" {@link Runnable} for execution
	 * after a layout pass.
	 *
	 * @param adaptToLayout
	 *            The {@link Runnable} that will be executed after a layout pass
	 *            and before all "update label" runnables.
	 */
	public void scheduleAdaptToLayout(Runnable adaptToLayout) {
		adaptToLayoutRunnables.add(adaptToLayout);
	}

	/**
	 * Schedules the given "update label" {@link Runnable} for execution after a
	 * layout pass and after all "adapt to layout" runnables.
	 *
	 * @param updateLabels
	 *            The {@link Runnable} that will be executed after a layout pass
	 *            and after all "adapt to layout" runnables.
	 */
	public void schedulePostLayoutPass(Runnable updateLabels) {
		updateLabelsRunnables.add(updateLabels);
	}

	/**
	 * Schedules the given "provide layout" {@link Runnable} for execution
	 * before a layout pass.
	 *
	 * @param provideLayout
	 *            The {@link Runnable} that will be executed before a layout
	 *            pass.
	 */
	public void scheduleProvideLayout(Runnable provideLayout) {
		provideLayoutRunnables.add(provideLayout);
	}

	/**
	 * Unschedules the given "adapt to layout" {@link Runnable} so that it will
	 * no longer be executed after a layout pass.
	 *
	 * @param adaptToLayout
	 *            The {@link Runnable} that will no longer be executed after a
	 *            layout pass and before all "update label" runnables.
	 */
	public void unscheduleAdaptToLayout(Runnable adaptToLayout) {
		adaptToLayoutRunnables.remove(adaptToLayout);
	}

	/**
	 * Unschedules the given "update label" {@link Runnable} so that it will no
	 * longer be executed after a layout pass and after all "adapt to layout"
	 * runnables.
	 *
	 * @param updateLabels
	 *            The {@link Runnable} that will no longer be executed after a
	 *            layout pass and after all "adapt to layout" runnables.
	 */
	public void unschedulePostLayoutPass(Runnable updateLabels) {
		updateLabelsRunnables.remove(updateLabels);
	}

	/**
	 * Unschedules the given "provide layout" {@link Runnable} so that it will
	 * no longer be executed before a layout pass.
	 *
	 * @param provideLayout
	 *            The {@link Runnable} that will no longer be executed before a
	 *            layout pass.
	 */
	public void unscheduleProvideLayout(Runnable provideLayout) {
		provideLayoutRunnables.remove(provideLayout);
	}

}
