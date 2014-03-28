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
import javafx.scene.Node;

import org.eclipse.gef4.fx.nodes.IFXConnection;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.behaviors.FXSelectionBehavior;
import org.eclipse.gef4.mvc.fx.ui.example.parts.FXGeometricCurvePart;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

public class FXExampleWayPointPolicy extends AbstractPolicy<Node> {

	protected static final double REMOVE_THRESHOLD = 10;
	
	private final FXGeometricCurvePart curvePart;

	public FXExampleWayPointPolicy(FXGeometricCurvePart fxGeometricCurvePart) {
		curvePart = fxGeometricCurvePart;
	}

	private boolean isCreate;
	private Point2D startPointInScene;
	private Point startPoint;

	/**
	 * Selects a way point on the curve to be manipulated. The way point is
	 * identified by its index.
	 * 
	 * @param wayPointIndex
	 *            index of the way point to select
	 */
	public void selectWayPoint(int wayPointIndex, Point p) {
		init(p);
		isCreate = false;
	}

	private void init(Point p) {
		curvePart.setRefreshFromModel(false);
		startPointInScene = new Point2D(p.x, p.y);
		Point2D pLocal = curvePart.getVisual().sceneToLocal(
				startPointInScene);
		startPoint = new Point(pLocal.getX(), pLocal.getY());
	}

	/**
	 * Creates a new way point on the curve at the specified index. Selects the
	 * new way point for manipulation
	 * 
	 * @param wayPointIndex
	 *            index of the way point to select
	 * @param p
	 *            {@link Point} providing start coordinates of the new way point
	 */
	public void createWayPoint(int wayPointIndex, Point p) {
		init(p);
		isCreate = true;
		((IFXConnection) curvePart.getVisual()).addWayPoint(wayPointIndex, new Point(startPoint));
	}

	/**
	 * Updates the selected way point. Sets its coordinates to the coordinates
	 * of the given point.
	 * 
	 * @param wayPointIndex
	 *            index of the selected way point
	 * @param p
	 *            {@link Point} providing new way point coordinates
	 */
	public void updateWayPoint(int wayPointIndex, Point p) {
		Point newWayPoint = transformToLocal(p);
		((IFXConnection) curvePart.getVisual()).setWayPoint(wayPointIndex, newWayPoint);
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

	/**
	 * Commits updates to the model.
	 * 
	 * @param wayPointIndex
	 *            index of the selected way point
	 * @param p
	 *            {@link Point} providing new way point coordinates
	 */
	public void commitWayPoint(int wayPointIndex, Point p) {
		curvePart.setRefreshFromModel(true);

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