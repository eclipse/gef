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

import java.util.Arrays;

import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;

/**
 * A RadialGradient {@link Gradient} is defined by an {@link Ellipse} and a
 * focus {@link Point}. A set of
 * {@link org.eclipse.gef4.swtfx.gc.GradientStop GradientStop}s determine
 * the {@link RgbaColor} at any given {@link Point}. The focus {@link Point}
 * specifies the origin of a radial gradient (percentual distance = 0). The
 * perimeter of the {@link Ellipse} specifies the border of a radial gradient
 * (percentual distance = 1).
 */
public class RadialGradient extends Gradient<RadialGradient> {

	private Ellipse boundary;
	private Point focus;
	private Vector fp = new Vector(0, 0);
	private Vector fs = new Vector(0, 0);
	private Vector focusLineDirectionUnit = new Vector(0, 0);
	private Line positiveFocusLine = new Line(0, 0, 0, 0);
	private double fsLen = 0;
	private double fpLen = 0;
	private RgbaColor[] auxColors;

	/**
	 * Constructs a new {@link RadialGradient} with its boundary set to the
	 * passed-in {@link Ellipse}. The {@link Ellipse#getCenter() center}
	 * {@link Point} of the {@link Ellipse} is used as the focal {@link Point}.
	 * 
	 * @param boundary
	 */
	public RadialGradient(Ellipse boundary) {
		this(boundary, boundary.getCenter());
	}

	/**
	 * Constructs a new {@link RadialGradient} from the passed-in values.
	 * 
	 * @param boundary
	 * @param focus
	 */
	public RadialGradient(Ellipse boundary, Point focus) {
		super();

		if (boundary == null) {
			throw new IllegalArgumentException(
					"The Gradient.Radial boundary parameter may not be null.");
		}
		this.boundary = boundary.getCopy();

		if (!boundary.contains(focus)) {
			throw new IllegalArgumentException(
					"The given focal Point may only lie inside the specified boundary.");
		}
		this.focus = focus.getCopy();
	}

	public RadialGradient(Ellipse boundary, Point focus, CycleMethod cycleMode) {
		this(boundary, focus);
		setCycleMethod(cycleMode);
	}

	@Override
	protected RadialGradient clone() throws CloneNotSupportedException {
		return getCopy();
	}

	public void computeAuxVars() {
		Line positiveFocusLine = new Line(focus, new Vector(focus,
				boundary.getCenter()).getNormalized()
				.getMultiplied(boundary.getWidth() + boundary.getHeight())
				.toPoint());
		Point[] intersections = boundary.getIntersections(positiveFocusLine);
		if (intersections.length < 1) {
			return;
		}

		Vector fs = new Vector(focus, intersections[0]);
		double len = fs.getLength();
		int auxColorsLen = (int) len + 1;
		auxColors = new RgbaColor[auxColorsLen];
		for (int i = 0; i < auxColors.length; i++) {
			double d = i / (double) (auxColors.length - 1);
			auxColors[i] = super.getPercentualColor(d);
		}
	}

	@Override
	public double computePercentualDistance(Point p) {
		double ratio;

		if (p.equals(focus)) {
			ratio = 0;
		} else {
			fp.x = p.x - focus.x;
			fp.y = p.y - focus.y;
			fpLen = fp.getLength();
			assert fpLen > 0;

			focusLineDirectionUnit.x = fp.x / fpLen;
			focusLineDirectionUnit.y = fp.y / fpLen;

			positiveFocusLine.setX1(focus.x);
			positiveFocusLine.setY1(focus.y);

			double transDist = boundary.getWidth() + boundary.getHeight();
			positiveFocusLine.setX2(p.x + focusLineDirectionUnit.x * transDist);
			positiveFocusLine.setY2(p.y + focusLineDirectionUnit.y * transDist);

			Point[] intersections = boundary
					.getIntersections(positiveFocusLine);

			if (intersections.length != 1) {
				throw new IllegalStateException(
						"There may always be an intersection. (Ellipse = "
								+ boundary
								+ ", Line = "
								+ positiveFocusLine
								+ ".) This is a bug. It would be awsome if you create a ticket at bugs.eclipse.org containing this error message.");
			}

			fs.x = intersections[0].x - focus.x;
			fs.y = intersections[0].y - focus.y;
			fsLen = fs.getLength();
			assert fsLen > 0;

			ratio = fsLen == 0 ? 0 : fpLen / fsLen;
		}

		return ratio;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RadialGradient) {
			RadialGradient o = (RadialGradient) obj;
			return boundary.equals(o.boundary) && focus.equals(o.focus)
					&& Arrays.equals(getStops(), o.getStops());
		}
		return false;
	}

	public Ellipse getBoundary() {
		return boundary.getCopy();
	}

	@Override
	public RadialGradient getCopy() {
		RadialGradient copy = new RadialGradient(boundary, focus,
				getCycleMethod()).setStops(getStops()).setGammaCorrection(
				getGammaCorrection());
		copy.auxColors = auxColors == null ? null : Arrays.copyOf(auxColors,
				auxColors.length);
		return copy;
	}

	public Point getFocus() {
		return focus.getCopy();
	}

	@Override
	protected RgbaColor getPercentualColor(double normalizedDistance) {
		if (auxColors == null) {
			return super.getPercentualColor(normalizedDistance);
		}
		return auxColors[(int) (normalizedDistance * (auxColors.length - 1))];
	}

	public void inject(RadialGradient other) {
		other.auxColors = auxColors == null ? null : Arrays.copyOf(auxColors,
				auxColors.length);
		other.boundary = boundary.getCopy();
		other.focus = focus.getCopy();
		other.setCycleMethod(getCycleMethod());
		other.setStops(getStops());
	}

	public void setBoundary(Ellipse boundary) {
		this.boundary.setBounds(boundary.getBounds());
	}

	public void setFocus(Point focus) {
		this.focus.setLocation(focus);
	}

}