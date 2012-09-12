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

public class PixelOperations {

	public static IImageOperation getGreyScaleOperation() {
		return getGreyScaleOperation(0.333, 0.334, 0.333);
	}

	public static IImageOperation getGreyScaleOperation(final double sr, final double sg, final double sb) {
		return new AbstractPixelFilterOperation() {
			@Override
			protected int processPixel(int pixel, int x, int y, Image input) {
				int[] argb = Utils.getARGB(pixel);
				int intensity = (int) (sr * argb[1] + sg * argb[2] + sb
						* argb[3]);
				return Utils.getPixel(argb[0], intensity, intensity, intensity);
			}
		};
	}

	public static IImageOperation getThresholdOperation(
			final int intensityThreshold) {
		return new AbstractPixelFilterOperation() {
			@Override
			protected int processPixel(int pixel, int x, int y, Image input) {
				int[] argb = Utils.getARGB(pixel);
				int intensity = Utils.computeIntensity(argb);
				if (intensity >= intensityThreshold) {
					return Utils.getPixel(argb[0], 0xff, 0xff, 0xff);
				} else {
					return Utils.getPixel(argb[0], 0, 0, 0);
				}
			}
		};
	}

}
