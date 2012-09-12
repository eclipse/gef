package org.eclipse.gef4.graphics.images;

import java.util.Arrays;

public class GenericMatrixFilter extends AbstractPixelFilter {

	private static final int MATRIX_COLS = 4; // number of channels
	private static final int MATRIX_ROWS = 5;
	private static final int MATRIX_ELEMS = MATRIX_COLS * MATRIX_ROWS;

	protected double[] channelMatrix = new double[MATRIX_ELEMS];

	/**
	 * Constructs a new {@link GenericMatrixFilter} from the given coefficients.
	 * 
	 * @param coefficients
	 *            a <code>double</code> array containing the channel
	 *            coefficients
	 * @see #GenericMatrixFilter(double, double, double, double, double, double,
	 *      double, double, double, double, double, double, double, double,
	 *      double, double, double, double, double, double)
	 */
	public GenericMatrixFilter(double... coefficients) {
		this.channelMatrix = Arrays.copyOf(coefficients, MATRIX_ELEMS);
	}

	/**
	 * <p>
	 * Constructs a new {@link GenericMatrixFilter} from the given coefficients.
	 * </p>
	 * 
	 * <p>
	 * The resulting pixel values are computed by adding the products
	 * </p>
	 * 
	 * @param aa
	 *            the alpha coefficient for the new alpha value
	 * @param ar
	 *            the red coefficient for the new alpha value
	 * @param ag
	 *            the green coefficient for the new alpha value
	 * @param ab
	 *            the blue coefficient for the new alpha value
	 * @param ac
	 *            the constant addend for the new alpha value
	 * @param ra
	 *            the alpha coefficient for the new red value
	 * @param rr
	 *            the red coefficient for the new red value
	 * @param rg
	 *            the green coefficient for the new red value
	 * @param rb
	 *            the blue coefficient for the new red value
	 * @param rc
	 *            the constant addend for the new red value
	 * @param ga
	 *            the alpha coefficient for the new green value
	 * @param gr
	 *            the red coefficient for the new green value
	 * @param gg
	 *            the green coefficient for the new green value
	 * @param gb
	 *            the blue coefficient for the new green value
	 * @param gc
	 *            the constant addend for the new green value
	 * @param ba
	 *            the alpha coefficient for the new blue value
	 * @param br
	 *            the red coefficient for the new blue value
	 * @param bg
	 *            the green coefficient for the new blue value
	 * @param bb
	 *            the blue coefficient for the new blue value
	 * @param bc
	 *            the constant addend for the new blue value
	 */
	public GenericMatrixFilter(double aa, double ar, double ag, double ab,
			double ac, double ra, double rr, double rg, double rb, double rc,
			double ga, double gr, double gg, double gb, double gc, double ba,
			double br, double bg, double bb, double bc) {
		this(new double[] { aa, ar, ag, ab, ac, ra, rr, rg, rb, rc, ga, gr, gg,
				gb, gc, ba, br, bg, bb, bc });
	}

	@Override
	protected void filter(int[] argbIn, int[] argbOut) {
		int i = 0;
		for (int j = 0; j < 4; j++) {
			double sum = 0;
			for (int k = 0; k < 4; k++) {
				sum += channelMatrix[i++] * argbIn[k];
			}
			argbOut[j] = Math.max(0,
					Math.min(255, (int) (sum + channelMatrix[i++])));
		}
	}

}
