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
import java.awt.RenderingHints;

import org.eclipse.gef4.geometry.convert.awt.Geometry2AWT;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.render.AbstractFillProperties;
import org.eclipse.gef4.graphics.render.IFillProperties;
import org.eclipse.gef4.graphics.render.IGraphics;
import org.eclipse.gef4.graphics.render.awt.AWTGraphics;

/**
 * The AWT {@link IFillProperties} implementation.
 * 
 * @author mwienand
 * 
 */
public class AWTFillProperties extends AbstractFillProperties {

	/**
	 * The {@link Color fill color} associated with this
	 * {@link AbstractFillProperties}.
	 */
	private Color fillColor;

	/**
	 * Creates a new {@link AWTFillProperties} with the {@link #fillColor} set
	 * to the default color specified by the {@link IFillProperties} interface.
	 */
	public AWTFillProperties() {
		fillColor = new Color();
	}

	public void applyOn(IGraphics graphics, Path path) {
		Graphics2D g = ((AWTGraphics) graphics).getGraphics2D();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				antialiasing ? RenderingHints.VALUE_ANTIALIAS_ON
						: RenderingHints.VALUE_ANTIALIAS_OFF);

		g.setColor(AWTGraphicsUtils.toAWTColor(fillColor));

		g.fill(Geometry2AWT.toAWTPath(path));
	}

	public void cleanUp(IGraphics g) {
		// TODO: reset to initial values
	}

	public Color getColor() {
		return fillColor.getCopy();
	}

	public AWTFillProperties getCopy() {
		AWTFillProperties copy = new AWTFillProperties();
		copy.fillColor = fillColor.getCopy();
		return copy;
	}

	public void init(IGraphics g) {
		// TODO: read out initial values
	}

	public IFillProperties setColor(Color fillColor) {
		this.fillColor.setTo(fillColor);
		return this;
	}

}
