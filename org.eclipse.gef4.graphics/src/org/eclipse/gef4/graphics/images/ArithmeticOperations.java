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

import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.Image;

/**
 * The ArithmeticOperations class contains methods to create arithmetic and
 * logical pixel filters.
 * 
 * @author mwienand
 * 
 */
public class ArithmeticOperations {

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
						.abs(v - Color.getPixelARGB(other.getPixel(x, y))[i]);
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
		final int[] argbOther = Color.getPixelARGB(pixelOther);
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
				return v + Color.getPixelARGB(addend.getPixel(x, y))[i];
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
		final int[] argbOffset = Color.getPixelARGB(pixelOffset);
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
				return v & Color.getPixelARGB(other.getPixel(x, y))[i];
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
		final int[] constant = Color.getPixelARGB(pixelOther);
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return v & constant[i];
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
			final double xr,
			final double xg, final double xb, final Image other) {
		final double[] xs = new double[] { 0, xr, xg, xb };
		return new AbstractColorChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return (int) (xs[i] * v + (1 - xs[i])
						* Color.getPixelARGB(other.getPixel(x, y))[i]);
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
			final Image divisor,
			final double scaleFactor) {
		return new AbstractColorChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				int v2 = Color.getPixelARGB(divisor.getPixel(x, y))[i];
				return (int) (scaleFactor * v / (v2 == 0 ? 1 : v2));
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
			final double fa,
			final double fr, final double fg, final double fb) {
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
				return v | Color.getPixelARGB(input.getPixel(x, y))[i];
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
		final int[] constant = Color.getPixelARGB(pixel);
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
				return v - Color.getPixelARGB(other.getPixel(x, y))[i];
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
		final int[] constant = Color.getPixelARGB(pixel);
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return v - constant[i];
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
				return v ^ Color.getPixelARGB(other.getPixel(x, y))[i];
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
		final int[] constant = Color.getPixelARGB(pixel);
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return v ^ constant[i];
			}
		};
	}

}
