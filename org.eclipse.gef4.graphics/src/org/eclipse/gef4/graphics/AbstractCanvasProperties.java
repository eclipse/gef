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

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Region;
import org.eclipse.gef4.geometry.planar.Ring;

/**
 * The AbstractCanvasProperties partially implements the
 * {@link ICanvasProperties} interface. You should extend this class to write
 * your own {@link ICanvasProperties} implementation.
 * 
 * @author mwienand
 * 
 */
public abstract class AbstractCanvasProperties extends
AbstractGraphicsProperties implements ICanvasProperties {

	/**
	 * The canvas transformations ({@link AffineTransform}) associated with this
	 * {@link AbstractCanvasProperties}.
	 */
	protected AffineTransform affineTransform;

	/**
	 * The clipping area ({@link Ring}) associated with this
	 * {@link AbstractCanvasProperties}.
	 */
	protected Ring clippingArea;

	/**
	 * The default constructor, initializing the {@link #clippingArea} to
	 * <code>null</code> and the {@link #affineTransform} to an identity
	 * transformation.
	 */
	protected AbstractCanvasProperties() {
		affineTransform = new AffineTransform();
		clippingArea = null;
	}

	public AffineTransform getAffineTransform() {
		if (!isActive()) {
			throw new IllegalStateException(
					"Access to this ICanvasProperties is denied, because it is currently deactivated.");
		}
		return affineTransform.getCopy();
	}

	public Ring getClippingArea() {
		if (!isActive()) {
			throw new IllegalStateException(
					"Access to this ICanvasProperties is denied, because it is currently deactivated.");
		}
		if (clippingArea == null) {
			return null;
		}
		return clippingArea.getCopy();
	}

	public ICanvasProperties setAffineTransform(AffineTransform affineTransform) {
		if (!isActive()) {
			throw new IllegalStateException(
					"Access to this ICanvasProperties is denied, because it is currently deactivated.");
		}
		this.affineTransform.setTransform(affineTransform);
		return this;
	}

	public ICanvasProperties setClippingArea(Region clippingArea) {
		if (!isActive()) {
			throw new IllegalStateException(
					"Access to this ICanvasProperties is denied, because it is currently deactivated.");
		}
		this.clippingArea = clippingArea == null ? null : clippingArea.toRing();
		return this;
	}

	public ICanvasProperties setClippingArea(Ring clippingArea) {
		if (!isActive()) {
			throw new IllegalStateException(
					"Access to this ICanvasProperties is denied, because it is currently deactivated.");
		}
		this.clippingArea = clippingArea == null ? null : clippingArea
				.getCopy();
		return this;
	}

}
