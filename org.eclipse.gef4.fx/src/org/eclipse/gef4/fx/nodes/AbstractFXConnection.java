/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.fx.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.collections.MapChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;

import org.eclipse.gef4.fx.anchors.FXStaticAnchor;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;

public abstract class AbstractFXConnection<T extends IGeometry> extends Group
		implements IFXConnection {

	// visuals
	private FXGeometryNode<T> curveNode = new FXGeometryNode<T>();
	private IFXDecoration startDecoration = null;
	private IFXDecoration endDecoration = null;

	private IFXAnchor startAnchor = null;
	private IFXAnchor endAnchor = null;
	private List<IFXAnchor> wayPointAnchors = new ArrayList<IFXAnchor>();

	private MapChangeListener<Node, Point> startPosCL = createStartPositionListener();
	private MapChangeListener<Node, Point> endPosCL = createEndPositionListener();
	private MapChangeListener<Node, Point> wayPosCL = createWayPositionListener();

	{
		// disable resizing children which would change their layout positions
		// in some cases
		setAutoSizeChildren(false);
	}

	@Override
	public IFXDecoration getEndDecoration() {
		return endDecoration;
	}

	@Override
	public IFXDecoration getStartDecoration() {
		return startDecoration;
	}

	@Override
	public void setEndDecoration(IFXDecoration endDeco) {
		endDecoration = endDeco;
		refreshGeometry();
	}

	@Override
	public void setStartDecoration(IFXDecoration startDeco) {
		startDecoration = startDeco;
		refreshGeometry();
	}

	@Override
	public IFXAnchor getStartAnchor() {
		if (startAnchor == null) {
			startAnchor = new FXStaticAnchor(this, new Point());
		}
		return startAnchor;
	}

	@Override
	public IFXAnchor getEndAnchor() {
		if (endAnchor == null) {
			endAnchor = new FXStaticAnchor(this, new Point());
		}
		return endAnchor;
	}

	@Override
	public FXGeometryNode<T> getCurveNode() {
		return curveNode;
	}

	@Override
	public void setStartAnchor(IFXAnchor startAnchor) {
		if (this.startAnchor != null) {
			this.startAnchor.positionProperty().removeListener(startPosCL);
		}
		this.startAnchor = startAnchor;
		startAnchor.positionProperty().addListener(startPosCL);
		refreshReferencePoints();
		refreshGeometry();
	}

	@Override
	public void setEndAnchor(IFXAnchor endAnchor) {
		if (this.endAnchor != null) {
			this.endAnchor.positionProperty().removeListener(endPosCL);
		}
		this.endAnchor = endAnchor;
		endAnchor.positionProperty().addListener(endPosCL);
		refreshReferencePoints();
		refreshGeometry();
	}

	@Override
	public List<IFXAnchor> getWayPointAnchors() {
		return Collections.unmodifiableList(wayPointAnchors);
	}

	@Override
	public void setWayPointAnchor(int index, IFXAnchor wayPointAnchor) {
		wayPointAnchors.get(index).positionProperty().removeListener(wayPosCL);
		wayPointAnchors.set(index, wayPointAnchor);
		wayPointAnchor.positionProperty().addListener(wayPosCL);
		refreshReferencePoints();
		refreshGeometry();
	}

	@Override
	public void addWayPointAnchor(int index, IFXAnchor wayPointAnchor) {
		wayPointAnchors.add(index, wayPointAnchor);
		wayPointAnchor.positionProperty().addListener(wayPosCL);
		refreshReferencePoints();
		refreshGeometry();
	}

	@Override
	public void removeWayPoint(int index) {
		IFXAnchor anchor = wayPointAnchors.get(index);
		anchor.positionProperty().removeListener(wayPosCL);
		wayPointAnchors.remove(index);
		refreshReferencePoints();
		refreshGeometry();
	}

	@Override
	public List<Point> getWayPoints() {
		List<Point> wayPoints = new ArrayList<Point>(wayPointAnchors.size());
		for (int i = 0; i < wayPointAnchors.size(); i++) {
			wayPoints.add(wayPointAnchors.get(i).getPosition(this));
		}
		return wayPoints;
	}

	@Override
	public void setWayPoints(List<Point> wayPoints) {
		removeAllWayPoints();
		for (Point wp : wayPoints) {
			addWayPoint(wayPointAnchors.size(), wp);
		}
	}

	@Override
	public void removeAllWayPoints() {
		for (int i = wayPointAnchors.size() - 1; i >= 0; i--) {
			removeWayPoint(i);
		}
	}

	@Override
	public void setWayPoint(int index, Point wayPoint) {
		setWayPointAnchor(index, new FXStaticAnchor(this, wayPoint));
	}

	@Override
	public void addWayPoint(int index, Point wayPoint) {
		addWayPointAnchor(index, new FXStaticAnchor(this, wayPoint));
	}

	@Override
	public Point getStartPoint() {
		return getStartAnchor().getPosition(this);
	}

	@Override
	public void setStartPoint(Point startPoint) {
		setStartAnchor(new FXStaticAnchor(this, startPoint));
	}

	@Override
	public Point getEndPoint() {
		return getEndAnchor().getPosition(this);
	}

	@Override
	public void setEndPoint(Point endPoint) {
		setEndAnchor(new FXStaticAnchor(this, endPoint));
	}

	@Override
	public Point[] getPoints() {
		List<Point> wayPoints = getWayPoints();
		Point[] points = new Point[wayPoints.size() + 2];

		points[0] = getStartPoint();
		int i = 1;
		for (Point wp : wayPoints)
			points[i++] = wp;
		points[points.length - 1] = getEndPoint();

		return points;
	}

	@Override
	public Point getWayPoint(int index) {
		return wayPointAnchors.get(index).getPosition(this);
	}

	protected void refreshGeometry() {
		// clear current visuals
		getChildren().clear();

		// compute new curve
		curveNode.setGeometry(computeGeometry(getCurvePoints()));

		// z-order decorations above curve
		getChildren().add(curveNode);
		if (startDecoration != null) {
			getChildren().add(startDecoration.getVisual());
		}
		if (endDecoration != null) {
			getChildren().add(endDecoration.getVisual());
		}

		// FIXME: #432035 rotation of decorations is slightly off right now
	}

	public abstract T computeGeometry(Point[] points);

	/**
	 * Returns all points of this connection which are relevant for computing
	 * the curveNode, which are:
	 * <ol>
	 * <li>curve start point: computed using start anchor, start decoration, and
	 * first way point (or end anchor)</li>
	 * <li>way points</li>
	 * <li>curve end point: computed using end anchor, end decoration, and last
	 * way point (or start anchor)</li>
	 * </ol>
	 * 
	 * @return all curve relevant points
	 */
	public Point[] getCurvePoints() {
		Point[] points = new Point[wayPointAnchors.size() + 2];
		points[0] = getCurveStartPoint();
		for (int i = 0; i < wayPointAnchors.size(); i++) {
			points[1 + i] = wayPointAnchors.get(i).getPosition(this);
		}
		points[points.length - 1] = getCurveEndPoint();
		return points;
	}

	/**
	 * Returns the start point for computing this connection's curve visual.
	 * 
	 * @return the start point for computing this connection's curve visual
	 */
	private Point getCurveStartPoint() {
		if (startDecoration == null) {
			return getStartPoint();
		}

		Point sp = getStartPoint();
		Point next = wayPointAnchors.size() > 0 ? wayPointAnchors.get(0)
				.getPosition(this) : getEndPoint();
		Vector sv = new Vector(sp, next);

		Point dsp = startDecoration.getLocalStartPoint();
		Point dep = startDecoration.getLocalEndPoint();
		Vector dv = new Vector(dsp, dep);

		// TODO: move arrangement to somewhere else
		return arrangeDecoration(startDecoration, sp, sv, dsp, dv);
	}

	/**
	 * Arranges the given decoration according to the passed-in values. Returns
	 * the transformed end point of the arranged decoration.
	 * 
	 * @param deco
	 * @param start
	 * @param direction
	 * @param decoStart
	 * @param decoDirection
	 * @return the transformed end point of the arranged decoration
	 */
	private Point arrangeDecoration(IFXDecoration deco, Point start,
			Vector direction, Point decoStart, Vector decoDirection) {
		Node visual = deco.getVisual();

		// position
		Point2D posInParent = visual.localToParent(visual.sceneToLocal(start.x,
				start.y));
		visual.setLayoutX(posInParent.getX());
		visual.setLayoutY(posInParent.getY());

		// rotation
		Angle angleCW = null;
		if (!direction.isNull() && !decoDirection.isNull()) {
			angleCW = decoDirection.getAngleCW(direction);
			visual.getTransforms().clear();
			visual.getTransforms().add(new Rotate(angleCW.deg(), 0, 0));
		}

		// return corresponding curve point
		return angleCW == null ? start : start.getTranslated(decoDirection
				.getRotatedCW(angleCW).toPoint());
	}

	/**
	 * Returns the end point for computing this connection's curve visual.
	 * 
	 * @return the end point for computing this connection's curve visual
	 */
	private Point getCurveEndPoint() {
		if (endDecoration == null) {
			return getEndPoint();
		}

		Point sp = getEndPoint();
		Point next = wayPointAnchors.size() > 0 ? wayPointAnchors.get(
				wayPointAnchors.size() - 1).getPosition(this) : getStartPoint();
		Vector sv = new Vector(sp, next);

		Point dsp = endDecoration.getLocalStartPoint();
		Point dep = endDecoration.getLocalEndPoint();
		Vector dv = new Vector(dsp, dep);

		// TODO: move arrangement to somewhere else
		return arrangeDecoration(endDecoration, sp, sv, dsp, dv);
	}

	/**
	 * Updates the start and end anchor reference points after computing them
	 * using {@link #computeReferencePoints()}.
	 */
	protected void refreshReferencePoints() {
		Point[] referencePoints = computeReferencePoints();
		if (!(getStartAnchor() instanceof FXStaticAnchor)) {
			getStartAnchor().setReferencePoint(this, referencePoints[0]);
		}
		if (!(getEndAnchor() instanceof FXStaticAnchor)) {
			getEndAnchor().setReferencePoint(this, referencePoints[1]);
		}
	}

	/**
	 * Returns a {@link Point} array containing reference points for the start
	 * and end anchors.
	 * 
	 * @return
	 */
	public Point[] computeReferencePoints() {
		// compute start/end point in local coordinate space
		Point start = getStartPoint();
		Point end = getEndPoint();

		// find reference points
		Point startReference = end;
		Point endReference = start;

		// first uncontained way point is start reference
		Node startNode = getStartAnchor().getAnchorageNode();
		if (startNode != null) {
			for (Point p : getWayPoints()) {
				Point2D local = startNode.sceneToLocal(localToScene(p.x, p.y));
				if (!startNode.contains(local)) {
					startReference = p;
					break;
				}
			}
		}

		// last uncontained way point is end reference
		Node endNode = getEndAnchor().getAnchorageNode();
		if (endNode != null) {
			for (Point p : getWayPoints()) {
				Point2D local = endNode.sceneToLocal(localToScene(p.x, p.y));
				if (!endNode.contains(local)) {
					endReference = p;
				}
			}
		}

		return new Point[] { startReference, endReference };
	}

	private MapChangeListener<Node, Point> createStartPositionListener() {
		return new MapChangeListener<Node, Point>() {
			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends Node, ? extends Point> change) {
				Node anchored = change.getKey();
				if (anchored == AbstractFXConnection.this) {
					Point[] referencePoints = computeReferencePoints();
					if (!(getEndAnchor() instanceof FXStaticAnchor)) {
						getEndAnchor().setReferencePoint(AbstractFXConnection.this,
								referencePoints[1]);
					}
					refreshGeometry();
				}
			}
		};
	}

	private MapChangeListener<Node, Point> createEndPositionListener() {
		return new MapChangeListener<Node, Point>() {
			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends Node, ? extends Point> change) {
				Node anchored = change.getKey();
				if (anchored == AbstractFXConnection.this) {
					Point[] referencePoints = computeReferencePoints();
					if (!(startAnchor instanceof FXStaticAnchor)) {
						startAnchor.setReferencePoint(
								AbstractFXConnection.this, referencePoints[0]);
					}
					refreshGeometry();
				}
			}
		};
	}

	private MapChangeListener<Node, Point> createWayPositionListener() {
		return new MapChangeListener<Node, Point>() {
			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends Node, ? extends Point> change) {
				Node anchored = change.getKey();
				if (anchored == AbstractFXConnection.this) {
					refreshReferencePoints();
					refreshGeometry();
				}
			}
		};
	}

}
