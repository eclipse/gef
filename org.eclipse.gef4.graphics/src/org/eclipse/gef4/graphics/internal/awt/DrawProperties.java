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
package org.eclipse.gef4.graphics.internal.awt;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import org.eclipse.gef4.geometry.convert.Geometry2AWT;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.graphics.AbstractDrawProperties;
import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.IDrawProperties;
import org.eclipse.gef4.graphics.IGraphics;

/**
 * The AWT {@link IDrawProperties} implementation.
 * 
 * @author mwienand
 * 
 */
public class DrawProperties extends AbstractDrawProperties {

	/**
	 * The {@link Color draw color} associated with this
	 * {@link AbstractDrawProperties}.
	 */
	protected Color drawColor;

	/**
	 * Creates a new {@link DrawProperties} with the {@link #drawColor} set to
	 * the default color specified by the {@link IDrawProperties} interface.
	 */
	public DrawProperties() {
		drawColor = new Color();
	}

	public void applyOn(IGraphics graphics, Path path) {
		Graphics2D g = ((DisplayGraphics) graphics).getGraphics2D();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				antialiasing ? RenderingHints.VALUE_ANTIALIAS_ON
						: RenderingHints.VALUE_ANTIALIAS_OFF);

		java.awt.Color awtColor = Utils.toAWTColor(drawColor);
		g.setColor(awtColor);

		g.setStroke(getAWTStroke());

		g.draw(Geometry2AWT.toAWTPath(path));
	}

	public void cleanUp(IGraphics g) {
		// TODO Auto-generated method stub

	}

	private BasicStroke getAWTStroke() {
		float[] dashes = null;
		if (dashArray != null) {
			dashes = new float[dashArray.length];
			for (int i = 0; i < dashArray.length; i++) {
				dashes[i] = (float) dashArray[i];
			}
		}

		return new BasicStroke((float) lineWidth,
				lineCap == LineCap.FLAT ? BasicStroke.CAP_BUTT
						: lineCap == LineCap.ROUND ? BasicStroke.CAP_ROUND
								: BasicStroke.CAP_SQUARE,
								lineJoin == LineJoin.BEVEL ? BasicStroke.JOIN_BEVEL
										: lineJoin == LineJoin.MITER ? BasicStroke.JOIN_MITER
												: BasicStroke.JOIN_ROUND, (float) miterLimit,
												dashes, (float) dashBegin);
	}

	public Color getColor() {
		return drawColor.getCopy();
	}

	public DrawProperties getCopy() {
		DrawProperties copy = new DrawProperties();
		copy.antialiasing = antialiasing;
		copy.setColor(drawColor);
		copy.setDashArray(dashArray);
		copy.setDashBegin(dashBegin);
		copy.setLineCap(lineCap);
		copy.setLineJoin(lineJoin);
		copy.setMiterLimit(miterLimit);
		copy.setLineWidth(lineWidth);
		return copy;
	}

	public void init(IGraphics g) {
		// TODO Auto-generated method stub

	}

	public IDrawProperties setColor(Color drawColor) {
		this.drawColor.setTo(drawColor);
		return this;
	}

}
