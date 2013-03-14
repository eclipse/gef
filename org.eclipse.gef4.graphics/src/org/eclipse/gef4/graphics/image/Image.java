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
package org.eclipse.gef4.graphics.image;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.InterpolationHint;
import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.image.operations.IImageOperation;

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

	private static BufferedImage getCopy(BufferedImage bufferedImage) {
		BufferedImage copy = new BufferedImage(bufferedImage.getWidth(),
				bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = copy.createGraphics();
		g2d.drawImage(bufferedImage, null, 0, 0);
		g2d.dispose();
		return copy;
	}

	/**
	 * Stores the image data.
	 */
	private BufferedImage bufferedImage = null;

	/**
	 * Constructs a new {@link Image} with a width and height of <code>0</code>.
	 */
	public Image() {
		this(1, 1, new Color());
	}

	/**
	 * Constructs a new {@link Image} from the given {@link BufferedImage}.
	 * 
	 * @param bufferedImage
	 */
	public Image(BufferedImage bufferedImage) {
		this.bufferedImage = getCopy(bufferedImage);
	}

	/**
	 * Constructs a new {@link Image} with the specified width and height.
	 * 
	 * @param w
	 *            width of the {@link Image}
	 * @param h
	 *            height of the {@link Image}
	 */
	public Image(int w, int h) {
		this(w, h, new Color());
	}

	/**
	 * Constructs a new {@link Image} with the specified width and height. Fills
	 * the {@link Image} with the specified background {@link Color}.
	 * 
	 * @param w
	 *            width of the {@link Image}
	 * @param h
	 *            height of the {@link Image}
	 * @param bg
	 *            background {@link Color} of the {@link Image}
	 * 
	 */
	public Image(int w, int h, Color bg) {
		this(w, h, Color.getPixel(bg.getRGBA()));
	}

	/**
	 * Constructs a new {@link Image} with the specified width and height. Fills
	 * the {@link Image} with the specified background {@link Color} which is
	 * represented by an integer pixel value in the pixel format ARGB, i.e. the
	 * first byte stores the alpha channel value, the second byte stores the red
	 * channel value, the third byte stores the green channel value, and the
	 * forth byte stores the blue channel value.
	 * 
	 * @param w
	 *            width of the {@link Image}
	 * @param h
	 *            height of the {@link Image}
	 * @param bgPixel
	 *            background color of the {@link Image} in ARGB pixel format
	 */
	public Image(int w, int h, int bgPixel) {
		bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bufferedImage.createGraphics();
		g.setColor(new java.awt.Color(bgPixel));
		g.fillRect(0, 0, w, h);
		g.dispose();
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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Image) {
			// quick acceptance test for identity
			if (obj == this) {
				return true;
			}

			Image o = (Image) obj;

			// quick rejection test for width and height
			if (getWidth() != o.getWidth() || getHeight() != o.getHeight()) {
				return false;
			}

			// TODO: Benchmark which is faster and check if it is okay to
			// allocate the memory for both pixel arrays.
			// return Arrays.equals(getPixels(), o.getPixels());

			for (int x = 0; x < getWidth(); x++) {
				for (int y = 0; y < getHeight(); y++) {
					if (o.getPixel(x, y) != getPixel(x, y)) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * <p>
	 * Returns a histogram of the alpha-channel values used in this
	 * {@link Image}.
	 * </p>
	 * 
	 * <p>
	 * The histogram is represented by an <code>int</code>-array with
	 * <code>256</code> elements. The number of elements equals the number of
	 * possible alpha-channel values. At every index, the number of pixels with
	 * an alpha-channel value equal to that index is stored in the array.
	 * </p>
	 * 
	 * @return a histogram of the alpha-channel values used in this
	 *         {@link Image}
	 */
	public int[] getAlphaHistogram() {
		int[] hist = new int[256];

		for (int i = 0; i < hist.length; i++) {
			hist[i] = 0;
		}

		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				hist[Color.getPixelAlpha(getPixel(x, y))]++;
			}
		}

		return hist;
	}

	/**
	 * <p>
	 * Returns a histogram of the blue-channel values used in this {@link Image}
	 * .
	 * </p>
	 * 
	 * <p>
	 * The histogram is represented by an <code>int</code>-array with
	 * <code>256</code> elements. The number of elements equals the number of
	 * possible blue-channel values. At every index, the number of pixels with a
	 * blue-channel value equal to that index is stored in the array.
	 * </p>
	 * 
	 * @return a histogram of the blue-channel values used in this {@link Image}
	 */
	public int[] getBlueHistogram() {
		int[] hist = new int[256];

		for (int i = 0; i < hist.length; i++) {
			hist[i] = 0;
		}

		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				hist[Color.getPixelBlue(getPixel(x, y))]++;
			}
		}

		return hist;
	}

	/**
	 * Returns the bounds of this {@link Image}, i.e. a {@link Rectangle} at
	 * location <code>(0, 0)</code> and the {@link Image}'s width and height.
	 * 
	 * @return the bounds of this {@link Image}
	 */
	public Rectangle getBounds() {
		return new Rectangle(0, 0, getWidth(), getHeight());
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
	 * <p>
	 * Returns a histogram of the green-channel values used in this
	 * {@link Image}.
	 * </p>
	 * 
	 * <p>
	 * The histogram is represented by an <code>int</code>-array with
	 * <code>256</code> elements. The number of elements equals the number of
	 * possible green-channel values. At every index, the number of pixels with
	 * a green-channel value equal to that index is stored in the array.
	 * </p>
	 * 
	 * @return a histogram of the green-channel values used in this
	 *         {@link Image}
	 */
	public int[] getGreenHistogram() {
		int[] hist = new int[256];

		for (int i = 0; i < hist.length; i++) {
			hist[i] = 0;
		}

		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				hist[Color.getPixelGreen(getPixel(x, y))]++;
			}
		}

		return hist;
	}

	/**
	 * Returns the height of <code>this</code> {@link Image}.
	 * 
	 * @return the height of <code>this</code> {@link Image}
	 */
	public int getHeight() {
		return bufferedImage.getHeight();
	}

	/**
	 * <p>
	 * Returns a histogram of the pixel-intensity values used in this
	 * {@link Image}.
	 * </p>
	 * 
	 * <p>
	 * The histogram is represented by an <code>int</code>-array with
	 * <code>256</code> elements. The number of elements equals the number of
	 * possible pixel-intensity values. At every index, the number of pixels
	 * with an intensity value equal to that index is stored in the array.
	 * </p>
	 * 
	 * @return a histogram of the pixel-intensity values used in this
	 *         {@link Image}
	 */
	public int[] getIntensityHistogram() {
		int[] hist = new int[256];

		for (int i = 0; i < hist.length; i++) {
			hist[i] = 0;
		}

		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				hist[Color.computePixelIntensity(getPixel(x, y))]++;
			}
		}

		return hist;
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
	 * Returns the pixel values of the specified row as an <code>int</code>
	 * array.
	 * 
	 * @param y
	 *            the row to extract (starting at <code>0</code>)
	 * @return an <code>int</code> array containing the pixel values of the
	 *         specified row
	 */
	public int[] getPixelRow(int y) {
		// TODO: check if this is correct
		return bufferedImage.getRGB(0, y, getWidth(), 1, new int[getWidth()],
				0, getWidth());
	}

	/**
	 * Returns an <code>int</code> array containing all pixels of this
	 * {@link Image}. You can access a particular pixel at position (x,y) as
	 * follows:
	 * 
	 * <blockquote>
	 * 
	 * <pre>
	 * int[] pixels = image.getPixels();
	 * int at_x_y = pixels[y * image.getWidth() + x];
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @return an <code>int</code> array containing all pixels of this
	 *         {@link Image}
	 */
	public int[] getPixels() {
		int w = getWidth();
		int h = getHeight();
		return getPixels(0, 0, w, h, new int[w * h], 0, w);
	}

	/**
	 * Returns an <code>int</code> array containing the pixels of this
	 * {@link Image} that are inside of the specified rectangular area. You can
	 * access a particular pixel at position (x,y) as follows:
	 * 
	 * <blockquote>
	 * 
	 * <pre>
	 * int[] pixels = image.getPixels(x0, y0, w, h);
	 * int at_x_y = pixels[(y - y0) * w + x - x0];
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param x0
	 *            the x coordinate of the top left corner of the rectangular
	 *            area to extract
	 * @param y0
	 *            the y coordinate of the top left corner of the rectangular
	 *            area to extract
	 * @param w
	 *            the width of the rectangular area to extract
	 * @param h
	 *            the height of the rectangular area to extract
	 * @param pixels
	 *            an <code>int</code> array where the pixel values are stored
	 * @param offset
	 *            the offset for indexing the <i>pixels</i> array
	 * @param pitch
	 *            the number of <code>int</code> values in one row of the
	 *            <i>pixels</i> array
	 * @return an <code>int</code> array containing the pixels of this
	 *         {@link Image} that are inside of the specified rectangular area
	 * @throws IllegalArgumentException
	 *             unless <code>0 <= <i>x0</i> < getWidth()</code>
	 * @throws IllegalArgumentException
	 *             unless <code>0 <= <i>w</i> < getWidth()</code>
	 * @throws IllegalArgumentException
	 *             unless <code>0 <= <i>y0</i> < getHeight()</code>
	 * @throws IllegalArgumentException
	 *             unless <code>0 <= <i>h</i> < getHeight()</code>
	 * @throws IllegalArgumentException
	 *             unless <code><i>offset</i> > 0</code>
	 * @throws IllegalArgumentException
	 *             unless <code><i>pitch</i> > 0</code>
	 * @throws IllegalArgumentException
	 *             unless <code><i>pixels</i> != null</code>
	 * @throws IllegalArgumentException
	 *             unless
	 *             <code><i>pixels</i>.length >= <i>offset</i> + <i>h</i> * <i>pitch</i></code>
	 */
	public int[] getPixels(int x0, int y0, int w, int h, int[] pixels,
			int offset, int pitch) {
		if (x0 < 0) {
			throw new IllegalArgumentException("x0 (" + x0 + ") < 0");
		}
		if (y0 < 0) {
			throw new IllegalArgumentException("y0 (" + y0 + ") < 0");
		}
		if (x0 + w > getWidth()) {
			throw new IllegalArgumentException("x0 + w (" + (x0 + w)
					+ ") > width (" + getWidth() + ")");
		}
		if (y0 + h > getHeight()) {
			throw new IllegalArgumentException("y0 + h (" + (y0 + h)
					+ ") > height (" + getHeight() + ")");
		}
		if (offset < 0) {
			throw new IllegalArgumentException("offset (" + offset + ") < 0");
		}
		if (pitch < 0) {
			throw new IllegalArgumentException("pitch (" + pitch + ") < 0");
		}
		if (pixels == null) {
			throw new IllegalArgumentException("pixels == null");
		}
		if (pixels.length < offset + h * pitch) {
			throw new IllegalArgumentException("pixels.length ("
					+ pixels.length + ") < offset + h * pitch ("
					+ (offset + h * pitch) + ")");
		}
		return bufferedImage.getRGB(x0, y0, w, h, pixels, offset, pitch);
	}

	/**
	 * <p>
	 * Returns a histogram of the red-channel values used in this {@link Image}.
	 * </p>
	 * 
	 * <p>
	 * The histogram is represented by an <code>int</code>-array with
	 * <code>256</code> elements. The number of elements equals the number of
	 * possible red-channel values. At every index, the number of pixels with a
	 * red-channel value equal to that index is stored in the array.
	 * </p>
	 * 
	 * @return a histogram of the red-channel values used in this {@link Image}
	 */
	public int[] getRedHistogram() {
		int[] hist = new int[256];

		for (int i = 0; i < hist.length; i++) {
			hist[i] = 0;
		}

		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				hist[Color.getPixelRed(getPixel(x, y))]++;
			}
		}

		return hist;
	}

	/**
	 * <p>
	 * Returns a scaled version of this {@link Image}. The dimensions of the
	 * resulting {@link Image} are computed by multiplying this {@link Image}'s
	 * dimensions with the given <i>scaleFactor</i>.
	 * </p>
	 * 
	 * <p>
	 * The interpolation is done exactly as with drawing an {@link Image}
	 * transformed with an {@link IGraphics} using the
	 * {@link InterpolationHint#QUALITY QUALITY} {@link InterpolationHint}.
	 * </p>
	 * 
	 * @param scaleFactor
	 * @return a scaled version of this {@link Image}
	 */
	public Image getScaled(double scaleFactor) {
		int newWidth = (int) (getWidth() * scaleFactor);
		int newHeight = (int) (getHeight() * scaleFactor);
		return getScaledTo(newWidth, newHeight);
	}

	/**
	 * Returns a version of this {@link Image} that is scaled to the passed-in
	 * <i>width</i> and <i>height</i>.
	 * 
	 * @param width
	 *            the width to scale to
	 * @param height
	 *            the height to scale to
	 * @return a scaled version of this {@link Image}
	 */
	public Image getScaledTo(int width, int height) {
		BufferedImage scaled = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = scaled.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.drawImage(bufferedImage, 0, 0, width, height, null);
		g2d.dispose();

		return new Image(scaled);
	}

	/**
	 * Cuts out a portion of this {@link Image} starting at the given <i>x</i>
	 * and <i>y</i> coordinates and extending by the given <i>width</i> and
	 * <i>height</i>.
	 * 
	 * @param x
	 *            starting x coordinate for the sub-{@link Image} to cut out
	 * @param y
	 *            starting y coordinate for the sub-{@link Image} to cut out
	 * @param width
	 *            width of the sub-{@link Image}
	 * @param height
	 *            height of the sub-{@link Image}
	 * @return a sub-{@link Image} of this {@link Image}
	 */
	public Image getSubImage(int x, int y, int width, int height) {
		return new Image(bufferedImage.getSubimage(x, y, width, height));
	}

	/**
	 * <p>
	 * Creates a thumbnail {@link Image} from this {@link Image}. The dimensions
	 * of the thumbnail are gueranteed to not exceed the passed-in maximum
	 * <i>width</i> and <i>height</i>.
	 * </p>
	 * 
	 * @param maxWidth
	 *            maximum width of the thumbnail
	 * @param maxHeight
	 *            maximum height of the thumbnail
	 * @return a thumbnail of this {@link Image}
	 */
	public Image getThumbnail(int maxWidth, int maxHeight) {
		int width = getWidth(), height = getHeight();
		double wScale = ((double) maxWidth) / width;
		double hScale = ((double) maxHeight) / height;
		double scale = wScale < hScale ? wScale : hScale;
		return getScaled(scale);
	}

	/**
	 * Returns the width of <code>this</code> {@link Image}.
	 * 
	 * @return the width of <code>this</code> {@link Image}
	 */
	public int getWidth() {
		return bufferedImage.getWidth();
	}

	/**
	 * Assigns the given {@link Color} value to the pixel at the passed-in
	 * position.
	 * 
	 * @param x
	 *            x-coordinate of the pixel to assign a new {@link Color}
	 * @param y
	 *            y-coordinate of the pixel to assign a new {@link Color}
	 * @param color
	 *            the new {@link Color} value for the pixel
	 */
	public void setPixel(int x, int y, Color color) {
		setPixel(x, y, color.toPixelARGB());
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
	 * Sets the pixels of the specified rectangular area of this {@link Image}
	 * to the specified values. The index of a pixel at position (x, y) is
	 * computed using the following formula: <blockquote>
	 * 
	 * <pre>
	 * int index = offset + (y - y0) * pitch + x - x0
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param x0
	 *            the x coordinate of the top left corner of the rectangular
	 *            area
	 * @param y0
	 *            the y coordinate of the top left corner of the rectangular
	 *            area
	 * @param w
	 *            the width of the rectangular area
	 * @param h
	 *            the height of the rectangular area
	 * @param pixels
	 *            an <code>in</code> array containing the pixel values
	 * @param offset
	 *            the offset for indexing the <i>pixels</i> array
	 * @param pitch
	 *            the number of <code>int</code> values in one row of the
	 *            <i>pixels</i> array
	 */
	public void setPixels(int x0, int y0, int w, int h, int[] pixels,
			int offset, int pitch) {
		bufferedImage.setRGB(x0, y0, w, h, pixels, offset, pitch);
	}

	@Override
	public String toString() {
		// TODO: we need a smarter output, possibly containing the image's
		// number of channels, bit depth, etc.
		return "Image(width = " + getWidth() + ", height = " + getHeight()
				+ ", bufferedImage = " + bufferedImage + ")";
	}

}