/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef.geometry.planar.AffineTransform;

import javafx.scene.Node;

/**
 * An {@link IContentPart} that supports content related transformations.
 *
 * @author anyssen
 *
 * @param <V>
 *            The visual node used by this {@link ITransformableContentPart}.
 *
 */
public interface ITransformableContentPart<V extends Node>
		extends IContentPart<V> {

	// getContentTransform()

	/**
	 * Returns the visual of this {@link ITransformableContentPart} to which
	 * transformations should be applied.
	 *
	 * @return The visual of this {@link ITransformableContentPart} to which
	 *         transformations should be applied.
	 */
	public default Node getTransformableVisual() {
		return getVisual();
	}

	/**
	 * Returns the current transform according to this
	 * {@link ITransformableContentPart}'s visual.
	 *
	 * @return The current transform according to this
	 *         {@link ITransformableContentPart}'s visual.
	 */
	public default AffineTransform getVisualTransform() {
		return FX2Geometry.toAffineTransform(
				getAdapter(IVisualPart.TRANSFORM_PROVIDER_KEY).get());
	}

	/**
	 * Transform the content element as specified by the given
	 * {@link AffineTransform}.
	 *
	 * @param deltaTransform
	 *            The {@link AffineTransform} to apply.
	 */
	public void transformContent(AffineTransform deltaTransform);

	/**
	 * Applies the given {@link AffineTransform} to the visual of this
	 * {@link ITransformableContentPart}.
	 *
	 * @param deltaTransform
	 *            The {@link AffineTransform} that is to be concatenated to the
	 *            current {@link #getVisualTransform()} of this
	 *            {@link ITransformableContentPart}.
	 */
	// TODO: this should be called by transform policy
	public default void transformVisual(AffineTransform deltaTransform) {
		NodeUtils.setAffine(
				getAdapter(IVisualPart.TRANSFORM_PROVIDER_KEY).get(),
				Geometry2FX.toFXAffine(
						getVisualTransform().concatenate(deltaTransform)));
	}

}
