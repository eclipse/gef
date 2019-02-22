/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
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
package org.eclipse.gef.zest.fx.behaviors;

import java.util.List;
import java.util.Map;

import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.layout.ILayoutFilter;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.layout.LayoutProperties;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.parts.PartUtils;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.mvc.fx.viewer.InfiniteCanvasViewer;
import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.gef.zest.fx.models.HidingModel;
import org.eclipse.gef.zest.fx.models.NavigationModel;
import org.eclipse.gef.zest.fx.models.NavigationModel.ViewportState;
import org.eclipse.gef.zest.fx.parts.GraphPart;
import org.eclipse.gef.zest.fx.parts.NodePart;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.SetChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * The {@link GraphLayoutBehavior} is responsible for initiating layout passes.
 * It is only applicable to {@link GraphPart}.
 *
 * @author mwienand
 *
 */
// only applicable for GraphPart (see #getHost())
public class GraphLayoutBehavior extends AbstractLayoutBehavior {

	private Runnable postLayout = new Runnable() {
		@Override
		public void run() {
			postLayout();
		}
	};

	private Runnable preLayout = new Runnable() {
		@Override
		public void run() {
			preLayout();
		}
	};

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

	private ListChangeListener<IVisualPart<? extends Node>> childrenObserver = new ListChangeListener<IVisualPart<? extends Node>>() {
		@Override
		public void onChanged(ListChangeListener.Change<? extends IVisualPart<? extends Node>> c) {
			applyLayout(true, null);
		}
	};

	private SetChangeListener<org.eclipse.gef.graph.Node> hidingModelObserver = new SetChangeListener<org.eclipse.gef.graph.Node>() {
		@Override
		public void onChanged(SetChangeListener.Change<? extends org.eclipse.gef.graph.Node> change) {
			applyLayout(true, null);
		}
	};

	private boolean skipNextLayout;

	/**
	 * Performs one layout pass using the static layout algorithm that is
	 * configured for the layout context.
	 *
	 * @param clean
	 *            Whether to fully re-compute the layout or not.
	 * @param extra
	 *            An extra {@link Object} that is passed-on to the
	 *            {@link ILayoutAlgorithm}.
	 */
	@SuppressWarnings("unchecked")
	public void applyLayout(boolean clean, Object extra) {
		// check child parts exist for all content children
		if (getHost().getChildrenUnmodifiable().size() != getHost().getContentChildrenUnmodifiable().size()) {
			return;
		} else {
			List<IContentPart<? extends Node>> childContentParts = PartUtils
					.filterParts(getHost().getChildrenUnmodifiable(), IContentPart.class);
			for (IContentPart<? extends Node> cp : childContentParts) {
				if (!getHost().getContentChildrenUnmodifiable().contains(cp.getContent())) {
					return;
				}
			}
		}

		if (skipNextLayout) {
			skipNextLayout = false;
			return;
		}

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
			// XXX: Use minimum of window size and canvas size, because the
			// canvas size is invalid when its scene is changed.
			double windowWidth = canvas.getScene().getWindow().getWidth();
			double windowHeight = canvas.getScene().getWindow().getHeight();
			newBounds = new Rectangle(0, 0,
					Double.isFinite(windowWidth) ? Math.min(canvas.getWidth(), windowWidth) : canvas.getWidth(),
					Double.isFinite(windowHeight) ? Math.min(canvas.getHeight(), windowHeight) : canvas.getHeight());
		}
		return newBounds;
	}

	@Override
	protected void doActivate() {
		getHost().getChildrenUnmodifiable().addListener(childrenObserver);

		LayoutContext layoutContext = getLayoutContext();
		layoutContext.schedulePreLayoutPass(preLayout);
		layoutContext.schedulePostLayoutPass(postLayout);

		// register listener for bounds changes
		if (getHost().getParent() == getHost().getRoot()) {
			/*
			 * Our graph is the root graph, therefore we listen to viewport
			 * changes to update the layout bounds in the context accordingly.
			 */
			// XXX: Window can be null when the viewportBoundsChangeListener is
			// notified about a bounds change. Unfortunately, the corresponding
			// windowProperty is only changed afterwards. Therefore, we cannot
			// prevent that bounds changes are processed even though the window
			// is null.
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
				Map<Object, IContentPart<? extends Node>> contentPartMap = getHost().getViewer().getContentPartMap();

				@Override
				public boolean isLayoutIrrelevant(Edge edge) {
					if (!contentPartMap.containsKey(edge)) {
						return true;
					}
					if (!contentPartMap.get(edge).isActive()) {
						return true;
					}
					return Boolean.TRUE.equals(ZestProperties.getLayoutIrrelevant(edge))
							|| isLayoutIrrelevant(edge.getSource()) || hidingModel.isHidden(edge.getSource())
							|| isLayoutIrrelevant(edge.getTarget()) || hidingModel.isHidden(edge.getTarget());
				}

				@Override
				public boolean isLayoutIrrelevant(org.eclipse.gef.graph.Node node) {
					if (!contentPartMap.containsKey(node)) {
						return true;
					}
					if (!contentPartMap.get(node).isActive()) {
						return true;
					}
					return Boolean.TRUE.equals(ZestProperties.getLayoutIrrelevant(node)) || hidingModel.isHidden(node);
				}
			});
			hidingModel.hiddenProperty().addListener(hidingModelObserver);
		}

		// initially apply layout if no viewport state is saved for this graph,
		// or we are nested inside a node, or the saved viewport is outdated
		NavigationModel navigationModel = getHost().getRoot().getViewer().getAdapter(NavigationModel.class);
		ViewportState savedViewport = navigationModel == null ? null
				: navigationModel.getViewportState(getHost().getContent());
		InfiniteCanvas canvas = ((InfiniteCanvasViewer) getHost().getRoot().getViewer()).getCanvas();
		boolean isNested = getNestingPart() != null;
		boolean isViewportChanged = savedViewport != null
				&& (savedViewport.getWidth() != canvas.getWidth() || savedViewport.getHeight() != canvas.getHeight());
		// TODO: we should store one viewport state for the viewport of the
		// nesting part and one for the viewport of the graph part, so that
		// nested graphs are not unnecessarily layouted
		skipNextLayout = savedViewport != null;
		if (savedViewport == null || isNested || isViewportChanged) {
			LayoutProperties.setBounds(getHost().getContent(), computeLayoutBounds());
			applyLayout(true, null);
		}
	}

	@Override
	protected void doDeactivate() {
		getHost().getChildrenUnmodifiable().removeListener(childrenObserver);

		final HidingModel hidingModel = getHost().getRoot().getViewer().getAdapter(HidingModel.class);
		if (hidingModel != null) {
			hidingModel.hiddenProperty().removeListener(hidingModelObserver);
		}

		LayoutContext layoutContext = getLayoutContext();
		layoutContext.unschedulePreLayoutPass(preLayout);
		layoutContext.unschedulePostLayoutPass(postLayout);
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
		return ((InfiniteCanvasViewer) getHost().getRoot().getViewer()).getCanvas();
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
		// execute post-layout of all nodes and edges
		for (IVisualPart<? extends Node> child : getHost().getChildrenUnmodifiable()) {
			// FIXME: Layout should only be triggered when content-part-map
			// is changed, not when the children are changed.
			if (child.getViewer() == null) {
				continue;
			}
			AbstractLayoutBehavior childLayoutBehavior = child.getAdapter(AbstractLayoutBehavior.class);
			if (childLayoutBehavior != null) {
				childLayoutBehavior.postLayout();
			}
		}
	}

	@Override
	protected void preLayout() {
		// execute pre-layout of all nodes and edges
		for (IVisualPart<? extends Node> child : getHost().getChildrenUnmodifiable()) {
			// FIXME: Layout should only be triggered when content-part-map
			// is changed, not when the children are changed.
			if (child.getViewer() == null) {
				continue;
			}
			AbstractLayoutBehavior childLayoutBehavior = child.getAdapter(AbstractLayoutBehavior.class);
			if (childLayoutBehavior != null) {
				childLayoutBehavior.preLayout();
			}
		}
	}

	/**
	 * Updates the bounds property from the visual (viewport or nesting node)
	 */
	protected void updateBounds() {
		// XXX: Prevent bounds updates when the scene is not rendered.
		Scene scene = getHost().getVisual().getScene();
		if (scene == null || scene.getWindow() == null) {
			return;
		}

		Rectangle newBounds = computeLayoutBounds();
		Rectangle oldBounds = LayoutProperties.getBounds(getHost().getContent());
		if (oldBounds != newBounds && (oldBounds == null || !oldBounds.equals(newBounds))) {
			LayoutProperties.setBounds(getHost().getContent(), newBounds);
			applyLayout(true, null);
		}
	}
}
