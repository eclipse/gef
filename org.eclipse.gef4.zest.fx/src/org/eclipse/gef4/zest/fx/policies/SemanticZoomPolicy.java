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
package org.eclipse.gef4.zest.fx.policies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.fx.nodes.InfiniteCanvas;
import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.behaviors.ContentBehavior;
import org.eclipse.gef4.mvc.fx.operations.FXChangeViewportOperation;
import org.eclipse.gef4.mvc.fx.policies.FXChangeViewportPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.parts.PartUtils;
import org.eclipse.gef4.zest.fx.models.NavigationModel;
import org.eclipse.gef4.zest.fx.operations.NavigateOperation;
import org.eclipse.gef4.zest.fx.parts.NodePart;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;

/**
 * The {@link SemanticZoomPolicy} extends the {@link FXChangeViewportPolicy} for
 * associating semantic changes with viewport changes, i.e. opening of
 * nested/nesting graphs when the zoom level is changed below/above a certain
 * threshold.
 *
 * @author mwienand
 *
 */
public class SemanticZoomPolicy extends FXChangeViewportPolicy {

	private ContentModel contentModel;
	private NavigationModel navigationModel;

	@Override
	protected ITransactionalOperation createOperation() {
		return new NavigateOperation((FXViewer) getHost().getRoot().getViewer());
	}

	/**
	 * Returns a {@link List} containing all {@link NodePart}s (within
	 * the currently rendered graph) that have a nested graph assigned to them.
	 *
	 * @return A {@link List} containing all {@link NodePart}s (within
	 *         the currently rendered graph) that have a nested graph assigned
	 *         to them.
	 */
	protected List<NodePart> findNestingNodes() {
		// find the first level visual parts (not considering nested graphs)
		List<IVisualPart<Node, ? extends Node>> rootChildren = getHost().getRoot().getChildrenUnmodifiable();

		// rootChildren.get(0) should be the GraphPart containing the
		// NodeContentParts
		List<IVisualPart<Node, ? extends Node>> graphChildren = rootChildren.size() > 0
				? rootChildren.get(0).getChildrenUnmodifiable()
				: Collections.<IVisualPart<Node, ? extends Node>> emptyList();

		// filter for NodePart
		List<NodePart> nestingNodeContentParts = PartUtils.filterParts(graphChildren, NodePart.class);

		// filter out all non-nesting nodes
		for (int i = nestingNodeContentParts.size() - 1; i >= 0; i--) {
			NodePart nodePart = nestingNodeContentParts.get(i);
			if (nodePart.getContent().getNestedGraph() == null) {
				nestingNodeContentParts.remove(i);
			}
		}
		return nestingNodeContentParts;
	}

	@Override
	protected FXChangeViewportOperation getChangeViewportOperation() {
		return getNavigateOperation().getChangeViewportOperation();
	}

	/**
	 * Returns the {@link NavigateOperation} that is used to open nested/nesting
	 * {@link Graph}s.
	 *
	 * @return The {@link NavigateOperation} that is used to open nested/nesting
	 *         {@link Graph}s.
	 */
	protected NavigateOperation getNavigateOperation() {
		return (NavigateOperation) getOperation();
	}

	@Override
	public void init() {
		contentModel = getHost().getRoot().getViewer().getAdapter(ContentModel.class);
		if (contentModel == null) {
			throw new IllegalArgumentException("ContentModel could not be obtained!");
		}

		navigationModel = getHost().getRoot().getViewer().getAdapter(NavigationModel.class);
		if (navigationModel == null) {
			throw new IllegalArgumentException("NavigationModel could not be obtained!");
		}

		super.init();
	}

	@SuppressWarnings("serial")
	@Override
	public void zoomRelative(double relativeZoom, double sceneX, double sceneY) {
		// long startTimeNanos = System.nanoTime();
		checkInitialized();

		// determine initial and final zoom level
		double initialZoomLevel = getChangeViewportOperation().getNewTransform().getScaleX();
		double finalZoomLevel = initialZoomLevel * relativeZoom;

		// open nested/nesting graph depending on zoom level
		boolean openGraph = false;
		if (initialZoomLevel < finalZoomLevel && finalZoomLevel > 3) {
			// zooming in => open nested graph (if any)
			// find all NodeContentParts with nested graphs
			List<NodePart> nestingNodeContentParts = findNestingNodes();

			// determine the nesting node that is at least partially visible and
			// nearest to the pivot point
			double pivotDistance = Double.MAX_VALUE;
			NodePart pivotPart = null;

			InfiniteCanvas infiniteCanvas = ((FXViewer) getHost().getRoot().getViewer()).getCanvas();
			org.eclipse.gef4.geometry.planar.Rectangle viewportBounds = new org.eclipse.gef4.geometry.planar.Rectangle(
					0, 0, infiniteCanvas.getWidth(), infiniteCanvas.getHeight());
			Point pivotPoint = FX2Geometry.toPoint(infiniteCanvas.sceneToLocal(sceneX, sceneY));

			for (NodePart nodePart : new ArrayList<>(nestingNodeContentParts)) {
				Group visual = nodePart.getVisual();
				Bounds boundsInScene = visual.localToScene(visual.getLayoutBounds());
				org.eclipse.gef4.geometry.planar.Rectangle boundsInViewport = FX2Geometry
						.toRectangle(infiniteCanvas.sceneToLocal(boundsInScene));
				if (boundsInViewport.touches(viewportBounds)) {
					double distance = boundsInViewport.getCenter().getDistance(pivotPoint);
					if (distance < pivotDistance) {
						pivotPart = nodePart;
					}
				}
			}

			// open nested graph
			if (pivotPart != null) {
				openGraph = true;
				getNavigateOperation().setFinalState(pivotPart.getContent().getNestedGraph(), true);
			}
		} else if (initialZoomLevel > finalZoomLevel && finalZoomLevel < 0.7) {
			// zooming out => open nesting graph (if any)
			final Graph currentGraph = (Graph) contentModel.getContents().get(0);
			final Graph nestingGraph = currentGraph.getNestingNode() != null ? currentGraph.getNestingNode().getGraph()
					: null;
			if (nestingGraph != null) {
				openGraph = true;
				getNavigateOperation().setFinalState(nestingGraph, false);
			}
		}

		if (openGraph) {
			// execute the semantic zoom operations
			locallyExecuteOperation();
		} else {
			// when no graph is opened, the viewport is regularly updated (the
			// super call executes the operation)
			super.zoomRelative(relativeZoom, sceneX, sceneY);
		}

		// synchronize content children of nesting node parts
		for (NodePart nestingNodePart : findNestingNodes()) {
			nestingNodePart.refreshContentChildren();
			nestingNodePart.getAdapter(new TypeToken<ContentBehavior<Node>>() {
			}).synchronizeContentChildren(ImmutableList.copyOf(nestingNodePart.getContentChildrenUnmodifiable()));
		}
		// System.out.println("zoom - " + (System.nanoTime() - startTimeNanos) /
		// 1000 / 1000 + "ms");
	}

}
