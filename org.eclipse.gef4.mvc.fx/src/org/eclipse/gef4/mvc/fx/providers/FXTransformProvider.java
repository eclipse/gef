/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
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

import org.eclipse.gef4.common.adapt.AbstractBoundProvider;
import org.eclipse.gef4.mvc.parts.IVisualPart;

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
public class FXTransformProvider extends
		AbstractBoundProvider<Affine, IVisualPart<Node, ? extends Node>> {

	private Affine affine = null;

	/**
	 * Default constructor.
	 */
	public void FXTransformaionProvider() {
	}

	@Override
	public Affine get() {
		if (affine == null) {
			affine = new Affine();
			getAdaptable().getVisual().getTransforms().add(affine);
		}
		return affine;
	}

}
