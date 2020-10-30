/*******************************************************************************
 * Copyright (c) 2020 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG)  - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.internal.nodes;

import java.util.List;

import org.eclipse.gef.fx.anchors.IAnchor;
import org.eclipse.gef.fx.anchors.StaticAnchor;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.geometry.planar.Point;

import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 * An {@link IBendableCurve} is a curve, whose geometry is defined by start,
 * end, and a list of control points.
 *
 * @param <C>
 *            Type of curve {@link Node}.
 * @param <D>
 *            Type of decoration {@link Node}.
 *
 * @noimplement This interface is not intended to be implemented by clients.
 * @noreference This interface is not intended to be referenced by clients.
 */
public interface IBendableCurve<C extends Node, D extends Node> {

	/**
	 * Adds the given {@link Point} as a control point anchor for the given
	 * index into this {@link IBendableCurve}. The control point is to be
	 * expected in the coordinate system of this {@link IBendableCurve}.
	 *
	 * @param index
	 *            The position where the {@link IAnchor} is inserted within the
	 *            control point anchorsByKeys of this {@link Connection}.
	 * @param controlPoint
	 *            The position for the specified control point.
	 */
	void addControlPoint(int index, Point controlPoint);

	/**
	 * Returns a (writable) property that controls the width of the clickable
	 * area. The clickable area is a transparent 'fat' curve overlaying the
	 * actual curve and serving as mouse target. It is only used if the value of
	 * the property is greater than the stroke width of the underlying curve.
	 *
	 * @return A property to control the width of the clickable area of this
	 *         connection.
	 */
	DoubleProperty clickableAreaWidthProperty();

	/**
	 * Retrieves the value of the clickable area width property (
	 * {@link #clickableAreaWidthProperty()}).
	 *
	 * @return The current value of the {@link #clickableAreaWidthProperty()}.
	 */
	double getClickableAreaWidth();

	/**
	 * Returns the control {@link Point} for the given control index within the
	 * coordinate system of this {@link IBendableCurve}.
	 *
	 * @param index
	 *            The control index for which to return the control point
	 *            position.
	 * @return The control point at the given control index, or
	 *         <code>null</code>.
	 */
	Point getControlPoint(int index);

	/**
	 * Returns a {@link List} containing the control {@link Point}s of this
	 * {@link IBendableCurve}.
	 *
	 * @return A {@link List} containing the control {@link Point}s of this
	 *         {@link IBendableCurve}.
	 */
	List<Point> getControlPoints();

	// ObservableList<Double> getCoordinatesUnmodifiable();

	/**
	 * Returns the {@link Node} which displays the curve geometry.
	 *
	 * @return The {@link Node} which displays the geometry.
	 */
	C getCurve();

	/**
	 * Returns the currently assigned end {@link IAnchor anchor}, or
	 * <code>null</code> if no end {@link IAnchor anchor} is assigned.
	 *
	 * @return The currently assigned end {@link IAnchor anchor}, or
	 *         <code>null</code>.
	 */
	IAnchor getEndAnchor();

	/**
	 * Returns the end decoration of this {@link IBendableCurve}, or
	 * <code>null</code>.
	 *
	 * @return The end decoration, or <code>null</code>.
	 */
	D getEndDecoration();

	/**
	 * Returns the end {@link Point} of this {@link IBendableCurve} within its
	 * coordinate system, which is determined by querying the anchor position
	 * for the end anchor, or <code>null</code> when no end anchor is assigned.
	 *
	 * @return The end {@link Point} of this {@link Connection}, or
	 *         <code>null</code>.
	 */
	Point getEndPoint();

	/**
	 * Returns the list of points making up this {@link IBendableCurve}.
	 *
	 * @return The list of points.
	 */
	ObservableList<Point> getPointsUnmodifiable();

	/**
	 * Returns the currently assigned start {@link IAnchor anchor}, or
	 * <code>null</code> if no start {@link IAnchor anchor} is assigned.
	 *
	 * @return The currently assigned start {@link IAnchor anchor}, or
	 *         <code>null</code>.
	 */
	IAnchor getStartAnchor();

	/**
	 * Returns the start decoration of this {@link IBendableCurve}, or
	 * <code>null</code>.
	 *
	 * @return The start decoration, or <code>null</code>.
	 */
	D getStartDecoration();

	/**
	 * Returns the start {@link Point} of this {@link IBendableCurve} within its
	 * coordinate system, which is determined by querying the anchor position
	 * for the start anchor, or <code>null</code> when no start anchor is
	 * assigned.
	 *
	 * @return The start {@link Point} of this {@link Connection}, or
	 *         <code>null</code>.
	 */
	Point getStartPoint();

	/**
	 * Return <code>true</code> in case the anchor is bound to an anchorage
	 * unequal to this connection.
	 *
	 * @param anchor
	 *            The anchor to test
	 * @return <code>true</code> if the anchor is connected, <code>false</code>
	 *         otherwise.
	 */
	default boolean isConnected(IAnchor anchor) {
		return anchor != null && anchor.getAnchorage() != null
				&& anchor.getAnchorage() != this;
	}

	/**
	 * Returns <code>true</code> if the currently assigned end anchor is bound
	 * to an anchorage. Otherwise returns <code>false</code>.
	 *
	 * @return <code>true</code> if the currently assigned end anchor is bound
	 *         to an anchorage, otherwise <code>false</code>.
	 */
	boolean isEndConnected();

	/**
	 * Returns <code>true</code> if the currently assigned start anchor is bound
	 * to an anchorage. Otherwise returns <code>false</code>.
	 *
	 * @return <code>true</code> if the currently assigned start anchor is bound
	 *         to an anchorage, otherwise <code>false</code>.
	 */
	boolean isStartConnected();

	/**
	 * Removes the control point specified by the given control anchor index
	 * from this {@link IBendableCurve}.
	 *
	 * @param index
	 *            The control anchor index specifying which control point to
	 *            remove.
	 */
	void removeControlPoint(int index);

	/**
	 * Sets the value of the property {@link #clickableAreaWidthProperty()
	 * clickable area width} property.
	 *
	 * @param clickableAreaWidth
	 *            The new value of the {@link #clickableAreaWidthProperty()
	 *            clickable area width} property.
	 */
	void setClickableAreaWidth(double clickableAreaWidth);

	/**
	 * Sets the control point for the given control index to the given
	 * {@link Point}.
	 *
	 * @param index
	 *            The control index of the control point to replace.
	 * @param controlPoint
	 *            The new control {@link Point} for the respective index within
	 *            local coordinates of the {@link Connection}.
	 */
	void setControlPoint(int index, Point controlPoint);

	/**
	 * Replaces all control points with the given {@link Point}s.
	 *
	 * @param controlPoints
	 *            The new control {@link Point}s.
	 */
	void setControlPoints(List<Point> controlPoints);

	/**
	 * Sets the end {@link IAnchor} to the given value.
	 *
	 * @param anchor
	 *            The new end {@link IAnchor}.
	 */
	void setEndAnchor(IAnchor anchor);

	/**
	 * Sets the end decoration {@link Node} to the given value.
	 *
	 * @param decoration
	 *            The new end decoration {@link Node}.
	 */
	void setEndDecoration(D decoration);

	/**
	 * Sets the {@link #setEndAnchor(IAnchor) end anchor} of this
	 * {@link Connection} to an {@link StaticAnchor} yielding the given
	 * {@link Point}.
	 *
	 * @param endPoint
	 *            The new end {@link Point} within local coordinates of the
	 *            {@link Connection}.
	 */
	void setEndPoint(Point endPoint);

	/**
	 * Replaces all anchors of this {@link Connection} with the given
	 * {@link IAnchor}s, i.e. the first given {@link IAnchor} replaces the
	 * currently assigned start anchor, the last given {@link IAnchor} replaces
	 * the currently assigned end anchor, and the intermediate {@link IAnchor}s
	 * replace the currently assigned control anchorsByKeys.
	 *
	 * @param points
	 *            The new {@link Point}s for this {@link Connection}.
	 * @throws IllegalArgumentException
	 *             when less than 2 {@link IAnchor}s are given.
	 */
	void setPoints(List<Point> points);

	/**
	 * Sets the start {@link IAnchor} of this {@link Connection} to the given
	 * value.
	 *
	 * @param anchor
	 *            The new start {@link IAnchor} for this {@link Connection}.
	 */
	void setStartAnchor(IAnchor anchor);

	/**
	 * Sets the start decoration {@link Node} of this {@link Connection} to the
	 * given value.
	 *
	 * @param decoration
	 *            The new start decoration {@link Node} for this
	 *            {@link Connection}.
	 */
	void setStartDecoration(D decoration);

	/**
	 * Sets the {@link #setStartAnchor(IAnchor) start anchor} of this
	 * {@link Connection} to an {@link StaticAnchor} yielding the given
	 * {@link Point}.
	 *
	 * @param startPoint
	 *            The new start {@link Point} within local coordinates of the
	 *            {@link Connection}.
	 */
	void setStartPoint(Point startPoint);

}