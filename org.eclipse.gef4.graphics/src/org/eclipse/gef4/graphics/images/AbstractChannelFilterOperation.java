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

/**
 * The AbstractChannelFilterOperation implements the
 * {@link #processPixel(int, int, int, Image)} method as a template method. It
 * provides the decomposition of a pixel into its channels and passes the
 * extracted channel values along to the
 * {@link #processChannel(int, int, int, int, Image)} hook method which is
 * responsible for the computation of the new channel value.
 * 
 * @author mwienand
 * 
 */
public abstract class AbstractChannelFilterOperation extends
AbstractPixelFilterOperation {

	/**
	 * Returns the new value of the channel at index <i>i</i>. This method is
	 * called for every channel of each pixel in the processed {@link Image}.
	 * 
	 * @param v
	 *            the current value of the channel
	 * @param x
	 *            the x-coordinate of the processed pixel
	 * @param y
	 *            the y-coordinate of the processed pixel
	 * @param i
	 *            the index of the processed channel:
	 *            <ul>
	 *            <li><code>0</code>: alpha</li>
	 *            <li><code>1</code>: red</li>
	 *            <li><code>2</code>: green</li>
	 *            <li><code>3</code>: blue</li>
	 *            </ul>
	 * @param input
	 *            the processed {@link Image}
	 * @return the new value of the channel at index <i>i</i>
	 */
	protected abstract int processChannel(int v, int x, int y, int i, Image input);

	@Override
	protected int processPixel(int argb, int x, int y, Image input) {
		int[] argbIn = Color.getPixelARGB(argb);
		int[] argbOut = new int[4];
		for (int i = 0; i < Image.NUM_CHANNELS; i++) {
			argbOut[i] = Color.getChannelClamped(processChannel(argbIn[i], x,
					y, i, input));
		}
		return Color.getPixel(argbOut);
	}

}
