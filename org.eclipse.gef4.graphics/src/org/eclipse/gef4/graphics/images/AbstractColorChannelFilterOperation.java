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
package org.eclipse.gef4.graphics.images;

import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.Image;

abstract public class AbstractColorChannelFilterOperation extends
AbstractChannelFilterOperation {

	@Override
	protected int processPixel(int argb, int x, int y, Image input) {
		int[] argbIn = Color.getPixelARGB(argb);
		int[] argbOut = new int[4];
		argbOut[0] = argbIn[0]; // can't touch this
		for (int i = 1; i < Image.NUM_CHANNELS; i++) {
			argbOut[i] = Color.getChannelClamped(processChannel(argbIn[i], x,
					y, i, input));
		}
		return Color.getPixel(argbOut);
	}

}
