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

import org.eclipse.gef4.geometry.convert.Geometry2AWT;
import org.eclipse.gef4.graphics.AbstractCanvasProperties;
import org.eclipse.gef4.graphics.ICanvasProperties;
import org.eclipse.gef4.graphics.IGraphics;

/**
 * The AWT {@link ICanvasProperties} implementation.
 * 
 * @author mwienand
 * 
 */
public class CanvasProperties extends AbstractCanvasProperties {

	/**
	 * Default constructor.
	 * 
	 * @see AbstractCanvasProperties#AbstractCanvasProperties()
	 */
	public CanvasProperties() {
	}

	public void applyOn(IGraphics graphics) {
		Graphics2D g = ((DisplayGraphics) graphics).getGraphics2D();

		if (clippingArea != null) {
			g.setClip(Geometry2AWT.toAWTPath(clippingArea.toPath()));
		}
		g.setTransform(new java.awt.geom.AffineTransform(affineTransform
				.getMatrix()));
	}

	public void cleanUp(IGraphics g) {
		// TODO Auto-generated method stub

	}

	public CanvasProperties getCopy() {
		CanvasProperties copy = new CanvasProperties();
		copy.affineTransform = getAffineTransform();
		copy.clippingArea = getClippingArea();
		return copy;
	}

	public void init(IGraphics g) {
		// TODO Auto-generated method stub

	}

}
