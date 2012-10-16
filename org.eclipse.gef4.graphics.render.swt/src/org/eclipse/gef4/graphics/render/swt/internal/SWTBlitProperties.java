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

import org.eclipse.gef4.graphics.Image;
import org.eclipse.gef4.graphics.render.AbstractBlitProperties;
import org.eclipse.gef4.graphics.render.IGraphics;
import org.eclipse.gef4.graphics.render.swt.SWTGraphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;

public class SWTBlitProperties extends AbstractBlitProperties {

	public SWTBlitProperties() {
	}

	public void applyOn(IGraphics g, Image image) {
		GC gc = ((SWTGraphics) g).getGC();
		switch (interpolationHint) {
		case SPEED:
			gc.setInterpolation(SWT.LOW);
			break;
		case QUALITY:
			gc.setInterpolation(SWT.HIGH);
			break;
		}

		org.eclipse.swt.graphics.Image swtImage = SWTGraphicsUtils.createSWTImage(image);
		gc.drawImage(swtImage, 0, 0);
		swtImage.dispose();
	}

	public void cleanUp(IGraphics g) {
		// TODO: reset to the original values
	}

	public SWTBlitProperties getCopy() {
		SWTBlitProperties copy = new SWTBlitProperties();
		copy.setInterpolationHint(interpolationHint);
		return copy;
	}

	public void init(IGraphics g) {
		// TODO: read out the initial values
	}

}
