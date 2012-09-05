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

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IMultiShape;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Path;

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
 * <li><code>{@link #draw(ICurve)}</code></li>
 * <li><code>{@link #draw(Path)}</code></li>
 * <li><code>{@link #fill(IShape)}</code></li>
 * <li><code>{@link #fill(IMultiShape)}</code></li>
 * <li><code>{@link #fill(Path)}</code></li>
 * </ul>
 * </p>
 * 
 * <p>
 * TODO
 * <ul>
 * <li><code>{@link #blit(Image)}</code></li>
 * <li><code>{@link #write(String)}</code></li>
 * </ul>
 * </p>
 * 
 * <p>
 * An IGraphics is associated with numerous properties that can be individually
 * read and set. The setters do always return the calling object, so that you
 * can chain method calls to set multiple attributes, as follows: <blockquote>
 * 
 * <pre>
 * graphics.getDrawProperties().setAntiAliasing(true).setLineWidth(10);
 * </pre>
 * 
 * </blockquote>
 * </p>
 * 
 * <p>
 * The properties are classified into different categories:
 * <ul>
 * <li><code>{@link IBlitProperties} {@link #blitProperties()}</code></li>
 * <li><code>{@link ICanvasProperties} {@link #canvasProperties()}</code></li>
 * <li><code>{@link IDrawProperties} {@link #drawProperties()}</code></li>
 * <li><code>{@link IFillProperties} {@link #fillProperties()}</code></li>
 * <li><code>{@link IWriteProperties} {@link #writeProperties()}</code></li>
 * </ul>
 * </p>
 * 
 * <p>
 * One set of properties is referred to as a state of an IGraphics. If you
 * modify a property, then you are modifying the current state of the IGraphics.
 * The {@link #pushState()} method saves the current set of properties to a
 * stack and the {@link #popState()} method restores the prior set of
 * properties.
 * </p>
 * 
 * @author mwienand
 * 
 */
public interface IGraphics {

	/**
	 * Draws the given {@link Image} on this {@link IGraphics} using the
	 * associated {@link ICanvasProperties} and {@link IBlitProperties}.
	 * 
	 * @param image
	 *            the {@link Image} to draw
	 */
	void blit(Image image);

	/**
	 * Returns the current states {@link IBlitProperties}.
	 * 
	 * @return the current states {@link IBlitProperties}
	 */
	IBlitProperties blitProperties();

	/**
	 * Returns the current states {@link ICanvasProperties}.
	 * 
	 * @return the current states {@link ICanvasProperties}
	 */
	ICanvasProperties canvasProperties();

	/**
	 * Draws the given {@link ICurve} on this {@link IGraphics} using the
	 * associated {@link ICanvasProperties} and {@link IDrawProperties}.
	 * 
	 * @param curve
	 *            the {@link ICurve} to draw
	 */
	void draw(ICurve curve);

	/**
	 * Draws the given {@link Path} on this {@link IGraphics} using the
	 * associated {@link ICanvasProperties} and {@link IDrawProperties}.
	 * 
	 * @param path
	 *            the {@link Path} to draw
	 */
	void draw(Path path);

	/**
	 * Returns the current states {@link IDrawProperties}.
	 * 
	 * @return the current states {@link IDrawProperties}
	 */
	IDrawProperties drawProperties();

	/**
	 * Fills the interior of the given {@link IMultiShape} on this
	 * {@link IGraphics} using the associated {@link ICanvasProperties} and
	 * {@link IFillProperties}.
	 * 
	 * @param multiShape
	 *            the {@link IMultiShape} to fill
	 */
	void fill(IMultiShape multiShape);

	/**
	 * Fills the interior of the given {@link IShape} on this {@link IGraphics}
	 * using the associated {@link ICanvasProperties} and
	 * {@link IFillProperties}.
	 * 
	 * @param shape
	 *            the {@link IShape} to fill
	 */
	void fill(IShape shape);

	/**
	 * Fills the interior of the given {@link Path} (closing it first, if
	 * needed) on this {@link IGraphics} using the associated
	 * {@link ICanvasProperties} and {@link IFillProperties}.
	 * 
	 * @param path
	 *            the {@link Path} to fill
	 */
	void fill(Path path);

	/**
	 * Returns the current states {@link IFillProperties}.
	 * 
	 * @return the current states {@link IFillProperties}
	 */
	IFillProperties fillProperties();

	/**
	 * Returns the width and height required to display the given {@link String}
	 * with the current {@link IWriteProperties#getFont() font} as a
	 * {@link Dimension}.
	 * 
	 * @param text
	 * @return a {@link Dimension} representing the width and height required to
	 *         display the given {@link String} with the current
	 *         {@link IWriteProperties#getFont() font}
	 */
	Dimension getTextDimension(String text);

	/**
	 * Returns the width required to display the given {@link String} with the
	 * current {@link IWriteProperties#getFont() font}.
	 * 
	 * @param text
	 * @return the width required to display the given {@link String} with the
	 *         current {@link IWriteProperties#getFont() font}
	 */
	// double getTextWidth(String text);

	/**
	 * Restores the prior set of properties saved.
	 */
	void popState();

	/**
	 * Saves the current set of properties.
	 */
	void pushState();

	/**
	 * Draws the given <i>text</i> on this {@link IGraphics} using the
	 * associated {@link ICanvasProperties} and {@link IWriteProperties}.
	 * 
	 * @param text
	 *            the text to draw
	 */
	void write(String text);

	/**
	 * Returns the current states {@link IWriteProperties}.
	 * 
	 * @return the current states {@link IWriteProperties}
	 */
	IWriteProperties writeProperties();

}
