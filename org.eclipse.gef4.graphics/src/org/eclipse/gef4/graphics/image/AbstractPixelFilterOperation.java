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

/**
 * The AbstractPixelFilterOperation implements the {@link #apply(Image)} method
 * as a template method. For every pixel in the processed {@link Image}, the
 * {@link #processPixel(int, int, int, Image)} hook method is used to compute
 * the new pixel value.
 * 
 * @author mwienand
 * 
 */
public abstract class AbstractPixelFilterOperation implements IImageOperation {

	@Override
	public Image apply(Image input) {
		Image output = input.getCopy();

		for (int x = 0; x < input.getWidth(); x++) {
			/*
			 * Optimization notice: We can avoid many getPixel(x,y) and
			 * setPixel(x,y) calls by fetching multiple pixels at once.
			 * Balancing memory usage and performance may be achieved by
			 * fetching a full row of pixels instead of fetching single or all
			 * pixels.
			 * 
			 * The Image class should provide a method to accelerated fetch
			 * multiple pixels, for example, getRow(int).
			 */
			for (int y = 0; y < input.getHeight(); y++) {
				output.setPixel(x, y,
						processPixel(input.getPixel(x, y), x, y, input));
			}
		}

		return output;
	}

	/**
	 * Computes the new pixel value for the currently processed pixel. This
	 * method is called for every pixel in the processed {@link Image}.
	 * 
	 * @param pixel
	 *            the current ARGB pixel value
	 * @param x
	 *            the x-coordinate of the processed pixel
	 * @param y
	 *            the y-coordinate of the processed pixel
	 * @param input
	 *            the processed {@link Image}
	 * @return the new pixel value for the currently processed pixel
	 */
	protected abstract int processPixel(int pixel, int x, int y, Image input);

}
