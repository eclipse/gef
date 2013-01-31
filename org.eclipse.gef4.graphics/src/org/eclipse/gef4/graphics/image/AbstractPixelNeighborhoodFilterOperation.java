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

import java.awt.image.BufferedImage;
import java.util.Arrays;


/**
 * The AbstractPixelNeighborhoodFilterOperation implements the
 * {@link IImageOperation} interface as a template method ({@link #apply(Image)}
 * ) which can be refined by the {@link #processNeighborhood(int[])} hook
 * method.
 * 
 * @author mwienand
 * 
 */
public abstract class AbstractPixelNeighborhoodFilterOperation implements IImageOperation {

	/**
	 * <p>
	 * The EdgeMode specifies the behavior of an
	 * {@link AbstractPixelNeighborhoodFilterOperation} when filtering the edges of the
	 * {@link Image} being processed.
	 * </p>
	 * 
	 * <p>
	 * At the edges of the {@link Image} being processed, the neighborhood
	 * matrix cannot be fully applied, because some neighbor pixels are missing.
	 * In such cases, the behavior of an {@link AbstractPixelNeighborhoodFilterOperation} can
	 * be altered. The following EdgeMode implementations are provided by
	 * default:
	 * <ul>
	 * <li>{@link EdgeMode.NoOperation}</li>
	 * <li>{@link EdgeMode.ConstantPixel}</li>
	 * <li>{@link EdgeMode.Overlap}</li>
	 * <li>{@link EdgeMode.ConstantPixelNeighbors}</li>
	 * </ul>
	 * </p>
	 * 
	 * @author mwienand
	 * 
	 */
	public abstract static class EdgeMode {

		/**
		 * <p>
		 * Sets the "edge" pixels in the destination {@link Image} to a constant
		 * color. Therefore, a border is created around the filtered part of the
		 * {@link Image}.
		 * </p>
		 * 
		 * @author mwienand
		 * 
		 */
		public static class ConstantPixel extends EdgeMode {
			/**
			 * Fully transparent black. (ARGB)
			 */
			public static final int DEFAULT_COLOR = 0;

			private int color = DEFAULT_COLOR;

			/**
			 * Constructs a new {@link ConstantPixel} {@link EdgeMode} with the
			 * constant pixel color set to the {@link #DEFAULT_COLOR}.
			 */
			public ConstantPixel() {
			}

			/**
			 * Constructs a new {@link ConstantPixel} {@link EdgeMode} with the
			 * constant pixel color set to the passed-in ARGB pixel value.
			 * 
			 * @param pixel
			 *            the ARGB pixel value used as the constant pixel color
			 */
			public ConstantPixel(int pixel) {
				color = pixel;
			}

			@Override
			public void fillNeighbors(AbstractPixelNeighborhoodFilterOperation op,
					Image src,
					int x, int y, int[] neighbors) {
				// we do not need to inspect the neighbors
			}

			/**
			 * Returns the current color value that is used to compensate the
			 * absence of neighborhood pixels.
			 * 
			 * @return the current color value that is used to compensate the
			 *         absence of neighborhood pixels
			 */
			public int getColor() {
				return color;
			}

			@Override
			public int processNeighborhodd(
					AbstractPixelNeighborhoodFilterOperation op, int[] neighbors) {
				return color;
			}

			/**
			 * Sets the current color value that is used to compensate the
			 * absence of neighborhood pixels.
			 * 
			 * @param pixel
			 *            the new color value that is used to compensate the
			 *            absence of neighborhood pixels
			 * @return <code>this</code> for convenience
			 */
			protected ConstantPixel setColor(int pixel) {
				color = pixel;
				return this;
			}
		}

		/**
		 * The absence of pixel-values outside of the {@link Image} is
		 * compensated by using a constant pixel value. Therefore, a thin border
		 * is created around the correctly filtered part of the {@link Image}.
		 * 
		 * @author mwienand
		 * 
		 */
		public static class ConstantPixelNeighbors extends EdgeMode {
			/**
			 * Fully opaque white. (ARGB)
			 */
			public static final int DEFAULT_COLOR = 0xffffffff;

			private int color = DEFAULT_COLOR;

			/**
			 * Constructs a new {@link ConstantPixelNeighbors} {@link EdgeMode}
			 * with the constant pixel neighbor color set to the
			 * {@link #DEFAULT_COLOR}.
			 */
			public ConstantPixelNeighbors() {
			}

			/**
			 * Constructs a new {@link ConstantPixelNeighbors} {@link EdgeMode}
			 * with the constant pixel neighbor color set to the passed-in ARGB
			 * pixel value.
			 * 
			 * @param pixel
			 *            the ARGB pixel value used as the constant pixel
			 *            neighbor color
			 */
			public ConstantPixelNeighbors(int pixel) {
				color = pixel;
			}

			@Override
			public void fillNeighbors(AbstractPixelNeighborhoodFilterOperation op,
					Image src,
					int x, int y, int[] neighbors) {
				int w = src.getWidth();
				int h = src.getHeight();
				int over = op.getDimension() / 2;
				int i = 0;
				for (int xx = x - over; xx <= x + over; xx++) {
					if (xx < 0 || xx >= w) {
						for (int yy = y - over; yy <= y + over; yy++) {
							neighbors[i++] = color;
						}
					} else {
						for (int yy = y - over; yy <= y + over; yy++) {
							if (yy < 0 || yy >= h) {
								neighbors[i++] = color;
							} else {
								neighbors[i++] = src.getPixel(xx, yy);
							}
						}
					}
				}
			}

			/**
			 * Returns the current color value that is used to compensate the
			 * absence of neighborhood pixels.
			 * 
			 * @return the current color value that is used to compensate the
			 *         absence of neighborhood pixels
			 */
			public int getColor() {
				return color;
			}

			@Override
			public int processNeighborhodd(AbstractPixelNeighborhoodFilterOperation op, int[] neighbors) {
				return op.processNeighborhood(neighbors);
			}

			/**
			 * Sets the current color value that is used to compensate the
			 * absence of neighborhood pixels.
			 * 
			 * @param pixel
			 *            the new color value that is used to compensate the
			 *            absence of neighborhood pixels
			 * @return <code>this</code> for convenience
			 */
			protected ConstantPixelNeighbors setColor(int pixel) {
				color = pixel;
				return this;
			}
		}

		/**
		 * Sets the "edge" pixels in the destination {@link Image} to the exact
		 * same pixels as in the source {@link Image}.
		 * 
		 * @author mwienand
		 * 
		 */
		public static class NoOperation extends EdgeMode {
			@Override
			public void fillNeighbors(AbstractPixelNeighborhoodFilterOperation op,
					Image src,
					int x, int y, int[] neighbors) {
				neighbors[0] = src.getPixel(x, y);
			}

			@Override
			public int processNeighborhodd(AbstractPixelNeighborhoodFilterOperation op, int[] neighbors) {
				return neighbors[0];
			}
		}

		/**
		 * The absence of pixel-values outside of the {@link Image} is
		 * compensated by using the values of the pixels of the opposite edge.
		 * Therefore, you can use this {@link EdgeMode} to correctly filter the
		 * polar transform of an {@link Image}.
		 * 
		 * @author mwienand
		 * 
		 */
		public static class Overlap extends EdgeMode {
			@Override
			public void fillNeighbors(AbstractPixelNeighborhoodFilterOperation op,
					Image src,
					int x, int y, int[] neighbors) {
				int w = src.getWidth();
				int h = src.getHeight();
				int over = op.getDimension() / 2;
				int i = 0;
				for (int xx = x - over; xx <= x + over; xx++) {
					int x2 = xx < 0 ? w + xx : xx >= w ? xx - w : xx;
					for (int yy = y - over; yy <= y + over; yy++) {
						int y2 = yy < 0 ? h + yy : yy >= h ? yy - h : yy;
						neighbors[i++] = src.getPixel(x2, y2);
					}
				}
			}

			@Override
			public int processNeighborhodd(AbstractPixelNeighborhoodFilterOperation op, int[] neighbors) {
				return op.processNeighborhood(neighbors);
			}
		}

		/**
		 * <p>
		 * This method is called for every "edge" pixel of an {@link Image}. It
		 * fills the passed-in <i>neighbors</i> array with appropriate ARGB
		 * pixel values.
		 * </p>
		 * 
		 * <p>
		 * The <i>neighbors</i> array is a one-dimensional <code>int</code>
		 * array that is used to store the pixel neighborhood at a specific
		 * position, so that further methods can base their calculations on that
		 * neighborhood.
		 * </p>
		 * 
		 * <p>
		 * Example implementation for non-"edge" pixels:
		 * {@link AbstractPixelNeighborhoodFilterOperation#fillNeighbors(Image, int, int)
		 * fillNeighbors}.
		 * </p>
		 * 
		 * <p>
		 * Example implemenation for {@link ConstantPixelNeighbors}:
		 * {@link ConstantPixelNeighbors#fillNeighbors(AbstractPixelNeighborhoodFilterOperation, Image, int, int, int[])
		 * fillNeighbors}.
		 * </p>
		 * 
		 * @param op
		 *            the applied
		 *            {@link AbstractPixelNeighborhoodFilterOperation}
		 * @param image
		 *            the processed {@link BufferedImage} (TODO: Make this a
		 *            GEF4 {@link Image}.)
		 * @param x
		 *            the x-coordinate of the processed pixel
		 * @param y
		 *            the y-coordinate of the processed pixel
		 * @param neighbors
		 *            the <code>int</code> array used to store the pixel
		 *            neighborhood at a the currently processed position
		 */
		public abstract void fillNeighbors(AbstractPixelNeighborhoodFilterOperation op,
				Image image,
				int x, int y, int[] neighbors);

		/**
		 * Returns the new ARGB pixel value for the passed-in
		 * {@link AbstractPixelNeighborhoodFilterOperation} and the passed-in
		 * <i>neighbors</i> array.
		 * 
		 * @param op
		 *            the currently applied
		 *            {@link AbstractPixelNeighborhoodFilterOperation}
		 * @param neighbors
		 *            the neighborhood pixel values for which the new ARGB pixel
		 *            value is to be calculated
		 * @return the new ARGB pixel value for the passed-in <i>neighbors</i>
		 *         array
		 */
		public abstract int processNeighborhodd(AbstractPixelNeighborhoodFilterOperation op,
				int[] neighbors);

	}

	/**
	 * The dimension of the neighborhood matrix.
	 */
	private int dimension;

	/**
	 * Determines how to filter the edges of an {@link Image}.
	 */
	private EdgeMode edgeMode;

	/**
	 * The neighborhood matrix.
	 */
	private int[] neighbors;

	/**
	 * Constructs a new {@link AbstractPixelNeighborhoodFilterOperation} with the given
	 * {@link #neighbors neighborhood matrix} dimension and {@link EdgeMode}.
	 * 
	 * @param dimension
	 *            the dimension of the neighborhood matrix
	 * @param edgeMode
	 *            determines how to filter edges of an {@link Image}
	 */
	public AbstractPixelNeighborhoodFilterOperation(final int dimension,
			final EdgeMode edgeMode) {
		if (dimension < 1 || dimension % 2 == 0) {
			throw new IllegalArgumentException(
					"The kernel dimension may only be positive and odd. Given dimension = "
							+ dimension + ".");
		}
		this.edgeMode = edgeMode;
		this.dimension = dimension;
		this.neighbors = new int[dimension * dimension];
	}

	public Image apply(final Image image) {
		Image res = image.getCopy();
		filterMiddle(image, res);
		filterEdges(image, res);
		return res;
	}

	/**
	 * <p>
	 * This method is called for every non-"edge" pixel of the processed
	 * {@link Image}. It fills the passed-in <i>neighbors</i> array with
	 * appropriate ARGB pixel values.
	 * </p>
	 * 
	 * <p>
	 * The <i>neighbors</i> array is a one-dimensional <code>int</code> array
	 * that is used to store the pixel neighborhood at a specific position, so
	 * that further methods can base their calculations on that neighborhood.
	 * </p>
	 * 
	 * @param image
	 *            the processed {@link BufferedImage} (TODO: Make this a GEF4
	 *            {@link Image}.)
	 * @param x
	 *            the x-coordinate of the processed pixel
	 * @param y
	 *            the y-coordinate of the processed pixel
	 */
	protected void fillNeighbors(Image image, int x, int y) {
		int over = dimension / 2;
		int i = 0;
		for (int xx = x - over; xx <= x + over; xx++) {
			for (int yy = y - over; yy <= y + over; yy++) {
				neighbors[i++] = image.getPixel(xx, yy);
			}
		}
	}

	private void filterEdges(Image image, Image res) {
		int width = image.getWidth();
		int height = image.getHeight();

		int edgeWidth = dimension / 2;

		// filter top
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < edgeWidth; y++) {
				edgeMode.fillNeighbors(this, image, x, y, neighbors);
				res.setPixel(x, y,
						edgeMode.processNeighborhodd(this, neighbors));
			}
		}

		// filter bottom
		for (int x = 0; x < width; x++) {
			for (int y = height - edgeWidth; y < height; y++) {
				edgeMode.fillNeighbors(this, image, x, y, neighbors);
				res.setPixel(x, y,
						edgeMode.processNeighborhodd(this, neighbors));
			}
		}

		// filter left
		for (int x = 0; x < edgeWidth; x++) {
			for (int y = edgeWidth; y < height - edgeWidth; y++) {
				edgeMode.fillNeighbors(this, image, x, y, neighbors);
				res.setPixel(x, y,
						edgeMode.processNeighborhodd(this, neighbors));
			}
		}

		// filter right
		for (int x = width - edgeWidth; x < width; x++) {
			for (int y = edgeWidth; y < height - edgeWidth; y++) {
				edgeMode.fillNeighbors(this, image, x, y, neighbors);
				res.setPixel(x, y,
						edgeMode.processNeighborhodd(this, neighbors));
			}
		}
	}

	private void filterMiddle(Image image, Image res) {
		int width = image.getWidth();
		int height = image.getHeight();
		int edgeWidth = dimension / 2;

		for (int x = edgeWidth; x < width - edgeWidth; x++) {
			for (int y = edgeWidth; y < height - edgeWidth; y++) {
				fillNeighbors(image, x, y);
				res.setPixel(x, y, processNeighborhood(neighbors));
			}
		}
	}

	/**
	 * Returns the dimension of the neighborhood matrix. The neighborhood matrix
	 * has a size of <code>dimension * dimension</code>. You can access a
	 * specific position in the neighborhood as follows:
	 * <code>neighbors[y * dimension + x]</code>
	 * 
	 * @return the dimension of the neighborhood matrix
	 */
	public int getDimension() {
		return dimension;
	}

	/**
	 * Returns a copy of the neighborhood matrix.
	 * 
	 * @return a copy of the neighborhood matrix
	 */
	public int[] getNeighbors() {
		return Arrays.copyOf(neighbors, neighbors.length);
	}

	/**
	 * Computes the pixel value for the passed-in neighborhood pixels.
	 * 
	 * @param neighbors
	 *            an <code>int</code> array containing the neighborhood pixels
	 *            of the currently processed pixel
	 * @return the pixel value for the passed-in neighborhood pixels
	 */
	public abstract int processNeighborhood(int[] neighbors);

}
