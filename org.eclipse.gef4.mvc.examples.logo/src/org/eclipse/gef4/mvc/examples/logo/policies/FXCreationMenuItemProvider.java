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
package org.eclipse.gef4.mvc.examples.logo.policies;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.paint.Color;

import org.eclipse.gef4.fx.nodes.GeometryNode;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricModel;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricShape;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IRootPart;

import com.google.inject.Provider;

public class FXCreationMenuItemProvider
		implements Provider<List<IFXCreationMenuItem>> {

	static class GeometricShapeItem implements IFXCreationMenuItem {
		private final FXGeometricShape template;

		public GeometricShapeItem(FXGeometricShape content) {
			template = content;
		}

		@Override
		public Object createContent() {
			FXGeometricShape content = new FXGeometricShape(
					template.getGeometry(), template.getTransform(),
					template.getFill(), template.getEffect());
			content.setStroke(template.getStroke());
			content.setStrokeWidth(template.getStrokeWidth());
			return content;
		}

		@Override
		public Node createVisual() {
			GeometryNode<IShape> visual = new GeometryNode<>(
					template.getGeometry());
			visual.setStroke(template.getStroke());
			visual.setStrokeWidth(template.getStrokeWidth());
			visual.setFill(template.getFill());
			visual.setEffect(template.getEffect());
			return visual;
		}

		@Override
		public IContentPart<Node, ? extends Node> findContentParent(
				IRootPart<Node, ? extends Node> rootPart) {
			return rootPart.getContentPartChildren().get(0);
		}
	}

	@Override
	public List<IFXCreationMenuItem> get() {
		List<IFXCreationMenuItem> items = new ArrayList<>();
		// handle shape
		items.add(new GeometricShapeItem(new FXGeometricShape(
				FXGeometricModel.createHandleShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 0, 0), Color.WHITE,
				FXGeometricModel.GEF_SHADOW_EFFECT)));
		// E shape
		items.add(new GeometricShapeItem(
				new FXGeometricShape(FXGeometricModel.createEShapeGeometry(),
						new AffineTransform(1, 0, 0, 1, 100, 22),
						FXGeometricModel.GEF_COLOR_BLUE,
						FXGeometricModel.GEF_SHADOW_EFFECT)));
		// cursor shape
		items.add(new GeometricShapeItem(new FXGeometricShape(
				FXGeometricModel.createCursorShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 227, 45), Color.WHITE, 2,
				Color.BLACK, FXGeometricModel.GEF_SHADOW_EFFECT)));
		return items;
	}

}
