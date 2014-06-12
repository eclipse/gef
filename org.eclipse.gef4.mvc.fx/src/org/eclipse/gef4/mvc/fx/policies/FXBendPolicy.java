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
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

// TODO: find a better name
public class FXBendPolicy extends AbstractPolicy<Node> implements
		ITransactional {

	protected static final double REMOVE_THRESHOLD = 10;

	private Point2D startPointInScene;
	private Point startPoint;
	private List<Point> initialWayPoints;
	private List<Point> currentWayPoints;

	private Point removed;
	private int removedIndex;

	private FXChangeWayPointsOperation op;
	private int wayPointIndex;
	private final Point newWayPoint = new Point();

	@Override
	public IUndoableOperation commit() {
		removed = null;
		removedIndex = -1;
		getHost().setRefreshVisual(true);
		return op;
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
		this.wayPointIndex = wayPointIndex;
		newWayPoint.setLocation(p);
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

	private IFXConnection getConnection() {
		return (IFXConnection) getHost().getVisual();
	}

	private void hideShowOverlaid() {
		// put removed back in
		if (removed != null) {
			if (removedIndex <= wayPointIndex) {
				wayPointIndex++;
			}
			currentWayPoints.add(removedIndex, removed);
			removed = null;
		}

		// determine overlaid neighbor
		removedIndex = -1;
		List<Point> points = currentWayPoints;
		if (wayPointIndex > 0) {
			if (newWayPoint.getDistance(points.get(wayPointIndex - 1)) < REMOVE_THRESHOLD) {
				removedIndex = wayPointIndex - 1;
			}
		}
		if (removedIndex == -1 && wayPointIndex + 1 < points.size()) {
			if (newWayPoint.getDistance(points.get(wayPointIndex + 1)) < REMOVE_THRESHOLD) {
				removedIndex = wayPointIndex + 1;
			}
		}

		// remove neighbor if overlaid
		if (removedIndex != -1) {
			if (wayPointIndex > removedIndex) {
				wayPointIndex--;
			}
			removed = currentWayPoints.get(removedIndex);
			currentWayPoints.remove(removedIndex);
		}
	}

	@Override
	public void init() {
		getHost().setRefreshVisual(false);
	}

	private void init(Point p) {
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
	 * Moves the previously selected/created way point to the given position.
	 * 
	 * @param wayPointIndex
	 *            index of the selected way point
	 * @param p
	 *            {@link Point} providing new way point coordinates
	 */
	public void moveWayPoint(Point p) {
		newWayPoint.setLocation(transformToLocal(p));
		currentWayPoints.set(wayPointIndex, newWayPoint);

		hideShowOverlaid();

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
	 * Selects a way point on the curve to be manipulated. The way point is
	 * identified by its index.
	 * 
	 * @param wayPointIndex
	 *            index of the way point to select
	 */
	public void selectWayPoint(int wayPointIndex, Point p) {
		init(p);
		this.wayPointIndex = wayPointIndex;
		newWayPoint.setLocation(p);
	}

	private Point transformToLocal(Point p) {
		Point2D pLocal = getHost().getVisual().sceneToLocal(p.x, p.y);
		Point2D initialPos = getHost().getVisual().sceneToLocal(
				startPointInScene);

		Point delta = new Point(pLocal.getX() - initialPos.getX(),
				pLocal.getY() - initialPos.getY());

		return new Point(startPoint.x + delta.x, startPoint.y + delta.y);
	}

}