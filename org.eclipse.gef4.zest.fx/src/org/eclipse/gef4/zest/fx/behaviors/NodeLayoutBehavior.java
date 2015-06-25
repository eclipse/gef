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

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Affine;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.layout.LayoutProperties;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.layout.GraphNodeLayout;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

public class NodeLayoutBehavior extends AbstractLayoutBehavior {

	public static Class<FXResizeRelocatePolicy> RESIZE_RELOCATE_POLICY_KEY = FXResizeRelocatePolicy.class;

	public NodeLayoutBehavior() {
	}

	@Override
	public void activate() {
		super.activate();
	}

	public void adaptLayoutInformation() {
		FXResizeRelocatePolicy policy = getHost().getAdapter(
				RESIZE_RELOCATE_POLICY_KEY);
		if (policy != null) {
			Node visual = getHost().getVisual();
			Bounds layoutBounds = visual.getLayoutBounds();
			FXTransformPolicy txPolicy = getHost().getAdapter(
					FXTransformPolicy.class);
			Affine transform = txPolicy.getNodeTransform();
			double x = transform.getTx();
			double y = transform.getTy();
			double w = layoutBounds.getWidth();
			double h = layoutBounds.getHeight();

			GraphNodeLayout nodeLayout = getNodeLayout();
			Point location = LayoutProperties.getLocation(nodeLayout);
			Dimension size = LayoutProperties.getSize(nodeLayout);

			// location is the center of the node, therefore we subtract half
			// width/height from it
			double dx = location.x - size.width / 2 - x;
			double dy = location.y - size.height / 2 - y;
			double dw = size.width - w;
			double dh = size.height - h;

			policy.init();
			policy.performResizeRelocate(dx, dy, dw, dh);
			IUndoableOperation operation = policy.commit();
			if (operation != null) {
				try {
					operation.execute(new NullProgressMonitor(), null);
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected GraphLayoutContext getGraphLayoutContext() {
		IContentPart<Node, ? extends Node> graphPart = getHost().getRoot()
				.getViewer().getContentPartMap()
				.get(getHost().getContent().getGraph());
		return graphPart.getAdapter(GraphLayoutContext.class);
	}

	@Override
	public NodeContentPart getHost() {
		return (NodeContentPart) super.getHost();
	}

	protected GraphNodeLayout getNodeLayout() {
		// TODO: use event to update node layout
		GraphNodeLayout nodeLayout = getGraphLayoutContext().getNodeLayout(
				getHost().getContent());
		if (nodeLayout == null) {
			throw new IllegalStateException(
					"Cannot find INodeLayout in LayoutModel.");
		}
		return nodeLayout;
	}

	@Override
	protected void postLayout() {
		adaptLayoutInformation();
		getHost().refreshVisual();
	}

	@Override
	protected void preLayout() {
		provideLayoutInformation();
	}

	public void provideLayoutInformation() {
		Node visual = getHost().getVisual();
		Bounds hostBounds = visual.getLayoutBounds();
		double minx = hostBounds.getMinX();
		double miny = hostBounds.getMinY();
		double maxx = hostBounds.getMaxX();
		double maxy = hostBounds.getMaxY();
		// union node bounds with bounds of feedback visuals
		for (IVisualPart<Node, ? extends Node> anchored : getHost()
				.getAnchoreds()) {
			if (!(anchored instanceof IFeedbackPart)) {
				continue;
			}
			Node anchoredVisual = anchored.getVisual();
			Bounds anchoredBounds = anchoredVisual.getLayoutBounds();
			Bounds anchoredBoundsInHost = visual.sceneToLocal(anchoredVisual
					.localToScene(anchoredBounds));
			minx = Math.min(minx, anchoredBoundsInHost.getMinX());
			miny = Math.min(miny, anchoredBoundsInHost.getMinY());
			maxx = Math.max(maxx, anchoredBoundsInHost.getMaxX());
			maxy = Math.max(maxy, anchoredBoundsInHost.getMaxY());
		}

		FXTransformPolicy txPolicy = getHost().getAdapter(
				FXTransformPolicy.class);
		Affine transform = txPolicy.getNodeTransform();

		GraphNodeLayout nodeLayout = getNodeLayout();
		LayoutProperties.setLocation(nodeLayout, transform.getTx() + minx,
				transform.getTy() + miny);
		LayoutProperties.setSize(nodeLayout, maxx - minx, maxy - miny);
		LayoutProperties.setResizable(nodeLayout, visual.isResizable());
	}

}
