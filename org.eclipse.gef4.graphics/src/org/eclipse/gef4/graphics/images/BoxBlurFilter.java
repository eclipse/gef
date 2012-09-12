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


public class BoxBlurFilter extends ConvolutionFilterOperation {

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
