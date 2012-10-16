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

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Region;
import org.eclipse.gef4.geometry.planar.Ring;

/**
 * <p>
 * An {@link ICanvasProperties} manages an {@link AffineTransform} and a
 * {@link Ring}. The {@link AffineTransform} is applied to every object before
 * displaying it via an {@link IGraphics}. Similarly, the {@link Ring} is used
 * as the clipping area of the {@link IGraphics} when displaying arbitrary
 * objects.
 * </p>
 * 
 * @author mwienand
 * 
 */
public interface ICanvasProperties extends IGraphicsProperties {

	/**
	 * Applies the {@link ICanvasProperties} stored in this object to the
	 * underlying graphics system of the passed-in {@link IGraphics}.
	 * 
	 * @param g
	 *            the {@link IGraphics} to apply the {@link ICanvasProperties}
	 *            on
	 */
	void applyOn(IGraphics g);

	/**
	 * Returns the current {@link AffineTransform} associated with this
	 * {@link ICanvasProperties}.
	 * 
	 * @return the current {@link AffineTransform} associated with this
	 *         {@link ICanvasProperties}
	 */
	AffineTransform getAffineTransform();

	/**
	 * Returns the current clipping area associated with this
	 * {@link ICanvasProperties}.
	 * 
	 * @return the current clipping area associated with this
	 *         {@link ICanvasProperties}
	 */
	Ring getClippingArea();

	ICanvasProperties getCopy();

	/**
	 * Sets the current {@link AffineTransform} of this
	 * {@link ICanvasProperties} to the given value.
	 * 
	 * @param transform
	 *            the new {@link AffineTransform} for this
	 *            {@link ICanvasProperties}
	 * @return <code>this</code> for convenience
	 */
	ICanvasProperties setAffineTransform(AffineTransform transform);

	/**
	 * Sets the current clipping area of this {@link ICanvasProperties} to the
	 * given {@link Region}.
	 * 
	 * @param clippingArea
	 *            the new clipping area for this {@link ICanvasProperties}
	 * @return <code>this</code> for convenience
	 */
	ICanvasProperties setClippingArea(Region clippingArea);

	/**
	 * Sets the current clipping area of this {@link ICanvasProperties} to the
	 * given {@link Ring}.
	 * 
	 * @param clippingArea
	 *            the new clipping area for this {@link ICanvasProperties}
	 * @return <code>this</code> for convenience
	 */
	ICanvasProperties setClippingArea(Ring clippingArea);

}
