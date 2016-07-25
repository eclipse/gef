/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.parts;

import java.util.Map;

import javafx.scene.Node;

import org.eclipse.gef.mvc.behaviors.IBehavior;
import org.eclipse.gef.mvc.examples.logo.model.FXGeometricCurve;
import org.eclipse.gef.mvc.examples.logo.model.FXGeometricModel;
import org.eclipse.gef.mvc.examples.logo.model.FXGeometricShape;
import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.mvc.parts.IContentPartFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class FXLogoContentPartFactory implements IContentPartFactory<Node> {

	@Inject
	private Injector injector;

	@Override
	public IContentPart<Node, ? extends Node> createContentPart(Object content,
			IBehavior<Node> contextBehavior, Map<Object, Object> contextMap) {

		if (content instanceof FXGeometricModel) {
			return injector.getInstance(FXGeometricModelPart.class);
		} else if (content instanceof FXGeometricShape) {
			return injector.getInstance(FXGeometricShapePart.class);
		} else if (content instanceof FXGeometricCurve) {
			return injector.getInstance(FXGeometricCurvePart.class);
		} else {
			throw new IllegalArgumentException(content.getClass().toString());
		}
	};

}
