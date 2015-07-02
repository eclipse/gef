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
package org.eclipse.gef4.zest.fx.parts;

import java.util.List;
import java.util.Map;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;

import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.behaviors.SelectionBehavior;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class ZestFxFeedbackPartFactory extends FXDefaultFeedbackPartFactory {

	@Inject
	private Injector injector;

	protected IFeedbackPart<Node, ? extends Node> createEdgeLabelLinkFeedbackPart(
			final EdgeContentPart edgeContentPart,
			final EdgeLabelPart edgeLabelPart) {
		Provider<IGeometry> linkFeedbackGeometryProvider = new Provider<IGeometry>() {
			@Override
			public IGeometry get() {
				// use the connection's middle point as the link source
				// point
				final FXGeometryNode<ICurve> curveNode = edgeContentPart
						.getVisual().getCurveNode();
				BezierCurve[] bezier = curveNode.getGeometry().toBezier();
				final Point sourcePoint = bezier[bezier.length / 2].get(0.5);
				Point2D sourcePoint2DInScene = curveNode.localToScene(
						sourcePoint.x, sourcePoint.y);
				Point sourcePointInScene = new Point(
						sourcePoint2DInScene.getX(),
						sourcePoint2DInScene.getY());

				// use the anchorage's center point as the link target
				// point
				Node labelVisual = edgeLabelPart.getVisual();
				Bounds bounds = labelVisual.getLayoutBounds();
				Point2D targetPoint2DInScene = labelVisual.localToScene(
						bounds.getMinX() + bounds.getWidth() / 2,
						bounds.getMinY() + bounds.getHeight() / 2);
				Point targetPointInScene = new Point(
						targetPoint2DInScene.getX(),
						targetPoint2DInScene.getY());

				// construct link line
				return new Line(sourcePointInScene, targetPointInScene);
			}
		};
		ZestFxEdgeLinkFeedbackPart part = injector
				.getInstance(ZestFxEdgeLinkFeedbackPart.class);
		part.setGeometryProvider(linkFeedbackGeometryProvider);
		return part;
	}

	@Override
	protected List<IFeedbackPart<Node, ? extends Node>> createSelectionFeedbackParts(
			List<? extends IVisualPart<Node, ? extends Node>> targets,
			SelectionBehavior<Node> selectionBehavior,
			Map<Object, Object> contextMap) {
		List<IFeedbackPart<Node, ? extends Node>> parts = super
				.createSelectionFeedbackParts(targets, selectionBehavior,
						contextMap);
		// create edge label link feedback
		for (IVisualPart<Node, ? extends Node> target : targets) {
			if (target instanceof EdgeContentPart) {
				// find EdgeLabelPart
				EdgeLabelPart labelPart = null;
				for (IVisualPart<Node, ? extends Node> anchored : target
						.getAnchoreds()) {
					if (anchored instanceof EdgeLabelPart) {
						labelPart = (EdgeLabelPart) anchored;
					}
				}
				if (labelPart != null) {
					IFeedbackPart<Node, ? extends Node> edgeLabelLinkFeedbackPart = createEdgeLabelLinkFeedbackPart(
							(EdgeContentPart) target, labelPart);
					if (edgeLabelLinkFeedbackPart != null) {
						parts.add(edgeLabelLinkFeedbackPart);
					}
				}
			}
		}
		return parts;
	}

}
