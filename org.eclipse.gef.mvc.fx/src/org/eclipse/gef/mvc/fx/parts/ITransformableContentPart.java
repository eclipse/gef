/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contributions for Bugzilla #504480
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.planar.AffineTransform;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

import javafx.scene.Node;
import javafx.scene.transform.Affine;

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
	 * The role for the adapter key of the <code>Provider&lt;Affine&gt;</code>
	 * that will be used to obtain the part's {@link Affine} transformation.
	 */
	// TODO: replace with a read-only object (Affine) property instead of using
	// a provider, inserting the Affine within #getVisual()
	String TRANSFORM_PROVIDER_ROLE = "transformProvider";

	/**
	 * The adapter key for the <code>Provider&lt;Affine&gt;</code> that will be
	 * used to obtain the host's {@link Affine} transformation.
	 */
	@SuppressWarnings("serial")
	AdapterKey<Provider<? extends Affine>> TRANSFORM_PROVIDER_KEY = AdapterKey
			.get(new TypeToken<Provider<? extends Affine>>() {
			}, TRANSFORM_PROVIDER_ROLE);

	/**
	 * Returns the current {@link Affine} according to this
	 * {@link ITransformableContentPart}'s content.
	 *
	 * @return The current {@link Affine} according to this
	 *         {@link ITransformableContentPart}'s content.
	 */
	public Affine getContentTransform();

	/**
	 * Returns the current transform according to this
	 * {@link ITransformableContentPart}'s visual.
	 *
	 * @return The current transform according to this
	 *         {@link ITransformableContentPart}'s visual.
	 */
	public default Affine getVisualTransform() {
		return getAdapter(TRANSFORM_PROVIDER_KEY).get();
	}

	/**
	 * Set the content transformation as specified by the given
	 * {@link AffineTransform}.
	 *
	 * @param totalTransform
	 *            The {@link AffineTransform} to set.
	 */
	public void setContentTransform(Affine totalTransform);

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
		NodeUtils.setAffine(getAdapter(TRANSFORM_PROVIDER_KEY).get(),
				totalTransform);
	}

}
