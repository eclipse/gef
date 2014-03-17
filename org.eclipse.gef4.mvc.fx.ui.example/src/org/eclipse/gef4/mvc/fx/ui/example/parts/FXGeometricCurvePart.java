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
package org.eclipse.gef4.mvc.fx.ui.example.parts;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Shape;

import org.eclipse.gef4.fx.anchors.IFXNodeAnchor;
import org.eclipse.gef4.fx.nodes.FXBinaryConnection;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.behaviors.FXSelectionBehavior;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.parts.FXSelectionHandlePart;
import org.eclipse.gef4.mvc.fx.ui.example.FXExampleHandlePartFactory;
import org.eclipse.gef4.mvc.fx.ui.example.model.FXGeometricCurve;
import org.eclipse.gef4.mvc.fx.ui.example.policies.AbstractReconnectionPolicy;
import org.eclipse.gef4.mvc.fx.ui.example.policies.AbstractWayPointPolicy;
import org.eclipse.gef4.mvc.fx.ui.example.policies.WayPointPolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IHoverPolicy;
import org.eclipse.gef4.mvc.policies.ISelectionPolicy;

public class FXGeometricCurvePart extends AbstractFXGeometricElementPart {

	private final class ReconnectionPolicy extends AbstractReconnectionPolicy {
		private boolean isStartAnchor;
		private FXSelectionHandlePart part;
		private Point2D startPointScene;
		private Point startPointLocal;
		private boolean connected;

		@Override
		public void loosen(int anchorIndex, Point startPointInScene,
				FXSelectionHandlePart part) {
			setModelRefresh(false);
			this.part = part;
			this.startPointScene = new Point2D(startPointInScene.x,
					startPointInScene.y);

			// determine anchor index and offset
			isStartAnchor = anchorIndex == 0;
			// replaceAnchorIndex = anchorIndex;
			Point2D pLocal = getVisual().sceneToLocal(startPointScene);
			startPointLocal = new Point(pLocal.getX(), pLocal.getY());

			removeCurrentAnchor();
		}

		@Override
		public void dragTo(Point pointInScene,
				List<IContentPart<Node>> partsUnderMouse) {
			FXGeometricShapePart anchorPart = getAnchorPart(partsUnderMouse);
			if (connected) {
				if (anchorPart != null) {
					// nothing to do/position still fixed by anchor
					return;
				} else {
					removeCurrentAnchor();
				}
			} else {
				if (anchorPart != null) {
					addAnchorPart(anchorPart);
				} else {
					// update reference position (static anchor)
					Point position = transformToLocal(pointInScene);
					IFXNodeAnchor anchor = isStartAnchor ? visual
							.getStartAnchor() : visual.getEndAnchor();
					anchor.setReferencePoint(visual, position);
					anchor.recomputePositions();
				}
			}
		}

		@Override
		public void releaseAt(Point pointInScene,
				List<IContentPart<Node>> partsUnderMouse) {
			setModelRefresh(true);
			// TODO: commit new anchor to model
			refreshVisual();
		}

		private void addAnchorPart(FXGeometricShapePart cp) {
			setStartAnchor = isStartAnchor;
			cp.addAnchored(FXGeometricCurvePart.this);
			((Shape) part.getVisual())
					.setFill(FXExampleHandlePartFactory.FILL_RED);
			if (isStartAnchor) {
				visual.getStartAnchor().recomputePositions();
			} else {
				visual.getEndAnchor().recomputePositions();
			}
			connected = true;
		}

		private Point transformToLocal(Point p) {
			Point2D pLocal = getVisual().sceneToLocal(p.x, p.y);
			Point2D initialPosLocal = getVisual().sceneToLocal(startPointScene);

			Point delta = new Point(pLocal.getX() - initialPosLocal.getX(),
					pLocal.getY() - initialPosLocal.getY());

			return new Point(startPointLocal.x + delta.x, startPointLocal.y
					+ delta.y);
		}

		private void removeCurrentAnchor() {
			IFXNodeAnchor currentAnchor = isStartAnchor ? visual
					.getStartAnchor() : visual.getEndAnchor();
			Node anchorageNode = currentAnchor.getAnchorageNode();
			if (anchorageNode != null) {
				setStartAnchor = isStartAnchor;
				getViewer().getVisualPartMap().get(anchorageNode)
						.removeAnchored(FXGeometricCurvePart.this);
				((Shape) part.getVisual())
						.setFill(FXSelectionHandlePart.FILL_BLUE);
			}
			connected = false;
		}

		private FXGeometricShapePart getAnchorPart(
				List<IContentPart<Node>> partsUnderMouse) {
			for (IContentPart<Node> cp : partsUnderMouse) {
				if (cp instanceof FXGeometricShapePart) {
					return (FXGeometricShapePart) cp;
				}
			}
			return null;
		}
	}

	private final FXSelectionBehavior selectionBehavior = new FXSelectionBehavior() {
		@Override
		public IGeometry getFeedbackGeometry() {
			return visual.getGeometry();
		}
	};

	private FXBinaryConnection visual;

	private boolean doRefreshVisual = true;

	private boolean setStartAnchor = true;

	public void setModelRefresh(boolean isModelRefresh) {
		doRefreshVisual = isModelRefresh;
	}

	public boolean isModelRefresh() {
		return doRefreshVisual;
	}

	public FXGeometricCurvePart() {
		visual = new FXBinaryConnection() {
			@Override
			public ICurve computeCurveGeometry() {
				return FXGeometricCurve
						.constructCurveFromWayPoints(getPoints());
			}
		};

		installBound(selectionBehavior);
		installBound(ISelectionPolicy.class, new ISelectionPolicy.Impl<Node>());
		installBound(IHoverPolicy.class, new IHoverPolicy.Impl<Node>() {
			@Override
			public boolean isHoverable() {
				return !getHost().getRoot().getViewer().getSelectionModel()
						.getSelected().contains(getHost());
			}
		});
		installBound(AbstractWayPointPolicy.class, new WayPointPolicy(this));
		installBound(AbstractReconnectionPolicy.class, new ReconnectionPolicy());
	}

	@Override
	public FXGeometricCurve getContent() {
		return (FXGeometricCurve) super.getContent();
	}

	@Override
	public void setContent(Object model) {
		if (!(model instanceof FXGeometricCurve)) {
			throw new IllegalArgumentException(
					"Only ICurve models are supported.");
		}
		super.setContent(model);
	}

	@Override
	public Node getVisual() {
		return visual;
	}

	@Override
	public void refreshVisual() {
		if (!doRefreshVisual) {
			return;
		}

		// TODO: compare way points to identify if we need to refresh
		FXGeometricCurve curve = getContent();
		List<Point> wayPoints = curve.getWayPoints();
		if (curve.getTransform() != null) {
			Point[] transformedWayPoints = curve.getTransform().getTransformed(
					wayPoints.toArray(new Point[] {}));
			visual.setWayPoints(transformedWayPoints);
		} else {
			visual.setWayPoints(wayPoints);
		}

		// apply stroke paint
		if (visual.getStroke() != curve.getStroke()) {
			visual.setStroke(curve.getStroke());
		}

		// stroke width
		if (visual.getStrokeWidth() != curve.getStrokeWidth()) {
			visual.setStrokeWidth(curve.getStrokeWidth());
		}

		// dashes
		List<Double> dashList = new ArrayList<Double>(curve.dashes.length);
		for (double d : curve.dashes) {
			dashList.add(d);
		}
		if (!visual.getStrokeDashArray().equals(dashList)) {
			visual.getStrokeDashArray().setAll(dashList);
		}

		// apply effect
		super.refreshVisual();
	}

	@Override
	public void attachVisualToAnchorageVisual(
			final IVisualPart<Node> anchorage, Node anchorageVisual) {
		final IFXNodeAnchor anchor = ((AbstractFXContentPart) anchorage)
				.getAnchor(this);
		if (setStartAnchor) {
			visual.setStartAnchor(anchor);
			setStartAnchor = false;
		} else {
			visual.setEndAnchor(anchor);
		}
	}

	@Override
	public void detachVisualFromAnchorageVisual(IVisualPart<Node> anchorage,
			Node anchorageVisual) {
		final IFXNodeAnchor anchor = ((AbstractFXContentPart) anchorage)
				.getAnchor(this);
		if (anchor == visual.getStartAnchor()) {
			visual.loosenStartAnchor();
		} else if (anchor == visual.getEndAnchor()) {
			visual.loosenEndAnchor();
		} else {
			throw new IllegalStateException("Detach unknown anchor: " + anchor);
		}
	}

	public FXSelectionBehavior getSelectionBehavior() {
		return selectionBehavior;
	}

}
