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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.Node;

import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.anchors.IAnchor;
import org.eclipse.gef4.mvc.fx.behaviors.FXSelectionBehavior;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricCurve;
import org.eclipse.gef4.mvc.fx.example.policies.AbstractWayPointPolicy;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IHoverPolicy;
import org.eclipse.gef4.mvc.policies.ISelectionPolicy;

public class FXGeometricCurvePart extends AbstractFXGeometricElementPart
		implements PropertyChangeListener {

	protected static final double REMOVE_THRESHOLD = 10;
	private FXGeometryNode<ICurve> visual;
	private List<IAnchor<Node>> anchors = new ArrayList<IAnchor<Node>>();

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
		Point[] startEnd = computeStartEnd(wayPoints);
		if (startEnd.length == 2) {
			ArrayList<Point> points = new ArrayList<Point>(wayPoints.size() + 2);
			points.add(startEnd[0]);

			if (anchors.size() == 2) {
				// add uncontained way points
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

			points.add(startEnd[1]);
			visual.setGeometry(FXGeometricCurve
					.constructCurveFromWayPoints(points.toArray(new Point[] {})));
		}
	}

	private Point[] computeStartEnd(List<Point> wayPoints) {
		if (anchors.size() != 2) {
			return new Point[] {};
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

		// compute new anchor positions
		try {
			Point start = anchors.get(0).getPosition(this.getVisual(),
					startReference);
			Point end = anchors.get(1).getPosition(this.getVisual(),
					endReference);
			return new Point[] { start, end };
		} catch (IllegalArgumentException x) {
			// fallback to center if points if no intersection can be found
			return new Point[] { startCenter, endCenter };
		}
	}

	@Override
	public void attachVisualToAnchorageVisual(Node anchorageVisual,
			IAnchor<Node> anchor) {
		anchors.add(anchor);
		anchor.addPropertyChangeListener(this);
	}

	@Override
	public void detachVisualFromAnchorageVisual(Node anchorageVisual,
			IAnchor<Node> anchor) {
		anchors.remove(anchor);
		anchor.removePropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getPropertyName().equals(IAnchor.REPRESH)) {
			if (anchors.size() == 2) {
				refreshVisual();
			}
		}
	}

	@Override
	protected IAnchor<Node> getAnchor(IVisualPart<Node> anchored) {
		return null;
	}

}
