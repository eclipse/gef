package org.eclipse.gef4.graphics.render;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graphics.Color;

/**
 * When a ColorFill {@link IFillMode} implementation is applied, the
 * {@link IGraphics#fill(org.eclipse.gef4.geometry.planar.Path)
 * IGraphics#fill()} methods will fill the given objects with a single
 * {@link Color}.
 * 
 * @see GradientFill
 * @see ImageFill
 * @author mwienand
 * 
 */
public class ColorFill implements IFillMode {

	private Color color = null;

	public ColorFill() {
		color = new Color();
	}

	public ColorFill(Color color) {
		this();
		setColor(color);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return getCopy();
	}

	public Color getColor() {
		return color.getCopy();
	}

	@Override
	public Color getColorAt(Point p) {
		return color.getCopy();
	}

	@Override
	public IFillMode getCopy() {
		return new ColorFill(color);
	}

	public void setColor(Color color) {
		if (color == null) {
			throw new IllegalArgumentException(
					"The value of the color parameter may not be null.");
		}
		this.color.setTo(color);
	}

}
