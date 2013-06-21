/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
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
package org.eclipse.gef4.swt.canvas;

import java.util.Arrays;

import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swt.canvas.gc.GraphicsContext;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * This {@link CanvasFigure} is counter-part of the JavaFx Canvas. Therefore it
 * provides a {@link GraphicsContext} which can be used to perform drawing
 * operations on it.
 * 
 * @author mwienand
 * 
 */
public class CanvasFigure extends AbstractFigure {

	private Device dev;
	private Image image;
	private GC gc;
	private GraphicsContext g;
	private double width;
	private double height;
	private int imgWidth;
	private int imgHeight;

	public CanvasFigure(Device dev, double width, double height,
			RGB transparentPixel) {
		if (width < 0) {
			throw new IllegalArgumentException("width < 0");
		}
		if (height < 0) {
			throw new IllegalArgumentException("height < 0");
		}
		this.dev = dev;
		this.width = width;
		this.height = height;
		imgWidth = (int) width + 1;
		imgHeight = (int) height + 1;
		ImageData id = new ImageData(imgWidth, imgHeight, 32, new PaletteData(
				0xff0000, 0xff00, 0xff));
		clear(id);
	}

	public CanvasFigure(double width, double height) {
		this(Display.getCurrent(), width, height, new RGB(255, 255, 255));
	}

	public void clear() {
		ImageData id = image.getImageData();
		clear(id);
	}

	private void clear(ImageData id) {
		// FIXME: we set the alphas to 255 (opaque) because the GC does not set
		// alpha when drawing, so that nothing would be visible if we used 0
		// (transparent)
		byte[] alphas = new byte[id.width];
		Arrays.fill(alphas, (byte) 255);

		int[] pixels = new int[id.width];
		Arrays.fill(pixels, id.palette.getPixel(new RGB(255, 255, 255))); // white

		for (int y = 0; y < id.height; y++) {
			id.setPixels(0, y, id.width, pixels, 0);
			id.setAlphas(0, y, id.width, alphas, 0);
		}

		if (image != null) {
			g.cleanUp();
			gc.dispose();
			image.dispose();
		}
		image = new Image(dev, id);
		gc = new GC(image);
		g = new GraphicsContext(gc);
	}

	@Override
	public IBounds getBounds() {
		return new GeneralBounds(new Rectangle(0, 0, width, height),
				getPaintStateByReference().getTransformByReference());
	}

	public GraphicsContext getGraphicsContext() {
		return g;
	}

	public double getHeight() {
		return height;
	}

	public Image getImage() {
		return image;
	}

	public double getWidth() {
		return width;
	}

	@Override
	public void paint(GraphicsContext g) {
		g.drawImage(image, 0, 0);
	}

}
