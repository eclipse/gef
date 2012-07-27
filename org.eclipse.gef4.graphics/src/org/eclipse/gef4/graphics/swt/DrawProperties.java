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
package org.eclipse.gef4.graphics.swt;

import org.eclipse.gef4.graphics.AbstractDrawProperties;
import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.LineAttributes;

public class DrawProperties extends AbstractDrawProperties {

	protected GCLazyProperty<Color, org.eclipse.swt.graphics.Color> drawColorProperty = new GCLazyProperty<Color, org.eclipse.swt.graphics.Color>() {
		@Override
		protected org.eclipse.swt.graphics.Color generate(Color data) {
			return Utils.createSWTColor(data);
		}

		@Override
		protected void write(GC gc, org.eclipse.swt.graphics.Color val) {
			gc.setForeground(val);
		}
	};
	private org.eclipse.swt.graphics.Color initialColor;

	public DrawProperties() {
		drawColorProperty.set(new Color());
	}

	public void applyOn(IGraphics g) {
		GC gc = ((DisplayGraphics) g).getGC();

		gc.setAntialias(antialiasing ? SWT.ON : SWT.OFF);
		gc.setLineAttributes(getSWTLineAttributes());

		drawColorProperty.apply(gc);
	}

	public void cleanUp(IGraphics g) {
		GC gc = ((DisplayGraphics) g).getGC();
		gc.setForeground(initialColor);
		drawColorProperty.clean();
	}

	public Color getColor() {
		return drawColorProperty.data.getCopy();
	}

	public DrawProperties getCopy() {
		DrawProperties copy = new DrawProperties();
		copy.antialiasing = antialiasing;
		copy.setColor(drawColorProperty.data);
		copy.setDashArray(dashArray);
		copy.setLineCap(lineCap);
		copy.setLineJoin(lineJoin);
		copy.setMiterLimit(miterLimit);
		copy.setLineWidth(lineWidth);
		return copy;
	}

	private LineAttributes getSWTLineAttributes() {
		float[] dashes = null;
		if (dashArray != null) {
			dashes = new float[dashArray.length];
			for (int i = 0; i < dashArray.length; i++) {
				dashes[i] = (float) dashArray[i];
			}
		}
		return new LineAttributes((float) lineWidth,
				lineCap == LineCap.FLAT ? SWT.CAP_FLAT
						: lineCap == LineCap.ROUND ? SWT.CAP_ROUND
								: SWT.CAP_SQUARE,
								lineJoin == LineJoin.BEVEL ? SWT.JOIN_BEVEL
										: lineJoin == LineJoin.ROUND ? SWT.JOIN_ROUND
												: SWT.JOIN_MITER, SWT.LINE_CUSTOM, dashes, 0,
												(float) miterLimit);
	}

	public void init(IGraphics g) {
		// abstract factory contract
		GC gc = ((DisplayGraphics) g).getGC();

		// read out initial values
		initialColor = gc.getForeground();
	}

	public DrawProperties setColor(Color drawColor) {
		drawColorProperty.set(drawColor);
		return this;
	}

}
