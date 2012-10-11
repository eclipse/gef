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

import java.util.Arrays;

/**
 * A ConvolutionFilterOperation computes...
 * 
 * @author mwienand
 * 
 */
public class ConvolutionFilterOperation extends AbstractPixelNeighborhoodFilterOperation {

	protected final double[] kernel;

	public ConvolutionFilterOperation(final int dimension, final EdgeMode edgeMode,
			final double... kernel) {
		super(dimension, edgeMode);

		int size = dimension * dimension;
		if (kernel.length != size) {
			throw new IllegalArgumentException(
					"The kernel dimension does not match the actual kernel size. Given dimension = " + dimension + "x" + dimension + ", kerne = "
							+ Arrays.toString(kernel) + ".");
		}
		this.kernel = Arrays.copyOf(kernel, size);
	}

	public double[] getKernel() {
		return Arrays.copyOf(kernel, kernel.length);
	}

	@Override
	public int processNeighborhood(int[] neighborhoodPixels) {
		double alpha = Utils
				.getAlpha(neighborhoodPixels[neighborhoodPixels.length / 2]);
		double red = 0, green = 0, blue = 0;

		for (int i = 0; i < kernel.length; i++) {
			int[] neighborhoodPixel = Utils.getARGB(neighborhoodPixels[i]);
			red += kernel[i] * neighborhoodPixel[1];
			green += kernel[i] * neighborhoodPixel[2];
			blue += kernel[i] * neighborhoodPixel[3];
		}

		return Utils.getPixel(Utils.getClamped((int) alpha),
				Utils.getClamped((int) red), Utils.getClamped((int) green),
				Utils.getClamped((int) blue));
	}

}
