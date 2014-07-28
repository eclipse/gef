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
import javafx.scene.Group;
import javafx.scene.Node;

import org.eclipse.gef4.fx.anchors.AnchorKey;
import org.eclipse.gef4.fx.anchors.AnchorLink;
import org.eclipse.gef4.fx.anchors.FXStaticAnchor;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.geometry.planar.Point;

public interface IFXConnection {

	/**
	 * The <i>id</i> used to identify the start point of this connection at the
	 * start anchor.
	 */
	public static final String START_ROLE = "start";

	/**
	 * The <i>id</i> used to identify the end point of this connection at the
	 * end anchor.
	 */
	public static final String END_ROLE = "end";

	/**
	 * Adds a new static anchored way point at the specified position.
	 * 
	 * @param index
	 * @param wayPoint
	 */
	public void addWayPoint(int index, Point wayPoint);

	/**
	 * Adds a new way point {@link AnchorLink} to this connection at the
	 * specified position.
	 * 
	 * @param index
	 * @param wayPointAnchorLink
	 */
	public void addWayPointAnchorLink(int index, AnchorLink wayPointAnchorLink);

	/**
	 * Returns an {@link AnchorLink} for a new way point which is to be anchored
	 * at the given anchor.
	 * 
	 * @param anchor
	 *            The {@link IFXAnchor} at which the way point will be anchored.
	 * @return An {@link AnchorLink} for a new way point.
	 */
	public AnchorLink createWayPointAnchorLink(IFXAnchor anchor);

	/**
	 * Returns an {@link AnchorLink} for a new way point which is stored in a
	 * new {@link FXStaticAnchor}.
	 * 
	 * @param wayPoint
	 *            The new {@link Point way point}.
	 * @return An {@link AnchorLink} for the new way point.
	 */
	public AnchorLink createWayPointAnchorLink(Point wayPoint);

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
	 * Returns the value of the {@link #endAnchorLinkProperty()}.
	 * 
	 * @return
	 */
	public AnchorLink getEndAnchorLink();

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
	 * Returns an array containing the {@link AnchorLink}s representing all
	 * points constituting this connection.
	 * 
	 * @return
	 */
	public AnchorLink[] getPointAnchorLinks();

	/**
	 * Returns an array containing all points (not anchors) constituting this
	 * connection, i.e. start point, way points, and end point.
	 * 
	 * @return An array containing all points constituting this connection.
	 */
	public Point[] getPoints();

	/**
	 * Returns the value of the {@link #startAnchorLinkProperty()}.
	 * 
	 * @return
	 */
	public AnchorLink getStartAnchorLink();

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

	/**
	 * Returns the specified way point (not anchor).
	 * 
	 * @param index
	 * @return The specified way point.
	 */
	public Point getWayPoint(int index);

	/**
	 * Returns the specified way point {@link AnchorLink}.
	 * 
	 * @param index
	 * @return The specified way point {@link AnchorLink}.
	 */
	public AnchorLink getWayPointAnchorLink(int index);

	/**
	 * Returns an unmodifiable list of way point {@link AnchorLink}s.
	 * 
	 * @return An unmodifiable list of way point {@link AnchorLink}s.
	 */
	public List<AnchorLink> getWayPointAnchorLinks();

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
	public ReadOnlyObjectProperty<ListChangeListener<? super AnchorLink>> onWayPointAnchorLinkChangeProperty();

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
	 * Changes the end {@link AnchorLink} of this connection to a new
	 * {@link AnchorLink} which is created for the given {@link IFXAnchor} and
	 * anchored under the default end {@link AnchorKey}.
	 * 
	 * @param endAnchor
	 */
	public void setEndAnchor(IFXAnchor endAnchor);

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
	 * @param onStartAnchorLinkChange
	 */
	public void setOnStartAnchorLinkChange(
			ChangeListener<? super AnchorLink> onStartAnchorLinkChange);

	/**
	 * Sets the corresponding {@link #onWayPointAnchorLinkChangeProperty()}.
	 * 
	 * @param onWayPointChange
	 */
	public void setOnWayPointAnchorLinkChange(
			ListChangeListener<? super AnchorLink> onWayPointChange);

	/**
	 * Changes the start {@link AnchorLink} of this connection to a new
	 * {@link AnchorLink} which is created for the given {@link IFXAnchor} and
	 * anchored under the default start {@link AnchorKey}.
	 * 
	 * @param startAnchor
	 *            The {@link IFXAnchor}
	 */
	public void setStartAnchor(IFXAnchor startAnchor);

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
	 * Sets the {@link AnchorLink} for the specified way point to the given
	 * value.
	 * 
	 * @param index
	 * @param wayPointAnchorLink
	 */
	public void setWayPointAnchorLink(int index, AnchorLink wayPointAnchorLink);

	/**
	 * Sets all way point {@link AnchorLink}s of this {@link IFXConnection} to
	 * the given List of {@link AnchorLink}s.
	 * 
	 * @param wayPointAnchorLinks
	 *            List of way point {@link AnchorLink}s.
	 */
	public void setWayPointAnchorLinks(List<AnchorLink> wayPointAnchorLinks);

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
	 * List of {@link AnchorLink}s which are used as way points for this
	 * connection.
	 * 
	 * @return List of {@link AnchorLink}s which are used as way points for this
	 *         connection.
	 */
	public ReadOnlyListProperty<AnchorLink> wayPointsProperty();

}