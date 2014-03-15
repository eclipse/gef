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
import java.util.Arrays;
import java.util.List;

import javafx.collections.MapChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Transform;

import org.eclipse.gef4.fx.anchors.FXChopBoxAnchor;
import org.eclipse.gef4.fx.anchors.FXStaticAnchor;
import org.eclipse.gef4.fx.anchors.IFXNodeAnchor;
import org.eclipse.gef4.fx.listener.VisualChangeListener;
import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
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
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IHoverPolicy;
import org.eclipse.gef4.mvc.policies.ISelectionPolicy;

public class FXGeometricCurvePart extends AbstractFXGeometricElementPart {

	protected static final double REMOVE_THRESHOLD = 10;

	private FXGeometryNode<ICurve> visual;

	// TODO: move anchor management to helper class
	private List<IFXNodeAnchor> anchors = new ArrayList<IFXNodeAnchor>(2);

	private MapChangeListener<Node, Point> startPosCL;
	private MapChangeListener<Node, Point> endPosCL;

	private VisualChangeListener startVisCL;
	private VisualChangeListener endVisCL;

	private int replaceAnchorIndex = 0;

	// TODO: remove start/end (always use anchors)
	private Point startPoint = new Point(); // TODO: replace by FXStaticAnchor
	private Point endPoint = new Point(); // TODO: replace by FXStaticAnchor
	private boolean doRefreshVisual = true;

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
					Point2D startPointInScene;
					Point startPoint;

					@Override
					public void selectWayPoint(int wayPointIndex, Point p) {
						init(p);
						isCreate = false;
					}

					private void init(Point p) {
						doRefreshVisual = false; // deactivate model refresh

						wayPoints.clear();
						wayPoints.addAll(getContent().getWayPoints());

						startPointInScene = new Point2D(p.x, p.y);
						Point2D pLocal = getVisual().sceneToLocal(
								startPointInScene);
						startPoint = new Point(pLocal.getX(), pLocal.getY());
					}

					@Override
					public void createWayPoint(int wayPointIndex, Point p) {
						init(p);
						isCreate = true;
						wayPoints.add(wayPointIndex, new Point(startPoint));
					}

					@Override
					public void updateWayPoint(int wayPointIndex, Point p) {
						Point newWayPoint = transformToLocal(p);
						wayPoints.set(wayPointIndex, newWayPoint);
						refreshVisualWith(wayPoints);
					}

					private Point transformToLocal(Point p) {
						Point2D pLocal = getVisual().sceneToLocal(p.x, p.y);
						Point2D initialPos = getVisual().sceneToLocal(
								startPointInScene);

						Point delta = new Point(pLocal.getX()
								- initialPos.getX(), pLocal.getY()
								- initialPos.getY());

						return new Point(startPoint.x + delta.x, startPoint.y
								+ delta.y);
					}

					@Override
					public void commitWayPoint(int wayPointIndex, Point p) {
						doRefreshVisual = true; // activate model refresh

						Point newWayPoint = transformToLocal(p);

						// create or update/remove?
						if (isCreate) {
							getContent()
									.addWayPoint(wayPointIndex, newWayPoint);
						} else {
							// remove or update?
							if (isRemove(wayPointIndex, newWayPoint)) {
								getContent().removeWayPoint(wayPointIndex);
							} else {
								getContent().setWayPoint(wayPointIndex,
										newWayPoint);
							}
						}
						refreshVisual();
					}

					private boolean isRemove(int wayPointIndex,
							Point newWayPoint) {
						boolean remove = false;
						List<Point> points = getContent().getWayPoints();
						if (wayPointIndex > 0) {
							remove = newWayPoint.getDistance(points
									.get(wayPointIndex - 1)) < REMOVE_THRESHOLD;
						}
						if (!remove && wayPointIndex + 1 < points.size()) {
							remove = newWayPoint.getDistance(points
									.get(wayPointIndex + 1)) < REMOVE_THRESHOLD;
						}
						return remove;
					}
				});
		installBound(AbstractReconnectionPolicy.class,
				new AbstractReconnectionPolicy() {
					private FXSelectionHandlePart part;
					private Point2D startPointScene;
					private Point startPointLocal;
					private boolean connected = true;

					@Override
					public void loosen(int anchorIndex,
							Point startPointInScene, FXSelectionHandlePart part) {
						this.part = part;
						this.startPointScene = new Point2D(startPointInScene.x,
								startPointInScene.y);

						// determine anchor index and offset
						replaceAnchorIndex = anchorIndex;
						Point2D pLocal = getVisual().sceneToLocal(
								startPointScene);
						startPointLocal = new Point(pLocal.getX(), pLocal
								.getY());

						removeCurrentAnchor();
					}

					@Override
					public void dragTo(Point pointInScene,
							List<IContentPart<Node>> partsUnderMouse) {
						if (connected) {
							FXGeometricShapePart anchorPart = getAnchorPart(partsUnderMouse);
							if (anchorPart != null) {
								// nothing to do/position still fixed by anchor
								return;
							} else {
								removeCurrentAnchor();
							}
						} else {
							FXGeometricShapePart anchorPart = getAnchorPart(partsUnderMouse);
							if (anchorPart != null) {
								addAnchorPart(anchorPart);
							} else {
								// update reference position (static anchor)
								Point position = transformToLocal(pointInScene);
								anchors.get(replaceAnchorIndex)
										.setReferencePoint(getVisual(),
												position);

								// TODO: updating start and end point should not
								// be necessary
								if (replaceAnchorIndex == 1) {
									endPoint = position;
								} else {
									startPoint = position;
								}
							}
							// TODO: automatic refresh
							refreshVisual();
						}
					}

					@Override
					public void releaseAt(Point pointInScene,
							List<IContentPart<Node>> partsUnderMouse) {
						FXGeometricShapePart cp = getAnchorPart(partsUnderMouse);
						if (cp != null) {
							addAnchorPart(cp);
						}
						refreshVisual();
					}

					private void addAnchorPart(FXGeometricShapePart cp) {
						cp.addAnchored(FXGeometricCurvePart.this);
						((Shape) part.getVisual())
								.setFill(FXExampleHandlePartFactory.FILL_RED);
						if (replaceAnchorIndex == 0) {
							anchors.get(0).recomputePositions();
							startPoint = anchors.get(0)
									.getPosition(getVisual());
						} else {
							anchors.get(1).recomputePositions();
							endPoint = anchors.get(1).getPosition(getVisual());
						}
						connected = true;
					}

					private Point transformToLocal(Point p) {
						Point2D pLocal = getVisual().sceneToLocal(p.x, p.y);
						Point2D initialPosLocal = getVisual().sceneToLocal(
								startPointScene);

						Point delta = new Point(pLocal.getX()
								- initialPosLocal.getX(), pLocal.getY()
								- initialPosLocal.getY());

						return new Point(startPointLocal.x + delta.x,
								startPointLocal.y + delta.y);
					}

					private void removeCurrentAnchor() {
						IFXNodeAnchor currentAnchor = anchors
								.get(replaceAnchorIndex);
						Node anchorageNode = currentAnchor.getAnchorageNode();
						if (anchorageNode != null) {
							getViewer().getVisualPartMap().get(anchorageNode)
									.removeAnchored(FXGeometricCurvePart.this);
							((Shape) part.getVisual())
									.setFill(FXSelectionHandlePart.FILL_BLUE);
							connected = false;
						}
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
				});
	}

	public IFXNodeAnchor getStartAnchor() {
		return anchors.get(0);
	}

	public IFXNodeAnchor getEndAnchor() {
		return anchors.get(1);
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
		points.add(startPoint);

		if (anchors.size() == 2) {
			// filter contained way points, so that only uncontained way
			// points are used to compute the curve
			Node startNode = anchors.get(0).getAnchorageNode();
			Node endNode = anchors.get(1).getAnchorageNode();
			List<Point> uncontainedWayPoints = new ArrayList<Point>(
					wayPoints.size());
			for (Point p : wayPoints) {
				boolean inStart = startNode != null
						&& startNode.contains(startNode.sceneToLocal(p.x, p.y));
				boolean inEnd = endNode != null
						&& endNode.contains(endNode.sceneToLocal(p.x, p.y));
				if (!inStart && !inEnd) {
					uncontainedWayPoints.add(p);
				}
			}
			points.addAll(uncontainedWayPoints);
		} else {
			// add all way points
			points.addAll(wayPoints);
		}

		points.add(endPoint);
		visual.setGeometry(FXGeometricCurve.constructCurveFromWayPoints(points
				.toArray(new Point[] {})));
	}

	private Point[] computeReferencePoints() {
		if (anchors.size() != 2) {
			return new Point[] { endPoint, startPoint };
		}

		Node startNode = anchors.get(0).getAnchorageNode();
		Node endNode = anchors.get(1).getAnchorageNode();

		// compute center points in local coordinate space
		Point startCenter = startNode == null ? null : JavaFX2Geometry
				.toRectangle(
						getVisual().sceneToLocal(
								startNode.localToScene(startNode
										.getBoundsInLocal()))).getCenter();
		Point endCenter = endNode == null ? null : JavaFX2Geometry.toRectangle(
				getVisual().sceneToLocal(
						endNode.localToScene(endNode.getBoundsInLocal())))
				.getCenter();

		// find reference points
		Point startReference = endCenter == null ? anchors.get(1)
				.getReferencePoint(getVisual()) : endCenter;
		Point endReference = startCenter == null ? anchors.get(0)
				.getReferencePoint(getVisual()) : startCenter;

		// first uncontained way point is start reference
		List<Point> wayPoints = getContent().getWayPoints();
		for (Point p : wayPoints) {
			Point2D local = startNode == null ? new Point2D(p.x, p.y)
					: startNode.sceneToLocal(p.x, p.y);
			if (startNode != null && !startNode.contains(local)) {
				startReference = p;
				break;
			}
		}

		// last uncontained way point is end reference
		for (Point p : wayPoints) {
			Point2D local = endNode == null ? new Point2D(p.x, p.y) : endNode
					.sceneToLocal(p.x, p.y);
			if (endNode != null && !endNode.contains(local)) {
				endReference = p;
			}
		}

		return new Point[] { startReference, endReference };
	}

	@Override
	public void attachVisualToAnchorageVisual(
			final IVisualPart<Node> anchorage, Node anchorageVisual) {
		final IFXNodeAnchor anchor = ((AbstractFXContentPart) anchorage)
				.getAnchor(this);
		final boolean isEndPoint;

		if (anchors.size() == 2) {
			// replace anchor
			isEndPoint = replaceAnchorIndex == 1;
			anchors.set(replaceAnchorIndex, anchor);
		} else {
			// add anchor
			isEndPoint = anchors.size() == 1;
			anchors.add(anchor);
		}

		// add listeners to know when to refreshVisual()
		MapChangeListener<Node, Point> positionChangeListener = new MapChangeListener<Node, Point>() {
			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends Node, ? extends Point> change) {
				Node anchored = change.getKey();
				if (anchored == getVisual()) {
					Point newPosition = change.getValueAdded();
					// store start/end point & refresh visual
					if (isEndPoint) {
						endPoint = newPosition;
					} else {
						startPoint = newPosition;
					}
					refreshVisual();
				}
			}
		};

		if (isEndPoint) {
			endPosCL = positionChangeListener;
			anchor.positionProperty().addListener(endPosCL);
		} else {
			startPosCL = positionChangeListener;
			anchor.positionProperty().addListener(startPosCL);
		}

		// add visual change listener to anchorage visual in order to update the
		// other end point reference point
		VisualChangeListener visualChangeListener = new VisualChangeListener() {
			@Override
			protected void transformChanged(Transform oldTransform,
					Transform newTransform) {
				recomputeReferencePoint();
			}

			@Override
			protected void boundsChanged(Bounds oldBounds, Bounds newBounds) {
				recomputeReferencePoint();
			}

			private void recomputeReferencePoint() {
				if (anchors.size() != 2) {
					return;
				}

				Point[] referencePoints = computeReferencePoints();
				if (isEndPoint) {
					if (anchors.get(0) instanceof FXChopBoxAnchor) {
						anchors.get(0).setReferencePoint(getVisual(),
								referencePoints[0]);
					}
				} else {
					if (anchors.get(1) instanceof FXChopBoxAnchor) {
						anchors.get(1).setReferencePoint(getVisual(),
								referencePoints[1]);
					}
				}
			}
		};

		if (isEndPoint) {
			endVisCL = visualChangeListener;
			visualChangeListener.register(anchorageVisual, getVisual()
					.getScene().getRoot());
		} else {
			startVisCL = visualChangeListener;
			visualChangeListener.register(anchorageVisual, getVisual()
					.getScene().getRoot());
		}

		// set reference points when we are fully initialized (both anchors set)
		if (anchors.size() == 2) {
			Point[] referencePoints = computeReferencePoints();
			if (anchors.get(0) instanceof FXChopBoxAnchor) {
				anchors.get(0).setReferencePoint(getVisual(),
						referencePoints[0]);
			}
			if (anchors.get(1) instanceof FXChopBoxAnchor) {
				anchors.get(1).setReferencePoint(getVisual(),
						referencePoints[1]);
			}
		}
	}

	@Override
	public void detachVisualFromAnchorageVisual(IVisualPart<Node> anchorage,
			Node anchorageVisual) {
		IFXNodeAnchor anchor = ((AbstractFXContentPart) anchorage)
				.getAnchor(this);

		// remove listeners
		if (anchor == anchors.get(0)) {
			anchor.positionProperty().removeListener(startPosCL);
			startPosCL = null;
			if (anchorageVisual != null) {
				startVisCL.unregister();
				startVisCL = null;
			}
		} else {
			anchor.positionProperty().removeListener(endPosCL);
			endPosCL = null;
			if (anchorageVisual != null) {
				endVisCL.unregister();
				endVisCL = null;
			}
		}

		// replace with static anchor
		int index = anchors.indexOf(anchor);
		FXStaticAnchor staticAnchor = new FXStaticAnchor(null);
		staticAnchor.setReferencePoint(getVisual(), index == 0 ? startPoint
				: endPoint);
		anchors.set(index, staticAnchor);
	}

}
