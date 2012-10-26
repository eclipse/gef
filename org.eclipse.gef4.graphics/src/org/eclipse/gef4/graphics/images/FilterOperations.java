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

import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.images.AbstractPixelNeighborhoodFilterOperation.EdgeMode;

public class FilterOperations {

	public static IImageOperation getBoxBlur() {
		return getBoxBlur(3);
	}

	public static IImageOperation getBoxBlur(int dimension) {
		return getBoxBlur(dimension, new EdgeMode.NoOperation());
	}

	public static IImageOperation getBoxBlur(int dimension, EdgeMode edgeMode) {
		// TODO: Optimize box blurring by applying two one-dimensional blurs
		// sequentially, instead of one two-dimensional blur

		int size = dimension * dimension;
		double fraction = 1d / size;
		double[] kernel = new double[size];
		for (int i = 0; i < size; i++) {
			kernel[i] = fraction;
		}
		return new ConvolutionFilterOperation(dimension, edgeMode, kernel);
	}

	public static IImageOperation getConservativeBlur(final int dimension,
			final EdgeMode edgeMode) {
		return new AbstractPixelNeighborhoodFilterOperation(dimension, edgeMode) {
			@Override
			public int processNeighborhood(int[] neighbors) {
				int midIdx = neighbors.length / 2, midValue = Color
						.computePixelIntensity(neighbors[midIdx]), minIdx = 0, minValue = Color
						.computePixelIntensity(neighbors[minIdx]), maxIdx = 0, maxValue = Color
						.computePixelIntensity(neighbors[maxIdx]);

				for (int i = 1; i < neighbors.length; i++) {
					if (i == midIdx) {
						continue;
					}

					int currentValue = Color
							.computePixelIntensity(neighbors[i]);

					if (currentValue > maxValue) {
						maxValue = currentValue;
						maxIdx = i;
					} else if (currentValue < minValue) {
						minValue = currentValue;
						minIdx = i;
					}
				}

				return midValue < minValue ? neighbors[minIdx]
						: midValue > maxValue ? neighbors[maxIdx]
								: neighbors[midIdx];
			}
		};
	}

	public static IImageOperation getGaussianBlur() {
		return getGaussianBlur(1d);
	}

	public static IImageOperation getGaussianBlur(double standardDeviation) {
		return getGaussianBlur(standardDeviation, new EdgeMode.NoOperation());
	}

	public static IImageOperation getGaussianBlur(double standardDeviation,
			EdgeMode edgeMode) {
		// TODO: do not accept a standard deviation greater than X (X may be
		// 2.125 or something...) so that we do not run into floating point
		// difficulties.

		// TODO: Optimize Gaussian blur by applying two one-dimensional blurs
		// sequentially, instead of one two-dimensional blur

		// TODO: explain why we do dimension = 6*s
		int dimension = (int) Math.ceil(6d * standardDeviation);

		// ensure dimension is odd
		if (dimension % 2 == 0) {
			dimension++;
		}

		// compute kernel values from the two dimensional Gaussian function
		double s2 = standardDeviation * standardDeviation;
		double gaussianCoefficient = 1d / (2d * Math.PI * s2);
		double exponentDenominator = 2d * s2;

		double sum = 0d;
		double kernel[] = new double[dimension * dimension];
		for (int i = 0, y = -dimension / 2; y <= dimension / 2; y++) {
			for (int x = -dimension / 2; x <= dimension / 2; x++, i++) {
				kernel[i] = gaussianCoefficient
						* Math.pow(Math.E, (-x * x - y * y)
								/ exponentDenominator);
				sum += kernel[i];
			}
		}
		for (int i = 0; i < kernel.length; i++) {
			kernel[i] /= sum;
		}

		return new ConvolutionFilterOperation(dimension, edgeMode, kernel);
	}

	public static ConvolutionFilterOperation getLaplacian() {
		return new ConvolutionFilterOperation(3, new EdgeMode.NoOperation(),
				new double[] { -1, -1, -1, -1, 8, -1, -1, -1, -1 });
	}

	public static IImageOperation getMedianBlur(int dimension, EdgeMode edgeMode) {
		return new AbstractPixelNeighborhoodFilterOperation(dimension, edgeMode) {
			@Override
			public int processNeighborhood(int[] neighbors) {
				int alpha = Color
						.getPixelAlpha(neighbors[neighbors.length / 2]);
				int[] red = new int[neighbors.length];
				int[] green = new int[neighbors.length];
				int[] blue = new int[neighbors.length];

				for (int i = 0; i < neighbors.length; i++) {
					int[] argb = Color.getPixelARGB(neighbors[i]);
					red[i] = argb[1];
					green[i] = argb[2];
					blue[i] = argb[3];
				}

				Arrays.sort(red);
				Arrays.sort(green);
				Arrays.sort(blue);

				return Color
						.getPixel(alpha, red[neighbors.length / 2],
								green[neighbors.length / 2],
								blue[neighbors.length / 2]);
			}
		};
	}

}
