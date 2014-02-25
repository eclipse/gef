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

import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricCurve;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricModel;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricShape;
import org.eclipse.gef4.mvc.fx.example.parts.FXGeometricCurvePart;
import org.eclipse.gef4.mvc.fx.example.parts.FXGeometricModelPart;
import org.eclipse.gef4.mvc.fx.example.parts.FXGeometricShapePart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;

public class FXExampleContentPartFactory implements IContentPartFactory<Node> {

	public org.eclipse.gef4.mvc.parts.IContentPart<Node> createContentPart(
			Object content, IBehavior<Node> contextBehavior) {
		if (content instanceof FXGeometricModel) {
			return new FXGeometricModelPart();
		} else if (content instanceof FXGeometricShape) {
			return new FXGeometricShapePart();
		} else if (content instanceof FXGeometricCurve) {
			return new FXGeometricCurvePart();
		} else {
			throw new IllegalArgumentException(content.getClass().toString());
		}
	};

}
