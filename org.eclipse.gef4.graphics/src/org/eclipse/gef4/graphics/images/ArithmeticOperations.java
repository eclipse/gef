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

public class ArithmeticOperations {

	public static IImageOperation getAddOperation(final Image addend) {
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return i == 0 ? v : v + Utils.getARGB(addend.getPixel(x, y))[i];
			}
		};
	}

	public static IImageOperation getAddOperation(final int pixelOffset) {
		final int[] argbOffset = Utils.getARGB(pixelOffset);
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return i == 0 ? v : v + argbOffset[i];
			}
		};
	}

	public static IImageOperation getAndOperation(final Image other) {
		return new AbstractPixelFilterOperation() {
			@Override
			protected int processPixel(int argb, int x, int y, Image input) {
				return argb & other.getPixel(x, y);
			}
		};
	}

	public static IImageOperation getAndOperation(final int argbOther) {
		return new AbstractPixelFilterOperation() {
			@Override
			protected int processPixel(int argb, int x, int y, Image input) {
				return argb & argbOther;
			}
		};
	}

	public static IImageOperation getBlendOperation(final double xr,
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

	public static IImageOperation getDivideOperation(final Image divisor,
			final double scaleFactor) {
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return i == 0 ? v : (int) (scaleFactor * v / Utils
						.getARGB(divisor.getPixel(x, y))[i]);
			}
		};
	}

	public static IImageOperation getInvertOperation() {
		return getXorOperation(0xffffff);
	}

	public static IImageOperation getMultiplyOperation(final double f) {
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return (int) (f * v);
			}
		};
	}

	public static IImageOperation getMultiplyOperation(final double fa,
			final double fr, final double fg, final double fb) {
		final double[] fs = new double[] { fa, fr, fg, fb };
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return (int) (fs[i] * v);
			}
		};
	}

	public static IImageOperation getOrOperation(final Image other) {
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return i == 0 ? v : v | Utils.getARGB(other.getPixel(x, y))[i];
			}
		};
	}

	public static IImageOperation getOrOperation(final int argbOther) {
		return new AbstractPixelFilterOperation() {
			@Override
			protected int processPixel(int argb, int x, int y, Image input) {
				return argb | argbOther;
			}
		};
	}

	public static IImageOperation getShiftLeftOperation(final int n) {
		return new AbstractPixelFilterOperation() {
			@Override
			protected int processPixel(int argb, int x, int y, Image input) {
				return argb << n;
			}
		};
	}

	public static IImageOperation getShiftRightOperation(final int n) {
		return new AbstractPixelFilterOperation() {
			@Override
			protected int processPixel(int argb, int x, int y, Image input) {
				return argb >>> n;
			}
		};
	}

	public static IImageOperation getSubtractOperation(final Image other) {
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return i == 0 ? v : v - Utils.getARGB(other.getPixel(x, y))[i];
			}
		};
	}

	public static IImageOperation getSubtractOperation(final int pixelOther) {
		final int[] argbOther = Utils.getARGB(pixelOther);
		return new AbstractChannelFilterOperation() {
			@Override
			protected int processChannel(int v, int x, int y, int i, Image input) {
				return i == 0 ? v : v - argbOther[i];
			}
		};
	}

	public static IImageOperation getXorOperation(final Image other) {
		return new AbstractPixelFilterOperation() {
			@Override
			protected int processPixel(int argb, int x, int y, Image input) {
				return argb ^ other.getPixel(x, y);
			}
		};
	}

	public static IImageOperation getXorOperation(final int argbOther) {
		return new AbstractPixelFilterOperation() {
			@Override
			protected int processPixel(int argb, int x, int y, Image input) {
				return argb ^ argbOther;
			}
		};
	}

}
