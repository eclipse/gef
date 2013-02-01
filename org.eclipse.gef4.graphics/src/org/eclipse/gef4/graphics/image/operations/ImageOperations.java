package org.eclipse.gef4.graphics.image.operations;

import java.util.Arrays;

import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.image.Image;
import org.eclipse.gef4.graphics.image.operations.AbstractPixelNeighborhoodFilterOperation.EdgeMode;

public class ImageOperations {
	/**
	 * Creates an {@link AbstractColorChannelFilterOperation} that computes the
	 * absolute difference of the given {@link Image} and the applied one, for
	 * each color channel.
	 * 
	 * @param other
	 * @return an {@link AbstractColorChannelFilterOperation} that computes the
	 *         absolute difference of the given {@link Image} and the applied
	 *         one
	 */
	public static AbstractColorChannelFilterOperation getAbsDifferenceOperation(
			final Image other) {
		return new AbstractColorChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return Math
						.abs(v - Color.getPixelRGBA(other.getPixel(x, y))[i]);
			}
		};
	}

	/**
	 * Creates an {@link AbstractChannelFilterOperation} that computes the
	 * absolute difference of the given constant pixel value and the applied
	 * {@link Image}.
	 * 
	 * @param pixelOther
	 * @return an {@link AbstractChannelFilterOperation} that computes the
	 *         absolute difference of the given constant pixel value and the
	 *         applied {@link Image}
	 */
	public static AbstractChannelFilterOperation getAbsDifferenceOperation(
			final int pixelOther) {
		final int[] argbOther = Color.getPixelRGBA(pixelOther);
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return Math.abs(v - argbOther[i]);
			}
		};
	}

	/**
	 * Creates an {@link AbstractColorChannelFilterOperation} that computes the
	 * sum of the given {@link Image} and the applied one, for each color
	 * channel.
	 * 
	 * @param addend
	 * @return an {@link AbstractColorChannelFilterOperation} that computes the
	 *         sum of the given {@link Image} and the applied one
	 */
	public static AbstractColorChannelFilterOperation getAddOperation(
			final Image addend) {
		return new AbstractColorChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return v + Color.getPixelRGBA(addend.getPixel(x, y))[i];
			}
		};
	}

	/**
	 * Creates an {@link AbstractChannelFilterOperation} that computes the sum
	 * of the given constant pixel value and the applied {@link Image}.
	 * 
	 * @param pixelOffset
	 * @return an {@link AbstractChannelFilterOperation} that computes the sum
	 *         of the given constant pixel value and the applied {@link Image}
	 */
	public static AbstractChannelFilterOperation getAddOperation(
			final int pixelOffset) {
		final int[] argbOffset = Color.getPixelRGBA(pixelOffset);
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return v + argbOffset[i];
			}
		};
	}

	/**
	 * Creates an {@link AbstractColorChannelFilterOperation} that computes the
	 * logical-AND of the given {@link Image} and the applied one, for each
	 * color channel.
	 * 
	 * @param other
	 * @return an {@link AbstractColorChannelFilterOperation} that computes the
	 *         logical-AND of the given {@link Image} and the applied one
	 */
	public static AbstractColorChannelFilterOperation getAndOperation(
			final Image other) {
		return new AbstractColorChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return v & Color.getPixelRGBA(other.getPixel(x, y))[i];
			}
		};
	}

	/**
	 * Creates an {@link AbstractChannelFilterOperation} that computes the
	 * logical-AND of the given constant pixel value and the applied
	 * {@link Image}.
	 * 
	 * @param pixelOther
	 * @return an {@link AbstractChannelFilterOperation} that computes the
	 *         logical-AND of the given constant pixel value and the applied
	 *         {@link Image}
	 */
	public static AbstractChannelFilterOperation getAndOperation(
			final int pixelOther) {
		final int[] constant = Color.getPixelRGBA(pixelOther);
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return v & constant[i];
			}
		};
	}

	/**
	 * Creates an {@link AbstractChannelFilterOperation} that computes the merge
	 * of the given {@link Image} and the applied {@link Image}, for each
	 * channel.
	 * 
	 * @param xr
	 *            [0;1] red balance factor
	 * @param xg
	 *            [0;1] green balance factor
	 * @param xb
	 *            [0;1] blue balance factor
	 * @param xa
	 *            [0;1] alpha balance factor
	 * @param other
	 *            the {@link Image} to merge with the applied {@link Image}
	 * @return an {@link AbstractChannelFilterOperation} that computes the merge
	 *         of the given {@link Image} and the applied {@link Image}
	 */
	public static AbstractColorChannelFilterOperation getBlendOperation(
			final double xr, final double xg, final double xb, final double xa,
			final Image other) {
		final double[] xs = new double[] { xr, xg, xb, xa };
		return new AbstractColorChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return (int) (xs[i] * v + (1 - xs[i])
						* Color.getPixelRGBA(other.getPixel(x, y))[i]);
			}
		};
	}

	/**
	 * Creates an {@link AbstractColorChannelFilterOperation} that computes the
	 * merge of the given {@link Image} and the applied {@link Image}, for each
	 * color channel.
	 * 
	 * @param xr
	 *            [0;1] red balance factor
	 * @param xg
	 *            [0;1] green balance factor
	 * @param xb
	 *            [0;1] blue balance factor
	 * @param other
	 *            the {@link Image} to merge with the applied {@link Image}
	 * @return an {@link AbstractColorChannelFilterOperation} that computes the
	 *         merge of the given {@link Image} and the applied {@link Image}
	 */
	public static AbstractColorChannelFilterOperation getBlendOperation(
			final double xr, final double xg, final double xb, final Image other) {
		return getBlendOperation(xr, xg, xb, 0, other);
	}

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

	/**
	 * <p>
	 * Creates an {@link AbstractColorChannelFilterOperation} that computes the
	 * quotient of the applied {@link Image} and the passed-in <i>divisor</i>
	 * {@link Image}.
	 * </p>
	 * 
	 * <p>
	 * The quotient of two {@link Image}s can be used to detect changes, similar
	 * to {@link #getAbsDifferenceOperation(Image) subtraction}. Unfortunately,
	 * only integer color channels are implemented, so that a division result
	 * below <code>1</code> is rounded down to <code>0</code>. To visualize such
	 * changes, it is necessary to scale the pixel-quotient appropriately. A
	 * good first try might be a factor of <code>128</code>.
	 * </p>
	 * 
	 * @param divisor
	 *            the {@link Image} which contains the divisor pixels
	 * @param scaleFactor
	 *            the quotient of two pixels is multiplied by this value
	 * @return an {@link AbstractColorChannelFilterOperation} that computes the
	 *         quotient of the applied {@link Image} and the passed-in
	 *         <i>divisor</i> {@link Image}
	 */
	public static AbstractColorChannelFilterOperation getDivideOperation(
			final Image divisor, final double scaleFactor) {
		return new AbstractColorChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				int v2 = Color.getPixelRGBA(divisor.getPixel(x, y))[i];
				return (int) (scaleFactor * v / (v2 == 0 ? 1 : v2));
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

		// ensure dimension is odd so that we have a middle
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

	/**
	 * Creates a new grayscale {@link IImageOperation}. A grayscale computes an
	 * intensity value for every pixel of an {@link Image}. The red, green, and
	 * blue channel values of one pixel are replaced by the pixels intensity
	 * value.
	 * 
	 * @return a new grayscale {@link IImageOperation}
	 */
	public static AbstractPixelFilterOperation getGreyScaleOperation() {
		return getGreyScaleOperation(0.3333, 0.3334, 0.3333);
	}

	/**
	 * Creates a new grayscale {@link IImageOperation} with the provided scale
	 * factors for the red, green, and blue channels. A grayscale
	 * {@link IImageOperation} computes an intensity value for every pixel of an
	 * {@link Image}. The red, green, and blue channel values of one pixel are
	 * replaced by the pixels intensity value.
	 * 
	 * @param sr
	 *            the red channel scale factor
	 * @param sg
	 *            the green channel scale factor
	 * @param sb
	 *            the blue channel scale factor
	 * @return a new grayscale {@link IImageOperation} with the provided scale
	 *         factors
	 */
	public static AbstractPixelFilterOperation getGreyScaleOperation(
			final double sr, final double sg, final double sb) {
		return new AbstractPixelFilterOperation() {
			@Override
			protected int processPixel(int pixel, int x, int y, Image input) {
				int[] rgba = Color.getPixelRGBA(pixel);
				int intensity = Color.computePixelIntensity(rgba, sr, sg, sb);
				return Color.getPixel(intensity, intensity, intensity, rgba[3]);
			}
		};
	}

	/**
	 * Creates an {@link AbstractChannelFilterOperation} that computes the
	 * photographic negative of the applied {@link Image}.
	 * 
	 * @return an {@link AbstractChannelFilterOperation} that computes the
	 *         photographic negative of the applied {@link Image}
	 */
	public static AbstractChannelFilterOperation getInvertOperation() {
		return getXorOperation(0xffffff);
	}

	public static IImageOperation getLaplacian() {
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
					int[] rgba = Color.getPixelRGBA(neighbors[i]);
					red[i] = rgba[0];
					green[i] = rgba[1];
					blue[i] = rgba[2];
				}

				Arrays.sort(red);
				Arrays.sort(green);
				Arrays.sort(blue);

				return Color.getPixel(red[neighbors.length / 2],
						green[neighbors.length / 2],
						blue[neighbors.length / 2], alpha);
			}
		};
	}

	/**
	 * Creates an {@link AbstractColorChannelFilterOperation} which multiplies
	 * the applied {@link Image} with the passed-in constant factor <i>f</i>,
	 * for each color channel.
	 * 
	 * @param f
	 *            the applied {@link Image} is multiplied by this factor
	 * @return an {@link AbstractChannelFilterOperation} which multiplies the
	 *         applied {@link Image} with the passed-in constant factor <i>f</i>
	 */
	public static AbstractColorChannelFilterOperation getMultiplyOperation(
			final double f) {
		return new AbstractColorChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return (int) (f * v);
			}
		};
	}

	/**
	 * Creates an {@link AbstractChannelFilterOperation} which multiplies the
	 * applied {@link Image} channel-wise with the given factors.
	 * 
	 * @param fa
	 *            the factor for the alpha channel
	 * @param fr
	 *            the factor for the red channel
	 * @param fg
	 *            the factor for the green channel
	 * @param fb
	 *            the factor for the blue channel
	 * @return an {@link AbstractChannelFilterOperation} which multiplies the
	 *         applied {@link Image} channel-wise with the given factors
	 */
	public static AbstractChannelFilterOperation getMultiplyOperation(
			final double fa, final double fr, final double fg, final double fb) {
		final double[] fs = new double[] { fa, fr, fg, fb };
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return (int) (fs[i] * v);
			}
		};
	}

	/**
	 * Creates an {@link AbstractColorChannelFilterOperation} that computes the
	 * logical-OR of the given {@link Image} and the applied one.
	 * 
	 * @param other
	 * @return an {@link AbstractColorChannelFilterOperation} that computes the
	 *         logical-OR of the given {@link Image} and the applied one
	 */
	public static AbstractColorChannelFilterOperation getOrOperation(
			final Image other) {
		return new AbstractColorChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return v | Color.getPixelRGBA(input.getPixel(x, y))[i];
			}
		};
	}

	/**
	 * Creates an {@link AbstractChannelFilterOperation} that computes the
	 * logical-OR of the given constant pixel value and the applied
	 * {@link Image}.
	 * 
	 * @param pixel
	 *            the constant pixel value to OR with the applied {@link Image}
	 * @return an {@link AbstractChannelFilterOperation} that computes the
	 *         logical-OR of the given constant pixel value and the applied
	 *         {@link Image}
	 */
	public static AbstractChannelFilterOperation getOrOperation(final int pixel) {
		final int[] constant = Color.getPixelRGBA(pixel);
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return v | constant[i];
			}
		};
	}

	/**
	 * Creates an {@link AbstractColorChannelFilterOperation} that shifts the
	 * bits representing the RGB color values channel-wise to the left by the
	 * given number of digits.
	 * 
	 * @param n
	 *            the number of digits to shift by
	 * @return an {@link AbstractColorChannelFilterOperation} that shifts the
	 *         bits representing the RGB color values to the left
	 */
	public static AbstractColorChannelFilterOperation getShiftLeftOperation(
			final int n) {
		return new AbstractColorChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return v << n;
			}
		};
	}

	/**
	 * Creates an {@link AbstractColorChannelFilterOperation} that shifts the
	 * bits representing the RGB color values channel-wise to the right by the
	 * given number of digits.
	 * 
	 * @param n
	 *            the number of digits to shift by
	 * @return an {@link AbstractColorChannelFilterOperation} that shifts the
	 *         bits representing the RGB color values to the right
	 */
	public static AbstractColorChannelFilterOperation getShiftRightOperation(
			final int n) {
		return new AbstractColorChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return v >>> n;
			}
		};
	}

	/**
	 * <p>
	 * Creates an {@link AbstractColorChannelFilterOperation} that computes the
	 * difference of the applied {@link Image} and the passed-in <i>other</i>
	 * {@link Image}.
	 * </p>
	 * 
	 * <p>
	 * Contrary to the {@link #getAbsDifferenceOperation(Image)} method, the
	 * results of the subtraction are not taken absolute, i.e. negative results
	 * are raised to <code>0</code>.
	 * </p>
	 * 
	 * @param other
	 *            the {@link Image} to subtract from the applied {@link Image}
	 * @return an {@link AbstractColorChannelFilterOperation} that computes the
	 *         difference of the applied {@link Image} and the passed-in
	 *         <i>other</i> {@link Image}
	 */
	public static AbstractColorChannelFilterOperation getSubtractOperation(
			final Image other) {
		return new AbstractColorChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return v - Color.getPixelRGBA(other.getPixel(x, y))[i];
			}
		};
	}

	/**
	 * <p>
	 * Creates an {@link AbstractColorChannelFilterOperation} that computes the
	 * difference of the applied {@link Image} and the passed-in constant pixel
	 * value.
	 * </p>
	 * 
	 * <p>
	 * Contrary to the {@link #getAbsDifferenceOperation(int)} method, the
	 * results of the subtraction are not taken absolute, i.e. negative results
	 * are raised to <code>0</code>.
	 * </p>
	 * 
	 * @param pixel
	 *            the constant pixel value to subtract from the applied
	 *            {@link Image}
	 * @return an {@link AbstractColorChannelFilterOperation} that computes the
	 *         difference of the applied {@link Image} and the passed-in
	 *         constant pixel value
	 */
	public static AbstractChannelFilterOperation getSubtractOperation(
			final int pixel) {
		final int[] constant = Color.getPixelRGBA(pixel);
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return v - constant[i];
			}
		};
	}

	/**
	 * <p>
	 * Creates a new threshold {@link IImageOperation}.
	 * </p>
	 * 
	 * <p>
	 * A threshold {@link IImageOperation} classifies each pixel of an
	 * {@link Image} into either being a foreground, or a background pixel
	 * depending on the pixel's intensity value. If the intensity of a pixel is
	 * greater than or equal to the passed-in intensity threshold, the pixel is
	 * set to white. Otherwise, the pixel is set to black.
	 * </p>
	 * 
	 * @param intensityThreshold
	 * @return a new threshold {@link IImageOperation}
	 */
	public static AbstractPixelFilterOperation getThresholdOperation(
			final int intensityThreshold) {
		return new AbstractPixelFilterOperation() {
			@Override
			protected int processPixel(int pixel, int x, int y, Image input) {
				int[] rgba = Color.getPixelRGBA(pixel);
				int intensity = Color.computePixelIntensity(rgba);
				if (intensity >= intensityThreshold) {
					return Color.getPixel(0xff, 0xff, 0xff, rgba[3]);
				} else {
					return Color.getPixel(0, 0, 0, rgba[3]);
				}
			}
		};
	}

	/**
	 * Creates an {@link AbstractColorChannelFilterOperation} that computes the
	 * logical-XOR of the given {@link Image} and the applied one.
	 * 
	 * @param other
	 *            the {@link Image} to XOR with the applied {@link Image}
	 * @return an {@link AbstractColorChannelFilterOperation} that computes the
	 *         logical-XOR of the given {@link Image} and the applied one
	 */
	public static AbstractColorChannelFilterOperation getXorOperation(
			final Image other) {
		return new AbstractColorChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return v ^ Color.getPixelRGBA(other.getPixel(x, y))[i];
			}
		};
	}

	/**
	 * Creates an {@link AbstractChannelFilterOperation} that computes the
	 * logical-XOR of the given constant pixel value and the applied
	 * {@link Image}.
	 * 
	 * @param pixel
	 *            the constant pixel value to XOR with the applied {@link Image}
	 * @return an {@link AbstractChannelFilterOperation} that computes the
	 *         logical-XOR of the given constant pixel value and the applied
	 *         {@link Image}
	 */
	public static AbstractChannelFilterOperation getXorOperation(final int pixel) {
		final int[] constant = Color.getPixelRGBA(pixel);
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return v ^ constant[i];
			}
		};
	}

}
