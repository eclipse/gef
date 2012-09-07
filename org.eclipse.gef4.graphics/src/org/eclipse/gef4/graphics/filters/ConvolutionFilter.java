package org.eclipse.gef4.graphics.filters;

import java.util.Arrays;

public class ConvolutionFilter implements IImageFilter {

	protected int dimension;
	protected double[] kernel;

	public ConvolutionFilter(int dimension, double... kernel) {
		if (dimension < 3 || dimension % 2 == 0) {
			throw new IllegalArgumentException(
					"The kernel dimension may only be positive and odd. Given dimension = " + dimension + ".");
		}
		this.dimension = dimension;
		int size = dimension * dimension;
		if (kernel.length != size) {
			throw new IllegalArgumentException(
					"The kernel dimension does not match the actual kernel size. Given dimension = " + dimension + "x" + dimension + ", kerne = "
							+ Arrays.toString(kernel) + ".");
		}
		this.kernel = Arrays.copyOf(kernel, size);
	}

	public int getDimension() {
		return dimension;
	}

	public double[] getKernel() {
		return Arrays.copyOf(kernel, kernel.length);
	}

}
