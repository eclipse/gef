package org.eclipse.gef4.graphics;

import org.eclipse.gef4.graphics.image.Image;

/**
 * An IImageGraphics is used to render into an {@link Image} instead of
 * rendering to a GUI component, for example. The user provides an {@link Image}
 * object on instantiation of an IImageGraphics. This {@link Image} is called
 * the user's {@link Image} in contrast to the back-end image. The user's
 * {@link Image} is not updated immediately when drawing operations are
 * performed, but forcing an update is possible using {@link #updateImage()}.
 * Naturally, the user's {@link Image} is updated on {@link #cleanUp()}.
 * 
 * @author mwienand
 * 
 */
public interface IImageGraphics extends IGraphics {

	/**
	 * Updates the user's {@link Image} before disposing all allocated system
	 * resources and resetting the underlying drawing toolkit.
	 */
	@Override
	void cleanUp();

	/**
	 * Returns the user's {@link Image}. Note that the {@link Image} is not
	 * updated immediately when drawing. You have to force the update by calling
	 * {@link #updateImage()} or {@link #cleanUp()}.
	 * 
	 * @return the user's {@link Image}
	 */
	Image getImage();

	/**
	 * Updates the user's {@link Image}.
	 * 
	 * @return <code>this</code> for convenience
	 */
	IImageGraphics updateImage();

}
