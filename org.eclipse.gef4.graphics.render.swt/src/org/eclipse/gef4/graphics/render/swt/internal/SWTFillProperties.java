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
package org.eclipse.gef4.graphics.render.swt.internal;

import org.eclipse.gef4.geometry.convert.swt.Geometry2SWT;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.render.AbstractFillProperties;
import org.eclipse.gef4.graphics.render.ColorFill;
import org.eclipse.gef4.graphics.render.IFillMode;
import org.eclipse.gef4.graphics.render.IFillProperties;
import org.eclipse.gef4.graphics.render.IGraphics;
import org.eclipse.gef4.graphics.render.ImageFill;
import org.eclipse.gef4.graphics.render.swt.SWTGraphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.widgets.Display;

public class SWTFillProperties extends AbstractFillProperties {

	private int initialAlpha;

	public SWTFillProperties() {
	}

	@Override
	public void applyOn(IGraphics g, Path path) {
		GC gc = ((SWTGraphics) g).getGC();
		gc.setAntialias(isAntialiasing() ? SWT.ON : SWT.OFF);

		IFillMode fillMode = getMode();
		if (fillMode instanceof ColorFill) {
			ColorFill m = (ColorFill) fillMode;

			Color color = m.getColor();
			gc.setAlpha(color.getAlpha());

			org.eclipse.swt.graphics.Color oldColor = gc.getBackground();
			org.eclipse.swt.graphics.Color colorSWT = new org.eclipse.swt.graphics.Color(
					gc.getDevice(), color.getRed(), color.getGreen(),
					color.getBlue());
			gc.setBackground(colorSWT);

			fillPath(path, gc);

			gc.setBackground(oldColor);
			colorSWT.dispose();
		} else if (fillMode instanceof ImageFill) {
			ImageFill m = (ImageFill) fillMode;

			Pattern oldPattern = gc.getBackgroundPattern();
			Pattern imagePattern = new Pattern(gc.getDevice(),
					SWTGraphicsUtils.createSWTImage(m.image()));
			gc.setBackgroundPattern(imagePattern);

			fillPath(path, gc);

			gc.setBackgroundPattern(oldPattern);
			imagePattern.dispose();
		}
		// else if (fillMode instanceof GradientFill.Linear) {
		// GradientFill.Linear m = (GradientFill.Linear) fillMode;
		//
		// GradientStop[] stops = m.getStops();
		// if (stops.length < 2) {
		// throw new IllegalStateException(
		// "A GradientFill requires at least 2 specified GradientStops.");
		// }
		//
		// // back-up GC's settings
		// Pattern oldPattern = gc.getBackgroundPattern();
		// Region oldClip = new Region();
		// gc.getClipping(oldClip);
		//
		// PathData swtPathData = Geometry2SWT.toSWTPathData(path);
		// org.eclipse.swt.graphics.Path swtPath = new
		// org.eclipse.swt.graphics.Path(
		// gc.getDevice(), swtPathData);
		//
		// Rectangle bounds = path.getBounds();
		//
		// Point start = m.getStart();
		// Point end = m.getEnd();
		// Vector direction = new Vector(start, end);
		// Vector normal = direction.getRotatedCCW(Angle.fromDeg(90));
		//
		// for (int i = 0; i < stops.length - 1; i++) {
		// GradientStop s0 = stops[i];
		// GradientStop s1 = stops[i + 1];
		//
		// Color c0 = s0.getColor();
		// Color c1 = s1.getColor();
		//
		// Point offset = start.getTranslated(direction.getMultiplied(
		// s0.getPercentualDistance()).toPoint());
		// Point stop = start.getTranslated(direction.getMultiplied(
		// s1.getPercentualDistance()).toPoint());
		//
		// Straight cutLine = new Straight(new Vector(offset), normal);
		// // TODO: find first two points of intersection
		//
		// cutLine = new Straight(new Vector(stop), normal);
		// // TODO: find next two points of intersection
		//
		// // TODO: construct clipping polygon from the points of
		// // intersection
		//
		// // TODO: intersect user's clipping region with the clipping
		// // polygon
		//
		// Pattern gradientPattern = new Pattern(gc.getDevice(),
		// (float) offset.x, (float) offset.y, (float) stop.x,
		// (float) stop.y, SWTGraphicsUtils.createSWTColor(c0),
		// c0.getAlpha(), SWTGraphicsUtils.createSWTColor(c1),
		// c1.getAlpha());
		//
		// // TODO: fill with gradientPattern
		// }
		//
		// // TODO: work out how to handle CycleMode
		//
		// // clean up
		// gc.setBackgroundPattern(oldPattern);
		// gc.setClipping(oldClip);
		// oldClip.dispose();
		// swtPath.dispose();
		// }
		else {
			super.generalFill(g, path);
		}
	}

	@Override
	public void cleanUp(IGraphics g) {
	}

	/**
	 * @param path
	 * @param gc
	 */
	private void fillPath(Path path, GC gc) {
		org.eclipse.swt.graphics.Path swtPath = new org.eclipse.swt.graphics.Path(
				Display.getCurrent(), Geometry2SWT.toSWTPathData(path));
		gc.fillPath(swtPath);
		swtPath.dispose();
	}

	@Override
	public IFillProperties getCopy() {
		SWTFillProperties copy = new SWTFillProperties();
		copy.setAntialiasing(isAntialiasing());
		copy.setMode(getMode());
		return copy;
	}

	@Override
	public void init(IGraphics g) {
	}

}
