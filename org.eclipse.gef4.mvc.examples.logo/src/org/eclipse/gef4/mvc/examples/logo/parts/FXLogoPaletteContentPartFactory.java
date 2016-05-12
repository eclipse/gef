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
package org.eclipse.gef4.mvc.examples.logo.parts;

import java.util.Map;

import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.examples.logo.model.AbstractFXGeometricElement;
import org.eclipse.gef4.mvc.examples.logo.model.PaletteModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javafx.scene.Node;

public class FXLogoPaletteContentPartFactory implements IContentPartFactory<Node> {

	@Inject
	private Injector injector;

	@Override
	public IContentPart<Node, ? extends Node> createContentPart(Object content, IBehavior<Node> contextBehavior,
			Map<Object, Object> contextMap) {
		if (content instanceof PaletteModel) {
			return injector.getInstance(PaletteModelPart.class);
		} else if (content instanceof AbstractFXGeometricElement) {
			return injector.getInstance(PaletteElementPart.class);
		} else {
			throw new IllegalArgumentException(content.getClass().toString());
		}
	};

}
