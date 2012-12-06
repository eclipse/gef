package org.eclipse.gef4.graphics.render;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.Image;

/**
 * An ImageFill is a specific {@link IFillMode} implementation used to fill an
 * area with image data.
 * 
 * @author mwienand
 * 
 */
public class ImageFill implements IFillMode {

	private static int circularIndex(int index, int size) {
		return index - ((int) Math.floor(((double) index) / size)) * size;
	}

	private Image image;

	public ImageFill(Image image) {
		setImage(image);
	}

	// @Override
	// public boolean equals(Object obj) {
	// if (obj instanceof ImageFill) {
	// ImageFill o = (ImageFill) obj;
	// return o.image.equals(image);
	// }
	// return false;
	// }

	// @Override
	// public int hashCode() {
	// return image.hashCode();
	// }

	@Override
	protected ImageFill clone() throws CloneNotSupportedException {
		return getCopy();
	}

	@Override
	public Color getColorAt(Point p) {
		int x = circularIndex((int) p.x, image.getWidth());
		int y = circularIndex((int) p.y, image.getHeight());
		return new Color(image.getPixel(x, y));
	}

	@Override
	public ImageFill getCopy() {
		return new ImageFill(image);
	}

	public Image image() {
		return image;
	}

	public ImageFill setImage(Image image) {
		this.image = image.getCopy();
		return this;
	}

	@Override
	public String toString() {
		return "ImageFill(image = " + image + ")";
	}

}
