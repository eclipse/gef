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
import org.eclipse.gef4.graphics.render.GradientFill;
import org.eclipse.gef4.graphics.render.GradientFill.GradientStop;
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
		} else if (fillMode instanceof GradientFill.Linear) {
			GradientFill.Linear m = (GradientFill.Linear) fillMode;
			GradientStop[] stops = normalize(m.getStops());

			if (stops.length == 2) {
				org.eclipse.swt.graphics.Color swtColorFrom = SWTGraphicsUtils
						.createSWTColor(stops[0].getColor());
				org.eclipse.swt.graphics.Color swtColorTo = SWTGraphicsUtils
						.createSWTColor(stops[1].getColor());

				Pattern gradientPattern = new Pattern(gc.getDevice(),
						(float) m.getStart().x, (float) m.getStart().y,
						(float) m.getEnd().x, (float) m.getEnd().y,
						swtColorFrom, stops[0].getColor().getAlpha(),
						swtColorTo, stops[1].getColor().getAlpha());

				Pattern oldPattern = gc.getBackgroundPattern();
				gc.setBackgroundPattern(gradientPattern);

				fillPath(path, gc);

				// clean up
				gc.setBackgroundPattern(oldPattern);
				swtColorFrom.dispose();
				swtColorTo.dispose();
			} else {
				super.generalFill(g, path);
			}
		} else {
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

	private GradientStop[] normalize(GradientStop[] stops) {
		boolean addFrom = stops[0].getPercentualDistance() > 0;
		boolean addTo = stops[stops.length - 1].getPercentualDistance() < 1;

		GradientStop[] normalizedStops = new GradientStop[stops.length
				+ (addFrom ? 1 : 0) + (addTo ? 1 : 0)];

		if (addFrom) {
			normalizedStops[0] = new GradientStop(0, stops[0].getColor());
		}

		System.arraycopy(stops, 0, normalizedStops, addFrom ? 1 : 0,
				stops.length);

		if (addTo) {
			normalizedStops[normalizedStops.length - 1] = new GradientStop(1,
					stops[stops.length - 1].getColor());
		}

		return normalizedStops;
	}
}
