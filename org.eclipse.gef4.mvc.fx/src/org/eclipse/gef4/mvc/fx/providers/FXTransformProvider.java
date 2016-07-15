/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.providers;

import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.inject.Provider;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.transform.Affine;

/**
 * The {@link FXTransformProvider} can be registered on an {@link IVisualPart}
 * to insert an {@link Affine} into its visual's transformations list and access
 * that {@link Affine}. Per default, this {@link Affine} is manipulated to
 * relocate or transform an {@link IVisualPart}.
 *
 * @author mwienand
 *
 */
public class FXTransformProvider
		extends IAdaptable.Bound.Impl<IVisualPart<Node, ? extends Node>>
		implements Provider<Affine> {

	private SimpleObjectProperty<Affine> affineProperty = new SimpleObjectProperty<>(
			null);
	private ChangeListener<Affine> affineChangeListener = new ChangeListener<Affine>() {
		@Override
		public void changed(ObservableValue<? extends Affine> affineProperty,
				Affine oldAffine, Affine newAffine) {
			onAffineChanged(oldAffine, newAffine);
		}
	};

	/**
	 * Default constructor.
	 */
	public FXTransformProvider() {
		affineProperty.addListener(affineChangeListener);
	}

	/**
	 * Returns an {@link ObjectProperty} that stores the {@link Affine}
	 * transformation that is applied to the {@link #getAdaptable()} of this
	 * {@link FXTransformProvider}.
	 *
	 * @return {@link ObjectProperty} that stores the {@link Affine}
	 *         transformation that is applied to the {@link #getAdaptable()} of
	 *         this {@link FXTransformProvider}.
	 * @since 1.1
	 */
	public ObjectProperty<Affine> affineProperty() {
		return affineProperty;
	}

	/**
	 * @deprecated Do not use.
	 */
	@Deprecated
	public void FXTransformaionProvider() {
	}

	@Override
	public Affine get() {
		if (affineProperty.get() == null) {
			affineProperty.set(new Affine());
		}
		return affineProperty.get();
	}

	/**
	 * Updates the transformation of the {@link #getAdaptable()} of this
	 * {@link FXTransformProvider} in response to {@link #affineProperty()}
	 * changes.
	 *
	 * @param oldAffine
	 *            The old {@link Affine} transformation.
	 * @param newAffine
	 *            The new {@link Affine} transformation.
	 * @since 1.1
	 */
	protected void onAffineChanged(Affine oldAffine, Affine newAffine) {
		int index = getAdaptable().getVisual().getTransforms()
				.indexOf(oldAffine);
		if (index == -1) {
			getAdaptable().getVisual().getTransforms().add(newAffine);
		} else {
			getAdaptable().getVisual().getTransforms().remove(oldAffine);
			getAdaptable().getVisual().getTransforms().add(index, newAffine);
		}
	}

}
