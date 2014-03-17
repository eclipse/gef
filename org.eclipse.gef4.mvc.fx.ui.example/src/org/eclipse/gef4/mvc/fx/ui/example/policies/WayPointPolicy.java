/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.ui.example.policies;

import java.util.List;

import javafx.geometry.Point2D;

import org.eclipse.gef4.fx.nodes.FXBinaryConnection;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.behaviors.FXSelectionBehavior;
import org.eclipse.gef4.mvc.fx.ui.example.parts.FXGeometricCurvePart;

public class WayPointPolicy extends AbstractWayPointPolicy {

	protected static final double REMOVE_THRESHOLD = 10;
	
	private final FXGeometricCurvePart curvePart;

	public WayPointPolicy(FXGeometricCurvePart fxGeometricCurvePart) {
		curvePart = fxGeometricCurvePart;
	}

	private boolean isCreate;
	private Point2D startPointInScene;
	private Point startPoint;

	@Override
	public void selectWayPoint(int wayPointIndex, Point p) {
		init(p);
		isCreate = false;
	}

	private void init(Point p) {
		curvePart.setModelRefresh(false);
		startPointInScene = new Point2D(p.x, p.y);
		Point2D pLocal = curvePart.getVisual().sceneToLocal(
				startPointInScene);
		startPoint = new Point(pLocal.getX(), pLocal.getY());
	}

	@Override
	public void createWayPoint(int wayPointIndex, Point p) {
		init(p);
		isCreate = true;
		((FXBinaryConnection) curvePart.getVisual()).addWayPoint(wayPointIndex, new Point(startPoint));
	}

	@Override
	public void updateWayPoint(int wayPointIndex, Point p) {
		Point newWayPoint = transformToLocal(p);
		((FXBinaryConnection) curvePart.getVisual()).setWayPoint(wayPointIndex, newWayPoint);
	}

	private Point transformToLocal(Point p) {
		Point2D pLocal = curvePart.getVisual().sceneToLocal(p.x, p.y);
		Point2D initialPos = curvePart.getVisual().sceneToLocal(
				startPointInScene);

		Point delta = new Point(pLocal.getX()
				- initialPos.getX(), pLocal.getY()
				- initialPos.getY());

		return new Point(startPoint.x + delta.x, startPoint.y
				+ delta.y);
	}

	@Override
	public void commitWayPoint(int wayPointIndex, Point p) {
		curvePart.setModelRefresh(true);

		Point newWayPoint = transformToLocal(p);

		// create or update/remove?
		if (isCreate) {
			curvePart.getContent()
					.addWayPoint(wayPointIndex, newWayPoint);
		} else {
			// remove or update?
			if (isRemove(wayPointIndex, newWayPoint)) {
				curvePart.getContent().removeWayPoint(wayPointIndex);
			} else {
				curvePart.getContent().setWayPoint(wayPointIndex,
						newWayPoint);
			}
		}
		
		FXSelectionBehavior selectionBehavior = curvePart.getSelectionBehavior();
		selectionBehavior.refreshFeedback();
		selectionBehavior.refreshHandles();
		curvePart.refreshVisual();
	}

	private boolean isRemove(int wayPointIndex,
			Point newWayPoint) {
		boolean remove = false;
		List<Point> points = curvePart.getContent().getWayPoints();
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
	
}