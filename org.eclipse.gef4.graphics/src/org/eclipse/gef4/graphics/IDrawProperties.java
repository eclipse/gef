/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
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
package org.eclipse.gef4.graphics;


/**
 * <p>
 * An {@link IDrawProperties} manages the {@link IGraphics} properties used when
 * displaying a geometric object using one of the
 * {@link IGraphics#draw(org.eclipse.gef4.geometry.planar.ICurve) draw(ICurve)},
 * or {@link IGraphics#draw(org.eclipse.gef4.geometry.planar.Path) draw(Path)}
 * methods.
 * </p>
 * 
 * @author mwienand
 * 
 */
public interface IDrawProperties extends IGraphicsProperties {

	/**
	 * <p>
	 * The LineCap specifies how to display unconnected end points of displayed
	 * lines. A displayed line does always have a width. That's why you are
	 * really dealing with two parallel lines, filled with some color.
	 * </p>
	 * <p>
	 * The different LineCaps are:
	 * <ul>
	 * <li>{@link #FLAT}</li>
	 * <li>{@link #SQUARE}</li>
	 * <li>{@link #ROUND}</li>
	 * </ul>
	 * </p>
	 */
	public enum LineCap {
		/**
		 * A FLAT {@link LineCap} will not extend a drawn line beyond its
		 * unconnected end points.
		 * 
		 * @see #SQUARE
		 * @see #ROUND
		 */
		FLAT,

		/**
		 * A ROUND {@link LineCap} will draw a semi-circle at the unconnected
		 * end points of a displayed line.
		 * 
		 * @see #FLAT
		 * @see #SQUARE
		 */
		ROUND,

		/**
		 * A SQUARE {@link LineCap} will extend a drawn line beyond its
		 * unconnected end points by half the line's width.
		 * 
		 * @see #FLAT
		 * @see #ROUND
		 */
		SQUARE
	}

	/**
	 * <p>
	 * The LineJoin specifies how to display the connection point of two
	 * displayed lines. A displayed line does always have a width. That's why
	 * one displayed line does really consist of two parallel lines, filled with
	 * some color. When two such lines are connected with each other in one end
	 * point, a bent corner is formed in this end point. The LineJoin specifies
	 * how to fill that bent corner.
	 * </p>
	 * <p>
	 * The different LineJoins are:
	 * <ul>
	 * <li>{@link #BEVEL}</li>
	 * <li>{@link #MITER}</li>
	 * <li>{@link #ROUND}</li>
	 * </ul>
	 * </p>
	 */
	public enum LineJoin {
		/**
		 * A BEVEL {@link LineJoin} fills the bent corner triangular.
		 * 
		 * @see {@link #MITER}
		 * @see {@link #ROUND}
		 */
		BEVEL,

		/**
		 * A MITER {@link LineJoin} fills the bent corner up to the intersection
		 * point of the two outermost lines if its distance to the middle
		 * intersection is less than or equal to the
		 * {@link IDrawProperties#getMiterLimit miter limit}. In case of
		 * exceeding the {@link IDrawProperties#getMiterLimit miter limit}, the
		 * {@link #BEVEL} {@link LineJoin} is used.
		 * 
		 * @see {@link #MITER}
		 * @see {@link #ROUND}
		 */
		MITER,

		/**
		 * A ROUND {@link LineJoin} fills the bent corner with a circular arc.
		 * 
		 * @see {@link #BEVEL}
		 * @see {@link #MITER}
		 */
		ROUND
	}

	/**
	 * The default dash array is set to <code>null</code>. So that a displayed
	 * line is drawn without dashes.
	 */
	static final double[] DEFAULT_DASH_ARRAY = null;

	/**
	 * The default initially assumed covered distance when applying the current
	 * {@link #getDashArray() dash-array} is set to <code>0d</code>.
	 */
	static final double DEFAULT_DASH_BEGIN = 0d;

	/**
	 * The default {@link LineCap} is set to {@link LineCap#FLAT FLAT}.
	 */
	static final LineCap DEFAULT_LINE_CAP = LineCap.FLAT;

	/**
	 * The default {@link LineJoin} is set to {@link LineJoin#BEVEL BEVEL}.
	 */
	static final LineJoin DEFAULT_LINE_JOIN = LineJoin.BEVEL;

	/**
	 * The default {@link #getMiterLimit() miter limit} is set to
	 * <code>10d</code>.
	 */
	static final double DEFAULT_MITER_LIMIT = 10d;

	/**
	 * The default {@link #getLineWidth() line width} is set to <code>1d</code>.
	 */
	static final double DEFAULT_LINE_WIDTH = 1d;

	/**
	 * Anti-aliasing is enabled per default.
	 */
	static final boolean DEFAULT_ANTIALIASING = true;

	/**
	 * Returns the {@link Color draw color} associated with this
	 * {@link IDrawProperties}.
	 * 
	 * @return the current {@link Color draw color}
	 */
	Color getColor();

	IDrawProperties getCopy();

	/**
	 * <p>
	 * Returns the dash-array associated with this {@link IDrawProperties}.
	 * </p>
	 * 
	 * <p>
	 * The dash-array consists of distance values which alternatingly specify
	 * opaque and transparent sections.
	 * </p>
	 * 
	 * @return the current dash-array
	 */
	double[] getDashArray();

	/**
	 * Returns the initially assumed covered distance when applying the
	 * {@link #getDashArray() dash-array}.
	 * 
	 * @return the initially assumed covered distance when applying the
	 *         {@link #getDashArray() dash-array}
	 */
	double getDashBegin();

	/**
	 * Returns the {@link LineCap} associated with this {@link IDrawProperties}.
	 * 
	 * @return the current {@link LineCap}
	 */
	LineCap getLineCap();

	/**
	 * Returns the {@link LineJoin} associated with this {@link IDrawProperties}
	 * .
	 * 
	 * @return the current {@link LineJoin}
	 */
	LineJoin getLineJoin();

	/**
	 * <p>
	 * Returns the line width associated with this {@link IDrawProperties}.
	 * </p>
	 * <p>
	 * When drawing a line, you are actually filling the area between two
	 * parallel lines with some color. The distance between these two parallel
	 * lines is called the line width.
	 * </p>
	 * 
	 * @return the current line width
	 */
	double getLineWidth();

	/**
	 * <p>
	 * Returns the miter limit associated with this {@link IDrawProperties}.
	 * </p>
	 * <p>
	 * When drawing the connection point of two lines, one option is to use the
	 * {@link LineJoin#MITER MITER} style to fill the bent corner that is formed
	 * by the two lines because of their thickness.
	 * </p>
	 * <p>
	 * The miter limit restricts the use of the {@link LineJoin#MITER MITER}
	 * style, because for low intersection angles, the intersection point may
	 * lie far away from the original connection point. Its value is the maximal
	 * quotient of the distance between the intersection point and the
	 * connection point and the line width.
	 * </p>
	 * 
	 * @return the current miter limit
	 */
	double getMiterLimit();

	/**
	 * Returns <code>true</code> if anti-aliasing is currently enabled.
	 * Otherwise, <code>false</code> is returned.
	 * 
	 * @return <code>true</code> if anti-aliasing is currently enabled,
	 *         otherwise <code>false</code>
	 */
	boolean isAntialiasing();

	/**
	 * Enables or disables anti-aliasing dependent on the given
	 * <i>antialiasing</i> where <code>true</code> enables anti-aliasing and
	 * <code>false</code> disables anti-aliasing.
	 * 
	 * @param antialiasing
	 * @return enables or disables anti-aliasing
	 */
	IDrawProperties setAntialiasing(boolean antialiasing);

	/**
	 * Sets the {@link Color draw color} associated with this
	 * {@link IDrawProperties} to the given value.
	 * 
	 * @param drawColor
	 *            the new {@link Color draw color}
	 * @return <code>this</code> for convenience
	 */
	IDrawProperties setColor(Color drawColor);

	/**
	 * Sets the dash-array associated with this {@link IDrawProperties} to the
	 * given value.
	 * 
	 * @param dashes
	 *            the new dash-array
	 * @return <code>this</code> for convenience
	 */
	IDrawProperties setDashArray(double... dashes);

	/**
	 * Sets the initially assumed covered distance when applying the current
	 * {@link #getDashArray() dash-array}.
	 * 
	 * @param distance
	 *            the initially assumed covered distance
	 * @return <code>this</code> for convenience
	 */
	IDrawProperties setDashBegin(double distance);

	/**
	 * Sets the {@link LineCap} associated with this {@link IDrawProperties} to
	 * the given value.
	 * 
	 * @param lineCap
	 *            the new {@link LineCap}
	 * @return <code>this</code> for convenience
	 */
	IDrawProperties setLineCap(LineCap lineCap);

	/**
	 * Sets the {@link LineJoin} associated with this {@link IDrawProperties} to
	 * the given value.
	 * 
	 * @param lineJoin
	 *            the new {@link LineJoin}
	 * @return <code>this</code> for convenience
	 */
	IDrawProperties setLineJoin(LineJoin lineJoin);

	/**
	 * Sets the line width associated with this {@link IDrawProperties} to the
	 * given value.
	 * 
	 * @param lineWidth
	 *            the new line width
	 * @return <code>this</code> for convenience
	 */
	IDrawProperties setLineWidth(double lineWidth);

	/**
	 * Sets the miter limit associated with this {@link IDrawProperties} to the
	 * given value.
	 * 
	 * @param limit
	 *            the new miter limit
	 * @return <code>this</code> for convenience
	 */
	IDrawProperties setMiterLimit(double limit);

}
