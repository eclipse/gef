/*******************************************************************************
 * Copyright (c) 2011 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.planar;

import java.awt.geom.Path2D;

import org.eclipse.gef4.geometry.convert.AWT2Geometry;
import org.eclipse.gef4.geometry.convert.AWT2SWT;
import org.eclipse.gef4.geometry.convert.Geometry2AWT;
import org.eclipse.gef4.geometry.convert.SWT2AWT;
import org.eclipse.gef4.geometry.utils.PrecisionUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.PathData;

/**
 * Represents the geometric shape of a path, which may consist of independent
 * subgraphs.
 * 
 * Note that while all manipulations (e.g. within shrink, expand) within this
 * class are based on double precision, all comparisons (e.g. within contains,
 * intersects, equals, etc.) are based on a limited precision (with an accuracy
 * defined within {@link PrecisionUtils}) to compensate for rounding effects.
 * 
 * @author anyssen
 */
public class Path extends AbstractGeometry implements IGeometry {

	/**
	 * Winding rule for determining the interior of the path. Indicates that a
	 * point is regarded to lie inside the path, if any ray starting in that
	 * point and pointing to infinity crosses the segments of the path an odd
	 * number of times.
	 */
	public static final int WIND_EVEN_ODD = SWT.FILL_EVEN_ODD;

	/**
	 * Winding rule for determining the interior of the path. Indicates that a
	 * point is regarded to lie inside the path, if any ray starting from that
	 * point and pointing to infinity is crossed by path segments a different
	 * number of times in the counter-clockwise direction than in the clockwise
	 * direction.
	 */
	public static final int WIND_NON_ZERO = SWT.FILL_WINDING;

	private static final long serialVersionUID = 1L;

	private Path2D.Double delegate = new Path2D.Double();

	/**
	 * Creates a new empty path with a default winding rule of
	 * {@link #WIND_NON_ZERO}.
	 */
	public Path() {
	}

	/**
	 * Creates a new empty path with given winding rule.
	 * 
	 * @param windingRule
	 *            the winding rule to use; one of {@link #WIND_EVEN_ODD} or
	 *            {@link #WIND_NON_ZERO}
	 */
	public Path(int windingRule) {
		delegate = new Path2D.Double(
				windingRule == WIND_EVEN_ODD ? Path2D.WIND_EVEN_ODD
						: Path2D.WIND_NON_ZERO);
	}

	/**
	 * Creates a path from the given SWT {@link PathData} using the default
	 * winding rule {@link #WIND_NON_ZERO}.
	 * 
	 * @param pathData
	 *            The data to initialize the path with
	 */
	public Path(PathData pathData) {
		delegate.append(SWT2AWT.toAWTPathIterator(pathData, WIND_NON_ZERO), false);
	}

	/**
	 * Creates a path from the given SWT {@link PathData}, using the given
	 * winding rule.
	 * 
	 * @param pathData
	 *            The data to initialize the path with
	 * @param windingRule
	 *            the winding rule to use; one of {@link #WIND_EVEN_ODD} or
	 *            {@link #WIND_NON_ZERO}
	 */
	public Path(PathData pathData, int windingRule) {
		delegate = new Path2D.Double(
				windingRule == WIND_EVEN_ODD ? Path2D.WIND_EVEN_ODD
						: Path2D.WIND_NON_ZERO);
		delegate.append(SWT2AWT.toAWTPathIterator(pathData, windingRule), false);
	}

	/**
	 * Closes the current sub-path by drawing a straight line (line-to) to the
	 * location of the last move to.
	 */
	public final void close() {
		delegate.closePath();
	}

	/**
	 * @see IGeometry#contains(Point)
	 */
	public boolean contains(Point p) {
		return delegate.contains(Geometry2AWT.toAWTPoint(p));
	}

	/**
	 * Returns <code>true</code> if the given {@link Rectangle} is contained
	 * within {@link IGeometry}, <code>false</code> otherwise.
	 * 
	 * TODO: Generalize to arbitrary {@link IGeometry} objects.
	 * 
	 * @param r
	 *            The {@link Rectangle} to test
	 * @return <code>true</code> if the {@link Rectangle} is fully contained
	 *         within this {@link IGeometry}
	 */
	public boolean contains(Rectangle r) {
		return delegate.contains(Geometry2AWT.toAWTRectangle(r));
	}

	/**
	 * Adds a cubic Bezier curve segment from the current position to the
	 * specified end position, using the two provided control points as Bezier
	 * control points.
	 * 
	 * @param control1X
	 *            The x-coordinate of the first Bezier control point
	 * @param control1Y
	 *            The y-coordinate of the first Bezier control point
	 * @param control2X
	 *            The x-coordinate of the second Bezier control point
	 * @param control2Y
	 *            The y-coordinate of the second Bezier control point
	 * @param x
	 *            The x-coordinate of the desired target point
	 * @param y
	 *            The y-coordinate of the desired target point
	 */
	public final void curveTo(double control1X, double control1Y,
			double control2X, double control2Y, double x, double y) {
		delegate.curveTo(control1X, control1Y, control2X, control2Y, x, y);
	}

	/**
	 * @see IGeometry#getBounds()
	 */
	public Rectangle getBounds() {
		return AWT2Geometry.toRectangle(delegate.getBounds2D());
	}

	/**
	 * @see IGeometry#getTransformed(AffineTransform)
	 */
	@Override
	public IGeometry getTransformed(AffineTransform t) {
		return new Path(AWT2SWT.toSWTPathData(delegate
				.getPathIterator(Geometry2AWT.toAWTAffineTransform(t))));
	}

	/**
	 * Returns the winding rule used to determine the interior of this path.
	 * 
	 * @return the winding rule, i.e. one of {@link #WIND_EVEN_ODD} or
	 *         {@link #WIND_NON_ZERO}
	 */
	public int getWindingRule() {
		return delegate.getWindingRule() == Path2D.WIND_EVEN_ODD ? WIND_EVEN_ODD
				: WIND_NON_ZERO;
	}

	/**
	 * Tests whether this {@link Path} and the given {@link Rectangle} touch,
	 * i.e. they have at least one {@link Point} in common.
	 * 
	 * @param r
	 *            the {@link Rectangle} to test for at least one {@link Point}
	 *            in common with this {@link Path}
	 * @return <code>true</code> if this {@link Path} and the {@link Rectangle}
	 *         touch, otherwise <code>false</code>
	 * @see IGeometry#touches(IGeometry)
	 */
	public boolean touches(Rectangle r) {
		return delegate.intersects(Geometry2AWT.toAWTRectangle(r));
	}

	/**
	 * Adds a straight line segment from the current position to the specified
	 * end position.
	 * 
	 * @param x
	 *            The x-coordinate of the desired target point
	 * @param y
	 *            The y-coordinate of the desired target point
	 */
	public final void lineTo(double x, double y) {
		delegate.lineTo(x, y);
	}

	/**
	 * Changes the current position without adding a new segment.
	 * 
	 * @param x
	 *            The x-coordinate of the desired target point
	 * @param y
	 *            The y-coordinate of the desired target point
	 */
	public final void moveTo(double x, double y) {
		delegate.moveTo(x, y);
	}

	/**
	 * Adds a quadratic curve segment from the current position to the specified
	 * end position, using the provided control point as a parametric control
	 * point.
	 * 
	 * @param controlX
	 *            The x-coordinate of the control point
	 * @param controlY
	 *            The y-coordinate of the control point
	 * @param x
	 *            The x-coordinate of the desired target point
	 * @param y
	 *            The y-coordinate of the desired target point
	 */
	public final void quadTo(double controlX, double controlY, double x,
			double y) {
		delegate.quadTo(controlX, controlY, x, y);
	}

	/**
	 * Resets the path to be empty.
	 */
	public final void reset() {
		delegate.reset();
	}

	/**
	 * @see IGeometry#toPath()
	 */
	public Path toPath() {
		return getCopy();
	}

	/**
	 * Converts this path into an SWT {@link PathData} representation.
	 * 
	 * @return The {@link PathData} representing this path.
	 */
	public PathData toSWTPathData() {
		return AWT2SWT.toSWTPathData(delegate.getPathIterator(null));
	}

	/**
	 * @see org.eclipse.gef4.geometry.planar.IGeometry#getCopy()
	 */
	public Path getCopy() {
		return new Path(toSWTPathData(), getWindingRule());
	}

}
