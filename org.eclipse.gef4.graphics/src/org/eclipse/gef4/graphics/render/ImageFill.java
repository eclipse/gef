package org.eclipse.gef4.graphics.render;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.Image;

public class ImageFill implements IFillMode {

	private Image image;

	// private int offsetX;
	// private int offsetY;

	public ImageFill(Image image) {
		setImage(image);
	}

	@Override
	protected ImageFill clone() throws CloneNotSupportedException {
		return getCopy();
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
	public Color getColorAt(Point p) {
		return new Color(image.getPixel((int) p.x, (int) p.y));
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
