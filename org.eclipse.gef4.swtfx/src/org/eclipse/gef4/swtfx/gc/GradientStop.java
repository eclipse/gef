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
package org.eclipse.gef4.swtfx.gc;

/**
 * A GradientStop combines a distance from the gradient start on a percentage
 * basis with a {@link Color} which is to be used from that distance on to fill
 * a specific area.
 */
public class GradientStop {

	private double percentualDistance;
	private RgbaColor color;

	public GradientStop(double percentualDistance,
			org.eclipse.swt.graphics.Color swtColor) {
		this(percentualDistance, new RgbaColor(swtColor.getRed(),
				swtColor.getGreen(), swtColor.getBlue()));
	}

	public GradientStop(double percentualDistance,
			org.eclipse.swt.graphics.Color swtColor, int alpha) {
		this(percentualDistance, new RgbaColor(swtColor.getRed(),
				swtColor.getGreen(), swtColor.getBlue(), alpha));
	}

	/**
	 * Constructs a new {@link GradientStop} and associates the passed-in
	 * <i>percentualDistance</i> and <i>color</i> with it.
	 * 
	 * @param percentualDistance
	 *            the percentual distance from the gradient's start from which
	 *            on to use the given {@link RgbaColor}
	 * @param color
	 *            the {@link RgbaColor} to use when reaching the given
	 *            percentual distance from the gradient's start
	 */
	public GradientStop(double percentualDistance, RgbaColor color) {
		setPercentualDistance(percentualDistance);
		setRgbaColor(color);
	}

	/**
	 * Returns the associated percentual distance from the gradient's start.
	 * 
	 * @return the associated percentual distance
	 */
	public double getPercentualDistance() {
		return percentualDistance;
	}

	/**
	 * Returns the associated {@link RgbaColor}.
	 * 
	 * @return the associated {@link RgbaColor}
	 */
	public RgbaColor getRgbaColor() {
		return color.getCopy();
	}

	/**
	 * Associates the passed-in <i>percentualDistance</i> with this
	 * {@link GradientStop}.
	 * 
	 * @param percentualDistance
	 *            the new percentual distance to associate with this
	 *            {@link GradientStop}
	 */
	public void setPercentualDistance(double percentualDistance) {
		this.percentualDistance = percentualDistance;
	}

	/**
	 * Associates the passed-in {@link RgbaColor} with this {@link GradientStop}
	 * .
	 * 
	 * @param color
	 *            the new {@link RgbaColor} to associate with this
	 *            {@link GradientStop}
	 */
	public void setRgbaColor(RgbaColor color) {
		this.color = color.getCopy();
	}

}