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

import java.awt.image.BufferedImage;
import java.util.Arrays;

import org.eclipse.gef4.graphics.Image;
import org.eclipse.gef4.graphics.images.AbstractPixelNeighborhoodFilterOperation.EdgeMode;

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

			public ConstantPixel() {
			}

			public ConstantPixel(int argb) {
				color = argb;
			}

			@Override
			public int computePixel(AbstractPixelNeighborhoodFilterOperation op, int[] neighbors
					) {
				return color;
			}

			@Override
			public void fillNeighbors(AbstractPixelNeighborhoodFilterOperation op,
					BufferedImage src, int x, int y, int[] neighbors) {
				// we do not need to inspect the neighbors
			}

			public int getColor() {
				return color;
			}

			protected ConstantPixel setColor(int argb) {
				color = argb;
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

			public ConstantPixelNeighbors() {
			}

			public ConstantPixelNeighbors(int argb) {
				color = argb;
			}

			@Override
			public int computePixel(AbstractPixelNeighborhoodFilterOperation op, int[] neighbors) {
				return op.computePixel(neighbors);
			}

			@Override
			public void fillNeighbors(AbstractPixelNeighborhoodFilterOperation op,
					BufferedImage src, int x, int y, int[] neighbors) {
				int w = src.getWidth();
				int h = src.getHeight();
				int over = op.getDimension() / 2;
				int i = 0;
				for (int xx = x - over; xx <= x + over; xx++) {
					boolean xIsOutside = xx < 0 || xx >= w;
					for (int yy = y - over; yy <= y + over; yy++) {
						if (xIsOutside || yy < 0 || yy >= h) {
							neighbors[i++] = color;
						} else {
							neighbors[i++] = src.getRGB(xx, yy);
						}
					}
				}
			}

			public int getColor() {
				return color;
			}

			protected ConstantPixelNeighbors setColor(int argb) {
				color = argb;
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
			public int computePixel(AbstractPixelNeighborhoodFilterOperation op, int[] neighbors) {
				return neighbors[0];
			}

			@Override
			public void fillNeighbors(AbstractPixelNeighborhoodFilterOperation op,
					BufferedImage src, int x, int y, int[] neighbors) {
				neighbors[0] = src.getRGB(x, y);
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
			public int computePixel(AbstractPixelNeighborhoodFilterOperation op, int[] neighbors) {
				return op.computePixel(neighbors);
			}

			@Override
			public void fillNeighbors(AbstractPixelNeighborhoodFilterOperation op,
					BufferedImage src, int x, int y, int[] neighbors) {
				int w = src.getWidth();
				int h = src.getHeight();
				int over = op.getDimension() / 2;
				int i = 0;
				for (int xx = x - over; xx <= x + over; xx++) {
					int x2 = xx < 0 ? w + xx : xx >= w ? xx - w : xx;
					for (int yy = y - over; yy <= y + over; yy++) {
						int y2 = yy < 0 ? h + yy : yy >= h ? yy - h : yy;
						neighbors[i++] = src.getRGB(x2, y2);
					}
				}
			}
		}

		public abstract int computePixel(AbstractPixelNeighborhoodFilterOperation op,
				int[] neighbors);

		public abstract void fillNeighbors(AbstractPixelNeighborhoodFilterOperation op,
				BufferedImage src, int x, int y, int[] neighbors);

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
		BufferedImage src = image.bufferedImage();
		BufferedImage res = new BufferedImage(src.getWidth(), src.getHeight(),
				BufferedImage.TYPE_INT_ARGB);

		filterMiddle(src, res);
		filterEdges(src, res);

		return new Image(res);
	}

	/**
	 * Computes the pixel value for the passed-in neighborhood pixels.
	 * 
	 * @param neighbors
	 *            an <code>int</code> array containing the neighborhood pixels
	 *            of the currently processed pixel
	 * @return the pixel value for the passed-in neighborhood pixels
	 */
	public abstract int computePixel(int[] neighbors);

	protected void fillNeighbors(BufferedImage src, int x, int y) {
		int over = dimension / 2;
		int i = 0;
		for (int xx = x - over; xx <= x + over; xx++) {
			for (int yy = y - over; yy <= y + over; yy++) {
				neighbors[i++] = src.getRGB(xx, yy);
			}
		}
	}

	private void filterEdges(BufferedImage src, BufferedImage dst) {
		int width = src.getWidth();
		int height = src.getHeight();

		int edgeWidth = dimension / 2;

		// filter top
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < edgeWidth; y++) {
				edgeMode.fillNeighbors(this, src, x, y, neighbors);
				dst.setRGB(x, y, edgeMode.computePixel(this, neighbors));
			}
		}

		// filter bottom
		for (int x = 0; x < width; x++) {
			for (int y = height - edgeWidth; y < height; y++) {
				edgeMode.fillNeighbors(this, src, x, y, neighbors);
				dst.setRGB(x, y, edgeMode.computePixel(this, neighbors));
			}
		}

		// filter left
		for (int x = 0; x < edgeWidth; x++) {
			for (int y = edgeWidth; y < height - edgeWidth; y++) {
				edgeMode.fillNeighbors(this, src, x, y, neighbors);
				dst.setRGB(x, y, edgeMode.computePixel(this, neighbors));
			}
		}

		// filter right
		for (int x = width - edgeWidth; x < width; x++) {
			for (int y = edgeWidth; y < height - edgeWidth; y++) {
				edgeMode.fillNeighbors(this, src, x, y, neighbors);
				dst.setRGB(x, y, edgeMode.computePixel(this, neighbors));
			}
		}
	}

	private void filterMiddle(BufferedImage src, BufferedImage dst) {
		int width = src.getWidth();
		int height = src.getHeight();
		int edgeWidth = dimension / 2;

		for (int x = edgeWidth; x < width - edgeWidth; x++) {
			for (int y = edgeWidth; y < height - edgeWidth; y++) {
				fillNeighbors(src, x, y);
				dst.setRGB(x, y, computePixel(neighbors));
			}
		}
	}

	public int getDimension() {
		return dimension;
	}

	public int[] getNeighbors() {
		return Arrays.copyOf(neighbors, neighbors.length);
	}

}
