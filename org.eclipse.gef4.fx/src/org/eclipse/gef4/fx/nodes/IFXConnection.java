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

import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;

import org.eclipse.gef4.fx.anchors.AnchorLink;
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
	 * Returns the {@link IFXAnchor} which specifies the end position of this
	 * connection.
	 * 
	 * @return the {@link IFXAnchor} which specifies the end position of this
	 *         connection
	 */
	public ReadOnlyObjectProperty<AnchorLink> endAnchorLinkProperty();

	/**
	 * Returns the curve visual used to render this connection.
	 * 
	 * @return
	 */
	public Node getCurveNode();

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
	 * Returns an unmodifiable list of way points (not their anchors).
	 * 
	 * @return an unmodifiable list of way points
	 */
	public List<Point> getWayPoints();

	/**
	 * Returns <code>true</code> if the end anchor link of this connection is
	 * bound to a FXStaticAnchor.
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
	 * {@link ChangeListener} which is notified when a new {@link AnchorLink} is
	 * provided for the end point of this connection.
	 * 
	 * @return
	 */
	public ReadOnlyObjectProperty<ChangeListener<? super AnchorLink>> onEndAnchorLinkChangeProperty();

	/**
	 * {@link ChangeListener} which is notified when a new {@link AnchorLink} is
	 * provided for the start point of this connection.
	 * 
	 * @return
	 */
	public ReadOnlyObjectProperty<ChangeListener<? super AnchorLink>> onStartAnchorLinkChangeProperty();

	/**
	 * {@link ChangeListener} which is notified when the way points of this
	 * connections change in any way.
	 * 
	 * @return
	 */
	public ReadOnlyObjectProperty<ListChangeListener<? super Point>> onWayPointChangeProperty();

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
	 * {@link AnchorLink}.
	 */
	public void setEndAnchorLink(AnchorLink endAnchorLink);

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
	 * Sets the corresponding {@link #onEndAnchorLinkChangeProperty()}.
	 * 
	 * @param onEndAnchorLinkChange
	 */
	public void setOnEndAnchorLinkChange(
			ChangeListener<? super AnchorLink> onEndAnchorLinkChange);

	/**
	 * Sets the corresponding {@link #onStartAnchorLinkChangeProperty()}.
	 * 
	 * @param onEndAnchorLinkChange
	 */
	public void setOnStartAnchorLinkChange(
			ChangeListener<? super AnchorLink> onStartAnchorLinkChange);

	/**
	 * Sets the corresponding {@link #onWayPointChangeProperty()}.
	 * 
	 * @param onEndAnchorLinkChange
	 */
	public void setOnWayPointChange(
			ListChangeListener<? super Point> onWayPointChange);

	/**
	 * Changes the start anchor of this connection to the passed-in
	 * {@link AnchorLink}.
	 */
	public void setStartAnchorLink(AnchorLink startAnchorLink);

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
	 * Sets all way point anchors of this connection to static anchors pointing
	 * to the given list of points.
	 * 
	 * @param wayPoints
	 */
	public void setWayPoints(List<Point> wayPoints);

	/**
	 * Returns the {@link AnchorLink} which specifies the start position of this
	 * connection.
	 * 
	 * @return the {@link AnchorLink} which specifies the start position of this
	 *         connection
	 */
	public ReadOnlyObjectProperty<AnchorLink> startAnchorLinkProperty();

	/**
	 * List of {@link Point} which are used as anchor points for this
	 * connection.
	 * 
	 * @return
	 */
	public ReadOnlyListProperty<Point> wayPointsProperty();

}