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

import org.eclipse.gef4.graphics.images.IImageOperation;

// TODO: delegate methods to BufferedImage

/**
 * An {@link Image} stores color and alpha data for a rectangular pixel raster.
 * 
 * @author mwienand
 * 
 */
public class Image {

	/**
	 * The number of data channels in an {@link Image}. GEF4 Graphics
	 * {@link Image}s do always consist of three color channels (red, green, and
	 * blue) and one alpha channel. Currently, the number of channels is kept
	 * constant to ease computations, but this may change in a future version.
	 */
	public static final int NUM_CHANNELS = 4;

	/**
	 * Stores the image data. TODO: Delegate {@link Image}'s methods to this
	 * {@link BufferedImage}.
	 */
	private BufferedImage bufferedImage = null;

	// TODO: add default constructor. (What should its semantics be?)
	// TODO: add (width, height, ?background-color?) constructor.

	/**
	 * Constructs a new {@link Image} from the given {@link BufferedImage}.
	 * 
	 * @param bufferedImage
	 */
	public Image(BufferedImage bufferedImage) {
		setTo(bufferedImage);
	}

	/**
	 * Constructs a new {@link Image} from the given image file {@link URL}.
	 * 
	 * @param imageFile
	 *            a {@link URL} locating the image file to load
	 * @throws IOException
	 *             in case no {@link Image} can be constructed from the given
	 *             {@link URL}
	 */
	public Image(URL imageFile) throws IOException {
		BufferedImage tmp = ImageIO.read(imageFile);
		setTo(tmp);
	}

	/**
	 * <p>
	 * Returns the result of the given {@link IImageOperation} on
	 * <code>this</code> {@link Image} as a new {@link Image}. <code>this</code>
	 * {@link Image} is not modified.
	 * </p>
	 * 
	 * <p>
	 * This is a convenience method to apply several {@link IImageOperation}s in
	 * a row to one {@link Image}: <blockquote>
	 * 
	 * <pre>
	 * image.apply(op0).apply(op1)...;
	 * </pre>
	 * 
	 * </blockquote>
	 * </p>
	 * 
	 * @param op
	 *            the {@link IImageOperation} to apply
	 * @return the result {@link Image} of the given {@link IImageOperation} on
	 *         <code>this</code> {@link Image}
	 */
	public Image apply(IImageOperation op) {
		return op.apply(this);
	}

	/**
	 * Returns the {@link BufferedImage} that is used to store the image data of
	 * <code>this</code> {@link Image}.
	 * 
	 * TODO: add note: method name convention: start without 'get', so the
	 * _real_ object is returned, and not just a copy of it.
	 * 
	 * @return the {@link BufferedImage} that is used to store the image data of
	 *         <code>this</code> {@link Image}
	 */
	public BufferedImage bufferedImage() {
		return bufferedImage;
	}

	/**
	 * Returns a copy of <code>this</code> {@link Image}.
	 * 
	 * @return a copy of <code>this</code> {@link Image}
	 */
	public Image getCopy() {
		return new Image(this.bufferedImage);
	}

	/**
	 * Returns the height of <code>this</code> {@link Image}.
	 * 
	 * TODO: add note: this is a delegation
	 * 
	 * @return the height of <code>this</code> {@link Image}
	 */
	public int getHeight() {
		return bufferedImage.getHeight();
	}

	/**
	 * Returns the ARGB pixel value at the given position.
	 * 
	 * @param x
	 *            x-coordinate of the pixel to return
	 * @param y
	 *            y-coordinate of the pixel to return
	 * @return the ARGB pixel value at the given position
	 */
	public int getPixel(int x, int y) {
		if (x < 0 || x >= getWidth()) {
			throw new IllegalArgumentException("x-coordinate (" + x
					+ ") out of image bounds (0-" + getWidth() + ").");
		}
		if (y < 0 || y >= getHeight()) {
			throw new IllegalArgumentException("y-coordinate (" + y
					+ ") out of image bounds (0-" + getHeight() + ").");
		}
		return bufferedImage.getRGB(x, y);
	}

	/**
	 * Returns the width of <code>this</code> {@link Image}.
	 * 
	 * TODO: add note: this is a delegation
	 * 
	 * @return the width of <code>this</code> {@link Image}
	 */
	public int getWidth() {
		return bufferedImage.getWidth();
	}

	/**
	 * Assigns the given ARGB value to the pixel at the passed-in position.
	 * 
	 * @param x
	 *            x-coordinate of the pixel to assign a new value
	 * @param y
	 *            y-coordinate of the pixel to assign a new value
	 * @param argb
	 *            the new ARGB value for the pixel
	 */
	public void setPixel(int x, int y, int argb) {
		if (x < 0 || x >= getWidth()) {
			throw new IllegalArgumentException("x-coordinate (" + x
					+ ") out of image bounds (0-" + getWidth() + ").");
		}
		if (y < 0 || y >= getHeight()) {
			throw new IllegalArgumentException("y-coordinate (" + y
					+ ") out of image bounds (0-" + getHeight() + ").");
		}
		bufferedImage.setRGB(x, y, argb);
	}

	/**
	 * Replaces the {@link BufferedImage} that is managed by <code>this</code>
	 * {@link Image} with the given <i>replacement</i>.
	 * 
	 * @param replacement
	 *            the new {@link BufferedImage} to manage
	 */
	protected void setTo(BufferedImage replacement) {
		bufferedImage = new BufferedImage(replacement.getWidth(),
				replacement.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bufferedImage.createGraphics();
		g2d.drawImage(replacement, null, 0, 0);
		g2d.dispose();
	}

	@Override
	public String toString() {
		// TODO: we need a smarter output, possibly containing the image's width
		// and height, number of channels, bit depth, etc.
		return "Image(bufferedImage = " + bufferedImage + ")";
	}

}