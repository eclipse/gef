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

import org.eclipse.gef4.graphics.Image;

public abstract class AbstractChannelFilterOperation extends
AbstractPixelFilterOperation {

	protected abstract int processChannel(int v, int x, int y, int i, Image input);

	@Override
	protected int processPixel(int argb, int x, int y, Image input) {
		int[] argbIn = Utils.getARGB(argb);
		int[] argbOut = new int[4];
		for (int i = 0; i < Image.NUM_CHANNELS; i++) {
			argbOut[i] = Utils.getClamped(processChannel(argbIn[i], x, y, i,
					input));
		}
		return Utils.getPixel(argbOut);
	}

}
