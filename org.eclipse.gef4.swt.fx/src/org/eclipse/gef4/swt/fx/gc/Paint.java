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
package org.eclipse.gef4.swt.fx.gc;

import org.eclipse.swt.graphics.Image;

/**
 * A Paint specifies where rendering operations obtain the respective drawing
 * {@link RgbaColor} for any particular pixel. It stores a {@link RgbaColor}, a
 * {@link Gradient}, and an {@link Image} as color sources. (TODO: Is
 * "color sources" the correct terminology?) The {@link Paint}'s {@link Mode}
 * specifies which of these is currently active.
 * 
 * @author mwienand
 * 
 */
public class Paint {

	/*
	 * TODO: Convert Paint to Interface, implement ColorPaint, GradientPaint,
	 * and ImagePaint.
	 */

	/**
	 * @param p0
	 * @param p1
	 */
	private static boolean equals(Object p0, Object p1) {
		if (p0 == null) {
			return p1 == null;
		} else {
			if (p1 == null) {
				return false;
			}
		}
		return p0.equals(p1);
	}

	private RgbaColor color;
	private Gradient<?> gradient;
	private Image image;

	// private Point imageOffset;
	private PaintMode mode;

	/**
	 * Creates a new {@link Paint} and sets its {@link Gradient} attribute to
	 * the passed-in value. The {@link Paint}'s {@link Mode} is set to
	 * {@link Mode#GRADIENT}.
	 * 
	 * @param gradient
	 *            the {@link Paint}'s {@link Gradient}
	 */
	public Paint(Gradient<?> gradient) {
		this(null, gradient, null, PaintMode.GRADIENT);
	}

	/**
	 * Creates a new {@link Paint} and sets its {@link Image} attribute to the
	 * passed-in value. The {@link Paint}'s {@link Mode} is set to
	 * {@link Mode#IMAGE}.
	 * 
	 * @param image
	 *            the {@link Paint}'s {@link Image}
	 */
	public Paint(Image image) {
		this(null, null, image, PaintMode.IMAGE);
	}

	/**
	 * Creates a new {@link Paint} and sets the its {@link RgbaColor} to the
	 * passed-in value. The {@link Paint}'s {@link Mode} is set to
	 * {@link Mode#COLOR}.
	 * 
	 * @param color
	 *            the {@link Paint}'s {@link RgbaColor}
	 */
	public Paint(RgbaColor color) {
		this(color, null, null, PaintMode.COLOR);
	}

	/**
	 * Creates a new {@link Paint} and sets its {@link RgbaColor} and
	 * {@link Gradient} attributes to the passed-in values. The {@link Paint} 's
	 * {@link Mode} is set to {@link Mode#COLOR}.
	 * 
	 * @param color
	 *            the {@link Paint}'s {@link RgbaColor}
	 * @param gradient
	 *            the {@link Paint}'s {@link Gradient}
	 */
	public Paint(RgbaColor color, Gradient<?> gradient) {
		this(color, gradient, null, PaintMode.COLOR);
	}

	/**
	 * Creates a new {@link Paint} and sets its {@link RgbaColor},
	 * {@link Gradient}, and {@link Image} attributes to the passed-in values.
	 * The {@link Paint}'s {@link Mode} is set to {@link Mode#COLOR}.
	 * 
	 * @param color
	 *            the {@link Paint}'s {@link RgbaColor}
	 * @param gradient
	 *            the {@link Paint}'s {@link Gradient}
	 * @param image
	 *            the {@link Paint}'s {@link Image}
	 */
	public Paint(RgbaColor color, Gradient<?> gradient, Image image) {
		this(color, gradient, image, PaintMode.COLOR);
	}

	/**
	 * Creates a new {@link Paint} and sets its {@link RgbaColor},
	 * {@link Gradient}, {@link Image}, and {@link Mode} attributes to the
	 * passed-in values.
	 * 
	 * @param color
	 *            the {@link Paint}'s {@link RgbaColor}
	 * @param gradient
	 *            the {@link Paint}'s {@link Gradient}
	 * @param image
	 *            the {@link Paint}'s {@link Image}
	 * @param mode
	 *            the {@link Paint}'s {@link Mode}
	 */
	public Paint(RgbaColor color, Gradient<?> gradient, Image image,
			PaintMode mode) {
		setColor(color);
		setGradient(gradient);
		setImage(image);
		setMode(mode);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Paint) {
			Paint o = (Paint) obj;
			return mode == o.mode && equals(color, o.color)
					&& equals(gradient, o.gradient) && equals(image, o.image);
		}
		return false;
	}

	/**
	 * Returns the currently selected Object which is either {@link RgbaColor},
	 * {@link Gradient}, or {@link Image}.
	 * 
	 * @return the currently selected Object
	 */
	public Object getActive() {
		switch (mode) {
		case COLOR:
			return color;
		case GRADIENT:
			return gradient;
		case IMAGE:
			return image;
		default:
			throw new IllegalStateException("Unknown PaintMode: " + mode);
		}
	}

	/**
	 * Returns a copy of this {@link Paint}.
	 * 
	 * @return a copy of this {@link Paint}
	 */
	public Paint getCopy() {
		return new Paint(color, gradient, image, mode);
	}

	/**
	 * Returns this {@link Paint}'s {@link Gradient}.
	 * 
	 * @return this {@link Paint}'s {@link Gradient}
	 */
	public Gradient<?> getGradient() {
		return gradient == null ? null : gradient.getCopy();
	}

	public Gradient<?> getGradientByReference() {
		return gradient;
	}

	/**
	 * Returns this {@link Paint}'s {@link Image}.
	 * 
	 * @return this {@link Paint}'s {@link Image}
	 */
	public Image getImage() {
		return image == null ? null : new Image(image.getDevice(),
				image.getImageData());
	}

	public Image getImageByReference() {
		return image;
	}

	/**
	 * Returns this {@link Paint}'s {@link Mode}.
	 * 
	 * @return this {@link Paint}'s {@link Mode}
	 */
	public PaintMode getMode() {
		return mode;
	}

	/**
	 * Returns this {@link Paint}'s {@link Color}.
	 * 
	 * @return this {@link Paint}'s {@link RgbaColor}
	 */
	public RgbaColor getRgbaColor() {
		return color == null ? null : color.getCopy();
	}

	public RgbaColor getRgbaColorByReference() {
		return color;
	}

	/**
	 * Sets this {@link Paint}'s {@link RgbaColor} to the given value.
	 * 
	 * @param color
	 *            the new {@link RgbaColor} value for this {@link Paint}
	 * @return <code>this</code> for convenience
	 */
	public Paint setColor(RgbaColor color) {
		this.color = color == null ? null : color.getCopy();
		return this;
	}

	public Paint setColorByReference(RgbaColor color) {
		this.color = color;
		return this;
	}

	/**
	 * Sets this {@link Paint}'s {@link Gradient} to the given value.
	 * 
	 * @param gradient
	 *            the new {@link Gradient} value for this {@link Paint}
	 * @return <code>this</code> for convenience
	 */
	public Paint setGradient(Gradient<?> gradient) {
		this.gradient = gradient == null ? null : gradient.getCopy();
		return this;
	}

	public Paint setGradientByReference(Gradient<?> gradient) {
		this.gradient = gradient;
		return this;
	}

	/**
	 * Sets this {@link Paint}'s {@link Image} to the given value.
	 * 
	 * @param image
	 *            the new {@link Image} value for this {@link Paint}
	 * @return <code>this</code> for convenience
	 */
	public Paint setImage(Image image) {
		this.image = image == null ? null : new Image(image.getDevice(),
				image.getImageData());
		return this;
	}

	public Paint setImageByReference(Image image) {
		this.image = image;
		return this;
	}

	/**
	 * Sets this {@link Paint}'s {@link Mode} to the given value.
	 * 
	 * @param mode
	 * @return <code>this</code> for convenience
	 */
	public Paint setMode(PaintMode mode) {
		if (mode == null) {
			throw new NullPointerException();
		}
		this.mode = mode;
		return this;
	}

}
