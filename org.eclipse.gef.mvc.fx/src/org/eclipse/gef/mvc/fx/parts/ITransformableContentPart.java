/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contributions for Bugzilla #504480
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.planar.AffineTransform;

import javafx.scene.Node;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

/**
 * An {@link IContentPart} that supports content related transformations.
 *
 * @author anyssen
 * @author mwienand
 *
 * @param <V>
 *            The visual node used by this {@link ITransformableContentPart}.
 *
 */
public interface ITransformableContentPart<V extends Node>
		extends IContentPart<V> {

	/**
	 * Returns the {@link Affine} transformation that yields the given final
	 * transformation if applied to the given initial transformation.
	 *
	 * @param initialTransform
	 *            The initial {@link Affine}.
	 * @param finalTransform
	 *            The final {@link Affine}.
	 * @return The delta transformation that yields the final {@link Affine} if
	 *         applied to the initial {@link Affine}.
	 */
	public static Transform computeDeltaTransform(Affine initialTransform,
			Affine finalTransform) {
		Affine inverse;
		try {
			inverse = initialTransform.createInverse();
		} catch (NonInvertibleTransformException e) {
			throw new RuntimeException(e);
		}
		return inverse.createConcatenation(finalTransform);
	}

	/**
	 * Returns the {@link AffineTransform} that yields the given final
	 * transformation if applied to the given initial transformation.
	 *
	 * @param initialTransform
	 *            The initial {@link AffineTransform}.
	 * @param finalTransform
	 *            The final {@link AffineTransform}.
	 * @return The delta transformation that yields the final
	 *         {@link AffineTransform} if applied to the initial
	 *         {@link AffineTransform}.
	 */
	public static AffineTransform computeDeltaTransform(
			AffineTransform initialTransform, AffineTransform finalTransform) {
		return initialTransform.getInverse().concatenate(finalTransform);
	}

	/**
	 * Returns the current {@link AffineTransform} according to this
	 * {@link ITransformableContentPart}'s content.
	 *
	 * @return The current {@link AffineTransform} according to this
	 *         {@link ITransformableContentPart}'s content.
	 */
	public default AffineTransform getContentTransform() {
		throw new UnsupportedOperationException();
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
	public default void setContentTransform(AffineTransform totalTransform) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Sets the given {@link Affine} as the {@link #getVisualTransform() visual
	 * transform} of this {@link ITransformableContentPart}.
	 *
	 * @param totalTransform
	 *            The {@link Affine} that is to be set as the
	 *            {@link #getVisualTransform() visual transform} of this
	 *            {@link ITransformableContentPart}.
	 */
	public default void setVisualTransform(Affine totalTransform) {
		NodeUtils.setAffine(
				getAdapter(IVisualPart.TRANSFORM_PROVIDER_KEY).get(),
				totalTransform);
	}

}
