package org.eclipse.gef4.graphics;

import org.eclipse.gef4.geometry.planar.Dimension;

/**
 * Provides utility methods for {@link Image}s.
 * 
 * @author mwienand
 * 
 */
public interface IImageUtils {

	/**
	 * Returns a {@link Dimension} representing the width and height of the
	 * given {@link Image}.
	 * 
	 * @param image
	 * @return a {@link Dimension} representing the width and height of the
	 *         given {@link Image}
	 */
	Dimension getImageDimension(Image image);

}
