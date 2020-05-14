/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.IShape;
import org.eclipse.gef.mvc.examples.logo.MvcLogoExample;
import org.eclipse.gef.mvc.examples.logo.model.GeometricShape;

import com.google.inject.Provider;

import javafx.scene.Node;

public class CreationMenuItemProvider implements Provider<List<CreationMenuOnClickHandler.ICreationMenuItem>> {

	static class GeometricShapeItem implements CreationMenuOnClickHandler.ICreationMenuItem {
		private final GeometricShape template;

		public GeometricShapeItem(GeometricShape content) {
			template = content;
		}

		@Override
		public Object createContent() {
			return template.getCopy();
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

	}

	@Override
	public List<CreationMenuOnClickHandler.ICreationMenuItem> get() {
		List<CreationMenuOnClickHandler.ICreationMenuItem> items = new ArrayList<>();
		for (GeometricShape shape : MvcLogoExample.createPaletteViewerContents()) {
			items.add(new GeometricShapeItem(shape));
		}
		return items;
	}
}
