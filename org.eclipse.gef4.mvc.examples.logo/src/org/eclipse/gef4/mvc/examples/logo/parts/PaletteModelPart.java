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

import java.util.List;

import org.eclipse.gef4.common.collections.CollectionUtils;
import org.eclipse.gef4.mvc.examples.logo.model.PaletteModel;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.collect.SetMultimap;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class PaletteModelPart extends AbstractFXContentPart<VBox> {

	@Override
	protected void addChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		getVisual().getChildren().add(index, child.getVisual());
	}

	@Override
	protected VBox createVisual() {
		return new VBox();
	}

	@Override
	protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
		return CollectionUtils.emptySetMultimap();
	}

	@Override
	protected List<? extends Object> doGetContentChildren() {
		return getContent().getCreatableGeometries();
	}

	@Override
	protected void doRefreshVisual(VBox visual) {
	}

	@Override
	public PaletteModel getContent() {
		return (PaletteModel) super.getContent();
	}

	@Override
	protected void removeChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		Node removed = getVisual().getChildren().remove(index);
		if (removed != child.getVisual()) {
			throw new IllegalStateException("Child visual was not removed!");
		}
	}

}
