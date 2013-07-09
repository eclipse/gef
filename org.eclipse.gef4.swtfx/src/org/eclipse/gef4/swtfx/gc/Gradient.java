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

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.gef4.geometry.planar.Point;

/**
 * <p>
 * A Gradient can be set on a {@link GraphicsContext} to fill or stroke
 * drawings.
 * </p>
 * 
 * <p>
 * There are two different Gradient types available:
 * <ul>
 * <li>{@link LinearGradient}</li>
 * <li>{@link RadialGradient}</li>
 * </ul>
 * </p>
 * 
 * @author mwienand
 * 
 * @param <T>
 *            the type parameter specifies the extending type to prevent
 *            unnecessary type casts.
 * 
 */
public abstract class Gradient<T extends Gradient<?>> {

	private SortedSet<GradientStop> stops;

	private CycleMethod cycle;

	private double gammaCorrection = 1d; // no correction

	/**
	 * Constructs a new {@link Gradient} with an empty set of
	 * {@link GradientStop}s.
	 */
	public Gradient() {
		stops = new TreeSet<GradientStop>(new Comparator<GradientStop>() {
			@Override
			public int compare(GradientStop a, GradientStop b) {
				double aDist = a.getPercentualDistance();
				double bDist = b.getPercentualDistance();
				if (aDist == bDist) {
					return 0;
				} else if (aDist < bDist) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		cycle = CycleMethod.DEFAULT;
	}

	/**
	 * Adds another {@link GradientStop} for the passed-in values to this
	 * {@link Gradient}.
	 * 
	 * @param percentualDistance
	 *            the percentual distance at which the {@link GradientStop} is
	 *            created
	 * @param color
	 *            the {@link Color} that is used from the new
	 *            {@link GradientStop} on
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public T addStop(double percentualDistance, RgbaColor color) {
		if (percentualDistance > 1) {
			throw new IllegalArgumentException(
					"The percentual distance from a gradient's start may not exceed 1.");
		}
		stops.add(new GradientStop(percentualDistance, color));
		return (T) this;
	}

	/**
	 * <p>
	 * Computes the percentual distance from the origin of this {@link Gradient}
	 * to the given {@link Point}. The percentual distance is not necessarily
	 * between 0 and 1. It can be any value, depending on the absolute distance
	 * from the origin of this {@link Gradient} to the given {@link Point} and
	 * the size of the {@link Gradient}.
	 * </p>
	 * 
	 * <p>
	 * Note, that the origin of a {@link Gradient} may not be a single point.
	 * For a {@link LinearGradient} the origin really is an infinite line
	 * perpendicular to the direction of the {@link LinearGradient}. For a
	 * {@link RadialGradient} the origin is a single point.
	 * </p>
	 * 
	 * @param p
	 *            the {@link Point} for which its percentual distance to this
	 *            {@link Gradient}'s origin is to be computed
	 * @return the percentual distance form the origin of this {@link Gradient}
	 *         to the given {@link Point}
	 */
	abstract public double computePercentualDistance(Point p);

	public RgbaColor getColorAt(Point p) {
		double d = computePercentualDistance(p);
		d = normalizePercentualDistance(d);
		return getPercentualColor(d);
	}

	/**
	 * Returns a copy of this {@link Gradient}.
	 * 
	 * @return a copy of this {@link Gradient}
	 */
	public abstract T getCopy();

	/**
	 * Returns the currently active {@link CycleMethod}.
	 * 
	 * @return the currently active {@link CycleMethod}
	 */
	public CycleMethod getCycleMethod() {
		return cycle;
	}

	public double getGammaCorrection() {
		return gammaCorrection;
	}

	/**
	 * @param normalizedDistance
	 * @return
	 */
	protected RgbaColor getPercentualColor(double normalizedDistance) {
		GradientStop[] stops = getStops();

		if (stops.length < 2) {
			throw new IllegalStateException(
					"At least 2 GradientStops required.");
		}

		GradientStop from = stops[0], to = stops[1];
		double blendRatio = 0;

		for (int i = 0; i < stops.length; i++) {
			double stopDistance = stops[i].getPercentualDistance();

			if (normalizedDistance < stopDistance) {
				break;
			}

			from = stops[i];
			to = i == stops.length - 1 ? from : stops[i + 1];

			double nextStopDistance = to.getPercentualDistance();

			if (nextStopDistance <= stopDistance) {
				blendRatio = 0;
			} else {
				blendRatio = (normalizedDistance - stopDistance)
						/ (nextStopDistance - stopDistance);
			}
		}

		return to.getRgbaColor().getBlended(from.getRgbaColor(), blendRatio,
				gammaCorrection);
	}

	/**
	 * Returns the sorted list of {@link GradientStop}s specified for this
	 * {@link Gradient}.
	 * 
	 * @return the sorted list of {@link GradientStop}s
	 */
	public GradientStop[] getStops() {
		GradientStop[] stops = new GradientStop[this.stops.size()];
		int i = 0;
		for (GradientStop stop : this.stops) {
			stops[i++] = new GradientStop(stop.getPercentualDistance(),
					stop.getRgbaColor());
		}
		return stops;
	}

	/**
	 * Normalizes the given percentual distance according to the current
	 * {@link CycleMethod}. The normalized percentual distance is in the range
	 * <code>[0;1]</code>.
	 * 
	 * @param d
	 * @return
	 */
	private double normalizePercentualDistance(double d) {
		if (getCycleMethod() == CycleMethod.NO_CYCLE && d > 1) {
			d = 1;
		} else if (getCycleMethod() == CycleMethod.REPEAT && d > 1) {
			d = d - (int) d;
		} else if (getCycleMethod() == CycleMethod.REFLECT && d > 1) {
			d -= 2 * (int) (d / 2);
			if (d > 1) {
				d = 2 - d;
			}
		}
		return d;
	}

	/**
	 * Sets the {@link CycleMethod} to use for this {@link Gradient} to the
	 * passed-in value.
	 * 
	 * @param cycle
	 *            the {@link CycleMethod} to use
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public T setCycleMethod(CycleMethod cycle) {
		this.cycle = cycle;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setGammaCorrection(double gammaCorrection) {
		this.gammaCorrection = gammaCorrection;
		return (T) this;
	}

	/**
	 * Sets the {@link GradientStop}s of this {@link Gradient} to the given
	 * values.
	 * 
	 * @param stops
	 *            the new {@link GradientStop}s for this {@link Gradient}
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public T setStops(GradientStop... stops) {
		this.stops.clear();
		if (stops == null || stops.length == 0) {
			return (T) this;
		}
		for (GradientStop gs : stops) {
			addStop(gs.getPercentualDistance(), gs.getRgbaColor());
		}
		return (T) this;
	}

}
