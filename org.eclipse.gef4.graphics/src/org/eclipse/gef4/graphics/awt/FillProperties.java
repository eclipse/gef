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

import java.awt.Graphics2D;
import java.awt.RenderingHints;

import org.eclipse.gef4.graphics.AbstractFillProperties;
import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.IFillProperties;
import org.eclipse.gef4.graphics.IGraphics;

/**
 * The AWT {@link IFillProperties} implementation.
 * 
 * @author mwienand
 * 
 */
public class FillProperties extends AbstractFillProperties {

	/**
	 * The {@link Color fill color} associated with this
	 * {@link AbstractFillProperties}.
	 */
	protected Color fillColor;

	/**
	 * Creates a new {@link FillProperties} with the
	 * {@link AbstractFillProperties#fillColor} set to the default color
	 * specified by the {@link IFillProperties} interface.
	 */
	public FillProperties() {
		fillColor = new Color();
	}

	public void applyOn(IGraphics graphics) {
		Graphics2D g = ((DisplayGraphics) graphics).getGraphics2D();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				antialiasing ? RenderingHints.VALUE_ANTIALIAS_ON
						: RenderingHints.VALUE_ANTIALIAS_OFF);

		g.setColor(Utils.toAWTColor(fillColor));
	}

	public void cleanUp(IGraphics g) {
		// TODO: reset to initial values
	}

	public Color getColor() {
		return fillColor.getCopy();
	}

	public FillProperties getCopy() {
		FillProperties copy = new FillProperties();
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
