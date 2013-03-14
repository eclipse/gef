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

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IMultiShape;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graphics.Pattern.Mode;
import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.font.Font;
import org.eclipse.gef4.graphics.image.Image;

/**
 * <p>
 * The IGraphics interface is used to draw planar geometry objects (
 * {@link IGeometry}), images, and text. The actual implementation depends on
 * the in-use GUI toolkit (such as AWT or SWT), but using this abstraction, your
 * graphics code does not.
 * </p>
 * 
 * <p>
 * The support for the GEF 4 Geometry component is covered by the different draw
 * and fill methods:
 * <ul>
 * <li><code>{@link #draw(Path)}</code></li>
 * <li><code>{@link #draw(ICurve)}</code></li>
 * <li><code>{@link #draw(Point)}</code></li>
 * <li><code>{@link #fill(Path)}</code></li>
 * <li><code>{@link #fill(IMultiShape)}</code></li>
 * <li><code>{@link #fill(IShape)}</code></li>
 * </ul>
 * </p>
 * 
 * <p>
 * In addition, you can display {@link Image}s or write text on the screen using
 * these two methods:
 * <ul>
 * <li><code>{@link #paint(Image)}</code></li>
 * <li><code>{@link #write(String)}</code></li>
 * </ul>
 * </p>
 * 
 * <p>
 * An IGraphics is associated with numerous properties that can individually be
 * read and set. The setters do always return the {@link IGraphics} object, so
 * that you can chain method calls to set multiple attributes, as follows:
 * <blockquote>
 * 
 * <pre>
 * graphics.setAntiAliasing(true).setLineWidth(10).draw(line);
 * </pre>
 * 
 * </blockquote>
 * </p>
 * 
 * <p>
 * The properties can be classified into groups:
 * <ul>
 * <li>General properties:
 * <ul>
 * <li>{@link #setAffineTransform(AffineTransform)},
 * {@link #translate(double, double)}, {@link #scale(double, double)},
 * {@link #rotate(Angle)}, {@link #shear(double, double)}</li>
 * <li>{@link #setClip(Path)}, {@link #intersectClip(Path)},
 * {@link #unionClip(Path)}</li>
 * <li>{@link #setAntiAliasing(boolean)}</li>
 * </ul>
 * </li>
 * <li>draw() properties:
 * <ul>
 * <li>{@link #setDashArray(double...)}, {@link #setDashBegin(double)}</li>
 * <li>{@link #setLineCap(LineCap)}, {@link #setLineJoin(LineJoin)},
 * {@link #setLineWidth(double)}, {@link #setMiterLimit(double)}</li>
 * <li>{@link #setDrawPattern(Pattern)},
 * {@link #setDrawPatternMode(Pattern.Mode)},
 * {@link #setDrawPatternColor(Color)},
 * {@link #setDrawPatternGradient(Gradient)},
 * {@link #setDrawPatternImage(Image)}</li>
 * </ul>
 * </li>
 * <li>fill() properties:
 * <ul>
 * <li>{@link #setFillPattern(Pattern)},
 * {@link #setFillPatternMode(Pattern.Mode)},
 * {@link #setFillPatternColor(Color)},
 * {@link #setFillPatternGradient(Gradient)},
 * {@link #setFillPatternImage(Image)}</li>
 * </ul>
 * </li>
 * <li>write() properties:
 * <ul>
 * <li>{@link #setFont(Font)}</li>
 * <li>{@link #setWritePattern(Pattern)},
 * {@link #setWritePatternMode(Pattern.Mode)},
 * {@link #setWritePatternColor(Color)},
 * {@link #setWritePatternGradient(Gradient)},
 * {@link #setWritePatternImage(Image)}</li>
 * </ul>
 * </li>
 * <li>blit() properties:
 * <ul>
 * <li>{@link #setInterpolationHint(InterpolationHint)}</li>
 * </ul>
 * </li>
 * </ul>
 * </p>
 * 
 * <p>
 * One set of properties is referred to as a state of an IGraphics. An IGraphics
 * uses a stack to store multiple states. If you modify a property, then you are
 * modifying the current state of the IGraphics. The {@link #pushState()} method
 * saves the current state to the stack and the {@link #popState()} method
 * removes and applies the state on top of the stack. Additionally, you can use
 * the {@link #restoreState()} method to restore the previously pushed state
 * without removing it from the stack.
 * </p>
 * 
 * @author mwienand
 * 
 */
public interface IGraphics {

	/**
	 * The default {@link AffineTransform} is an identity transform, i.e.
	 * coordinates are not affected by it.
	 */
	static final AffineTransform DEFAULT_AFFINE_TRANSFORM = new AffineTransform();

	/**
	 * Anti-alising is enabled per default.
	 */
	static final boolean DEFAULT_ANTI_ALIASING = true;

	/**
	 * The default clipping area is set to <code>null</code>, i.e. nothing will
	 * be clipped.
	 */
	static final Path DEFAULT_CLIPPING_AREA = null;

	/**
	 * The default dash array is empty, i.e. lines are not dashed.
	 */
	static final double[] DEFAULT_DASH_ARRAY = new double[] {};

	/**
	 * The default dash begin is 0.
	 */
	static final double DEFAULT_DASH_BEGIN = 0;

	/**
	 * The default draw {@link Pattern} uses the default {@link Color}.
	 */
	static final Pattern DEFAULT_DRAW_PATTERN = new Pattern(new Color());

	/**
	 * The default fill {@link Pattern} used the default {@link Color}.
	 */
	static final Pattern DEFAULT_FILL_PATTERN = new Pattern(new Color());

	/**
	 * The default {@link Font}.
	 */
	static final Font DEFAULT_FONT = new Font();

	/**
	 * The default {@link InterpolationHint} is set to
	 * {@link InterpolationHint#QUALITY}.
	 */
	static final InterpolationHint DEFAULT_INTERPOLATION_HINT = InterpolationHint.QUALITY;

	/**
	 * The default {@link LineCap} is set to {@link LineCap#FLAT}.
	 */
	static final LineCap DEFAULT_LINE_CAP = LineCap.FLAT;

	/**
	 * The default {@link LineJoin} is set to {@link LineJoin#BEVEL}.
	 */
	static final LineJoin DEFAULT_LINE_JOIN = LineJoin.BEVEL;

	/**
	 * The default line width is 1.
	 */
	static final double DEFAULT_LINE_WIDTH = 1;

	/**
	 * The default miter limit is 11.
	 */
	static final double DEFAULT_MITER_LIMIT = 11;

	/**
	 * The default text background {@link Color} is the default {@link Color}.
	 */
	static final Color DEFAULT_WRITE_BACKGROUND = new Color(255, 255, 255, 0);

	/**
	 * The default text {@link Pattern} uses the default {@link Color}.
	 */
	static final Pattern DEFAULT_WRITE_PATTERN = new Pattern(new Color());

	/**
	 * The <code>xor</code>-mode is disabled per default.
	 */
	static final boolean DEFAULT_XOR_MODE = false;

	/**
	 * The default logical DPI is set to 72 per default.
	 */
	static final int DEFAULT_LOGICAL_DPI = 72;

	/**
	 * Resets the underlying drawing toolkit and disposes all system resources
	 * allocated by this {@link IGraphics}.
	 */
	void cleanUp();

	/**
	 * Creates a new {@link IImageGraphics} for the same drawing toolkit that is
	 * used by this {@link IGraphics} to draw into the given {@link Image}.
	 * 
	 * @param image
	 *            the {@link Image} to draw into
	 * @return a new {@link IImageGraphics} to draw into the given {@link Image}
	 */
	IImageGraphics createImageGraphics(Image image);

	/**
	 * Draws the given {@link ICurve}.
	 * 
	 * @param curve
	 *            the {@link ICurve} to draw
	 * @return <code>this</code> for convenience
	 * 
	 * @see #draw(Path)
	 * @see #draw(Point)
	 * @see #setDashArray(double...)
	 * @see #setDrawPattern(Pattern)
	 * @see #setLineCap(LineCap)
	 * @see #setLineJoin(LineJoin)
	 * @see #setLineWidth(double)
	 */
	IGraphics draw(ICurve curve);

	/**
	 * Draws the given {@link Path}.
	 * 
	 * @param path
	 *            the {@link Path} to draw
	 * @return <code>this</code> for convenience
	 * 
	 * @see #draw(ICurve)
	 * @see #draw(Point)
	 * @see #setDashArray(double...)
	 * @see #setDrawPattern(Pattern)
	 * @see #setLineCap(LineCap)
	 * @see #setLineJoin(LineJoin)
	 * @see #setLineWidth(double)
	 */
	IGraphics draw(Path path);

	/**
	 * Draws the given {@link Point}.
	 * 
	 * @param point
	 *            the {@link Point} to draw
	 * @return <code>this</code> for convenience
	 * 
	 * @see #draw(Path)
	 * @see #draw(ICurve)
	 * @see #setDashArray(double...)
	 * @see #setDrawPattern(Pattern)
	 * @see #setLineCap(LineCap)
	 * @see #setLineJoin(LineJoin)
	 * @see #setLineWidth(double)
	 */
	IGraphics draw(Point point);

	/**
	 * Fills the interior of the given {@link IMultiShape}.
	 * 
	 * @param multiShape
	 *            the {@link IMultiShape} to fill
	 * @return <code>this</code> for convenience
	 * 
	 * @see #fill(Path)
	 * @see #fill(IShape)
	 * @see #setFillPattern(Pattern)
	 */
	IGraphics fill(IMultiShape multiShape);

	// /**
	// * Creates a new {@link PrintConfiguration} that contains page bounds
	// inside
	// * of the specified <i>bounds</i> by considering page size and printer
	// * resolution. If the <i>multiPage</i> parameter is set to
	// <code>true</code>
	// * then the given <i>bounds</i> is divided into page sized sub-bounds.
	// * Otherwise, the {@link PrintConfiguration} will contain a single page
	// with
	// * the given <i>bounds</i>, so that the rendering is scaled down later.
	// *
	// * @param bounds
	// * a {@link Rectangle} bounding the area that you want to print
	// * @param multiPage
	// * a boolean indicating whether to split the area to print into
	// * multiple pages
	// * @return a {@link PrintConfiguration} that can be used to print the
	// given
	// * <i>bounds</i> on a single or on multiple pages (<i>multiPage</i>)
	// */
	// PrintConfiguration createPrintConfiguration(Rectangle bounds,
	// boolean multiPage);

	/**
	 * Fills the interior of the given {@link IShape}.
	 * 
	 * @param shape
	 *            the {@link IShape} to fill
	 * @return <code>this</code> for convenience
	 * 
	 * @see #fill(Path)
	 * @see #fill(IMultiShape)
	 * @see #setFillPattern(Pattern)
	 */
	IGraphics fill(IShape shape);

	/**
	 * Fills the interior of the given {@link Path} (closing it first, if
	 * needed).
	 * 
	 * @param path
	 *            the {@link Path} to fill
	 * @return <code>this</code> for convenience
	 * 
	 * @see #fill(IMultiShape)
	 * @see #fill(IShape)
	 * @see #setFillPattern(Pattern)
	 */
	IGraphics fill(Path path);

	/**
	 * Returns an {@link AffineTransform} representing the current coordinate
	 * transformations.
	 * 
	 * @return an {@link AffineTransform} representing the current coordinate
	 *         transformations
	 * 
	 * @see #setAffineTransform(AffineTransform)
	 */
	AffineTransform getAffineTransform();

	/**
	 * Returns a {@link Path} representing the current clipping area.
	 * 
	 * @return a {@link Path} representing the current clipping area
	 * 
	 * @see #setClip(Path)
	 */
	Path getClip();

	/**
	 * Returns a <code>double[]</code> representing the current dash array.
	 * 
	 * @return a <code>double[]</code> representing the current dash array
	 * 
	 * @see #setDashArray(double...)
	 * @see #getDashBegin()
	 */
	double[] getDashArray();

	/**
	 * Returns the current dash begin.
	 * 
	 * @return the current dash begin
	 * 
	 * @see #setDashBegin(double)
	 * @see #getDashArray()
	 */
	double getDashBegin();

	/**
	 * Returns the default device DPI, i.e. the value returned by the used
	 * native. Note that this value is not guaranteed to be correct.
	 * 
	 * @return the default device DPI
	 */
	int getDefaultDeviceDpi();

	/**
	 * Returns the dots per inch (DPI) of the current display device. Note that
	 * this value is not guaranteed to be correct. Therefore, it is possible to
	 * set the DPI for the current display device and users should be able to do
	 * this from an application menu.
	 * 
	 * @return the DPI of the current display device
	 */
	int getDeviceDpi();

	/**
	 * Returns the current draw {@link Pattern}.
	 * 
	 * @return the current draw {@link Pattern}
	 * 
	 * @see #setDrawPattern(Pattern)
	 * @see #getDrawPatternMode()
	 * @see #getDrawPatternColor()
	 * @see #getDrawPatternGradient()
	 * @see #getDrawPatternImage()
	 */
	Pattern getDrawPattern();

	/**
	 * Returns the {@link Color} of the current draw {@link Pattern}.
	 * 
	 * @return the {@link Color} of the current draw {@link Pattern}
	 * 
	 * @see #setDrawPatternColor(Color)
	 * @see #getDrawPattern()
	 * @see #getDrawPatternMode()
	 */
	Color getDrawPatternColor();

	/**
	 * Returns the {@link Gradient} of the current draw {@link Pattern}.
	 * 
	 * @return the {@link Gradient} of the current draw {@link Pattern}
	 * 
	 * @see #setDrawPatternGradient(Gradient)
	 * @see #getDrawPattern()
	 * @see #getDrawPatternMode()
	 */
	Gradient<?> getDrawPatternGradient();

	/**
	 * Returns the {@link Image} of the current draw {@link Pattern}.
	 * 
	 * @return the {@link Image} of the current draw {@link Pattern}
	 * 
	 * @see #setDrawPatternImage(Image)
	 * @see #getDrawPattern()
	 * @see #getDrawPatternMode()
	 */
	Image getDrawPatternImage();

	/**
	 * Returns the current draw {@link Pattern.Mode}.
	 * 
	 * @return the current draw {@link Pattern.Mode}
	 * 
	 * @see #setDrawPatternMode(Pattern.Mode)
	 * @see #getDrawPattern()
	 */
	Pattern.Mode getDrawPatternMode();

	/**
	 * Returns the current fill {@link Pattern}.
	 * 
	 * @return the current fill {@link Pattern}
	 * 
	 * @see #setFillPattern(Pattern)
	 * @see #getFillPatternMode()
	 * @see #getFillPatternColor()
	 * @see #getFillPatternGradient()
	 * @see #getFillPatternImage()
	 */
	Pattern getFillPattern();

	/**
	 * Returns the {@link Color} of the current fill {@link Pattern}.
	 * 
	 * @return the {@link Color} of the current fill {@link Pattern}
	 * 
	 * @see #setFillPatternColor(Color)
	 * @see #getFillPattern()
	 * @see #getFillPatternMode()
	 */
	Color getFillPatternColor();

	/**
	 * Returns the {@link Gradient} of the current fill {@link Pattern}.
	 * 
	 * @return the {@link Gradient} of the current fill {@link Pattern}
	 * 
	 * @see #setFillPatternGradient(Gradient)
	 * @see #getFillPattern()
	 * @see #getFillPatternMode()
	 */
	Gradient<?> getFillPatternGradient();

	/**
	 * Returns the {@link Image} of the current fill {@link Pattern}.
	 * 
	 * @return the {@link Image} of the current fill {@link Pattern}
	 * 
	 * @see #setFillPatternImage(Image)
	 * @see #getFillPattern()
	 * @see #getFillPatternMode()
	 */
	Image getFillPatternImage();

	/**
	 * Returns the current fill {@link Pattern.Mode}.
	 * 
	 * @return the current fill {@link Pattern.Mode}
	 * 
	 * @see #setFillPatternMode(Pattern.Mode)
	 * @see #getFillPattern()
	 */
	Pattern.Mode getFillPatternMode();

	/**
	 * Returns the current {@link Font}.
	 * 
	 * @return the current {@link Font}
	 * 
	 * @see #setFont(Font)
	 */
	Font getFont();

	/**
	 * Returns the family of the current {@link Font}.
	 * 
	 * @return the family of the current {@link Font}
	 */
	String getFontFamily();

	/**
	 * Returns the size of the current {@link Font}.
	 * 
	 * @return the size of the current {@link Font}
	 */
	double getFontSize();

	/**
	 * Returns the style of the current {@link Font}.
	 * 
	 * @return the style of the current {@link Font}
	 */
	int getFontStyle();

	/**
	 * Returns the current {@link InterpolationHint}.
	 * 
	 * @return the current {@link InterpolationHint}
	 * 
	 * @see #setInterpolationHint(InterpolationHint)
	 */
	InterpolationHint getInterpolationHint();

	/**
	 * Returns the current {@link LineCap}.
	 * 
	 * @return the current {@link LineCap}
	 * 
	 * @see #setLineCap(LineCap)
	 */
	LineCap getLineCap();

	/**
	 * Returns the current {@link LineJoin}.
	 * 
	 * @return the current {@link LineJoin}
	 * 
	 * @see #setLineJoin(LineJoin)
	 * @see #getMiterLimit()
	 */
	LineJoin getLineJoin();

	/**
	 * Returns the current line width.
	 * 
	 * @return the current line width
	 * 
	 * @see #setLineWidth(double)
	 */
	double getLineWidth();

	/**
	 * Returns the dots per inch (DPI) that equal one unit of length.
	 * 
	 * @return the DPI that equal one unit of length
	 */
	int getLogicalDpi();

	/**
	 * Returns the current miter limit.
	 * 
	 * @return the current miter limit
	 * 
	 * @see #setMiterLimit(double)
	 * @see #getLineJoin()
	 */
	double getMiterLimit();

	/**
	 * Returns a {@link GraphicsState} representing the state of this
	 * {@link IGraphics}.
	 * 
	 * @return a {@link GraphicsState} representing the state of this
	 *         {@link IGraphics}
	 */
	GraphicsState getState();

	/**
	 * Returns a {@link Dimension} representing the width and height of a
	 * rectangle which encloses the given {@link String} when it is drawn via
	 * {@link #write(String)} using this {@link IGraphics}. The
	 * {@link Dimension} is not guaranteed to store the minimum width and height
	 * needed to display the given {@link String}. But it is guaranteed to be at
	 * least as big as that.
	 * 
	 * @param text
	 *            the {@link String} for which the {@link Dimension} is computed
	 * @return a {@link Dimension} representing the width and height of a
	 *         rectangle which encloses the given {@link String} when drawn by
	 *         this {@link IGraphics}
	 */
	Dimension getTextDimension(String text);

	/**
	 * Returns the current text background {@link Color}.
	 * 
	 * @return the current text background {@link Color}
	 * 
	 * @see #setWriteBackground(Color)
	 * @see #getWritePattern()
	 */
	Color getWriteBackground();

	/**
	 * Returns the current text {@link Pattern}.
	 * 
	 * @return the current text {@link Pattern}
	 * 
	 * @see #setWritePattern(Pattern)
	 * @see #getWritePatternMode()
	 * @see #getWritePatternColor()
	 * @see #getWritePatternGradient()
	 * @see #getWritePatternImage()
	 */
	Pattern getWritePattern();

	/**
	 * Returns the {@link Color} of the current text {@link Pattern}.
	 * 
	 * @return the {@link Color} of the current text {@link Pattern}
	 * 
	 * @see #setWritePatternColor(Color)
	 * @see #getWritePattern()
	 * @see #getWritePatternMode()
	 */
	Color getWritePatternColor();

	/**
	 * Returns the {@link Gradient} of the current text {@link Pattern}.
	 * 
	 * @return the {@link Gradient} of the current text {@link Pattern}
	 * 
	 * @see #setWritePatternGradient(Gradient)
	 * @see #getWritePattern()
	 * @see #getWritePatternMode()
	 */
	Gradient<?> getWritePatternGradient();

	/**
	 * Returns the {@link Image} of the current text {@link Pattern}.
	 * 
	 * @return the {@link Image} of the current text {@link Pattern}
	 * 
	 * @see #setWritePatternImage(Image)
	 * @see #getWritePattern()
	 * @see #getWritePatternMode()
	 */
	Image getWritePatternImage();

	/**
	 * Returns the current text {@link Pattern.Mode}.
	 * 
	 * @return the current text {@link Pattern.Mode}
	 * 
	 * @see #setWritePatternMode(Pattern.Mode)
	 * @see #getWritePattern()
	 */
	Pattern.Mode getWritePatternMode();

	/**
	 * Intersects the given {@link Path} with the clipping area.
	 * 
	 * @param toClip
	 * @return <code>this</code> for convenience
	 * 
	 * @see #intersectClip(Path)
	 * @see #intersectClip(IShape)
	 * @see #setClip(Path)
	 */
	IGraphics intersectClip(IMultiShape toClip);

	/**
	 * Intersects the given {@link Path} with the clipping area.
	 * 
	 * @param toClip
	 * @return <code>this</code> for convenience
	 * 
	 * @see #intersectClip(Path)
	 * @see #intersectClip(IMultiShape)
	 * @see #setClip(Path)
	 */
	IGraphics intersectClip(IShape toClip);

	/**
	 * Intersects the given {@link Path} with the clipping area.
	 * 
	 * @param toClip
	 * @return <code>this</code> for convenience
	 * 
	 * @see #intersectClip(IMultiShape)
	 * @see #intersectClip(IShape)
	 * @see #setClip(Path)
	 */
	IGraphics intersectClip(Path toClip);

	/**
	 * Returns <code>true</code> if anti-aliasing is active, otherwise
	 * <code>false</code>.
	 * 
	 * @return <code>true</code> if anti-aliasing is active, otherwise
	 *         <code>false</code>
	 * 
	 * @see #setAntiAliasing(boolean)
	 */
	boolean isAntiAliasing();

	/**
	 * Returns <code>true</code> if <code>xor</code> mode is enabled. Otherwise,
	 * <code>false</code> is returned.
	 * 
	 * @return <code>true</code> if <code>xor</code> mode is enabled, otherwise
	 *         <code>false</code>
	 * @see #setXorMode(boolean)
	 */
	boolean isXorMode();

	/**
	 * Paints the given {@link Image}.
	 * 
	 * @param image
	 *            the {@link Image} to paint
	 * @return <code>this</code> for convenience
	 * 
	 * @see #setInterpolationHint(InterpolationHint)
	 */
	IGraphics paint(Image image);

	/**
	 * Restores the set of properties that was pushed last.
	 * 
	 * @return <code>this</code> for convenience
	 * 
	 * @see #pushState()
	 * @see #restoreState()
	 */
	IGraphics popState();

	/**
	 * Saves the current set of properties.
	 * 
	 * @return <code>this</code> for convenience
	 * 
	 * @see #popState()
	 * @see #restoreState()
	 */
	IGraphics pushState();

	/**
	 * <p>
	 * Restores the previously {@link #pushState() pushed} state, i.e. replaces
	 * the current state with it.
	 * </p>
	 * 
	 * <p>
	 * The method is exactly equivalent to the following sequence of operations:
	 * <blockquote>
	 * 
	 * <pre>
	 * popState();
	 * pushState();
	 * </pre>
	 * 
	 * </blockquote>
	 * </p>
	 * 
	 * @return <code>this</code> for convenience
	 * 
	 * @see #pushState()
	 * @see #popState()
	 */
	IGraphics restoreState();

	/**
	 * Multiplies the current {@link AffineTransform} by a rotation matrix for
	 * the given {@link Angle}.
	 * 
	 * @param theta
	 * @return <code>this</code> for convenience
	 * 
	 * @see #setAffineTransform(AffineTransform)
	 */
	IGraphics rotate(Angle theta);

	/**
	 * Multiplies the current transformation matrix ({@link AffineTransform}) by
	 * a scaling matrix for the given scaling factors.
	 * 
	 * @param sx
	 *            horizontal scaling factor
	 * @param sy
	 *            vertical scaling factor
	 * @return <code>this</code> for convenience
	 * 
	 * @see #setAffineTransform(AffineTransform)
	 */
	IGraphics scale(double sx, double sy);

	/**
	 * Uses the given {@link AffineTransform} as the current transformation
	 * matrix.
	 * 
	 * @param transformations
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getAffineTransform()
	 * @see #translate(double, double)
	 * @see #rotate(Angle)
	 * @see #scale(double, double)
	 * @see #shear(double, double)
	 */
	IGraphics setAffineTransform(AffineTransform transformations);

	/**
	 * Enables or disables anti-aliasing. If <code>true</code> is passed-in
	 * anti-aliasing is enabled, otherwise disabled.
	 * 
	 * @param aa
	 * @return <code>this</code> for convenience
	 * 
	 * @see #isAntiAliasing()
	 */
	IGraphics setAntiAliasing(boolean aa);

	/**
	 * <p>
	 * Sets the clip to the passed-in value.
	 * </p>
	 * <p>
	 * If the passed-in <i>clip</i> is <code>null</code> clipping is disabled.
	 * </p>
	 * 
	 * @param clip
	 *            {@link Path} to use as clip, <code>null</code> to disable
	 *            clipping
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getClip()
	 * @see #intersectClip(Path)
	 * @see #unionClip(Path)
	 */
	IGraphics setClip(Path clip);

	/**
	 * Sets the current dash array to the given value(s).
	 * 
	 * @param dashArray
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getDashArray()
	 * @see #setDashBegin(double)
	 */
	IGraphics setDashArray(double... dashArray);

	/**
	 * Sets the dash begin to the given value.
	 * 
	 * @param dashBegin
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getDashBegin()
	 * @see #setDashArray(double...)
	 */
	IGraphics setDashBegin(double dashBegin);

	/**
	 * Sets the dots per inch (DPI) to assume for the current display device. A
	 * default device DPI is offered by the {@link IGraphics}. But the default
	 * is not guaranteed to be correct. That's why you have the possibility to
	 * set the device DPI.
	 * 
	 * @param dpi
	 *            the DPI to assume for the current display device
	 * @return <code>this</code> for convenience
	 */
	IGraphics setDeviceDpi(int dpi);

	/**
	 * Sets the draw {@link Pattern}'s {@link Color} to the passed-in value.
	 * Additionally, the draw {@link Pattern}'s {@link Mode} is set to
	 * {@link Mode#COLOR}. This method is equivalent to the following code:
	 * <blockquote>
	 * 
	 * <pre>
	 * igraphics.setDrawPatternMode(Mode.COLOR).setDrawPatternColor(color);
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param color
	 *            the new drawing {@link Color}
	 * @return <code>this</code> for convenience
	 */
	IGraphics setDraw(Color color);

	/**
	 * Sets the draw {@link Pattern}'s {@link Gradient} to the passed-in value.
	 * Additionally, the draw {@link Pattern}'s {@link Mode} is set to
	 * {@link Mode#GRADIENT}. This method is equivalent to the following code:
	 * <blockquote>
	 * 
	 * <pre>
	 * igraphics.setDrawPatternMode(Mode.GRADIENT).setDrawPatternGradient(gradient);
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param gradient
	 *            the new drawing {@link Gradient}
	 * @return <code>this</code> for convenience
	 */
	IGraphics setDraw(Gradient<?> gradient);

	/**
	 * Sets the draw {@link Pattern}'s {@link Image} to the passed-in value.
	 * Additionally, the draw {@link Pattern}'s {@link Mode} is set to
	 * {@link Mode#IMAGE}. This method is equivalent to the following code:
	 * <blockquote>
	 * 
	 * <pre>
	 * igraphics.setDrawPatternMode(Mode.IMAGE).setDrawPatternImage(image);
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param image
	 *            the new drawing {@link Image}
	 * @return <code>this</code> for convenience
	 */
	IGraphics setDraw(Image image);

	/**
	 * Sets the current draw {@link Pattern} to the given value.
	 * 
	 * @param pattern
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getDrawPattern()
	 * @see #setDrawPatternMode(Pattern.Mode)
	 * @see #setDrawPatternColor(Color)
	 * @see #setDrawPatternGradient(Gradient)
	 * @see #setDrawPatternImage(Image)
	 */
	IGraphics setDrawPattern(Pattern pattern);

	/**
	 * Sets the {@link Color} of the current draw {@link Pattern} to the given
	 * value.
	 * 
	 * @param color
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getDrawPatternColor()
	 * @see #setDrawPattern(Pattern)
	 * @see #setDrawPatternMode(Pattern.Mode)
	 */
	IGraphics setDrawPatternColor(Color color);

	/**
	 * Sets the {@link Gradient} of the current draw {@link Pattern} to the
	 * given value.
	 * 
	 * @param gradient
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getDrawPatternGradient()
	 * @see #setDrawPattern(Pattern)
	 * @see #setDrawPatternMode(Pattern.Mode)
	 */
	IGraphics setDrawPatternGradient(Gradient<?> gradient);

	/**
	 * Sets the {@link Image} of the current draw {@link Pattern} to the given
	 * value.
	 * 
	 * @param image
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getDrawPatternImage()
	 * @see #setDrawPattern(Pattern)
	 * @see #setDrawPatternMode(Pattern.Mode)
	 */
	IGraphics setDrawPatternImage(Image image);

	/**
	 * Sets the current draw {@link Pattern.Mode} to the given value.
	 * 
	 * @param drawMode
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getDrawPatternMode()
	 * @see #setDrawPattern(Pattern)
	 */
	IGraphics setDrawPatternMode(Pattern.Mode drawMode);

	/**
	 * Sets the fill {@link Pattern}'s {@link Color} to the passed-in value.
	 * Additionally, the fill {@link Pattern}'s {@link Mode} is set to
	 * {@link Mode#COLOR}. This method is equivalent to the following code:
	 * <blockquote>
	 * 
	 * <pre>
	 * igraphics.setFillPatternMode(Mode.COLOR).setFillPatternColor(color);
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param color
	 *            the new fill {@link Color}
	 * @return <code>this</code> for convenience
	 */
	IGraphics setFill(Color color);

	/**
	 * Sets the fill {@link Pattern}'s {@link Gradient} to the passed-in value.
	 * Additionally, the fill {@link Pattern}'s {@link Mode} is set to
	 * {@link Mode#GRADIENT}. This method is equivalent to the following code:
	 * <blockquote>
	 * 
	 * <pre>
	 * igraphics.setFillPatternMode(Mode.GRADIENT).setFillPatternGradient(gradient);
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param gradient
	 *            the new fill {@link Gradient}
	 * @return <code>this</code> for convenience
	 */
	IGraphics setFill(Gradient<?> gradient);

	/**
	 * Sets the fill {@link Pattern}'s {@link Image} to the passed-in value.
	 * Additionally, the fill {@link Pattern}'s {@link Mode} is set to
	 * {@link Mode#IMAGE}. This method is equivalent to the following code:
	 * <blockquote>
	 * 
	 * <pre>
	 * igraphics.setFillPatternMode(Mode.IMAGE).setFillPatternImage(image);
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param image
	 *            the new fill {@link Image}
	 * @return <code>this</code> for convenience
	 */
	IGraphics setFill(Image image);

	/**
	 * Sets the current fill {@link Pattern} to the given value.
	 * 
	 * @param pattern
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getFillPattern()
	 * @see #setFillPatternMode(Pattern.Mode)
	 * @see #setFillPatternColor(Color)
	 * @see #setFillPatternGradient(Gradient)
	 * @see #setFillPatternImage(Image)
	 */
	IGraphics setFillPattern(Pattern pattern);

	/**
	 * Sets the {@link Color} of the current fill {@link Pattern} to the given
	 * value.
	 * 
	 * @param color
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getFillPatternColor()
	 * @see #setFillPattern(Pattern)
	 * @see #setFillPatternMode(Pattern.Mode)
	 */
	IGraphics setFillPatternColor(Color color);

	/**
	 * Sets the {@link Gradient} of the current fill {@link Pattern} to the
	 * given value.
	 * 
	 * @param gradient
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getFillPatternGradient()
	 * @see #setFillPattern(Pattern)
	 * @see #setFillPatternMode(Pattern.Mode)
	 */
	IGraphics setFillPatternGradient(Gradient<?> gradient);

	/**
	 * Sets the {@link Image} of the current fill {@link Pattern} to the given
	 * value.
	 * 
	 * @param image
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getFillPatternImage()
	 * @see #setFillPattern(Pattern)
	 * @see #setFillPatternMode(Pattern.Mode)
	 */
	IGraphics setFillPatternImage(Image image);

	/**
	 * Sets the current fill {@link Pattern.Mode} to the given value.
	 * 
	 * @param fillMode
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getFillPatternMode()
	 * @see #setFillPattern(Pattern)
	 */
	IGraphics setFillPatternMode(Pattern.Mode fillMode);

	/**
	 * Sets the current {@link Font} to the given value.
	 * 
	 * @param font
	 * @return <code>this</code> for convenience
	 */
	IGraphics setFont(Font font);

	/**
	 * Sets the family of the current {@link Font} to the passed-in value.
	 * 
	 * @param family
	 *            the new family of the current {@link Font}
	 * @return <code>this</code> for convenience
	 */
	IGraphics setFontFamily(String family);

	/**
	 * Sets the size of the current {@link Font} to the passed-in value.
	 * 
	 * @param size
	 *            the new size of the current {@link Font}
	 * @return <code>this</code> for convenience
	 */
	IGraphics setFontSize(double size);

	/**
	 * Sets the style of the current {@link Font} to the passed-in value.
	 * 
	 * @param style
	 *            the new style of the current {@link Font}
	 * @return <code>this</code> for convenience
	 */
	IGraphics setFontStyle(int style);

	/**
	 * Sets the current {@link InterpolationHint} to the given value.
	 * 
	 * @param interpolationHint
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getInterpolationHint()
	 */
	IGraphics setInterpolationHint(InterpolationHint interpolationHint);

	/**
	 * Sets the current {@link LineCap} to the given value.
	 * 
	 * @param lineCap
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getLineCap()
	 */
	IGraphics setLineCap(LineCap lineCap);

	/**
	 * Sets the current {@link LineJoin} to the given value.
	 * 
	 * @param lineJoin
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getLineJoin()
	 * @see #setMiterLimit(double)
	 */
	IGraphics setLineJoin(LineJoin lineJoin);

	/**
	 * Sets the current line width to the given value.
	 * 
	 * @param lineWidth
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getLineWidth()
	 */
	IGraphics setLineWidth(double lineWidth);

	/**
	 * Sets the logical dots per inch (DPI), i.e. the DPI that equals one unit
	 * of length.
	 * 
	 * @param dpi
	 *            the new logical DPI
	 * @return <code>this</code> for convenience
	 */
	IGraphics setLogicalDpi(int dpi);

	/**
	 * Sets the current miter limit to the given value.
	 * 
	 * @param miterLimit
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getMiterLimit()
	 * @see #setLineJoin(LineJoin)
	 */
	IGraphics setMiterLimit(double miterLimit);

	/**
	 * Applies the given {@link GraphicsState} to this {@link IGraphics}.
	 * 
	 * @param state
	 *            the {@link GraphicsState} to apply to this {@link IGraphics}
	 * @return <code>this</code> for convenience
	 */
	IGraphics setState(GraphicsState state);

	/**
	 * Sets the current stroke to the passed-in values.
	 * 
	 * @param width
	 *            the new line width
	 * @param cap
	 *            the new {@link LineCap}
	 * @param join
	 *            the new {@link LineJoin}
	 * @param miterLimit
	 *            the new limit for {@link LineJoin#MITER}
	 * @param dashBegin
	 *            the dash offset length
	 * @param dashes
	 *            the alternating opaque and transparent dash lengths
	 * @return <code>this</code> for convenience
	 */
	IGraphics setStroke(double dashBegin, double... dashes);

	/**
	 * Sets the current stroke to the passed-in values.
	 * 
	 * @param width
	 *            the new line width
	 * @param cap
	 *            the new {@link LineCap}
	 * @param join
	 *            the new {@link LineJoin}
	 * @param miterLimit
	 *            the new limit for {@link LineJoin#MITER}
	 * @return <code>this</code> for convenience
	 */
	IGraphics setStroke(double width, LineCap cap, LineJoin join,
			double miterLimit);

	/**
	 * Sets the current stroke to the passed-in values.
	 * 
	 * @param width
	 *            the new line width
	 * @param cap
	 *            the new {@link LineCap}
	 * @param join
	 *            the new {@link LineJoin}
	 * @param miterLimit
	 *            the new limit for {@link LineJoin#MITER}
	 * @param dashBegin
	 *            the dash offset length
	 * @param dashes
	 *            the alternating opaque and transparent dash lengths
	 * @return <code>this</code> for convenience
	 */
	IGraphics setStroke(double width, LineCap cap, LineJoin join,
			double miterLimit, double dashBegin, double... dashes);

	/**
	 * Sets the write {@link Pattern}'s {@link Color} to the passed-in value.
	 * Additionally, the write {@link Pattern}'s {@link Mode} is set to
	 * {@link Mode#COLOR}. This method is equivalent to the following code:
	 * <blockquote>
	 * 
	 * <pre>
	 * igraphics.setWritePatternMode(Mode.COLOR).setWritePatternImage(color);
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param color
	 *            the new write {@link Pattern}'s {@link Color}
	 * @return <code>this</code> for convenience
	 */
	IGraphics setWrite(Color color);

	/**
	 * Sets the write {@link Pattern}'s {@link Gradient} to the passed-in value.
	 * Additionally, the write {@link Pattern}'s {@link Mode} is set to
	 * {@link Mode#GRADIENT}. This method is equivalent to the following code:
	 * <blockquote>
	 * 
	 * <pre>
	 * igraphics.setWritePatternMode(Mode.GRADIENT).setWritePatternGradient(gradient);
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param gradient
	 *            the new write {@link Pattern}'s {@link Gradient}
	 * @return <code>this</code> for convenience
	 */
	IGraphics setWrite(Gradient<?> gradient);

	/**
	 * Sets the write {@link Pattern}'s {@link Image} to the passed-in value.
	 * Additionally, the write {@link Pattern}'s {@link Mode} is set to
	 * {@link Mode#IMAGE}. This method is equivalent to the following code:
	 * <blockquote>
	 * 
	 * <pre>
	 * igraphics.setWritePatternMode(Mode.IMAGE).setWritePatternImage(image);
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param image
	 *            the new write {@link Pattern}'s {@link Image}
	 * @return <code>this</code> for convenience
	 */
	IGraphics setWrite(Image image);

	/**
	 * Sets the background {@link Color} for text to the given value.
	 * 
	 * @param textBackColor
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getWriteBackground()
	 * @see #setWritePattern(Pattern)
	 */
	IGraphics setWriteBackground(Color textBackColor);

	/**
	 * Sets the current text {@link Pattern} to the given value.
	 * 
	 * @param textPattern
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getWritePattern()
	 * @see #setWritePatternMode(Pattern.Mode)
	 * @see #setWritePatternColor(Color)
	 * @see #setWritePatternGradient(Gradient)
	 * @see #setWritePatternImage(Image)
	 */
	IGraphics setWritePattern(Pattern textPattern);

	/**
	 * Sets the {@link Color} of the current text {@link Pattern} to the given
	 * value.
	 * 
	 * @param textPatternColor
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getWritePatternColor()
	 * @see #setWritePattern(Pattern)
	 * @see #setWritePatternMode(Pattern.Mode)
	 */
	IGraphics setWritePatternColor(Color textPatternColor);

	/**
	 * Sets the {@link Gradient} of the current text {@link Pattern} to the
	 * given value.
	 * 
	 * @param textPatternGradient
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getWritePatternGradient()
	 * @see #setWritePattern(Pattern)
	 * @see #setWritePatternMode(Pattern.Mode)
	 */
	IGraphics setWritePatternGradient(Gradient<?> textPatternGradient);

	/**
	 * Sets the {@link Image} of the current text {@link Pattern} to the given
	 * value.
	 * 
	 * @param textPatternImage
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getWritePatternImage()
	 * @see #setWritePattern(Pattern)
	 * @see #setWritePatternMode(Pattern.Mode)
	 */
	IGraphics setWritePatternImage(Image textPatternImage);

	/**
	 * Sets the current text {@link Pattern.Mode} to the given value.
	 * 
	 * @param textPatternMode
	 * @return <code>this</code> for convenience
	 * 
	 * @see #getWritePatternMode()
	 * @see #setWritePattern(Pattern)
	 */
	IGraphics setWritePatternMode(Pattern.Mode textPatternMode);

	/**
	 * Enables or disables <code>xor</code> mode. If the given value is
	 * <code>true</code> <code>xor</code> mode is enabled. Otherwise it is
	 * disabled.
	 * 
	 * @param xor
	 *            <code>true</code> to enable <code>xor</code> mode,
	 *            <code>false</code> to disable it
	 * @return <code>this</code> for convenience
	 * @see #isXorMode()
	 */
	IGraphics setXorMode(boolean xor);

	/**
	 * Multiplies the current transformation matrix ({@link AffineTransform}) by
	 * a shearing matrix for the given shear factors.
	 * 
	 * @param shx
	 *            horizontal shear factor
	 * @param shy
	 *            vertical shear factor
	 * @return <code>this</code> for convenience
	 * 
	 * @see #setAffineTransform(AffineTransform)
	 * @see #translate(double, double)
	 * @see #rotate(Angle)
	 * @see #scale(double, double)
	 */
	IGraphics shear(double shx, double shy);

	/**
	 * Multiplies the current transformation matrix ({@link AffineTransform}) by
	 * the passed-in transformation matrix.
	 * 
	 * @param transformations
	 *            the transformation matrix ({@link AffineTransform}) by which
	 *            the current transformation matrix is multiplied
	 * @return <code>this</code> for convenience
	 */
	IGraphics transform(AffineTransform transformations);

	/*
	 * TODO: In which direction shall we multiply transformation matrices? Right
	 * to left might be the better default, because transformations are applied
	 * in order then.
	 */

	/**
	 * Multiplies the current transformation matrix ({@link AffineTransform}) by
	 * a translation matrix for the given offsets.
	 * 
	 * @param x
	 *            horizontal offset
	 * @param y
	 *            vertical offset
	 * @return <code>this</code> for convenience
	 * 
	 * @see #setAffineTransform(AffineTransform)
	 * @see #rotate(Angle)
	 * @see #scale(double, double)
	 * @see #shear(double, double)
	 */
	IGraphics translate(double x, double y);

	/**
	 * Adds the given {@link IMultiShape} to the current clipping area, i.e.
	 * marks the given area as not affected by clipping.
	 * 
	 * @param toShow
	 * @return <code>this</code> for convenience
	 * 
	 * @see #setClip(Path)
	 * @see #unionClip(IShape)
	 * @see #unionClip(Path)
	 */
	IGraphics unionClip(IMultiShape toShow);

	/**
	 * Adds the given {@link IShape} to the current clipping area, i.e. marks
	 * the given area as not affected by clipping.
	 * 
	 * @param toShow
	 * @return <code>this</code> for convenience
	 * 
	 * @see #setClip(Path)
	 * @see #unionClip(IMultiShape)
	 * @see #unionClip(Path)
	 */
	IGraphics unionClip(IShape toShow);

	/**
	 * Adds the given {@link Path} to the current clipping area, i.e. marks the
	 * given area as not affected by clipping.
	 * 
	 * @param toShow
	 * @return <code>this</code> for convenience
	 * 
	 * @see #setClip(Path)
	 * @see #unionClip(IShape)
	 * @see #unionClip(IMultiShape)
	 */
	IGraphics unionClip(Path toShow);

	/*
	 * TODO: write(): Where does text rendering start? Is the current position
	 * at the baseline? Or at the ascent? Or at the maximum ascent?
	 */

	/**
	 * Draws the given <i>text</i> on this {@link IGraphics}.
	 * 
	 * @param text
	 *            the text to draw
	 * @return <code>this</code> for convenience
	 * 
	 * @see #setFont(Font)
	 */
	IGraphics write(String text);

}
