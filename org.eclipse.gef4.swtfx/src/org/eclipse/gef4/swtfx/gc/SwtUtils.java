/*******************************************************************************
 * Copyright (c) 2012, 2013 itemis AG and others.
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
package org.eclipse.gef4.swtfx.gc;

import org.eclipse.gef4.geometry.convert.swt.Geometry2SWT;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Transform;

public class SwtUtils {

	/**
	 * Creates an {@link Image} filled with the given {@link Gradient}. The
	 * specified {@link Rectangle} determines the {@link Image}'s width and
	 * height.
	 * 
	 * @param bounds
	 * @param gradient
	 * @return
	 */
	public static ImageData createGradientImageData(Device dev,
			Rectangle bounds, Gradient<?> gradient) {
		int w = (int) bounds.getWidth();
		int h = (int) bounds.getHeight();

		PaletteData paletteData = new PaletteData(0xff0000, 0xff00, 0xff);
		ImageData imageData = new ImageData(w, h, 32, paletteData);

		Point p = new Point();
		int[] pixels = new int[w];
		for (int y = 0; y < h; y++) {
			p.y = y;
			for (int x = 0; x < w; x++) {
				p.x = x;
				RgbaColor colorAt = gradient.getColorAt(p);
				pixels[x] = RgbaColor.getPixel(colorAt.getRGBA());
			}
			imageData.setPixels(0, y, w, pixels, 0);
		}

		return imageData;
	}

	public static RgbaColor createRgbaColor(Color fillColor, int alpha) {
		return new RgbaColor(fillColor.getRed(), fillColor.getGreen(),
				fillColor.getBlue(), alpha);
	}

	/**
	 * <p>
	 * Converts a {@link RgbaColor} to an SWT {@link Color}.
	 * </p>
	 * 
	 * <p>
	 * Note, that the alpha component of the {@link RgbaColor} cannot be
	 * preserved by this conversion as an SWT {@link Color} does not have an
	 * alpha attribute.
	 * </p>
	 * 
	 * @param color
	 * @return
	 */
	public static org.eclipse.swt.graphics.Color createSwtColor(Device dev,
			RgbaColor color) {
		return new org.eclipse.swt.graphics.Color(dev, color.getRed(),
				color.getGreen(), color.getBlue());
	}

	public static org.eclipse.swt.graphics.Path createSwtPath(Path path,
			Device device) {
		org.eclipse.swt.graphics.Path swtPath = new org.eclipse.swt.graphics.Path(
				device, Geometry2SWT.toSWTPathData(path));
		return swtPath;
	}

	/**
	 * <p>
	 * Given two arrays <i>xs</i> and <i>ys</i>, where <code>xs[i]</code>
	 * denotes the x coordinate of point number i and <code>ys[i]</code> denotes
	 * the y coordinate of point number i, this function generates one array
	 * containing alternating x and y coordinates.
	 * </p>
	 * 
	 * @param xs
	 *            x coordinates
	 * @param ys
	 *            y coordinates
	 * @param n
	 *            number of points
	 * @return <code>int</code> array of alternating x and y coordinates
	 */
	public static int[] createSwtPointsArray(double[] xs, double[] ys, int n) {
		int[] pts = new int[2 * n];
		for (int i = 0; i < n; i++) {
			pts[2 * i] = (int) xs[i];
			pts[2 * i + 1] = (int) ys[i];
		}
		return pts;
	}

	public static Transform createSwtTransform(AffineTransform at, Device dev) {
		Transform t = new Transform(dev);
		double[] m = at.getMatrix();
		t.setElements((float) m[0], (float) m[1], (float) m[2], (float) m[3],
				(float) m[4], (float) m[5]);
		return t;
	}

}
