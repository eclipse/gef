/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.planar.AffineTransform;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

import javafx.scene.Node;
import javafx.scene.transform.Affine;

/**
 *
 *
 * @author wienand
 *
 * @param <V>
 *            visual type
 */
// TODO: Merge into ITransformableContentPart.
public interface ITransformableVisualPart<V extends Node>
		extends IVisualPart<V> {

	/**
	 * The role for the adapter key of the <code>Provider&lt;Affine&gt;</code>
	 * that will be used to obtain the part's {@link Affine} transformation.
	 */
	// TODO: replace with property in IVisualPart/AbstractVisualPart
	public static final String TRANSFORMATION_PROVIDER_ROLE = "transformationProvider";

	/**
	 * The adapter key for the <code>Provider&lt;Affine&gt;</code> that will be
	 * used to obtain the host's {@link Affine} transformation.
	 */
	@SuppressWarnings("serial")
	public static final AdapterKey<Provider<? extends Affine>> TRANSFORM_PROVIDER_KEY = AdapterKey
			.get(new TypeToken<Provider<? extends Affine>>() {
			}, ITransformableVisualPart.TRANSFORMATION_PROVIDER_ROLE);

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
	 * {@link ITransformableVisualPart}'s visual.
	 *
	 * @return The current transform according to this
	 *         {@link ITransformableVisualPart}'s visual.
	 */
	public default AffineTransform getVisualTransform() {
		return FX2Geometry.toAffineTransform(
				getAdapter(ITransformableVisualPart.TRANSFORM_PROVIDER_KEY)
						.get());
	}

	/**
	 * Applies the given {@link AffineTransform} to the visual of this
	 * {@link IResizableVisualPart}.
	 *
	 * @param transformation
	 *            The {@link AffineTransform} that is applied to the visual of
	 *            this {@link IResizableVisualPart}.
	 */
	// TODO: this should be called by transform policy
	public default void transformVisual(AffineTransform transformation) {

	}

}
