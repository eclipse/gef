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

import org.eclipse.gef4.graphics.AbstractBlitProperties;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.Image;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;

public class BlitProperties extends AbstractBlitProperties {

	public BlitProperties() {
	}

	public void applyOn(IGraphics g, Image image) {
		GC gc = ((DisplayGraphics) g).getGC();
		switch (interpolationHint) {
		case SPEED:
			gc.setInterpolation(SWT.LOW);
			break;
		case QUALITY:
			gc.setInterpolation(SWT.HIGH);
			break;
		}

		org.eclipse.swt.graphics.Image swtImage = Utils.createSWTImage(image);
		gc.drawImage(swtImage, 0, 0);
		swtImage.dispose();
	}

	public void cleanUp(IGraphics g) {
		// TODO: reset to the original values
	}

	public BlitProperties getCopy() {
		BlitProperties copy = new BlitProperties();
		copy.setInterpolationHint(interpolationHint);
		return copy;
	}

	public void init(IGraphics g) {
		// TODO: read out the initial values
	}

}
