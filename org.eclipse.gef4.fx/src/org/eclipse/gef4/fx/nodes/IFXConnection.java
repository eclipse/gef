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

import java.util.List;

import javafx.scene.Node;

import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.geometry.planar.Point;

public interface IFXConnection {

	/**
	 * Adds a new static anchored way point at the specified position.
	 * 
	 * @param index
	 * @param wayPoint
	 */
	public void addWayPoint(int index, Point wayPoint);

	/**
	 * Inserts a new way point (anchor) at the specified index.
	 * 
	 * @param index
	 * @param wayPointAnchor
	 */
	public void addWayPointAnchor(int index, IFXAnchor wayPointAnchor);

	public Node getCurveNode();

	/**
	 * Returns the {@link IFXAnchor} which specifies the end position of this
	 * connection.
	 * 
	 * @return the {@link IFXAnchor} which specifies the end position of this
	 *         connection
	 */
	public IFXAnchor getEndAnchor();

	/**
	 * Returns the current end decoration of this connection. Per default, a
	 * connection does not have an end decoration, i.e. it is set to
	 * <code>null</code>.
	 * 
	 * @return the current end decoration of this connection
	 */
	public IFXDecoration getEndDecoration();

	/**
	 * Returns the end point (not anchor) of this connection.
	 * 
	 * @return the end point of this connection
	 */
	public Point getEndPoint();

	/**
	 * Returns an array containing all points (not anchors) constituting this
	 * connection, i.e. start point, way points, and end point.
	 * 
	 * @return an array containing all points constituting this connection
	 */
	public Point[] getPoints();

	/**
	 * Returns the {@link IFXAnchor} which specifies the start position of this
	 * connection.
	 * 
	 * @return the {@link IFXAnchor} which specifies the start position of this
	 *         connection
	 */
	public IFXAnchor getStartAnchor();

	/**
	 * Returns the current start decoration of this connection. Per default, a
	 * connection does not have a start decoration, i.e. it is set to
	 * <code>null</code>.
	 * 
	 * @return the current start decoration of this connection
	 */
	public IFXDecoration getStartDecoration();

	/**
	 * Returns the start point (not anchor) of this connection.
	 * 
	 * @return the start point of this connection
	 */
	public Point getStartPoint();

	/**
	 * Returns the specified way point (not anchor).
	 * 
	 * @param index
	 * @return the specified way point
	 */
	public Point getWayPoint(int index);

	/**
	 * Returns an unmodifiable list of {@link IFXAnchor}s which specify the
	 * positions of this connection's way points.
	 * 
	 * @return an unmodifiable list of {@link IFXAnchor}s which specify the
	 *         positions of this connection's way points
	 */
	public List<IFXAnchor> getWayPointAnchors();

	/**
	 * Returns an unmodifiable list of way points (not their anchors).
	 * 
	 * @return an unmodifiable list of way points
	 */
	public List<Point> getWayPoints();

	/**
	 * Removes all way points from this connection.
	 */
	public void removeAllWayPoints();

	/**
	 * Removes the specified way point from this connection.
	 * 
	 * @param index
	 */
	public void removeWayPoint(int index);

	/**
	 * Changes the end anchor of this connection to the passed-in
	 * {@link IFXAnchor}.
	 */
	public void setEndAnchor(IFXAnchor endAnchor);

	/**
	 * Sets the end decoration for this connection. Setting the end decoration
	 * to <code>null</code> disables it.
	 * 
	 * @param endDeco
	 *            new end decoration for this connection
	 */
	public void setEndDecoration(IFXDecoration endDeco);

	/**
	 * Sets the end point anchor of this connection to a static anchor pointing
	 * to the given endPoint.
	 * 
	 * @param endPoint
	 */
	public void setEndPoint(Point endPoint);

	/**
	 * Changes the start anchor of this connection to the passed-in
	 * {@link IFXAnchor}.
	 */
	public void setStartAnchor(IFXAnchor startAnchor);

	/**
	 * Sets the start decoration for this connection. Setting the start
	 * decoration to <code>null</code> disables it.
	 * 
	 * @param startDeco
	 *            new start decoration for this connection
	 */
	public void setStartDecoration(IFXDecoration startDeco);

	/**
	 * Sets the start point anchor of this connection to a static anchor
	 * pointing to the given startPoint.
	 * 
	 * @param startPoint
	 */
	public void setStartPoint(Point startPoint);

	/**
	 * Sets the specified way point anchor to a static anchor pointing to the
	 * given position.
	 * 
	 * @param index
	 * @param wayPoint
	 */
	public void setWayPoint(int index, Point wayPoint);

	/**
	 * Sets the way point anchor at the given index to the given
	 * {@link IFXAnchor}.
	 * 
	 * @param index
	 * @param wayPointAnchor
	 */
	public void setWayPointAnchor(int index, IFXAnchor wayPointAnchor);

	/**
	 * Sets all way point anchors of this connection to static anchors pointing
	 * to the given list of points.
	 * 
	 * @param wayPoints
	 */
	public void setWayPoints(List<Point> wayPoints);

}