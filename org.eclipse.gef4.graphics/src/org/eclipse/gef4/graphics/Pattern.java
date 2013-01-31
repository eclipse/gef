package org.eclipse.gef4.graphics;

import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.image.Image;

/**
 * A Pattern specifies where rendering operations obtain the respective drawing
 * {@link Color} for any particular pixel. It stores a {@link Color}, a
 * {@link Gradient}, and an {@link Image} as color sources. (TODO: Is
 * "color sources" the correct terminology?) The {@link Pattern}'s {@link Mode}
 * specifies which of these is currently active.
 * 
 * @author mwienand
 * 
 */
public class Pattern {

	/**
	 * The {@link Pattern}'s Mode specifies where rendering operations obtain
	 * the drawing {@link Color} for any particular pixel.
	 * 
	 * @see Mode#COLOR
	 * @see Mode#GRADIENT
	 * @see Mode#IMAGE
	 * 
	 * @author mwienand
	 * 
	 */
	public enum Mode {
		/**
		 * The COLOR {@link Mode} specifies that rendering operations use the
		 * {@link Pattern}'s {@link Color} as the drawing {@link Color} for any
		 * particular pixel.
		 * 
		 * @see Mode
		 * @see #GRADIENT
		 * @see #IMAGE
		 */
		COLOR,

		/**
		 * The GRADIENT {@link Mode} specifies that rendering operations use the
		 * {@link Pattern}'s {@link Gradient} to get the drawing {@link Color}
		 * at any particular pixel.
		 * 
		 * @see Mode
		 * @see #COLOR
		 * @see #IMAGE
		 */
		GRADIENT,

		/**
		 * The IMAGE {@link Mode} specifies that rendering operations use the
		 * {@link Pattern}'s {@link Image} to get the drawing {@link Color} at
		 * any particular pixel.
		 * 
		 * @see Mode
		 * @see #COLOR
		 * @see #GRADIENT
		 */
		IMAGE
	}

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

	private Color color;
	private Gradient<?> gradient;
	private Image image;

	// private Point imageOffset;
	private Mode mode;

	/**
	 * Creates a new {@link Pattern} and sets the its {@link Color} to the
	 * passed-in value. The {@link Pattern}'s {@link Mode} is set to
	 * {@link Mode#COLOR}.
	 * 
	 * @param color
	 *            the {@link Pattern}'s {@link Color}
	 */
	public Pattern(Color color) {
		this(color, null, null, Mode.COLOR);
	}

	/**
	 * Creates a new {@link Pattern} and sets its {@link Color} and
	 * {@link Gradient} attributes to the passed-in values. The {@link Pattern}
	 * 's {@link Mode} is set to {@link Mode#COLOR}.
	 * 
	 * @param color
	 *            the {@link Pattern}'s {@link Color}
	 * @param gradient
	 *            the {@link Pattern}'s {@link Gradient}
	 */
	public Pattern(Color color, Gradient<?> gradient) {
		this(color, gradient, null, Mode.COLOR);
	}

	/**
	 * Creates a new {@link Pattern} and sets its {@link Color},
	 * {@link Gradient}, and {@link Image} attributes to the passed-in values.
	 * The {@link Pattern}'s {@link Mode} is set to {@link Mode#COLOR}.
	 * 
	 * @param color
	 *            the {@link Pattern}'s {@link Color}
	 * @param gradient
	 *            the {@link Pattern}'s {@link Gradient}
	 * @param image
	 *            the {@link Pattern}'s {@link Image}
	 */
	public Pattern(Color color, Gradient<?> gradient, Image image) {
		this(color, gradient, image, Mode.COLOR);
	}

	/**
	 * Creates a new {@link Pattern} and sets its {@link Color},
	 * {@link Gradient}, {@link Image}, and {@link Mode} attributes to the
	 * passed-in values.
	 * 
	 * @param color
	 *            the {@link Pattern}'s {@link Color}
	 * @param gradient
	 *            the {@link Pattern}'s {@link Gradient}
	 * @param image
	 *            the {@link Pattern}'s {@link Image}
	 * @param mode
	 *            the {@link Pattern}'s {@link Mode}
	 */
	public Pattern(Color color, Gradient<?> gradient, Image image, Mode mode) {
		setColor(color);
		setGradient(gradient);
		setImage(image);
		setMode(mode);
	}

	/**
	 * Creates a new {@link Pattern} and sets its {@link Gradient} attribute to
	 * the passed-in value. The {@link Pattern}'s {@link Mode} is set to
	 * {@link Mode#GRADIENT}.
	 * 
	 * @param gradient
	 *            the {@link Pattern}'s {@link Gradient}
	 */
	public Pattern(Gradient<?> gradient) {
		this(null, gradient, null, Mode.GRADIENT);
	}

	/**
	 * Creates a new {@link Pattern} and sets its {@link Image} attribute to the
	 * passed-in value. The {@link Pattern}'s {@link Mode} is set to
	 * {@link Mode#IMAGE}.
	 * 
	 * @param image
	 *            the {@link Pattern}'s {@link Image}
	 */
	public Pattern(Image image) {
		this(null, null, image, Mode.IMAGE);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pattern) {
			Pattern o = (Pattern) obj;
			return mode == o.mode && equals(color, o.color)
					&& equals(gradient, o.gradient) && equals(image, o.image);
		}
		return false;
	}

	/**
	 * Returns this {@link Pattern}'s {@link Color}.
	 * 
	 * @return this {@link Pattern}'s {@link Color}
	 */
	public Color getColor() {
		return color == null ? null : color.getCopy();
	}

	/**
	 * Returns a copy of this {@link Pattern}.
	 * 
	 * @return a copy of this {@link Pattern}
	 */
	public Pattern getCopy() {
		return new Pattern(color, gradient, image, mode);
	}

	/**
	 * Returns this {@link Pattern}'s {@link Gradient}.
	 * 
	 * @return this {@link Pattern}'s {@link Gradient}
	 */
	public Gradient<?> getGradient() {
		return gradient == null ? null : gradient.getCopy();
	}

	/**
	 * Returns this {@link Pattern}'s {@link Image}.
	 * 
	 * @return this {@link Pattern}'s {@link Image}
	 */
	public Image getImage() {
		return image == null ? null : image.getCopy();
	}

	/**
	 * Returns this {@link Pattern}'s {@link Mode}.
	 * 
	 * @return this {@link Pattern}'s {@link Mode}
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * Sets this {@link Pattern}'s {@link Color} to the given value.
	 * 
	 * @param color
	 *            the new {@link Color} value for this {@link Pattern}
	 * @return <code>this</code> for convenience
	 */
	public Pattern setColor(Color color) {
		this.color = color == null ? null : color.getCopy();
		return this;
	}

	/**
	 * Sets this {@link Pattern}'s {@link Gradient} to the given value.
	 * 
	 * @param gradient
	 *            the new {@link Gradient} value for this {@link Pattern}
	 * @return <code>this</code> for convenience
	 */
	public Pattern setGradient(Gradient<?> gradient) {
		this.gradient = gradient == null ? null : gradient.getCopy();
		return this;
	}

	/**
	 * Sets this {@link Pattern}'s {@link Image} to the given value.
	 * 
	 * @param image
	 *            the new {@link Image} value for this {@link Pattern}
	 * @return <code>this</code> for convenience
	 */
	public Pattern setImage(Image image) {
		this.image = image == null ? null : image.getCopy();
		return this;
	}

	/**
	 * Sets this {@link Pattern}'s {@link Mode} to the given value.
	 * 
	 * @param mode
	 * @return <code>this</code> for convenience
	 */
	public Pattern setMode(Mode mode) {
		if (mode == null) {
			throw new NullPointerException();
		}
		this.mode = mode;
		return this;
	}

}
