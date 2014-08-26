/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.parts;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.fx.nodes.FXUtils;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.parts.IContentPart;

import com.google.inject.Provider;

public class FXSelectionLinkFeedbackPart extends FXSelectionFeedbackPart {

	private final Provider<IGeometry> selectionLinkAnchoredGeometryProvider;
	private final Provider<IGeometry> selectionLinkAnchorageGeometryProvider;
	private final Node anchorageVisual;
	private final Node anchoredVisual;

	// TODO: this is not yet nice (need to adapt to the parts
	public FXSelectionLinkFeedbackPart(IContentPart<Node> anchorage,
			IContentPart<Node> anchored,
			Provider<IGeometry> selectionLinkAnchoredGeometryProvider,
			Provider<IGeometry> selectionLinkAnchorageGeometryProvider) {
		super(null);
		this.anchorageVisual = anchorage.getVisual();
		this.anchoredVisual = anchored.getVisual(); // we will be anchored on
		// this
		this.selectionLinkAnchoredGeometryProvider = selectionLinkAnchoredGeometryProvider;
		this.selectionLinkAnchorageGeometryProvider = selectionLinkAnchorageGeometryProvider;
	}

	@Override
	protected FXGeometryNode<IGeometry> createFeedbackVisual() {
		FXGeometryNode<IGeometry> visual = super.createFeedbackVisual();
		visual.setStroke(Color.GREY);
		visual.getStrokeDashArray().add(5.0);
		visual.setStrokeLineJoin(StrokeLineJoin.BEVEL);
		visual.setStrokeType(StrokeType.CENTERED);
		return visual;
	}

	@Override
	protected Provider<IGeometry> getFeedbackGeometryProvider() {
		if (selectionLinkAnchoredGeometryProvider == null
				|| selectionLinkAnchorageGeometryProvider == null) {
			return null;
		}

		IGeometry anchorageGeometryInLocal = selectionLinkAnchorageGeometryProvider
				.get();
		final IGeometry anchorageGeometryInAnchoredLocal = FXUtils
				.sceneToLocal(anchoredVisual, FXUtils.localToScene(
						anchorageVisual, anchorageGeometryInLocal));

		ICurve anchorageOutlineInAnchoredLocal = anchorageGeometryInAnchoredLocal instanceof ICurve ? (ICurve) anchorageGeometryInAnchoredLocal
				: ((IShape) anchorageGeometryInAnchoredLocal).getOutline();

		final IGeometry anchoredGeometryInLocal = selectionLinkAnchoredGeometryProvider
				.get();
		ICurve anchoredOutlineInLocal = anchoredGeometryInLocal instanceof ICurve ? (ICurve) anchoredGeometryInLocal
				: ((IShape) anchoredGeometryInLocal).getOutline();

		final Line centerLine = new Line(anchorageGeometryInAnchoredLocal
				.getBounds().getCenter(), anchoredGeometryInLocal.getBounds()
				.getCenter());

		Point sourcePoint = anchorageOutlineInAnchoredLocal
				.getNearestIntersection(centerLine, anchoredGeometryInLocal
						.getBounds().getCenter());
		if (sourcePoint == null) {
			sourcePoint = anchorageGeometryInAnchoredLocal.getBounds()
					.getCenter();
		}
		Point targetPoint = anchoredOutlineInLocal.getNearestIntersection(
				centerLine, anchorageGeometryInAnchoredLocal.getBounds()
				.getCenter());
		if (targetPoint == null) {
			targetPoint = anchoredGeometryInLocal.getBounds().getCenter();
		}

		final Line linkLine = new Line(sourcePoint, targetPoint);

		return new Provider<IGeometry>() {

			@Override
			public IGeometry get() {
				return linkLine;
			}
		};
	}
}
