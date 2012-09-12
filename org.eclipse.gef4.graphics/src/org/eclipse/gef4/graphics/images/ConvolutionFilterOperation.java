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

	@Override
	public int computePixel(int[] neighbors) {
		double aAvg, rAvg, gAvg, bAvg;
		aAvg = rAvg = gAvg = bAvg = 0d;

		for (int i = 0; i < kernel.length; i++) {
			// TODO: move channel decomposition to Utils class
			aAvg += kernel[i] * ((neighbors[i] & 0xff000000) >> 24);
			rAvg += kernel[i] * ((neighbors[i] & 0xff0000) >> 16);
			gAvg += kernel[i] * ((neighbors[i] & 0xff00) >> 8);
			bAvg += kernel[i] * (neighbors[i] & 0xff);
		}

		return (int) aAvg << 24 | (int) rAvg << 16 | (int) gAvg << 8
				| (int) bAvg;
	}

	public double[] getKernel() {
		return Arrays.copyOf(kernel, kernel.length);
	}

}
