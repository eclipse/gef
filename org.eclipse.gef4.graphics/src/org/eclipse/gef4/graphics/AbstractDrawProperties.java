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

import java.util.Arrays;

/**
 * The AbstractDrawProperties class partially implements the
 * {@link IDrawProperties} interface.
 * 
 * @author mwienand
 * 
 */
public abstract class AbstractDrawProperties implements IDrawProperties {

	/**
	 * The anti-aliasing setting associated with this
	 * {@link AbstractDrawProperties}.
	 */
	protected boolean antialiasing = DEFAULT_ANTIALIASING;

	/**
	 * The dash array associated with this {@link AbstractDrawProperties}.
	 */
	protected double[] dashArray = DEFAULT_DASH_ARRAY;

	/**
	 * The {@link IDrawProperties.LineCap} associated with this
	 * {@link AbstractDrawProperties}.
	 */
	protected LineCap lineCap = DEFAULT_LINE_CAP;

	/**
	 * The {@link IDrawProperties.LineJoin} associated with this
	 * {@link AbstractDrawProperties}.
	 */
	protected LineJoin lineJoin = DEFAULT_LINE_JOIN;

	/**
	 * The miter limit associated with this {@link AbstractDrawProperties}.
	 */
	protected double miterLimit = DEFAULT_MITER_LIMIT;

	/**
	 * The line width associated with this {@link AbstractDrawProperties}.
	 */
	protected double lineWidth = DEFAULT_LINE_WIDTH;

	public double[] getDashArray() {
		return dashArray == null ? null : Arrays.copyOf(dashArray,
				dashArray.length);
	}

	public LineCap getLineCap() {
		return lineCap;
	}

	public LineJoin getLineJoin() {
		return lineJoin;
	}

	public double getLineWidth() {
		return lineWidth;
	}

	public double getMiterLimit() {
		return miterLimit;
	}

	public boolean isAntialiasing() {
		return antialiasing;
	}

	public IDrawProperties setAntialiasing(boolean antialiasing) {
		this.antialiasing = antialiasing;
		return this;
	}

	public IDrawProperties setDashArray(double... dashes) {
		dashArray = dashes == null ? null : Arrays
				.copyOf(dashes, dashes.length);
		return this;
	}

	public IDrawProperties setLineCap(LineCap lineCap) {
		this.lineCap = lineCap;
		return this;
	}

	public IDrawProperties setLineJoin(LineJoin lineJoin) {
		this.lineJoin = lineJoin;
		return this;
	}

	public IDrawProperties setLineWidth(double lineWidth) {
		this.lineWidth = lineWidth;
		return this;
	}

	public IDrawProperties setMiterLimit(double limit) {
		miterLimit = limit;
		return this;
	}

}
