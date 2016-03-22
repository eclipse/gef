package org.eclipse.gef4.mvc.parts;

import org.eclipse.gef4.geometry.planar.AffineTransform;

/**
 * An {@link IContentPart} that supports content related transformations.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit this
 *            {@link ITransformableContentPart} is used in, e.g.
 *            javafx.scene.Node in case of JavaFX.
 *
 * @param <V>
 *            The visual node used by this {@link ITransformableContentPart}.
 *
 */
public interface ITransformableContentPart<VR, V extends VR>
		extends IContentPart<VR, V> {

	/**
	 * Transform the content element as specified by the given
	 * {@link AffineTransform}.
	 *
	 * @param transform
	 *            The {@link AffineTransform} to apply.
	 */
	public void transformContent(AffineTransform transform);

}
