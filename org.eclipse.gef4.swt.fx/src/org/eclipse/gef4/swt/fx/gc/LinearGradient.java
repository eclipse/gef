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
package org.eclipse.gef4.swt.fx.gc;

import java.util.Arrays;

import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.planar.Point;

/**
 * A LinearGradient {@link Gradient} advances only in one direction. It has
 * definite start and end {@link Point}s and a set of
 * {@link org.eclipse.gef4.swt.fx.gc.GradientStop GradientStop}s which
 * determine the {@link Color} at any given {@link Point}.
 */
public class LinearGradient extends Gradient<LinearGradient> {

	private Point start;
	private Point end;

	private RgbaColor[] auxColors;
	private Vector auxUnitDirection;
	private double auxDistance;

	/**
	 * <p>
	 * Constructs a new {@link org.eclipse.gef4.swt.fx.gc.LinearGradient
	 * LinearGradient} representing a {@link Gradient} from the given start
	 * {@link Point} to the given end {@link Point}.
	 * </p>
	 * 
	 * <p>
	 * The {@link org.eclipse.gef4.swt.fx.gc.GradientStop GradientStop}s can
	 * be added afterwards via the {@link #addStop(double, Color)} method.
	 * </p>
	 * 
	 * @param from
	 *            the {@link Point} at which the {@link LinearGradient} starts
	 * @param to
	 *            the {@link Point} at which the {@link LinearGradient} ends
	 */
	public LinearGradient(Point from, Point to) {
		this(from, to, CycleMethod.DEFAULT);
	}

	/**
	 * <p>
	 * Constructs a new {@link LinearGradient} from the given start
	 * {@link Point} to the given end {@link Point} spreading as specified by
	 * the given {@link org.eclipse.gef4.swt.fx.gc.CycleMethod CycleMethod}.
	 * </p>
	 * 
	 * <p>
	 * The {@link org.eclipse.gef4.swt.fx.gc.GradientStop GradientStop}s can
	 * be added afterwards via the {@link #addStop(double, Color)} method.
	 * </p>
	 * 
	 * @param from
	 *            the {@link Point} at which the {@link LinearGradient} starts
	 * @param to
	 *            the {@link Point} at which the {@link LinearGradient} ends
	 * @param cycleMode
	 *            the {@link org.eclipse.gef4.swt.fx.gc.CycleMethod} which
	 *            specifies how the {@link Gradient} spreads outside of its main
	 *            area
	 */
	public LinearGradient(Point from, Point to, CycleMethod cycleMode) {
		super();
		start = new Point(from);
		end = new Point(to);
		setCycleMode(cycleMode);
	}

	@Override
	protected LinearGradient clone() throws CloneNotSupportedException {
		return getCopy();
	}

	public void computeAuxVars() {
		Vector direction = new Vector(start, end);
		auxUnitDirection = direction.getNormalized();
		auxDistance = direction.getLength();
		if (auxDistance < 1) {
			auxDistance = 1;
		}
		auxColors = new RgbaColor[(int) auxDistance];
		for (int i = 0; i < auxColors.length; i++) {
			double d = i / (double) (auxColors.length - 1);
			auxColors[i] = super.getPercentualColor(d);
		}
	}

	@Override
	public double computePercentualDistance(Point p) {
		if (auxUnitDirection != null) {
			return new Vector(p).getDotProduct(auxUnitDirection) / auxDistance;
		}

		Vector direction = new Vector(start, end);
		if (direction.isNull()) {
			return 0;
		}
		Vector unitDirection = direction.getNormalized();
		double d = new Vector(p).getDotProduct(unitDirection)
				/ direction.getLength();
		return d;
	}

	/**
	 * Copies private fields from this {@link LinearGradient} to the given
	 * {@link LinearGradient}. Start {@link Point}, end {@link Point}, and
	 * {@link CycleMethod} are not copied.
	 * 
	 * @param o
	 */
	protected void copyInto(LinearGradient o) {
		o.setGammaCorrection(getGammaCorrection());
		o.setStops(getStops());
		if (auxColors != null) {
			o.auxDistance = auxDistance;
			o.auxUnitDirection = auxUnitDirection;
			o.auxColors = Arrays.copyOf(auxColors, auxColors.length);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LinearGradient) {
			LinearGradient o = (LinearGradient) obj;
			return start.equals(o.start) && end.equals(o.end)
					&& Arrays.equals(getStops(), o.getStops());
		}
		return false;
	}

	@Override
	public LinearGradient getCopy() {
		LinearGradient copy = new LinearGradient(start, end, getCycleMode());
		copyInto(copy);
		return copy;
	}

	public Point getEnd() {
		return end.getCopy();
	}

	@Override
	protected RgbaColor getPercentualColor(double normalizedDistance) {
		if (auxColors == null) {
			return super.getPercentualColor(normalizedDistance);
		}
		return auxColors[(int) (normalizedDistance * (auxColors.length - 1))];
	}

	public Point getStart() {
		return start.getCopy();
	}

	public void setEnd(Point end) {
		this.end.setLocation(end);
	}

	public void setStart(Point start) {
		this.start.setLocation(start);
	}

}