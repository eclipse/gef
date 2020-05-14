/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.parts;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef.fx.anchors.DynamicAnchor.AnchorageReferenceGeometry;
import org.eclipse.gef.fx.anchors.DynamicAnchor.AnchoredReferencePoint;
import org.eclipse.gef.fx.anchors.IComputationStrategy;
import org.eclipse.gef.fx.anchors.ProjectionStrategy;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.fx.parts.DefaultSelectionFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.IFeedbackPart;
import org.eclipse.gef.mvc.fx.parts.IFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.parts.SelectionLinkFeedbackPart;
import org.eclipse.gef.zest.fx.ZestProperties;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import javafx.scene.Node;

/**
 * A specific {@link IFeedbackPartFactory} for selection feedback.
 *
 * @author anyssen
 */
public class ZestFxSelectionFeedbackPartFactory extends DefaultSelectionFeedbackPartFactory {

	@Inject
	private Injector injector;

	@Override
	protected IFeedbackPart<? extends Node> createLinkFeedbackPart(IVisualPart<? extends Node> anchorage,
			Provider<? extends IGeometry> anchorageLinkFeedbackGeometryProvider, IVisualPart<? extends Node> anchored,
			Provider<? extends IGeometry> anchoredLinkFeedbackGeometryProvider, String role) {

		// only show link feedback when anchored is no connection
		if (!(anchored.getVisual() instanceof Connection)) {
			Provider<IGeometry> linkFeedbackGeometryProvider = new Provider<IGeometry>() {
				// TODO (#471628): inject; maybe extend IComputationStrategy
				// interface
				private final ProjectionStrategy computationStrategy = new ProjectionStrategy();

				private Point computePosition(EdgePart anchorage, String role) {
					Point position = null;
					if (ZestProperties.LABEL__NE.equals(role)) {
						position = anchorage.getVisual().getCenter();
					} else if (ZestProperties.EXTERNAL_LABEL__NE.equals(role)) {
						position = anchorage.getVisual().getCenter();
					} else if (ZestProperties.SOURCE_LABEL__E.equals(role)) {
						position = anchorage.getVisual().getStartPoint();
					} else if (ZestProperties.TARGET_LABEL__E.equals(role)) {
						position = anchorage.getVisual().getEndPoint();
					} else {
						throw new IllegalArgumentException("Unsupported role " + role);
					}
					return FX2Geometry.toPoint(anchorage.getVisual().localToScene(Geometry2FX.toFXPoint(position)));
				}

				private Point computePosition(Node n1, IGeometry n1Geometry, Node n2, Point n2RefPoint) {
					// TODO: let computation strategy initialize the
					// parameters, then populate them
					Set<IComputationStrategy.Parameter<?>> parameters = new HashSet<>();
					parameters.add(new AnchorageReferenceGeometry(n1Geometry));
					parameters.add(new AnchoredReferencePoint(n2RefPoint));
					return computationStrategy.computePositionInScene(n1, n2, parameters);
				}

				@Override
				public IGeometry get() {
					// get anchored visual and geometry
					Node anchoredVisual = anchored.getVisual();
					IGeometry anchoredGeometryInLocal = anchoredLinkFeedbackGeometryProvider.get();

					// get anchorage visual and geometry
					Node anchorageVisual = anchorage.getVisual();
					IGeometry anchorageGeometryInLocal = anchorageLinkFeedbackGeometryProvider.get();

					// determine link target point
					Point targetPointInScene = anchored instanceof EdgeLabelPart
							? computePosition((EdgePart) anchorage, role)
							: computePosition(anchorageVisual, anchorageGeometryInLocal, anchoredVisual,
									anchoredGeometryInLocal.getBounds().getCenter());

					// determine link source point
					Point sourcePointInScene = computePosition(anchoredVisual, anchoredGeometryInLocal, anchorageVisual,
							FX2Geometry.toPoint(
									(anchorageVisual.sceneToLocal(Geometry2FX.toFXPoint(targetPointInScene)))));

					// construct link line
					return new Line(sourcePointInScene, targetPointInScene);
				}

			};
			SelectionLinkFeedbackPart part = injector.getInstance(SelectionLinkFeedbackPart.class);
			part.setGeometryProvider(linkFeedbackGeometryProvider);
			return part;
		}
		return null;
	}
}
