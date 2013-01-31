package org.eclipse.gef4.graphics;

import org.eclipse.gef4.graphics.image.Image;

/**
 * The {@link InterpolationHint} specifies whether to apply a fast and low
 * quality ({@link #SPEED}) interpolation when displaying a transformed
 * {@link Image} or to apply a slower but higher quality ({@link #QUALITY})
 * interpolation when displaying a transformed {@link Image}.
 */
public enum InterpolationHint {
	/**
	 * If the {@link InterpolationHint} is set to {@link #SPEED}, a fast, but
	 * low quality interpolation is used when displaying transformed
	 * {@link Image}s using {@link IGraphics#blit(Image)}.
	 */
	SPEED,

	/**
	 * If the {@link InterpolationHint} is set to {@link #QUALITY}, a slower,
	 * but higher quality interpolation is used when displaying transformed
	 * {@link Image}s using {@link IGraphics#blit(Image)}.
	 */
	QUALITY
}