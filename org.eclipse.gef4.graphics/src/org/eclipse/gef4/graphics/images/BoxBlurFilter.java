package org.eclipse.gef4.graphics.images;


public class BoxBlurFilter extends ConvolutionFilter {

	private static double[] computeKernel(int size) {
		int kernelLength = size * size;
		double value = 1d / kernelLength;
		double[] kernel = new double[kernelLength];
		for (int i = 0; i < kernelLength; i++) {
			kernel[i] = value;
		}
		return kernel;
	}

	public BoxBlurFilter(int size, EdgeMode edgeMode) {
		this(size, edgeMode, computeKernel(size));
	}

	private BoxBlurFilter(int dimension, EdgeMode edgeMode, double[] kernel) {
		super(dimension, edgeMode, kernel);
	}

}
