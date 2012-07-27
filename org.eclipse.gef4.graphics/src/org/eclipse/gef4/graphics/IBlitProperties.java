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

/**
 * <p>
 * An {@link IBlitProperties} manages the {@link IGraphics} properties used when
 * displaying an {@link Image} using the {@link IGraphics#blit(Image)} method.
 * </p>
 * 
 * @author mwienand
 * 
 */
public interface IBlitProperties extends IGraphicsProperties {

	/**
	 * The {@link InterpolationHint} specifies whether to apply a fast and low
	 * quality ({@link #SPEED}) interpolation when displaying a transformed
	 * {@link Image} or to apply a slower but higher quality ({@link #QUALITY})
	 * interpolation when displaying a transformed {@link Image}.
	 */
	public enum InterpolationHint {
		/**
		 * If the {@link InterpolationHint} of an {@link IBlitProperties} is set
		 * to {@link #SPEED}, a fast, but low quality interpolation is used when
		 * displaying transformed {@link Image}s using
		 * {@link IGraphics#blit(Image)}.
		 */
		SPEED,

		/**
		 * If the {@link InterpolationHint} of an {@link IBlitProperties} is set
		 * to {@link #QUALITY}, a slower, but higher quality interpolation is
		 * used when displaying transformed {@link Image}s using
		 * {@link IGraphics#blit(Image)}.
		 */
		QUALITY
	}

	/**
	 * The {@link InterpolationHint} used if the user does not specify it.
	 */
	static final InterpolationHint DEFAULT_INTERPOLATION_HINT = InterpolationHint.QUALITY;

	/**
	 * Returns a copy of this {@link IBlitProperties}.
	 * 
	 * @return a copy of this {@link IBlitProperties}
	 */
	IBlitProperties getCopy();

	/**
	 * Returns the current {@link InterpolationHint} of this
	 * {@link IBlitProperties}.
	 * 
	 * @return the current {@link InterpolationHint} of this
	 *         {@link IBlitProperties}
	 */
	InterpolationHint getInterpolationHint();

	/**
	 * Sets the {@link InterpolationHint} of this {@link IBlitProperties} to the
	 * given value.
	 * 
	 * @param interpolationHint
	 *            the new {@link InterpolationHint} for this
	 *            {@link IBlitProperties}
	 * @return <code>this</code> for convenience
	 */
	IBlitProperties setInterpolationHint(InterpolationHint interpolationHint);

}
