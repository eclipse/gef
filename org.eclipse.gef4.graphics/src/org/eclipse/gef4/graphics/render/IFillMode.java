package org.eclipse.gef4.graphics.render;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graphics.Color;

/**
 * <p>
 * The IFillMode interface provides the {@link #getColorAt(Point)} method which
 * returns the {@link Color} for a given {@link Point} of an area which is to be
 * {@link IGraphics#fill(org.eclipse.gef4.geometry.planar.Path) filled}.
 * </p>
 * 
 * <p>
 * Various IFillMode implementations exist to mime the behavior available via
 * one drawing toolkit that does not exist in another drawing toolkit:
 * <ul>
 * <li>{@link ColorFill}</li>
 * <li>{@link GradientFill}</li>
 * <li>{@link ImageFill}</li>
 * </ul>
 * </p>
 * 
 * @author mwienand
 * 
 */
public interface IFillMode extends Cloneable {

	/**
	 * Returns the {@link Color} to fill the given {@link Point} with.
	 * 
	 * @param p
	 *            the {@link Point} to fill with {@link Color}
	 * @return the {@link Color} to fill the given {@link Point} with
	 */
	Color getColorAt(Point p);

	/**
	 * Returns a copy of this {@link IFillMode}.
	 * 
	 * @return a copy of this {@link IFillMode}
	 */
	IFillMode getCopy();

}
