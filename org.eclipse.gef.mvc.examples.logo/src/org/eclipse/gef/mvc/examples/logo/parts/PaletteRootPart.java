/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.parts;

import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.parts.LayeredRootPart;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

/**
 * A specific root part for the palette viewer.
 *
 * @author Alexander Nyßen
 */
public class PaletteRootPart extends LayeredRootPart {

	@Override
	protected Group createContentLayer() {
		Group contentLayer = super.createContentLayer();
		VBox vbox = new VBox();
		vbox.setPickOnBounds(true);
		// define padding and spacing
		vbox.setPadding(new Insets(10));
		vbox.setSpacing(10d);
		// fixed at top/right position
		vbox.setAlignment(Pos.TOP_LEFT);
		contentLayer.getChildren().add(vbox);
		return contentLayer;
	}

	@Override
	protected void doAddChildVisual(IVisualPart<? extends Node> child, int index) {
		if (child instanceof IContentPart) {
			int contentLayerIndex = 0;
			for (int i = 0; i < index; i++) {
				if (i < getChildrenUnmodifiable().size() && getChildrenUnmodifiable().get(i) instanceof IContentPart) {
					contentLayerIndex++;
				}
			}
			((VBox) getContentLayer().getChildren().get(0)).getChildren().add(contentLayerIndex,
					new Group(child.getVisual()));
		} else {
			super.doAddChildVisual(child, index);
		}
	}

	@Override
	protected void doRemoveChildVisual(IVisualPart<? extends Node> child, int index) {
		if (child instanceof IContentPart) {
			((VBox) getContentLayer().getChildren().get(0)).getChildren().remove(index);
		} else {
			super.doRemoveChildVisual(child, index);
		}
	}
}
