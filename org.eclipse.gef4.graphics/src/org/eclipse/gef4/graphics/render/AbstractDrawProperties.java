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

import java.util.Arrays;

/**
 * The AbstractDrawProperties class partially implements the
 * {@link IDrawProperties} interface.
 * 
 * @author mwienand
 * 
 */
public abstract class AbstractDrawProperties extends AbstractGraphicsProperties
implements IDrawProperties {

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
	 * The initially assumed covered distance when applying the current
	 * {@link #dashArray}.
	 */
	protected double dashBegin = DEFAULT_DASH_BEGIN;

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
		if (!isActive()) {
			throw new IllegalStateException(
					"Access to this IDrawProperties is denied, because it is currently deactivated.");
		}
		return dashArray == null ? null : Arrays.copyOf(dashArray,
				dashArray.length);
	}

	public double getDashBegin() {
		if (!isActive()) {
			throw new IllegalStateException(
					"Access to this IDrawProperties is denied, because it is currently deactivated.");
		}
		return dashBegin;
	}

	public LineCap getLineCap() {
		if (!isActive()) {
			throw new IllegalStateException(
					"Access to this IDrawProperties is denied, because it is currently deactivated.");
		}
		return lineCap;
	}

	public LineJoin getLineJoin() {
		if (!isActive()) {
			throw new IllegalStateException(
					"Access to this IDrawProperties is denied, because it is currently deactivated.");
		}
		return lineJoin;
	}

	public double getLineWidth() {
		if (!isActive()) {
			throw new IllegalStateException(
					"Access to this IDrawProperties is denied, because it is currently deactivated.");
		}
		return lineWidth;
	}

	public double getMiterLimit() {
		if (!isActive()) {
			throw new IllegalStateException(
					"Access to this IDrawProperties is denied, because it is currently deactivated.");
		}
		return miterLimit;
	}

	public boolean isAntialiasing() {
		if (!isActive()) {
			throw new IllegalStateException(
					"Access to this IDrawProperties is denied, because it is currently deactivated.");
		}
		return antialiasing;
	}

	public AbstractDrawProperties setAntialiasing(boolean antialiasing) {
		if (!isActive()) {
			throw new IllegalStateException(
					"Access to this IDrawProperties is denied, because it is currently deactivated.");
		}
		this.antialiasing = antialiasing;
		return this;
	}

	public AbstractDrawProperties setDashArray(double... dashes) {
		if (!isActive()) {
			throw new IllegalStateException(
					"Access to this IDrawProperties is denied, because it is currently deactivated.");
		}
		dashArray = dashes == null ? null : Arrays
				.copyOf(dashes, dashes.length);
		return this;
	}

	public AbstractDrawProperties setDashBegin(double distance) {
		if (!isActive()) {
			throw new IllegalStateException(
					"Access to this IDrawProperties is denied, because it is currently deactivated.");
		}
		dashBegin = distance;
		return this;
	}

	public AbstractDrawProperties setLineCap(LineCap lineCap) {
		if (!isActive()) {
			throw new IllegalStateException(
					"Access to this IDrawProperties is denied, because it is currently deactivated.");
		}
		this.lineCap = lineCap;
		return this;
	}

	public AbstractDrawProperties setLineJoin(LineJoin lineJoin) {
		if (!isActive()) {
			throw new IllegalStateException(
					"Access to this IDrawProperties is denied, because it is currently deactivated.");
		}
		this.lineJoin = lineJoin;
		return this;
	}

	public AbstractDrawProperties setLineWidth(double lineWidth) {
		if (!isActive()) {
			throw new IllegalStateException(
					"Access to this IDrawProperties is denied, because it is currently deactivated.");
		}
		this.lineWidth = lineWidth;
		return this;
	}

	public AbstractDrawProperties setMiterLimit(double limit) {
		if (!isActive()) {
			throw new IllegalStateException(
					"Access to this IDrawProperties is denied, because it is currently deactivated.");
		}
		miterLimit = limit;
		return this;
	}

}
