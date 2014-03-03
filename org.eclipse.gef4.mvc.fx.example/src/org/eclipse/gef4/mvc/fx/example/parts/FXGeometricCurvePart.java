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
package org.eclipse.gef4.mvc.fx.example.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.MapChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.transform.Transform;

import org.eclipse.gef4.fx.anchors.IFXNodeAnchor;
import org.eclipse.gef4.fx.listener.VisualChangeListener;
import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.behaviors.FXSelectionBehavior;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricCurve;
import org.eclipse.gef4.mvc.fx.example.policies.AbstractWayPointPolicy;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IHoverPolicy;
import org.eclipse.gef4.mvc.policies.ISelectionPolicy;

public class FXGeometricCurvePart extends AbstractFXGeometricElementPart {

	protected static final double REMOVE_THRESHOLD = 10;
	private FXGeometryNode<ICurve> visual;
	private List<IFXNodeAnchor> anchors = new ArrayList<IFXNodeAnchor>();
	private Map<IFXNodeAnchor, MapChangeListener<Node, Point>> mapChangeListeners = new HashMap<IFXNodeAnchor, MapChangeListener<Node, Point>>(
			2);
	private Map<IFXNodeAnchor, VisualChangeListener> visualChangeListeners = new HashMap<IFXNodeAnchor, VisualChangeListener>(
			2);
	private Point start = new Point();
	private Point end = new Point();

	public FXGeometricCurvePart() {
		visual = new FXGeometryNode<ICurve>();
		installBound(ISelectionPolicy.class, new ISelectionPolicy.Impl<Node>());
		installBound(IHoverPolicy.class, new IHoverPolicy.Impl<Node>() {
			@Override
			public boolean isHoverable() {
				return !getHost().getRoot().getViewer().getSelectionModel()
						.getSelected().contains(getHost());
			}
		});

		installBound(new FXSelectionBehavior() {
			@Override
			public IGeometry getFeedbackGeometry() {
				return visual.getGeometry();
			}
		});
		installBound(AbstractWayPointPolicy.class,
				new AbstractWayPointPolicy() {
					private List<Point> wayPoints = new ArrayList<Point>();
					private boolean isCreate;

					@Override
					public void selectWayPoint(int wayPointIndex) {
						isCreate = false;
						wayPoints.clear();
						wayPoints.addAll(getContent().getWayPoints());
					}

					@Override
					public void createWayPoint(int wayPointIndex, Point p) {
						isCreate = true;
						wayPoints.clear();
						wayPoints.addAll(getContent().getWayPoints());
						wayPoints.add(wayPointIndex, new Point(p));
					}

					@Override
					public void updateWayPoint(int wayPointIndex, Point p) {
						Point point = wayPoints.get(wayPointIndex);
						point.x = p.x;
						point.y = p.y;
						refreshVisualWith(wayPoints);
					}

					@Override
					public void commitWayPoint(int wayPointIndex, Point p) {
						if (isCreate) {
							// create new way point
							getContent().addWayPoint(wayPointIndex, p);
						} else {
							// check if we have to remove it
							boolean remove = false;
							List<Point> points = getContent().getWayPoints();
							if (wayPointIndex > 0) {
								remove = p.getDistance(points
										.get(wayPointIndex - 1)) < REMOVE_THRESHOLD;
							}
							if (!remove && wayPointIndex + 1 < points.size()) {
								remove = p.getDistance(points
										.get(wayPointIndex + 1)) < REMOVE_THRESHOLD;
							}

							if (remove) {
								getContent().removeWayPoint(wayPointIndex);
							} else {
								// update existing way point
								getContent().setWayPoint(wayPointIndex, p);
							}
						}
					}

					@Override
					public void removeWayPoint(int wayPointIndex) {
						getContent().removeWayPoint(wayPointIndex);
					}
				});
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
		// TODO: compare way points to identify if we need to refresh

		// use anchors as start and end point
		FXGeometricCurve curveVisual = getContent();
		Point[] wayPoints = curveVisual.getWayPoints().toArray(new Point[] {});
		if (curveVisual.getTransform() != null) {
			wayPoints = curveVisual.getTransform().getTransformed(wayPoints);
		}
		refreshVisualWith(Arrays.asList(wayPoints));

		// apply stroke paint
		if (visual.getStroke() != curveVisual.getStroke()) {
			visual.setStroke(curveVisual.getStroke());
		}

		// stroke width
		if (visual.getStrokeWidth() != curveVisual.getStrokeWidth()) {
			visual.setStrokeWidth(curveVisual.getStrokeWidth());
		}

		// dashes
		List<Double> dashList = new ArrayList<Double>(curveVisual.dashes.length);
		for (double d : curveVisual.dashes) {
			dashList.add(d);
		}
		if (!visual.getStrokeDashArray().equals(dashList)) {
			visual.getStrokeDashArray().setAll(dashList);
		}

		// apply effect
		super.refreshVisual();
	}

	private void refreshVisualWith(List<Point> wayPoints) {
		ArrayList<Point> points = new ArrayList<Point>(wayPoints.size() + 2);
		points.add(start);

		if (anchors.size() == 2) {
			// filter contained way points, so that only uncontained way
			// points are used to compute the curve
			Node startNode = anchors.get(0).getAnchorage();
			Node endNode = anchors.get(1).getAnchorage();
			List<Point> uncontainedWayPoints = new ArrayList<Point>(
					wayPoints.size());
			for (Point p : wayPoints) {
				Point2D slp = startNode.sceneToLocal(p.x, p.y);
				Point2D elp = endNode.sceneToLocal(p.x, p.y);
				if (!startNode.contains(slp) && !endNode.contains(elp)) {
					uncontainedWayPoints.add(p);
				}
			}
			points.addAll(uncontainedWayPoints);
		} else {
			// add all way points
			points.addAll(wayPoints);
		}

		points.add(end);
		visual.setGeometry(FXGeometricCurve.constructCurveFromWayPoints(points
				.toArray(new Point[] {})));
	}

	private Point[] computeReferencePoints() {
		if (anchors.size() != 2) {
			System.out
					.println("Cannot compute reference points: Exactly two anchors expected, but <"
							+ anchors.size() + "> given.");
			return new Point[] { end, start };
		}

		Node startNode = anchors.get(0).getAnchorage();
		Node endNode = anchors.get(1).getAnchorage();

		// compute center points in local coordinate space
		Point startCenter = JavaFX2Geometry.toRectangle(
				getVisual().sceneToLocal(
						startNode.localToScene(startNode.getBoundsInLocal())))
				.getCenter();
		Point endCenter = JavaFX2Geometry.toRectangle(
				getVisual().sceneToLocal(
						endNode.localToScene(endNode.getBoundsInLocal())))
				.getCenter();

		// find reference points
		Point startReference = endCenter;
		Point endReference = startCenter;

		// first uncontained way point is start reference
		List<Point> wayPoints = getContent().getWayPoints();
		for (Point p : wayPoints) {
			Point2D local = startNode.sceneToLocal(p.x, p.y);
			if (!startNode.contains(local)) {
				startReference = p;
				break;
			}
		}

		// last uncontained way point is end reference
		for (Point p : wayPoints) {
			Point2D local = endNode.sceneToLocal(p.x, p.y);
			if (!endNode.contains(local)) {
				endReference = p;
			}
		}

		return new Point[] { startReference, endReference };
	}

	@Override
	public void attachVisualToAnchorageVisual(
			final IVisualPart<Node> anchorage, Node anchorageVisual) {
		// add anchor
		final IFXNodeAnchor anchor = ((AbstractFXContentPart) anchorage)
				.getAnchor(this);
		anchors.add(anchor);
		final boolean isEndPoint = anchors.size() == 2;

		// add listeners to know when to refreshVisual()
		MapChangeListener<Node, Point> mapChangeListener = new MapChangeListener<Node, Point>() {
			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends Node, ? extends Point> change) {
				Node anchored = change.getKey();
				if (anchored == getVisual()) {
					Point newPosition = change.getValueAdded();
					// store start/end point & refresh visual
					if (isEndPoint) {
						end = newPosition;
					} else {
						start = newPosition;
					}
					refreshVisual();
				}
			}
		};
		mapChangeListeners.put(anchor, mapChangeListener);
		anchor.positionProperty().addListener(mapChangeListener);

		// add visual change listener to anchorage visual in order to update the
		// other end point reference point
		VisualChangeListener visualChangeListener = new VisualChangeListener() {
			@Override
			protected void transformChanged(Transform oldTransform,
					Transform newTransform) {
				recomputeReferencePoint(isEndPoint);
			}

			@Override
			protected void boundsChanged(Bounds oldBounds, Bounds newBounds) {
				recomputeReferencePoint(isEndPoint);
			}

			private void recomputeReferencePoint(final boolean isEndPoint) {
				if (anchors.size() != 2) {
					System.out
							.println("not enough anchors to recompute reference points");
					return;
				}

				Point[] referencePoints = computeReferencePoints();
				if (isEndPoint) {
					anchors.get(0).setReferencePoint(getVisual(),
							referencePoints[0]);
				} else {
					anchors.get(1).setReferencePoint(getVisual(),
							referencePoints[1]);
				}
			}
		};
		visualChangeListeners.put(anchor, visualChangeListener);
		visualChangeListener.register(anchorageVisual, getVisual().getScene()
				.getRoot());

		// set reference points when we are fully initialized (both anchors set)
		if (anchors.size() == 2) {
			Point[] referencePoints = computeReferencePoints();
			anchors.get(0).setReferencePoint(getVisual(), referencePoints[1]);
			anchors.get(1).setReferencePoint(getVisual(), referencePoints[0]);
		}
	}

	@Override
	public void detachVisualFromAnchorageVisual(IVisualPart<Node> anchorage,
			Node anchorageVisual) {
		IFXNodeAnchor anchor = ((AbstractFXContentPart) anchorage)
				.getAnchor(this);
		mapChangeListeners.remove(anchor);
		visualChangeListeners.remove(anchor);
		anchors.remove(anchor);
	}

}
