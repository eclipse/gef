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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.layout.LayoutProperties;
import org.eclipse.gef4.mvc.fx.policies.FXResizePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.layout.GraphNodeLayout;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Affine;

/**
 * The {@link NodeLayoutBehavior} is a {@link NodeContentPart}-specific
 * {@link AbstractLayoutBehavior} implementation.
 *
 * @author mwienand
 *
 */
// only applicable to NodeContentPart (see #getHost())
public class NodeLayoutBehavior extends AbstractLayoutBehavior {

	/**
	 * The class key that is used to retrieve the {@link FXResizePolicy} that is
	 * installed on a {@link NodeContentPart}.
	 */
	public static Class<FXResizePolicy> RESIZE_POLICY_KEY = FXResizePolicy.class;

	/**
	 * The class key that is used to retrieve the {@link FXTransformPolicy} that
	 * is installed on a {@link NodeContentPart}.
	 */
	public static Class<FXTransformPolicy> TRANSFORM_POLICY_KEY = FXTransformPolicy.class;

	/**
	 * Default constructor.
	 */
	public NodeLayoutBehavior() {
	}

	@Override
	public void activate() {
		super.activate();
	}

	/**
	 * Reads the location and size of its host {@link NodeContentPart} from the
	 * layout model and updates the visualization accordingly.
	 */
	public void adaptLayoutInformation() {
		FXResizePolicy resizePolicy = getHost().getAdapter(RESIZE_POLICY_KEY);
		FXTransformPolicy transformPolicy = getHost().getAdapter(TRANSFORM_POLICY_KEY);
		if (resizePolicy != null && transformPolicy != null) {
			Node visual = getHost().getVisual();
			Bounds layoutBounds = visual.getLayoutBounds();
			Affine transform = transformPolicy.getNodeTransform();
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

			resizePolicy.init();
			transformPolicy.init();
			resizePolicy.performResize(dw, dh);
			transformPolicy.setPreTranslate(transformPolicy.createPreTransform(), dx, dy);
			ForwardUndoCompositeOperation fwd = new ForwardUndoCompositeOperation("AdaptLayout");
			ITransactionalOperation operation = resizePolicy.commit();
			if (operation != null) {
				fwd.add(operation);
			}
			operation = transformPolicy.commit();
			if (operation != null) {
				fwd.add(operation);
			}
			operation = fwd.unwrap(true);
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
		IContentPart<Node, ? extends Node> graphPart = getHost().getRoot().getViewer().getContentPartMap()
				.get(getHost().getContent().getGraph());
		return graphPart.getAdapter(GraphLayoutContext.class);
	}

	@Override
	public NodeContentPart getHost() {
		return (NodeContentPart) super.getHost();
	}

	/**
	 * Returns the {@link GraphNodeLayout} that corresponds to the
	 * {@link NodeContentPart} on which this {@link NodeLayoutBehavior} is
	 * installed.
	 *
	 * @return The {@link GraphNodeLayout} that corresponds to the
	 *         {@link NodeContentPart} on which this {@link NodeLayoutBehavior}
	 *         is installed.
	 */
	protected GraphNodeLayout getNodeLayout() {
		// TODO: use event to update node layout
		GraphNodeLayout nodeLayout = getGraphLayoutContext().getNodeLayout(getHost().getContent());
		if (nodeLayout == null) {
			throw new IllegalStateException("Cannot find INodeLayout in NavigationModel.");
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

	/**
	 * Writes the location and size of its host {@link NodeContentPart} into the
	 * layout model.
	 */
	public void provideLayoutInformation() {
		Node visual = getHost().getVisual();
		Bounds hostBounds = visual.getLayoutBounds();
		double minx = hostBounds.getMinX();
		double miny = hostBounds.getMinY();
		double maxx = hostBounds.getMaxX();
		double maxy = hostBounds.getMaxY();
		// union node bounds with bounds of feedback visuals
		for (IVisualPart<Node, ? extends Node> anchored : getHost().getAnchoreds()) {
			if (!(anchored instanceof IFeedbackPart)) {
				continue;
			}
			Node anchoredVisual = anchored.getVisual();
			Bounds anchoredBounds = anchoredVisual.getLayoutBounds();
			Bounds anchoredBoundsInHost = visual.sceneToLocal(anchoredVisual.localToScene(anchoredBounds));
			minx = Math.min(minx, anchoredBoundsInHost.getMinX());
			miny = Math.min(miny, anchoredBoundsInHost.getMinY());
			maxx = Math.max(maxx, anchoredBoundsInHost.getMaxX());
			maxy = Math.max(maxy, anchoredBoundsInHost.getMaxY());
		}

		FXTransformPolicy txPolicy = getHost().getAdapter(FXTransformPolicy.class);
		Affine transform = txPolicy.getNodeTransform();

		GraphNodeLayout nodeLayout = getNodeLayout();
		LayoutProperties.setLocation(nodeLayout, transform.getTx() + minx, transform.getTy() + miny);
		LayoutProperties.setSize(nodeLayout, maxx - minx, maxy - miny);
		LayoutProperties.setResizable(nodeLayout, visual.isResizable());
	}

}
