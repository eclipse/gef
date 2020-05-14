/*******************************************************************************
 * Copyright (c) 2012, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef.geometry.planar;

/**
 * <p>
 * The {@link ITranslatable} interface collects all translation short-cut
 * methods.
 * </p>
 * 
 * <p>
 * Translation can be applied directly on an object via the
 * {@link #translate(Point)} and {@link #translate(double, double)} methods.
 * They return the scaled, calling object for convenience.
 * </p>
 * 
 * <p>
 * On the other hand, the {@link #getTranslated(Point)} and
 * {@link #getTranslated(double, double)} methods create a translated copy of
 * the original object.
 * </p>
 * 
 * @param <T>
 *            the implementing type
 * 
 * @author mwienand
 * 
 */
public interface ITranslatable<T extends IGeometry> {

	/**
	 * Translates a copy of this object by the given values in x and y
	 * direction.
	 * 
	 * @param dx
	 *            x-translation
	 * @param dy
	 *            y-translation
	 * @return a new, translated object
	 */
	public T getTranslated(double dx, double dy);

	/**
	 * Translates a copy of this object by the given {@link Point}.
	 * 
	 * @param d
	 *            translation {@link Point}
	 * @return a new, translated object
	 */
	public T getTranslated(Point d);

	/**
	 * Translates the object by the given values in x and y direction.
	 * 
	 * @param dx
	 *            x-translation
	 * @param dy
	 *            y-translation
	 * @return <code>this</code> for convenience
	 */
	public T translate(double dx, double dy);

	/**
	 * Translates the object by the given {@link Point}.
	 * 
	 * @param d
	 *            translation {@link Point}
	 * @return <code>this</code> for convenience
	 */
	public T translate(Point d);

}
