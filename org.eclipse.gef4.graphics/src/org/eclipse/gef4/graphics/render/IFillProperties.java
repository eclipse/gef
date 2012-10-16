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

import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.graphics.Color;

/**
 * An {@link IFillProperties} manages the {@link IGraphics} properties used when
 * displaying a geometric object using one of the
 * {@link IGraphics#fill(org.eclipse.gef4.geometry.planar.IMultiShape)
 * fill(IMultiShape)},
 * {@link IGraphics#fill(org.eclipse.gef4.geometry.planar.IShape) fill(IShape)},
 * or {@link IGraphics#fill(org.eclipse.gef4.geometry.planar.Path) fill(Path)}
 * methods.
 * 
 * @author mwienand
 * 
 */
public interface IFillProperties extends IGraphicsProperties {

	/**
	 * Anti-aliasing is enabled per default.
	 */
	static final boolean DEFAULT_ANTIALIASING = true;

	/**
	 * Applies the {@link IFillProperties} stored in this object to the
	 * underlying graphics system of the passed-in {@link IGraphics}. This
	 * operation renders the given {@link Path}. It is called when the
	 * {@link IGraphics#fill(Path)} method is called.
	 * 
	 * @param g
	 *            the {@link IGraphics} to apply the {@link IFillProperties} on
	 * @param p
	 *            the {@link Path} to render
	 */
	void applyOn(IGraphics g, Path p);

	/**
	 * Returns the {@link Color fill color} associated with this
	 * {@link IFillProperties}.
	 * 
	 * @return the current {@link Color fill color}
	 */
	Color getColor();

	IFillProperties getCopy();

	/**
	 * Returns the {@link Image} associated with this {@link IFillProperties}.
	 * This {@link Image} is used when filling a geometry object using the...
	 * 
	 * @return the {@link Image} associated with this {@link IFillProperties}
	 */
	// Image getTexture();

	/**
	 * Returns <code>true</code> if anti-aliasing is enabled. Otherwise,
	 * <code>false</code> is returned.
	 * 
	 * @return <code>true</code> if anti-aliasing is enabled, otherwise
	 *         <code>false</code>
	 */
	boolean isAntialiasing();

	/**
	 * Enables or disables anti-aliasing for this {@link IFillProperties}. When
	 * given a <code>true</code> value, anti-aliasing is enabled. Otherwise,
	 * anti-aliasing is disabled.
	 * 
	 * @param antialiasing
	 *            the new anti-aliasing setting for this {@link IFillProperties}
	 * @return <code>this</code> for convenience
	 */
	IFillProperties setAntialiasing(boolean antialiasing);

	/**
	 * Sets the {@link Color fill color} associated with this
	 * {@link IFillProperties} to the given value.
	 * 
	 * @param fillColor
	 *            the new {@link Color fill color}
	 * @return <code>this</code> for convenience
	 */
	IFillProperties setColor(Color fillColor);

	// IFillProperties setTexture(Image texture);

}
