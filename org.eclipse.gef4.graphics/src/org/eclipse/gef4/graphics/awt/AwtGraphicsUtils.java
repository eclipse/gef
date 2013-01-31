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
package org.eclipse.gef4.graphics.awt;

import java.awt.BasicStroke;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.MultipleGradientPaint.ColorSpaceType;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.RadialGradientPaint;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import org.eclipse.gef4.geometry.convert.awt.Geometry2AWT;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graphics.Gradient;
import org.eclipse.gef4.graphics.Gradient.CycleMode;
import org.eclipse.gef4.graphics.Gradient.GradientStop;
import org.eclipse.gef4.graphics.LineCap;
import org.eclipse.gef4.graphics.LineJoin;
import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.font.Font;
import org.eclipse.gef4.graphics.image.Image;

class AwtGraphicsUtils {

	public static double computeFontScale(Font font) {
		/*
		 * As AWT assumes a screen resolution of 72dpi independent of the actual
		 * screen resolution, we have to compute a scale factor for write
		 * operations.
		 * (http://www.eclipse.org/articles/Article-Swing-SWT-Integration/)
		 */
		double res = Toolkit.getDefaultToolkit().getScreenResolution();
		double scale = res / 72d;

		// adjust scale for fractional font sizes
		double fontScale = font.getSize() * scale / (int) font.getSize();

		return fontScale;
	}

	private static void convertStops(GradientStop[] stops, float[] fractions,
			java.awt.Color[] colors) {
		for (int i = 0; i < stops.length; i++) {
			fractions[i] = (float) stops[i].getPercentualDistance();
			colors[i] = AwtGraphicsUtils.toAwtColor(stops[i].getColor());
		}
	}

	private static double maxSide(Ellipse boundary) {
		double maxSide = boundary.getWidth() > boundary.getHeight() ? boundary
				.getWidth() : boundary.getHeight();
		return maxSide == 0 ? 1 : maxSide;
	}

	public static BasicStroke toAwtBasicStroke(double[] dashArray,
			double dashBegin, LineCap lineCap, LineJoin lineJoin,
			double lineWidth, double miterLimit) {
		int awtCap = lineCap == LineCap.FLAT ? BasicStroke.CAP_BUTT
				: lineCap == LineCap.ROUND ? BasicStroke.CAP_ROUND
						: BasicStroke.CAP_SQUARE;

		int awtJoin = lineJoin == LineJoin.BEVEL ? BasicStroke.JOIN_BEVEL
				: lineJoin == LineJoin.MITER ? BasicStroke.JOIN_MITER
						: BasicStroke.JOIN_ROUND;

		float[] awtDashes = new float[dashArray.length];
		for (int i = 0; i < dashArray.length; i++) {
			awtDashes[i] = (float) dashArray[i];
		}

		return new BasicStroke((float) lineWidth, awtCap, awtJoin,
				(float) miterLimit, dashArray.length == 0 ? null : awtDashes,
				(float) dashBegin);
	}

	/**
	 * Constructs a new {@link java.awt.Color} representation of the given
	 * {@link Color}.
	 * 
	 * @param color
	 *            the {@link Color} object to transform
	 * @return a new {@link java.awt.Color} representation of this {@link Color}
	 */
	public static java.awt.Color toAwtColor(Color color) {
		return new java.awt.Color(color.getRed(), color.getGreen(),
				color.getBlue(), color.getAlpha());
	}

	public static CycleMethod toAwtCycleMethod(CycleMode cycleMode) {
		CycleMethod cycleMethod;
		if (cycleMode == CycleMode.NO_CYCLE) {
			cycleMethod = CycleMethod.NO_CYCLE;
		} else if (cycleMode == CycleMode.REFLECT) {
			cycleMethod = CycleMethod.REFLECT;
		} else if (cycleMode == CycleMode.REPEAT) {
			cycleMethod = CycleMethod.REPEAT;
		} else {
			throw new IllegalStateException("Unsupported CycleMode '"
					+ cycleMode + "' used.");
		}
		return cycleMethod;
	}

	public static java.awt.Font toAwtFont(Font font) {
		// /*
		// * As AWT assumes a screen resolution of 72dpi independent of the
		// actual
		// * screen resolution, we have to convert the font size appropriately.
		// * (http://www.eclipse.org/articles/Article-Swing-SWT-Integration/)
		// */
		// double resolution =
		// Toolkit.getDefaultToolkit().getScreenResolution();
		// int awtSize = (int) Math.round(font.getSize() * resolution / 72d);
		int awtSize = (int) font.getSize();

		int awtStyle = (font.isBold() ? java.awt.Font.BOLD : 0)
				| (font.isItalic() ? java.awt.Font.ITALIC : 0);

		return new java.awt.Font(font.getFamily(), awtStyle, awtSize);
	}

	public static MultipleGradientPaint toAwtGradient(Gradient<?> gradient) {
		if (gradient instanceof Gradient.Linear) {
			Gradient.Linear linearGradient = (Gradient.Linear) gradient;

			GradientStop[] stops = linearGradient.getStops();
			float[] fractions = new float[stops.length];
			java.awt.Color[] colors = new java.awt.Color[stops.length];
			convertStops(stops, fractions, colors);

			return new LinearGradientPaint(
					Geometry2AWT.toAWTPoint(linearGradient.getStart()),
					Geometry2AWT.toAWTPoint(linearGradient.getEnd()),
					fractions, colors,
					toAwtCycleMethod(linearGradient.getCycleMode()));
		} else if (gradient instanceof Gradient.Radial) {
			Gradient.Radial radialGradient = (Gradient.Radial) gradient;

			GradientStop[] stops = radialGradient.getStops();
			float[] fractions = new float[stops.length];
			java.awt.Color[] colors = new java.awt.Color[stops.length];
			convertStops(stops, fractions, colors);

			Ellipse boundary = radialGradient.getBoundary();
			Point center = boundary.getCenter();
			double maxSide = maxSide(boundary);
			float radius = (float) maxSide / 2;

			AffineTransform gradientTransform = new AffineTransform()
					.translate(center.x, center.y)
					.scale(boundary.getWidth() / maxSide,
							boundary.getHeight() / maxSide)
					.translate(-center.x, -center.y);

			return new RadialGradientPaint(Geometry2AWT.toAWTPoint(center),
					radius, Geometry2AWT.toAWTPoint(radialGradient.getFocus()),
					fractions, colors,
					toAwtCycleMethod(radialGradient.getCycleMode()),
					ColorSpaceType.SRGB,
					Geometry2AWT.toAWTAffineTransform(gradientTransform));
		} else {
			throw new UnsupportedOperationException(
					"Only Gradient.Linear and Gradient.Radial may be used to specify a gradient Pattern.");
		}
	}

	public static BufferedImage toAwtImage(Image image) {
		BufferedImage awtImage = new BufferedImage(image.getWidth(),
				image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		awtImage.setRGB(0, 0, image.getWidth(), image.getHeight(),
				image.getPixels(), 0, image.getWidth());

		return awtImage;
	}

}
