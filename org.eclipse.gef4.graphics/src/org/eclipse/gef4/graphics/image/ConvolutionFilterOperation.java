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
package org.eclipse.gef4.graphics.image;

import java.util.Arrays;

import org.eclipse.gef4.graphics.color.Color;

public class ConvolutionFilterOperation extends
		AbstractPixelNeighborhoodFilterOperation {

	private final double[] kernel;

	public ConvolutionFilterOperation(final int dimension,
			final EdgeMode edgeMode, final double... kernel) {
		super(dimension, edgeMode);

		int size = dimension * dimension;
		if (kernel.length != size) {
			throw new IllegalArgumentException(
					"The kernel dimension does not match the actual kernel size. Given dimension = "
							+ dimension
							+ "x"
							+ dimension
							+ ", kerne = "
							+ Arrays.toString(kernel) + ".");
		}
		this.kernel = Arrays.copyOf(kernel, size);
	}

	public double[] getKernel() {
		return Arrays.copyOf(kernel, kernel.length);
	}

	@Override
	public int processNeighborhood(int[] neighborhoodPixels) {
		double alpha = Color
				.getPixelAlpha(neighborhoodPixels[neighborhoodPixels.length / 2]);
		double red = 0, green = 0, blue = 0;

		for (int i = 0; i < kernel.length; i++) {
			int[] neighborhoodPixel = Color.getPixelRGBA(neighborhoodPixels[i]);
			red += kernel[i] * neighborhoodPixel[0];
			green += kernel[i] * neighborhoodPixel[1];
			blue += kernel[i] * neighborhoodPixel[2];
		}

		return Color.getPixel(Color.getChannelClamped((int) red),
				Color.getChannelClamped((int) green),
				Color.getChannelClamped((int) blue),
				Color.getChannelClamped((int) alpha));
	}

}
