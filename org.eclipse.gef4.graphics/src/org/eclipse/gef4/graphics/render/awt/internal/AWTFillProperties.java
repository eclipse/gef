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
package org.eclipse.gef4.graphics.render.awt.internal;

import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.MultipleGradientPaint.ColorSpaceType;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;

import org.eclipse.gef4.geometry.convert.awt.Geometry2AWT;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.render.AbstractFillProperties;
import org.eclipse.gef4.graphics.render.ColorFill;
import org.eclipse.gef4.graphics.render.GradientFill;
import org.eclipse.gef4.graphics.render.GradientFill.CycleMode;
import org.eclipse.gef4.graphics.render.GradientFill.GradientStop;
import org.eclipse.gef4.graphics.render.IFillMode;
import org.eclipse.gef4.graphics.render.IFillProperties;
import org.eclipse.gef4.graphics.render.IGraphics;
import org.eclipse.gef4.graphics.render.ImageFill;
import org.eclipse.gef4.graphics.render.awt.AWTGraphics;

/**
 * The AWT {@link IFillProperties} implementation.
 * 
 * @author mwienand
 * 
 */
public class AWTFillProperties extends AbstractFillProperties {

	/**
	 * Creates a new {@link AWTFillProperties} with the {@link #fillColor} set
	 * to the default color specified by the {@link IFillProperties} interface.
	 */
	public AWTFillProperties() {
		new Color();
	}

	@Override
	public void applyOn(IGraphics graphics, Path path) {
		Graphics2D g = ((AWTGraphics) graphics).getGraphics2D();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				isAntialiasing() ? RenderingHints.VALUE_ANTIALIAS_ON
						: RenderingHints.VALUE_ANTIALIAS_OFF);

		IFillMode fillMode = getMode();
		if (fillMode instanceof ColorFill) {
			fill(path, g, (ColorFill) fillMode);
		} else if (fillMode instanceof GradientFill.Linear) {
			fill(path, g, (GradientFill.Linear) fillMode);
		} else if (fillMode instanceof GradientFill.Radial) {
			fill(path, g, (GradientFill.Radial) fillMode);
		} else if (fillMode instanceof ImageFill) {
			fill(path, g, (ImageFill) fillMode);
		} else {
			super.generalFill(graphics, path);
		}
	}

	@Override
	public void cleanUp(IGraphics g) {
	}

	/**
	 * @param stops
	 * @param fractions
	 * @param colors
	 */
	private void convertStops(GradientStop[] stops, float[] fractions,
			java.awt.Color[] colors) {
		for (int i = 0; i < stops.length; i++) {
			fractions[i] = (float) stops[i].getPercentualDistance();
			colors[i] = AWTGraphicsUtils.toAWTColor(stops[i].getColor());
		}
	}

	/**
	 * @param path
	 * @param g
	 * @param m
	 */
	private void fill(Path path, Graphics2D g, ColorFill m) {
		g.setColor(AWTGraphicsUtils.toAWTColor(m.getColor()));
		g.fill(Geometry2AWT.toAWTPath(path));
	}

	/**
	 * @param path
	 * @param g
	 * @param m
	 */
	private void fill(Path path, Graphics2D g, GradientFill.Linear m) {
		GradientStop[] stops = m.getStops();
		float[] fractions = new float[stops.length];
		java.awt.Color[] colors = new java.awt.Color[stops.length];
		convertStops(stops, fractions, colors);

		MultipleGradientPaint gradientPaint = new LinearGradientPaint(
				Geometry2AWT.toAWTPoint(m.getStart()),
				Geometry2AWT.toAWTPoint(m.getEnd()), fractions, colors,
				getAWTCycleMode(m));

		fillGradient(path, g, gradientPaint);
	}

	/**
	 * @param path
	 * @param g
	 * @param m
	 */
	private void fill(Path path, Graphics2D g, GradientFill.Radial m) {
		GradientStop[] stops = m.getStops();
		float[] fractions = new float[stops.length];
		java.awt.Color[] colors = new java.awt.Color[stops.length];
		convertStops(stops, fractions, colors);

		Ellipse boundary = m.getBoundary();
		Point center = boundary.getCenter();
		double maxSide = maxSide(boundary);
		float radius = (float) maxSide / 2;

		AffineTransform gradientTransform = new AffineTransform().scale(
				boundary.getWidth() / maxSide, boundary.getHeight() / maxSide);

		MultipleGradientPaint gradientPaint = new RadialGradientPaint(
				Geometry2AWT.toAWTPoint(center), radius,
				Geometry2AWT.toAWTPoint(m.getFocus()), fractions, colors,
				getAWTCycleMode(m), ColorSpaceType.SRGB,
				Geometry2AWT.toAWTAffineTransform(gradientTransform));

		fillGradient(path, g, gradientPaint);
	}

	/**
	 * @param path
	 * @param g
	 * @param m
	 */
	private void fill(Path path, Graphics2D g, ImageFill m) {
		// We do not use TexturePaint but clip appropriately instead, because a
		// TexturePaint is not recommended for large images.
		Shape oldClip = g.getClip();
		g.clip(Geometry2AWT.toAWTPath(path));
		g.drawImage(AWTGraphicsUtils.toAWTImage(m.image()), null, 0, 0);
		g.setClip(oldClip);
	}

	/**
	 * @param path
	 * @param g
	 * @param gradientPaint
	 */
	private void fillGradient(Path path, Graphics2D g,
			MultipleGradientPaint gradientPaint) {
		Paint oldPaint = g.getPaint();
		g.setPaint(gradientPaint);
		g.fill(Geometry2AWT.toAWTPath(path));
		g.setPaint(oldPaint);
	}

	/**
	 * @param m
	 * @return
	 */
	private CycleMethod getAWTCycleMode(GradientFill m) {
		CycleMode cycleMode = m.getCycleMode();
		CycleMethod cycleModeAWT;
		if (cycleMode == CycleMode.NO_CYCLE) {
			cycleModeAWT = CycleMethod.NO_CYCLE;
		} else if (cycleMode == CycleMode.REFLECT) {
			cycleModeAWT = CycleMethod.REFLECT;
		} else if (cycleMode == CycleMode.REPEAT) {
			cycleModeAWT = CycleMethod.REPEAT;
		} else {
			throw new IllegalStateException("Unsupported CycleMode '"
					+ cycleMode + "' used.");
		}
		return cycleModeAWT;
	}

	@Override
	public AWTFillProperties getCopy() {
		AWTFillProperties copy = new AWTFillProperties();
		copy.setAntialiasing(isAntialiasing());
		copy.setMode(getMode());
		return copy;
	}

	@Override
	public void init(IGraphics g) {
	}

	/**
	 * @param boundary
	 * @return
	 */
	private double maxSide(Ellipse boundary) {
		double maxSide = boundary.getWidth() > boundary.getHeight() ? boundary
				.getWidth() : boundary.getHeight();

		if (maxSide == 0) {
			maxSide = 1;
		}
		return maxSide;
	}

}
