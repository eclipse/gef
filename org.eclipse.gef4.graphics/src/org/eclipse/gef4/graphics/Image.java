/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
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
package org.eclipse.gef4.graphics;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.images.ICompositionRule;
import org.eclipse.gef4.graphics.images.IImageFilter;

/**
 * An {@link Image} is a lightweight object that stores a {@link URL} to an
 * image file.
 * 
 * @author mwienand
 * 
 */
public class Image {

	private static void readPixel(BufferedImage img, int x, int y, int[] argb) {
		int pixel = img.getRGB(x, y);
		argb[0] = (pixel & 0xff000000) >>> 24;
		argb[1] = (pixel & 0xff0000) >>> 16;
		argb[2] = (pixel & 0xff00) >>> 8;
		argb[3] = (pixel & 0xff);
	}

	private static void writePixel(BufferedImage img, int x, int y, int[] argb) {
		int pixel = argb[0] << 24;
		pixel |= argb[1] << 16;
		pixel |= argb[2] << 8;
		pixel |= argb[3];
		img.setRGB(x, y, pixel);
	}

	// private URL imageFile;

	protected BufferedImage bufferedImage = null;

	public Image(BufferedImage bufferedImage) {
		this.bufferedImage = new BufferedImage(bufferedImage.getWidth(),
				bufferedImage.getHeight(), bufferedImage.getType());
		Graphics2D g2d = this.bufferedImage.createGraphics();
		g2d.drawImage(bufferedImage, null, 0, 0);
		g2d.dispose();
	}

	/**
	 * Tries to load the given image file into a new {@link BufferedImage} using
	 * the {@link ImageIO#read(URL)} method.
	 * 
	 * @param imageFile
	 */
	public Image(URL imageFile) {
		try {
			bufferedImage = ImageIO.read(imageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// this.imageFile = imageFile;
	}

	public BufferedImage bufferedImage() {
		return bufferedImage;
	}

	// TODO: move the composition into a composition class, alter the interface
	public Image getComposed(ICompositionRule rule, Image other, int offsetX,
			int offsetY) {
		BufferedImage imgA = this.bufferedImage;
		BufferedImage imgB = other.bufferedImage;

		Rectangle areaA = new Rectangle(0, 0, imgA.getWidth(), imgA.getHeight());
		Rectangle areaB = new Rectangle(offsetX, offsetY, imgB.getWidth(), imgB.getHeight());
		Rectangle areaR = areaA.getUnioned(areaB);
		Rectangle intersection = areaA.getIntersected(areaB);

		BufferedImage imgR = new BufferedImage((int) areaR.getWidth(), (int) areaR.getHeight(), this.bufferedImage.getType());

		int[] a = new int[4];
		int[] b = new int[4];
		int[] r = new int[4];

		for (int x = (int) intersection.getX(); x < (int) intersection
				.getWidth(); x++) {
			for (int y = (int) intersection.getY(); y < (int) intersection
					.getHeight(); y++) {
				readPixel(imgA, x, y, a);
				readPixel(imgB, x, y, b);
				rule.compose(a, b, r);
				writePixel(imgR, x, y, r);
			}
		}

		return new Image(imgR);
	}

	public Image getCopy() {
		return new Image(this.bufferedImage);
	}

	public Image getFiltered(IImageFilter filter) {
		return filter.apply(this);
	}

	public int getHeight() {
		return bufferedImage.getHeight();
	}

	public int getWidth() {
		return bufferedImage.getWidth();
	}

	@Override
	public String toString() {
		return "Image(bufferedImage = " + bufferedImage + ")";
	}

}