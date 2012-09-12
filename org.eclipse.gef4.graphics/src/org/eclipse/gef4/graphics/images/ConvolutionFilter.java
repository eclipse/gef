package org.eclipse.gef4.graphics.images;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.Arrays;

import org.eclipse.gef4.graphics.Image;

public class ConvolutionFilter implements IImageFilter {

	/**
	 * <p>
	 * The EdgeMode specifies the behavior of a {@link ConvolutionFilter} when
	 * filtering the edges of the {@link Image} being processed.
	 * </p>
	 * 
	 * <p>
	 * At the edges of the {@link Image} being processed, the convolution kernel
	 * cannot be fully applied, because some neighbor pixels are missing. In
	 * such cases, the behavior of a {@link ConvolutionFilter} can be altered by
	 * the EdgeMode in three different ways:
	 * <ul>
	 * <li>{@link #EDGE_NO_OP}</li>
	 * <li>{@link #EDGE_ZERO}</li>
	 * <li>{@link #EDGE_OVERLAP}</li>
	 * </ul>
	 * </p>
	 * 
	 * @author mwienand
	 * 
	 */
	public enum EdgeMode {
		/**
		 * If not all neighbor pixels in a convolution kernel are specified, the
		 * current pixel is not filtered.
		 * 
		 * @see #EDGE_ZERO
		 * @see #EDGE_OVERLAP
		 */
		EDGE_NO_OP,

		/**
		 * <p>
		 * If a {@link ConvolutionFilter} is processing the edge of an
		 * {@link Image}, the absence of pixel-values outside of the
		 * {@link Image} is compensated by using the values of the pixels of the
		 * opposite edge.
		 * </p>
		 * 
		 * <p>
		 * Note that this {@link EdgeMode} is most useful when simulating a zoom
		 * and/or spin blur by blurring the polar transform of an {@link Image}.
		 * </p>
		 * 
		 * @see #EDGE_NO_OP
		 * @see #EDGE_ZERO
		 */
		EDGE_OVERLAP,

		/**
		 * <p>
		 * If not all neighbor pixels in a convolution kernel are specified, the
		 * current pixel is set to zero, i.e. it will disappear.
		 * </p>
		 * 
		 * <p>
		 * Note that this {@link EdgeMode} will leave a border around the
		 * filtered {@link Image}.
		 * </p>
		 * 
		 * @see #EDGE_NO_OP
		 * @see #EDGE_OVERLAP
		 */
		EDGE_ZERO,

		// /**
		// * <p>
		// * If a {@link ConvolutionFilter} is processing the edge of an
		// * {@link Image}, the absence of pixel-values outside of the
		// * {@link Image} is compensated by assuming a constant color
		// * (ConvolutionFilter#edgeColor) for every missing pixel value.
		// * </p>
		// *
		// * <p>
		// * Note that this {@link EdgeMode} will leave a border around the
		// filtered {@link Image}.
		// * </p>
		// */
		// EDGE_COLOR,
	}

	private static int convolve(double[] kernel, int[] neighbors) {
		double aAvg, rAvg, gAvg, bAvg;
		aAvg = rAvg = gAvg = bAvg = 0d;

		for (int i = 0; i < kernel.length; i++) {
			// no need to mask alpha, because it is the first component
			aAvg += kernel[i] * (neighbors[i] >> 24);
			rAvg += kernel[i] * ((neighbors[i] & 0xff0000) >> 16);
			gAvg += kernel[i] * ((neighbors[i] & 0xff00) >> 8);
			bAvg += kernel[i] * (neighbors[i] & 0xff);
		}

		return (int) aAvg << 24 | (int) rAvg << 16 | (int) gAvg << 8
				| (int) bAvg;
	}

	private static void fillNeighbors(int[] neighbors, int dimension,
			BufferedImage src, int x, int y) {
		int w = src.getWidth();
		int h = src.getHeight();
		int over = dimension / 2;
		int i = 0;
		for (int xx = x - over; xx <= x + over; xx++) {
			for (int yy = y - over; yy <= y + over; yy++) {
				int x2 = xx < 0 ? w + xx : xx >= w ? xx - w
						: xx;
				int y2 = yy < 0 ? h + yy : yy >= h ? yy - h : yy;
				neighbors[i++] = src.getRGB(x2, y2);
			}
		}
	}
	protected final int dimension;

	protected final double[] kernel;

	protected final EdgeMode edgeMode;

	public ConvolutionFilter(final int dimension, final EdgeMode edgeMode,
			final double... kernel) {
		if (dimension < 1 || dimension % 2 == 0) {
			throw new IllegalArgumentException(
					"The kernel dimension may only be positive and odd. Given dimension = " + dimension + ".");
		}
		this.edgeMode = edgeMode;
		this.dimension = dimension;
		int size = dimension * dimension;
		if (kernel.length != size) {
			throw new IllegalArgumentException(
					"The kernel dimension does not match the actual kernel size. Given dimension = " + dimension + "x" + dimension + ", kerne = "
							+ Arrays.toString(kernel) + ".");
		}
		this.kernel = Arrays.copyOf(kernel, size);
	}

	public Image apply(final Image image) {
		float[] kernelAsFloats = new float[kernel.length];
		for (int i = 0; i < kernelAsFloats.length; i++) {
			kernelAsFloats[i] = (float) kernel[i];
		}
		Kernel awtKernel = new Kernel(dimension, dimension, kernelAsFloats);

		if (edgeMode == EdgeMode.EDGE_ZERO) {
			return new Image(new ConvolveOp(awtKernel,
					ConvolveOp.EDGE_ZERO_FILL, null).filter(
							image.bufferedImage(), null));
		} else {
			Image middleFiltered = new Image(new ConvolveOp(awtKernel,
					ConvolveOp.EDGE_NO_OP,
					null).filter(image.bufferedImage(), null));
			if (edgeMode == EdgeMode.EDGE_NO_OP) {
				return middleFiltered;
			} else if (edgeMode == EdgeMode.EDGE_OVERLAP) {
				return filterEdgesOverlap(image, middleFiltered);
			} else {
				throw new UnsupportedOperationException(
						"The specified edge mode (" + edgeMode
						+ ") is not yet implemented.");
			}
		}
	}

	private Image filterEdgesOverlap(Image image, Image middleFiltered) {
		BufferedImage src = image.bufferedImage();
		BufferedImage dst = middleFiltered.bufferedImage();
		int height = src.getHeight();
		int width = src.getWidth();

		int edgeWidth = dimension / 2 + 1;
		int[] neighbors = new int[kernel.length];

		// filter top
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < edgeWidth; y++) {
				fillNeighbors(neighbors, dimension, src, x, y);
				dst.setRGB(x, y, convolve(kernel, neighbors));
			}
		}

		// filter bottom
		for (int x = 0; x < width; x++) {
			for (int y = height - edgeWidth; y < height; y++) {
				fillNeighbors(neighbors, dimension, src, x, y);
				dst.setRGB(x, y, convolve(kernel, neighbors));
			}
		}

		// filter left
		for (int x = 0; x < edgeWidth; x++) {
			for (int y = edgeWidth; y < height - edgeWidth; y++) {
				fillNeighbors(neighbors, dimension, src, x, y);
				dst.setRGB(x, y, convolve(kernel, neighbors));
			}
		}

		// filter right
		for (int x = width - edgeWidth; x < width; x++) {
			for (int y = edgeWidth; y < height - edgeWidth; y++) {
				fillNeighbors(neighbors, dimension, src, x, y);
				dst.setRGB(x, y, convolve(kernel, neighbors));
			}
		}

		return middleFiltered;
	}

	public int getDimension() {
		return dimension;
	}

	public double[] getKernel() {
		return Arrays.copyOf(kernel, kernel.length);
	}

}
