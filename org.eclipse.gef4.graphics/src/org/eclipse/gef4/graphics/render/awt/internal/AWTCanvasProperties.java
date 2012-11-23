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

import org.eclipse.gef4.geometry.convert.awt.Geometry2AWT;
import org.eclipse.gef4.graphics.render.AbstractCanvasProperties;
import org.eclipse.gef4.graphics.render.ICanvasProperties;
import org.eclipse.gef4.graphics.render.IGraphics;
import org.eclipse.gef4.graphics.render.awt.AWTGraphics;

/**
 * The AWT {@link ICanvasProperties} implementation.
 * 
 * @author mwienand
 * 
 */
public class AWTCanvasProperties extends AbstractCanvasProperties {

	/**
	 * Default constructor.
	 * 
	 * @see AbstractCanvasProperties#AbstractCanvasProperties()
	 */
	public AWTCanvasProperties() {
	}

	@Override
	public void applyOn(IGraphics graphics) {
		Graphics2D g = ((AWTGraphics) graphics).getGraphics2D();

		if (clippingArea != null) {
			g.setClip(Geometry2AWT.toAWTPath(clippingArea.toPath()));
		}
		g.setTransform(new java.awt.geom.AffineTransform(affineTransform
				.getMatrix()));
	}

	@Override
	public void cleanUp(IGraphics g) {
		// reset clip and transform
	}

	@Override
	public AWTCanvasProperties getCopy() {
		AWTCanvasProperties copy = new AWTCanvasProperties();
		copy.affineTransform = getAffineTransform();
		copy.clippingArea = getClippingArea();
		return copy;
	}

	@Override
	public void init(IGraphics g) {
		// read clip and transform
	}

}
