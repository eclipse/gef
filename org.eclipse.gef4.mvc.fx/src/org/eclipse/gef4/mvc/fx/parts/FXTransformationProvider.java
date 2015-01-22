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
package org.eclipse.gef4.mvc.fx.parts;

import javafx.scene.Node;
import javafx.scene.transform.Affine;

import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.inject.Provider;

public class FXTransformationProvider implements
		IAdaptable.Bound<IVisualPart<Node, ? extends Node>>, Provider<Affine> {

	private IVisualPart<Node, ? extends Node> host;
	private Affine affine = null;

	public void FXTransformaionProvider() {
	}

	@Override
	public Affine get() {
		if (affine == null) {
			affine = new Affine();
			host.getVisual().getTransforms().add(affine);
		}
		return affine;
	}

	@Override
	public IVisualPart<Node, ? extends Node> getAdaptable() {
		return host;
	}

	@Override
	public void setAdaptable(IVisualPart<Node, ? extends Node> adaptable) {
		host = adaptable;
	}

}
