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
 * The PixelOperations class contains construction methods for grayscale and
 * threshold {@link IImageOperation}s.
 * 
 * @author mwienand
 * 
 */
public class PixelOperations {

	/**
	 * Creates a new grayscale {@link IImageOperation}. A grayscale computes an
	 * intensity value for every pixel of an {@link Image}. The red, green, and
	 * blue channel values of one pixel are replaced by the pixels intensity
	 * value.
	 * 
	 * @return a new grayscale {@link IImageOperation}
	 */
	public static AbstractPixelFilterOperation getGreyScaleOperation() {
		return getGreyScaleOperation(0.3333, 0.3334, 0.3333);
	}

	/**
	 * Creates a new grayscale {@link IImageOperation} with the provided scale
	 * factors for the red, green, and blue channels. A grayscale
	 * {@link IImageOperation} computes an intensity value for every pixel of an
	 * {@link Image}. The red, green, and blue channel values of one pixel are
	 * replaced by the pixels intensity value.
	 * 
	 * @param sr
	 *            the red channel scale factor
	 * @param sg
	 *            the green channel scale factor
	 * @param sb
	 *            the blue channel scale factor
	 * @return a new grayscale {@link IImageOperation} with the provided scale
	 *         factors
	 */
	public static AbstractPixelFilterOperation getGreyScaleOperation(
			final double sr, final double sg, final double sb) {
		return new AbstractPixelFilterOperation() {
			@Override
			protected int processPixel(int pixel, int x, int y, Image input) {
				int[] rgba = Color.getPixelRGBA(pixel);
				int intensity = Color.computePixelIntensity(rgba, sr, sg, sb);
				return Color.getPixel(intensity, intensity, intensity, rgba[3]);
			}
		};
	}

	/**
	 * <p>
	 * Creates a new threshold {@link IImageOperation}.
	 * </p>
	 * 
	 * <p>
	 * A threshold {@link IImageOperation} classifies each pixel of an
	 * {@link Image} into either being a foreground, or a background pixel
	 * depending on the pixel's intensity value. If the intensity of a pixel is
	 * greater than or equal to the passed-in intensity threshold, the pixel is
	 * set to white. Otherwise, the pixel is set to black.
	 * </p>
	 * 
	 * @param intensityThreshold
	 * @return a new threshold {@link IImageOperation}
	 */
	public static AbstractPixelFilterOperation getThresholdOperation(
			final int intensityThreshold) {
		return new AbstractPixelFilterOperation() {
			@Override
			protected int processPixel(int pixel, int x, int y, Image input) {
				int[] rgba = Color.getPixelRGBA(pixel);
				int intensity = Color.computePixelIntensity(rgba);
				if (intensity >= intensityThreshold) {
					return Color.getPixel(0xff, 0xff, 0xff, rgba[3]);
				} else {
					return Color.getPixel(0, 0, 0, rgba[3]);
				}
			}
		};
	}

}
