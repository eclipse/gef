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
package org.eclipse.gef4.swtfx;

import java.awt.Paint;

import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.swtfx.gc.Gradient;
import org.eclipse.gef4.swtfx.gc.GraphicsContext;
import org.eclipse.gef4.swtfx.gc.GraphicsContextState;
import org.eclipse.gef4.swtfx.gc.LineCap;
import org.eclipse.gef4.swtfx.gc.LineJoin;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public interface IFigure extends INode {

	/**
	 * Returns the clipping {@link Path} associated with this {@link IFigure}.
	 * 
	 * @return the clipping {@link Path} associated with this {@link IFigure}.
	 */
	public Path getClipPath();

	/**
	 * Returns the dash list associated with this {@link IFigure}. The dash list
	 * specifies a dash pattern used when stroking an object. A dash pattern
	 * consists of alternating opaque and transparent distances. For example, a
	 * dash pattern of <code>new double[] { 10, 5, 5, 5 }</code> would stroke
	 * the object as follows: <code>__ _ __ _ __ _ </code> (Where one character
	 * represents 5 distance units.)
	 * 
	 * @return the dash pattern consisting of alternating opaque and transparent
	 *         distances
	 */
	public double[] getDashes();

	/**
	 * Returns the dash offset, i.e. the start distance within the dash pattern.
	 * For example, using a dash pattern of
	 * <code>new double[] { 10, 5, 5, 5 }</code> and a dash offset of
	 * <code>5</code> would stroke the object as follows:
	 * <code>_ _ __ _ __ _ __ _</code> (Where one character represents 5
	 * distance units.) Note that the first opaque segment, in this example, is
	 * only of length 5, where as the dash pattern starts with an opaque segment
	 * of length 10.
	 * 
	 * @return the dash offset
	 */
	public double getDashOffset();

	/**
	 * Returns the {@link Object} (TODO: {@link Paint}) that is used to fill an
	 * object. This can be {@link RgbaColor}, {@link Gradient}, or {@link Image}
	 * .
	 * 
	 * @return the {@link Object} used to fill shapes
	 */
	public Object getFill();

	/**
	 * Returns the currently used {@link Font}.
	 * 
	 * @return the currently used {@link Font}
	 */
	public Font getFont();

	/**
	 * @return the global opacity level in range <code>0</code> (transparent) to
	 *         <code>255</code> (opaque)
	 */
	public int getGlobalAlpha();

	/**
	 * @return the {@link LineCap}
	 */
	public LineCap getLineCap();

	/**
	 * @return the {@link LineJoin}
	 */
	public LineJoin getLineJoin();

	/**
	 * @return the line width
	 */
	public double getLineWidth();

	/**
	 * @return the miter-limit
	 */
	public double getMiterLimit();

	/**
	 * Returns the {@link GraphicsContextState} associated with this IFigure.
	 * 
	 * @deprecated The paint state will not be part of the public API.
	 * @return the {@link GraphicsContextState} associated with this IFigure
	 */
	@Deprecated
	GraphicsContextState getPaintStateByReference();

	/**
	 * @return the current stroke {@link Object} which can be either
	 *         {@link RgbaColor}, {@link Gradient}, or {@link Image}
	 */
	public Object getStroke();

	/**
	 * Draws this {@link IFigure} using the passed-in {@link GraphicsContext}.
	 * 
	 * @param g
	 */
	void paint(GraphicsContext g);

	/**
	 * Sets the clipping {@link Path} of this {@link IFigure} to the given
	 * {@link Path}.
	 * 
	 * @param clipPath
	 */
	public void setClipPath(Path clipPath);

	/**
	 * Sets the dash list associated with this {@link IFigure}. The dash list
	 * specifies a dash pattern used when stroking an object. A dash pattern
	 * consists of alternating opaque and transparent distances. For example, a
	 * dash pattern of <code>new double[] { 10, 5, 5, 5 }</code> would stroke
	 * the object as follows: <code>__ _ __ _ __ _ </code> (Where one character
	 * represents 5 distance units.)
	 * 
	 * @param dashes
	 *            the new dash pattern used when stroking an object
	 */
	public void setDashes(double... dashes);

	/**
	 * Sets the dash offset, i.e. the start distance within the dash pattern, to
	 * the specified value. For example, using a dash pattern of
	 * <code>new double[] { 10, 5, 5, 5 }</code> and a dash offset of
	 * <code>5</code> would stroke the object as follows:
	 * <code>_ _ __ _ __ _ __ _</code> (Where one character represents 5
	 * distance units.) Note that the first opaque segment, in this example, is
	 * only of length 5, where as the dash pattern starts with an opaque segment
	 * of length 10.
	 * 
	 * @param dashOffset
	 *            the new dash offset used when stroking an object
	 */
	public void setDashOffset(double dashOffset);

	/**
	 * Sets the {@link Object} (TODO: {@link Paint}) used to fill shapes to the
	 * passed-in {@link Gradient}.
	 * 
	 * @param gradient
	 */
	public void setFill(Gradient<?> gradient);

	/**
	 * Sets the {@link Object} (TODO: {@link Paint}) used to fill shapes to the
	 * passed-in {@link Image}
	 * 
	 * @param image
	 */
	public void setFill(Image image);

	/**
	 * Sets the {@link Object} (TODO: {@link Paint}) used to fill shapes to the
	 * passed-in {@link RgbaColor}.
	 * 
	 * @param color
	 */
	public void setFill(RgbaColor color);

	/**
	 * Sets the {@link Font} used to render text to the passed-in value.
	 * 
	 * @param font
	 */
	public void setFont(Font font);

	/**
	 * Sets the global opacity level to the passed-in value in range
	 * <code>0</code> (transparent) to <code>255</code> (opaque).
	 * 
	 * @param alpha
	 *            global opacity level in range <code>0</code> (transparent) to
	 *            <code>255</code> (opaque)
	 */
	public void setGlobalAlpha(int alpha);

	/**
	 * Sets the {@link LineCap} to the passed-in value.
	 * 
	 * @param cap
	 */
	public void setLineCap(LineCap cap);

	/**
	 * Sets the {@link LineJoin} to the passed-in value.
	 * 
	 * @param join
	 */
	public void setLineJoin(LineJoin join);

	/**
	 * Sets the line width to the passed-in value.
	 * 
	 * @param width
	 */
	public void setLineWidth(double width);

	/**
	 * Sets the miter-limit to the passed-in value.
	 * 
	 * @param miterLimit
	 */
	public void setMiterLimit(double miterLimit);

	/**
	 * Sets the stroke to the passed-in {@link Gradient}.
	 * 
	 * @param gradient
	 */
	public void setStroke(Gradient<?> gradient);

	/**
	 * Sets the stroke to the passed-in {@link Image}.
	 * 
	 * @param image
	 */
	public void setStroke(Image image);

	/**
	 * Sets the stroke to the passed-in {@link RgbaColor}.
	 * 
	 * @param color
	 */
	public void setStroke(RgbaColor color);

	/**
	 * Requests the container to redraw.
	 */
	void update();

}
