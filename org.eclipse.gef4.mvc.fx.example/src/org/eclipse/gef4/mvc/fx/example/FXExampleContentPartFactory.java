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
package org.eclipse.gef4.mvc.fx.example;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.fx.example.model.FXGeometricCurve;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricShape;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricModel;
import org.eclipse.gef4.mvc.fx.example.parts.FXGeometricCurvePart;
import org.eclipse.gef4.mvc.fx.example.parts.FXGeometricModelPart;
import org.eclipse.gef4.mvc.fx.example.parts.FXGeometricShapePart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;

public class FXExampleContentPartFactory implements IContentPartFactory<Node> {

	@Override
	public IContentPart<Node> createRootContentPart(IRootPart<Node> root,
			Object model) {
		if (model instanceof FXGeometricModel) {
			return new FXGeometricModelPart();
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public IContentPart<Node> createChildContentPart(IContentPart<Node> parent,
			Object model) {
		if (model instanceof FXGeometricShape) {
			return new FXGeometricShapePart();
		} else if (model instanceof FXGeometricCurve) {
			return new FXGeometricCurvePart();
		} else {
			throw new IllegalArgumentException(model.getClass().toString());
		}
	}

}
