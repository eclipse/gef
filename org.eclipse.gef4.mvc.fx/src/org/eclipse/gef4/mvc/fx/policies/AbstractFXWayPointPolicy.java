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
package org.eclipse.gef4.mvc.fx.policies;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.Node;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.fx.nodes.IFXConnection;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.operations.FXChangeWayPointsOperation;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

// TODO: implement ITransactional
public abstract class AbstractFXWayPointPolicy extends AbstractPolicy<Node> {

	protected static final double REMOVE_THRESHOLD = 10;

	private boolean isCreate;
	private Point2D startPointInScene;
	private Point startPoint;
	private List<Point> initialWayPoints;
	private List<Point> currentWayPoints;
	private FXChangeWayPointsOperation op;

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
		getHost().setRefreshVisual(false);
		startPointInScene = new Point2D(p.x, p.y);
		Point2D pLocal = getHost().getVisual().sceneToLocal(startPointInScene);
		startPoint = new Point(pLocal.getX(), pLocal.getY());
		initialWayPoints = getConnection().getWayPoints();
		currentWayPoints = new ArrayList<Point>(initialWayPoints.size());
		for (int i = 0; i < initialWayPoints.size(); i++) {
			currentWayPoints.add(initialWayPoints.get(i).getCopy());
		}
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
		currentWayPoints.add(wayPointIndex, startPoint);
		op = new FXChangeWayPointsOperation("Change way points",
				getConnection(), initialWayPoints, currentWayPoints);

		// execute locally
		try {
			op.execute(null, null);
		} catch (ExecutionException e) {
			throw new IllegalStateException(e);
		}
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
		currentWayPoints.set(wayPointIndex, newWayPoint);

		op = new FXChangeWayPointsOperation("Change way points",
				getConnection(), initialWayPoints, currentWayPoints);

		// execute locally
		try {
			op.execute(null, null);
		} catch (ExecutionException e) {
			throw new IllegalStateException(e);
		}
	}

	private Point transformToLocal(Point p) {
		Point2D pLocal = getHost().getVisual().sceneToLocal(p.x, p.y);
		Point2D initialPos = getHost().getVisual().sceneToLocal(
				startPointInScene);

		Point delta = new Point(pLocal.getX() - initialPos.getX(),
				pLocal.getY() - initialPos.getY());

		return new Point(startPoint.x + delta.x, startPoint.y + delta.y);
	}

	/**
	 * Commits updates to the model.
	 * 
	 * @param wayPointIndex
	 *            index of the selected way point
	 * @param p
	 *            {@link Point} providing new way point coordinates
	 */
	public IUndoableOperation commitWayPoint(int wayPointIndex, Point p) {
		updateWayPoint(wayPointIndex, p);
		getHost().setRefreshVisual(true);

		Point newWayPoint = transformToLocal(p);

		if (!isCreate && isRemove(wayPointIndex, newWayPoint)) {
			currentWayPoints.remove(wayPointIndex);
			op = new FXChangeWayPointsOperation("Change way points",
					getConnection(), initialWayPoints, currentWayPoints);
		}

		// execute locally
		try {
			op.execute(null, null);
		} catch (ExecutionException e) {
			throw new IllegalStateException(e);
		}

		return op;
	}

	private boolean isRemove(int wayPointIndex, Point newWayPoint) {
		boolean remove = false;
		List<Point> points = currentWayPoints;
		if (wayPointIndex > 0) {
			remove = newWayPoint.getDistance(points.get(wayPointIndex - 1)) < REMOVE_THRESHOLD;
		}
		if (!remove && wayPointIndex + 1 < points.size()) {
			remove = newWayPoint.getDistance(points.get(wayPointIndex + 1)) < REMOVE_THRESHOLD;
		}
		return remove;
	}

	public abstract IFXConnection getConnection();

}