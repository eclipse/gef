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
package org.eclipse.gef4.swt.fx;

import java.util.Arrays;

import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swt.fx.gc.GraphicsContext;
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

	/*
	 * TODO: private IBlendOperation blender;
	 * 
	 * Create an interface IBlendOperation which provides a single method
	 * blend(Image source, Image destination) : Image. The blender is used to
	 * merge source and destination images, i.e. the CanvasFigure's image and
	 * the underlying drawings.
	 * 
	 * MAYBE: private Effect effect;
	 * 
	 * The specified Effect can be used to blend the CanvasFigure's image with
	 * the underlying drawings. A CompositeEffect can be used to allow combined
	 * Effects on a CanvasFigure. Effects should only be allowed if available on
	 * any IFigure, at least, or ideally on any INode.
	 */

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

	@Override
	public void absoluteToLocal(Point absoluteIn, Point localOut) {
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

		// FIXME: use a resource pool to get rid of the Image, the GC, and the
		// resources of the GraphicsContext
		image = new Image(dev, id);
		gc = new GC(image);
		g = new GraphicsContext(gc);
	}

	@Override
	public boolean contains(double localX, double localY) {
		return getLayoutBounds().contains(localX, localY);
	}

	private void forceImageUpdate() {
		// FIXME: ~5-10 millis (way too much!)
		ImageData imageData = image.getImageData();
		g.cleanUp();
		gc.dispose();
		image.dispose();
		image = new Image(dev, imageData);
		gc = new GC(image);
		g = new GraphicsContext(gc);
	}

	@Override
	public Rectangle getBoundsInLocal() {
		return getLayoutBounds();
	}

	@Override
	public Path getClipPath() {
		// TODO Auto-generated method stub
		return null;
	}

	public GraphicsContext getGraphicsContext() {
		/*
		 * XXX: The underlying Image is sometimes not updated when the user
		 * draws into it using the returned GraphicsContext. The call to
		 * forceImageUpdate() fixes this strange behavior, but we should find
		 * out in detail what is going wrong.
		 */
		forceImageUpdate();
		return g;
	}

	public double getHeight() {
		return height;
	}

	public Image getImage() {
		return image;
	}

	@Override
	public Rectangle getLayoutBounds() {
		return new Rectangle(0, 0, width, height);
	}

	public double getWidth() {
		return width;
	}

	@Override
	public void paint(GraphicsContext g) {
		g.drawImage(image, 0, 0);
	}

	@Override
	public void setClipPath(Path clipPath) {
	}

	@Override
	public String toString() {
		return "CanvasFigure(" + width + "x" + height + ")";
	}

}
