package org.eclipse.gef4.graphics.images;

import org.eclipse.gef4.graphics.Image;

/**
 * An ICompositionRule implementation is used to compose two {@link Image}s
 * pixel by pixel.
 * 
 * @author mwienand
 * 
 */
public interface ICompositionRule {

	/**
	 * <p>
	 * The {@link #compose(int[], int[], int[]) compose} method computes the
	 * result of the composition of two pixels. Note that the individual color
	 * (and alpha) components of the pixels are integer values in the range
	 * <code>[0;255]</code>.
	 * </p>
	 * 
	 * <p>
	 * For example, a simple AddComposite could look like this:
	 * 
	 * <pre>
	 * <blockquote> public class AddComposite implements ICompositionRule {
	 *     public void compose(int[] a, int[] b, int[] r) {
	 *         for (int i = 0; i &lt; 4; i++) {
	 *             r[i] = Math.min(255, a[i] + b[i]);
	 *         }
	 *     }
	 * }</blockquote>
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param a
	 *            an <code>int</code> array containing the alpha, red, green,
	 *            and blue components of the first pixel
	 * @param b
	 *            an <code>int</code> array containing the alpha, red, green,
	 *            and blue components of the first pixel
	 * @param r
	 *            an <code>int</code> array to store the alpha, red, green, and
	 *            blue components of the resulting pixel
	 */
	void compose(int[] a, int[] b, int[] r);

}
