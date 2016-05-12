package org.eclipse.gef4.mvc.parts;

import org.eclipse.gef4.geometry.planar.Dimension;

/**
 * An {@link IContentPart} that supports content related resize.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit this
 *            {@link IResizableContentPart} is used in, e.g. javafx.scene.Node
 *            in case of JavaFX.
 *
 * @param <V>
 *            The visual node used by this {@link IResizableContentPart}.
 *
 */
public interface IResizableContentPart<VR, V extends VR>
		extends IContentPart<VR, V> {

	/**
	 * Resizes the content element as specified by the given {@link Dimension}.
	 *
	 * @param size
	 *            The new size.
	 */
	public void resizeContent(Dimension size);

}
