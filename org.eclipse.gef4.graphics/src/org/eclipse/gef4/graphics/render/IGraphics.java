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
package org.eclipse.gef4.graphics.render;

import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IMultiShape;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graphics.Image;

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
 * In addition, you can display {@link Image}s or write text on the screen using
 * these two methods:
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
 * graphics.drawProperties().setAntiAliasing(true).setLineWidth(10);
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
 * <p>
 * Accessing an {@link IGraphicsProperties} is only permitted when that
 * {@link IGraphicsProperties} is currently active on an {@link IGraphics}, i.e.
 * it will affect the next corresponding drawing operation.
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
	 * Walks backwards through the states-stack using the {@link #popState()}
	 * method. For every state, the corresponding {@link IGraphicsProperties}
	 * objects' {@link IGraphicsProperties#cleanUp(IGraphics)} methods are
	 * called.
	 */
	void cleanUp();

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
	 * Draws the given {@link Point} on this {@link IGraphics} using the
	 * associated {@link ICanvasProperties} and {@link IDrawProperties}.
	 * 
	 * @param point
	 *            the {@link Point} to draw
	 */
	void draw(Point point);

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
	 * Returns an {@link IFontUtils} implementation for this specific
	 * {@link IGraphics} implementation.
	 * 
	 * @return an {@link IFontUtils} for this specific {@link IGraphics}
	 */
	IFontUtils fontUtils();

	/**
	 * <p>
	 * Restores the set of {@link IGraphicsProperties} that was saved last.
	 * </p>
	 * 
	 * <p>
	 * Cleans-up the currently active set of {@link IGraphicsProperties} (see
	 * {@link IGraphicsProperties#cleanUp(IGraphics)}).
	 * </p>
	 * 
	 * <p>
	 * Deactivates the currently active set of {@link IGraphicsProperties} (see
	 * {@link IGraphicsProperties#deactivate()}). Activates the restored set of
	 * {@link IGraphicsProperties} (see {@link IGraphicsProperties#activate()}).
	 * </p>
	 */
	void popState();

	/**
	 * <p>
	 * Saves the current set of {@link IGraphicsProperties}.
	 * </p>
	 * 
	 * <p>
	 * Deactivates the saved set of {@link IGraphicsProperties} (see
	 * {@link IGraphicsProperties#deactivate()}). Note that the new set of
	 * {@link IGraphicsProperties} is expected to be active.
	 * </p>
	 * 
	 * <p>
	 * Initializes the new set of {@link IGraphicsProperties} (see
	 * {@link IGraphicsProperties#init(IGraphics)}).
	 * </p>
	 */
	void pushState();

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
	 */
	void restoreState();

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
