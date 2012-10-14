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
	 * Creates an {@link IImageOperation} that computes the absolute difference
	 * of the given {@link Image} and the applied one, for each color channel.
	 * 
	 * @param other
	 * @return an {@link IImageOperation} that computes the absolute difference
	 *         of the given {@link Image} and the applied one
	 */
	public static AbstractChannelFilterOperation getAbsDifferenceOperation(
			final Image other) {
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return i == 0 ? v : Math.abs(v - Utils.getARGB(other.getPixel(x, y))[i]);
			}
		};
	}

	/**
	 * Creates an {@link IImageOperation} that computes the absolute difference
	 * of the given constant pixel value and the applied {@link Image}.
	 * 
	 * @param pixelOther
	 * @return an {@link IImageOperation} that computes the absolute difference
	 *         of the given constant pixel value and the applied {@link Image}
	 */
	public static AbstractChannelFilterOperation getAbsDifferenceOperation(
			final int pixelOther) {
		final int[] argbOther = Utils.getARGB(pixelOther);
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return i == 0 ? v : Math.abs(v - argbOther[i]);
			}
		};
	}

	/**
	 * Creates an {@link IImageOperation} that computes the sum of the given
	 * {@link Image} and the applied one, for each color channel.
	 * 
	 * @param addend
	 * @return an {@link IImageOperation} that computes the sum of the given
	 *         {@link Image} and the applied one
	 */
	public static AbstractChannelFilterOperation getAddOperation(
			final Image addend) {
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return i == 0 ? v : v + Utils.getARGB(addend.getPixel(x, y))[i];
			}
		};
	}

	/**
	 * Creates an {@link IImageOperation} that computes the sum of the given
	 * constant pixel value and the applied {@link Image}, for each color
	 * channel.
	 * 
	 * @param pixelOffset
	 * @return an {@link IImageOperation} that computes the absolute difference
	 *         of the given constant pixel value and the applied {@link Image}
	 */
	public static AbstractChannelFilterOperation getAddOperation(
			final int pixelOffset) {
		final int[] argbOffset = Utils.getARGB(pixelOffset);
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return i == 0 ? v : v + argbOffset[i];
			}
		};
	}

	/**
	 * Creates an {@link IImageOperation} that computes the logical-and of the
	 * given {@link Image} and the applied one.
	 * 
	 * @param other
	 * @return an {@link IImageOperation} that computes the logical-and of the
	 *         given {@link Image} and the applied one
	 */
	public static AbstractPixelFilterOperation getAndOperation(final Image other) {
		return new AbstractPixelFilterOperation() {
			@Override
			protected int processPixel(int argb, int x, int y, Image input) {
				return argb & other.getPixel(x, y);
			}
		};
	}

	/**
	 * Creates an {@link IImageOperation} that computes the logical-and of the
	 * given constant pixel value and the applied {@link Image}.
	 * 
	 * @param pixelOther
	 * @return an {@link IImageOperation} that computes the logical-and of the
	 *         given constant pixel value and the applied {@link Image}
	 */
	public static AbstractPixelFilterOperation getAndOperation(
			final int pixelOther) {
		return new AbstractPixelFilterOperation() {
			@Override
			protected int processPixel(int argb, int x, int y, Image input) {
				return argb & pixelOther;
			}
		};
	}

	public static AbstractChannelFilterOperation getBlendOperation(
			final double xr,
			final double xg, final double xb, final Image other) {
		final double[] xs = new double[] { xr, xg, xb };
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return i == 0 ? v : (int) (xs[i] * v + (1 - xs[i])
						* Utils.getARGB(other.getPixel(x, y))[i]);
			}
		};
	}

	public static AbstractChannelFilterOperation getDivideOperation(
			final Image divisor,
			final double scaleFactor) {
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return i == 0 ? v : (int) (scaleFactor * v / Utils
						.getARGB(divisor.getPixel(x, y))[i]);
			}
		};
	}

	public static AbstractPixelFilterOperation getInvertOperation() {
		return getXorOperation(0xffffff);
	}

	public static AbstractChannelFilterOperation getMultiplyOperation(
			final double f) {
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return (int) (f * v);
			}
		};
	}

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

	public static AbstractChannelFilterOperation getOrOperation(
			final Image other) {
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return i == 0 ? v : v | Utils.getARGB(other.getPixel(x, y))[i];
			}
		};
	}

	public static AbstractPixelFilterOperation getOrOperation(
			final int argbOther) {
		return new AbstractPixelFilterOperation() {
			@Override
			protected int processPixel(int argb, int x, int y, Image input) {
				return argb | argbOther;
			}
		};
	}

	public static AbstractPixelFilterOperation getShiftLeftOperation(final int n) {
		return new AbstractPixelFilterOperation() {
			@Override
			protected int processPixel(int argb, int x, int y, Image input) {
				return argb << n;
			}
		};
	}

	public static AbstractPixelFilterOperation getShiftRightOperation(
			final int n) {
		return new AbstractPixelFilterOperation() {
			@Override
			protected int processPixel(int argb, int x, int y, Image input) {
				return argb >>> n;
			}
		};
	}

	public static AbstractChannelFilterOperation getSubtractOperation(
			final Image other) {
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return i == 0 ? v : v - Utils.getARGB(other.getPixel(x, y))[i];
			}
		};
	}

	public static AbstractChannelFilterOperation getSubtractOperation(
			final int pixelOther) {
		final int[] argbOther = Utils.getARGB(pixelOther);
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return i == 0 ? v : v - argbOther[i];
			}
		};
	}

	public static AbstractPixelFilterOperation getXorOperation(final Image other) {
		return new AbstractPixelFilterOperation() {
			@Override
			protected int processPixel(int argb, int x, int y, Image input) {
				return argb ^ other.getPixel(x, y);
			}
		};
	}

	public static AbstractPixelFilterOperation getXorOperation(
			final int argbOther) {
		return new AbstractPixelFilterOperation() {
			@Override
			protected int processPixel(int argb, int x, int y, Image input) {
				return argb ^ argbOther;
			}
		};
	}

}
