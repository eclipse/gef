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
import org.eclipse.gef.geometry.planar.AffineTransform;

import javafx.scene.Node;
import javafx.scene.transform.Affine;

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

	/**
	 * Returns the current {@link AffineTransform} according to this
	 * {@link ITransformableContentPart}'s content.
	 *
	 * @return The current {@link AffineTransform} according to this
	 *         {@link ITransformableContentPart}'s content.
	 */
	public AffineTransform getContentTransform();

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
	public default Affine getVisualTransform() {
		return getAdapter(IVisualPart.TRANSFORM_PROVIDER_KEY).get();
	}

	/**
	 * Set the content transformation as specified by the given
	 * {@link AffineTransform}.
	 *
	 * @param totalTransform
	 *            The {@link AffineTransform} to set.
	 */
	public void transformContent(AffineTransform totalTransform);

	/**
	 * Sets the given {@link Affine} as the {@link #getVisualTransform() visual
	 * transform} of this {@link ITransformableContentPart}.
	 *
	 * @param totalTransform
	 *            The {@link Affine} that is to be set as the
	 *            {@link #getVisualTransform() visual transform} of this
	 *            {@link ITransformableContentPart}.
	 */
	public default void transformVisual(Affine totalTransform) {
		NodeUtils.setAffine(
				getAdapter(IVisualPart.TRANSFORM_PROVIDER_KEY).get(),
				totalTransform);
	}

}
