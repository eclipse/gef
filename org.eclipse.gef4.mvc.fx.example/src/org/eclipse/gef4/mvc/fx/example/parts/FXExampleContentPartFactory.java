/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.example.parts;

import java.util.Map;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricCurve;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricModel;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricShape;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class FXExampleContentPartFactory implements IContentPartFactory<Node> {

	@Inject
	private Injector injector;
	
	public org.eclipse.gef4.mvc.parts.IContentPart<Node> createContentPart(
			Object content, IBehavior<Node> contextBehavior, Map<Object, Object> contextMap) {
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
