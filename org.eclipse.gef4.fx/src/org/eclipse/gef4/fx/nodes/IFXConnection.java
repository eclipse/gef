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

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.scene.Group;
import javafx.scene.Node;

import org.eclipse.gef4.fx.anchors.AnchorKey;
import org.eclipse.gef4.fx.anchors.FXStaticAnchor;
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
	 * Adds a new way point {@link IFXAnchor} to this connection at the
	 * specified position.
	 *
	 * @param index
	 * @param anchor
	 */
	public void addWayPointAnchor(int index, IFXAnchor anchor);

	public ReadOnlyMapProperty<AnchorKey, IFXAnchor> anchorsProperty();

	/**
	 * Returns an array containing the {@link IFXAnchor}s representing all
	 * points constituting this connection.
	 *
	 * @return
	 */
	public List<IFXAnchor> getAnchors();

	/**
	 * Returns the curve visual used to render this connection.
	 *
	 * @return
	 */
	public Node getCurveNode();

	/**
	 * Returns the end {@link IFXAnchor}.
	 *
	 * @return
	 */
	public IFXAnchor getEndAnchor();

	public AnchorKey getEndAnchorKey();

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
	 * @return An array containing all points constituting this connection.
	 */
	public Point[] getPoints();

	/**
	 * Returns the start {@link IFXAnchor}.
	 *
	 * @return
	 */
	public IFXAnchor getStartAnchor();

	public AnchorKey getStartAnchorKey();

	/**
	 * Returns the current start decoration of this connection. Per default, a
	 * connection does not have a start decoration, i.e. it is set to
	 * <code>null</code>.
	 *
	 * @return the current start decoration of this connection
	 */
	public IFXDecoration getStartDecoration();

	/**
	 * Returns the start point (not anchor) of this connection, relative to the
	 * local coordinate system of the connection {@link #getVisual() visual}.
	 *
	 * @return the start point of this connection
	 */
	public Point getStartPoint();

	/**
	 * Returns the {@link Node} which represents this connection. This might be
	 * the same node as returned by {@link #getCurveNode()}. But in the case of
	 * decorations, this might be a {@link Group} which contains the curve
	 * visual and the decoration visuals.
	 *
	 * @return the {@link Node} which visually represents this connection
	 */
	public Node getVisual();

	public AnchorKey getWayAnchorKey(int index);

	/**
	 * Returns the specified way point (not anchor).
	 *
	 * @param index
	 * @return The specified way point.
	 */
	public Point getWayPoint(int index);

	/**
	 * Returns the specified way point {@link IFXAnchor}.
	 *
	 * @param index
	 * @return The specified way point {@link IFXAnchor}.
	 */
	public IFXAnchor getWayPointAnchor(int index);

	/**
	 * Returns an unmodifiable list of way point {@link IFXAnchor}s.
	 *
	 * @return An unmodifiable list of way point {@link IFXAnchor}s.
	 */
	public List<IFXAnchor> getWayPointAnchors();

	/**
	 * Returns an unmodifiable list of way points (not their anchors).
	 *
	 * @return An unmodifiable list of way points
	 */
	public List<Point> getWayPoints();

	/**
	 * Returns <code>true</code> if the end anchor link of this connection is
	 * bound to a FXStaticAnchor. Otherwise returns <code>false</code>.
	 *
	 * @return
	 */
	public boolean isEndConnected();

	/**
	 * Returns <code>true</code> if the start anchor link of this connection is
	 * bound to a FXStaticAnchor.
	 *
	 * @return
	 */
	public boolean isStartConnected();

	/**
	 * Returns <code>true</code> if the specified way point of this connection
	 * is bound to a {@link FXStaticAnchor}.
	 *
	 * @param index
	 * @return <code>true</code> if the specified way point is bound to
	 *         {@link FXStaticAnchor}, otherwise <code>false</code>.
	 */
	public boolean isWayPointConnected(int index);

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

	public void setAnchors(List<IFXAnchor> anchors);

	/**
	 * Changes the end {@link IFXAnchor} of this connection to a new
	 * {@link IFXAnchor} which is created for the given {@link IFXAnchor} and
	 * anchored under the default end {@link AnchorKey}.
	 *
	 * @param endAnchor
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
	 * Generates a FXStaticAnchor with the given position and assigns the start
	 * anchor link of this connection correspondingly.
	 *
	 * @param start
	 */
	public void setEndPoint(Point start);

	/**
	 * Changes the start {@link IFXAnchor} of this connection to a new
	 * {@link IFXAnchor} which is created for the given {@link IFXAnchor} and
	 * anchored under the default start {@link AnchorKey}.
	 *
	 * @param startAnchor
	 *            The {@link IFXAnchor}
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
	 * Generates a FXStaticAnchor with the given position and assigns the start
	 * anchor link of this connection correspondingly.
	 *
	 * @param start
	 */
	public void setStartPoint(Point start);

	/**
	 * Sets the specified way point anchor to a static anchor pointing to the
	 * given position.
	 *
	 * @param index
	 * @param wayPoint
	 */
	public void setWayPoint(int index, Point wayPoint);

	/**
	 * Sets the {@link IFXAnchor} for the specified way point to the given
	 * value.
	 *
	 * @param index
	 * @param wayPointAnchor
	 */
	public void setWayPointAnchor(int index, IFXAnchor wayPointAnchor);

	/**
	 * Sets all way point {@link IFXAnchor}s of this {@link IFXConnection} to
	 * the given List of {@link IFXAnchor}s.
	 *
	 * @param wayPointAnchors
	 *            List of way point {@link IFXAnchor}s.
	 */
	public void setWayPointAnchors(List<IFXAnchor> wayPointAnchors);

	/**
	 * Sets all way point anchors of this connection to static anchors pointing
	 * to the given list of points.
	 *
	 * @param wayPoints
	 */
	public void setWayPoints(List<Point> wayPoints);

}