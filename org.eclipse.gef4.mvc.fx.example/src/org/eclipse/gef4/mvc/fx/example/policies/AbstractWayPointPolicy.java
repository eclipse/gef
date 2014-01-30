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
package org.eclipse.gef4.mvc.fx.example.policies;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

/*
 * TODO: Create marker IPolicy interface as base
 * interface in the interface hierarchy, so that passive policies do not have to be implemented as abstract classes.
 */

/**
 * The AbstractWayPointPolicy implements way point operations on the curve part
 * side. It handles creation, selection, merge (removal), update, and commit of way
 * points on the curve.
 * 
 * @author mwienand
 * 
 */
public abstract class AbstractWayPointPolicy extends AbstractPolicy<Node> {

	/*
	 * Initialization of behavior
	 */

	/**
	 * Selects a way point on the curve to be manipulated. The way point is
	 * identified by its index.
	 * 
	 * @param wayPointIndex
	 *            index of the way point to select
	 */
	public abstract void selectWayPoint(int wayPointIndex);

	/**
	 * Creates a new way point on the curve at the specified index. Selects the
	 * new way point for manipulation
	 * 
	 * @param wayPointIndex
	 *            index of the way point to select
	 * @param p
	 *            {@link Point} providing start coordinates of the new way point
	 */
	public abstract void createWayPoint(int wayPointIndex, Point p);
	
	/**
	 * Removes the way point specified by the given index from the curves way points.
	 * 
	 * @param wayPointIndex
	 */
	public abstract void removeWayPoint(int wayPointIndex);

	/*
	 * Visual updates
	 */

	/**
	 * Updates the selected way point. Sets its coordinates to the coordinates
	 * of the given point.
	 * 
	 * @param wayPointIndex
	 *            index of the selected way point
	 * @param p
	 *            {@link Point} providing new way point coordinates
	 */
	public abstract void updateWayPoint(int wayPointIndex, Point p);

	/*
	 * Model updates
	 */

	/**
	 * Commits updates to the model.
	 * 
	 * @param wayPointIndex
	 *            index of the selected way point
	 * @param p
	 *            {@link Point} providing new way point coordinates
	 */
	public abstract void commitWayPoint(int wayPointIndex, Point p);

}
