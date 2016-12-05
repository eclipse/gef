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
package org.eclipse.gef.mvc.examples.logo.policies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.geometry.planar.IShape;
import org.eclipse.gef.mvc.examples.logo.model.GeometricModel;
import org.eclipse.gef.mvc.examples.logo.model.GeometricShape;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.policies.CreationMenuOnClickPolicy;

import com.google.inject.Provider;

import javafx.scene.Node;
import javafx.scene.paint.Color;

public class FXCreationMenuItemProvider implements Provider<List<CreationMenuOnClickPolicy.ICreationMenuItem>> {

	static class GeometricShapeItem implements CreationMenuOnClickPolicy.ICreationMenuItem {
		private final GeometricShape template;

		public GeometricShapeItem(GeometricShape content) {
			template = content;
		}

		@Override
		public Object createContent() {
			GeometricShape content = new GeometricShape(template.getGeometry(), template.getTransform(),
					template.getFill(), template.getEffect());
			content.setStroke(template.getStroke());
			content.setStrokeWidth(template.getStrokeWidth());
			return content;
		}

		@Override
		public Node createVisual() {
			GeometryNode<IShape> visual = new GeometryNode<>(template.getGeometry());
			visual.setStroke(template.getStroke());
			visual.setStrokeWidth(template.getStrokeWidth());
			visual.setFill(template.getFill());
			visual.setEffect(template.getEffect());
			return visual;
		}

		@Override
		public IContentPart<? extends Node> findContentParent(IRootPart<? extends Node> rootPart) {
			return rootPart.getContentPartChildren().get(0);
		}
	}

	@Override
	public List<CreationMenuOnClickPolicy.ICreationMenuItem> get() {
		List<CreationMenuOnClickPolicy.ICreationMenuItem> items = new ArrayList<>();
		// handle shape
		items.add(new GeometricShapeItem(new GeometricShape(GeometricModel.createHandleShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 0, 0), Color.WHITE, GeometricModel.GEF_SHADOW_EFFECT)));
		// E shape
		items.add(new GeometricShapeItem(
				new GeometricShape(GeometricModel.createEShapeGeometry(), new AffineTransform(1, 0, 0, 1, 0, 0),
						GeometricModel.GEF_COLOR_BLUE, GeometricModel.GEF_SHADOW_EFFECT)));
		// cursor shape
		items.add(new GeometricShapeItem(new GeometricShape(GeometricModel.createCursorShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 0, 0), Color.WHITE, 2, Color.BLACK, GeometricModel.GEF_SHADOW_EFFECT)));
		return items;
	}

}
