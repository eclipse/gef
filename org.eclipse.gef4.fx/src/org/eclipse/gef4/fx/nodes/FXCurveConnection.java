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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.collections.MapChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Node;

import org.eclipse.gef4.fx.anchors.FXStaticAnchor;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Polyline;

public class FXCurveConnection extends FXGeometryNode<ICurve> {

	// start and end point anchors (static (0,0) by default)
	private IFXAnchor startAnchor = new FXStaticAnchor(null) {
		{
			setReferencePoint(FXCurveConnection.this, new Point());
		}
	};
	private IFXAnchor endAnchor = new FXStaticAnchor(null) {
		{
			setReferencePoint(FXCurveConnection.this, new Point());
		}
	};

	// position listeners for the anchors
	private MapChangeListener<Node, Point> startPosCL = null;
	private MapChangeListener<Node, Point> endPosCL = null;

	private MapChangeListener<Node, Point> createStartPositionListener() {
		return new MapChangeListener<Node, Point>() {
			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends Node, ? extends Point> change) {
				Node anchored = change.getKey();
				if (anchored == FXCurveConnection.this) {
					Point[] referencePoints = computeReferencePoints();
					if (!(endAnchor instanceof FXStaticAnchor)) {
						endAnchor.setReferencePoint(FXCurveConnection.this,
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
				if (anchored == FXCurveConnection.this) {
					Point[] referencePoints = computeReferencePoints();
					if (!(startAnchor instanceof FXStaticAnchor)) {
						startAnchor.setReferencePoint(FXCurveConnection.this,
								referencePoints[0]);
					}
					refreshGeometry();
				}
			}
		};
	}

	private List<Point> wayPoints = new ArrayList<Point>();

	public FXCurveConnection() {
	}

	public FXCurveConnection(IFXAnchor startAnchor, IFXAnchor endAnchor) {
		setStartAnchor(startAnchor);
		setEndAnchor(endAnchor);
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
		Node startNode = startAnchor.getAnchorageNode();
		if (startNode != null) {
			for (Point p : wayPoints) {
				Point2D local = startNode.sceneToLocal(localToScene(p.x, p.y));
				if (!startNode.contains(local)) {
					startReference = p;
					break;
				}
			}
		}

		// last uncontained way point is end reference
		Node endNode = endAnchor.getAnchorageNode();
		if (endNode != null) {
			for (Point p : wayPoints) {
				Point2D local = endNode.sceneToLocal(localToScene(p.x, p.y));
				if (!endNode.contains(local)) {
					endReference = p;
				}
			}
		}

		return new Point[] { startReference, endReference };
	}

	/**
	 * Returns an unmodifiable list of way points.
	 * 
	 * @return
	 */
	public List<Point> getWayPoints() {
		return Collections.unmodifiableList(wayPoints);
	}

	public void setWayPoints(List<Point> wayPoints) {
		this.wayPoints.clear();
		this.wayPoints.addAll(wayPoints);
		refreshGeometry();
	}

	public void setWayPoints(Point... wayPoints) {
		setWayPoints(Arrays.asList(wayPoints));
	}

	public void setWayPoint(int index, Point wayPoint) {
		wayPoints.set(index, wayPoint);
		refreshGeometry();
	}

	private void refreshGeometry() {
		setGeometry(computeCurveGeometry());
	}

	public void removeWayPoint(int index) {
		wayPoints.remove(index);
		refreshGeometry();
	}

	public void addWayPoint(int index, Point wayPoint) {
		wayPoints.add(index, wayPoint);
		refreshGeometry();
	}

	public IFXAnchor getEndAnchor() {
		return endAnchor;
	}
	
	public void loosenStartAnchor() {
		FXStaticAnchor staticAnchor = new FXStaticAnchor(null);
		staticAnchor.setReferencePoint(this, getStartPoint());
		setStartAnchor(staticAnchor);
	}
	
	public void loosenEndAnchor() {
		FXStaticAnchor staticAnchor = new FXStaticAnchor(null);
		staticAnchor.setReferencePoint(this, getEndPoint());
		setEndAnchor(staticAnchor);
	}

	public Point getStartPoint() {
		return startAnchor.getPosition(this);
	}
	
	public Point getEndPoint() {
		return endAnchor.getPosition(this);
	}

	public void setEndAnchor(IFXAnchor endAnchor) {
		if (endPosCL == null) {
			endPosCL = createEndPositionListener();
		} else {
			this.endAnchor.positionProperty().removeListener(endPosCL);
		}
		this.endAnchor = endAnchor;
		endAnchor.positionProperty().addListener(endPosCL);
		refreshReferencePoints();
		refreshGeometry();
	}

	public IFXAnchor getStartAnchor() {
		return startAnchor;
	}

	public void setStartAnchor(IFXAnchor startAnchor) {
		if (startPosCL == null) {
			startPosCL = createStartPositionListener();
		} else {
			this.startAnchor.positionProperty().removeListener(startPosCL);
		}
		this.startAnchor = startAnchor;
		startAnchor.positionProperty().addListener(startPosCL);
		refreshReferencePoints();
		refreshGeometry();
	}

	private void refreshReferencePoints() {
		Point[] referencePoints = computeReferencePoints();
		if (!(startAnchor instanceof FXStaticAnchor)) {
			startAnchor.setReferencePoint(this, referencePoints[0]);
		}
		if (!(endAnchor instanceof FXStaticAnchor)) {
			endAnchor.setReferencePoint(this, referencePoints[1]);
		}
	}

	public ICurve computeCurveGeometry() {
		return new Polyline(getPoints());
	}

	public Point[] getPoints() {
		Point[] points = new Point[wayPoints.size() + 2];
		points[0] = getStartPoint();
		for (int i = 0; i < wayPoints.size(); i++) {
			points[i + 1] = wayPoints.get(i);
		}
		points[points.length - 1] = getEndPoint();
		return points;
	}

}
