package org.eclipse.gef4.graphics;

import org.eclipse.gef4.geometry.planar.Dimension;

/**
 * Provides utility methods for {@link Font}s.
 * 
 * @author mwienand
 * 
 */
public interface IFontUtils {

	/**
	 * Returns the width and height required to display the given {@link String}
	 * with the {@link Font} that is currently set in the
	 * {@link IWriteProperties} of the given {@link IGraphics} as a
	 * {@link Dimension}.
	 * 
	 * @param graphics
	 * @param text
	 * @return a {@link Dimension} representing the width and height required to
	 *         display the given {@link String} with the {@link Font} currently
	 *         set in the {@link IWriteProperties} of the given
	 *         {@link IGraphics} {@link IWriteProperties#getFont() font}
	 */
	Dimension getTextDimension(String text);

}
