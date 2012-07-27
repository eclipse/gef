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

import org.eclipse.gef4.graphics.AbstractBlitProperties;
import org.eclipse.gef4.graphics.IBlitProperties;
import org.eclipse.gef4.graphics.IGraphics;

/**
 * The AWT {@link IBlitProperties} implementation.
 * 
 * @author mwienand
 * 
 */
public class BlitProperties extends AbstractBlitProperties {

	/**
	 * Default constructor.
	 * 
	 * @see AbstractBlitProperties#AbstractBlitProperties()
	 */
	public BlitProperties() {
	}

	public void applyOn(IGraphics g) {
		Graphics2D g2d = ((DisplayGraphics) g).getGraphics2D();
		switch (interpolationHint) {
		case SPEED:
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			break;
		case QUALITY:
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			break;
		}
	}

	public void cleanUp(IGraphics g) {
		// TODO Auto-generated method stub

	}

	public BlitProperties getCopy() {
		BlitProperties copy = new BlitProperties();
		copy.setInterpolationHint(interpolationHint);
		return copy;
	}

	public void init(IGraphics g) {
		// TODO Auto-generated method stub

	}

}
