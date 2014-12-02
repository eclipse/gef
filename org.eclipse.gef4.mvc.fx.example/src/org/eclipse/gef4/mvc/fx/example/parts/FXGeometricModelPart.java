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

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.fx.example.model.AbstractFXGeometricElement;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricModel;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class FXGeometricModelPart extends AbstractFXContentPart<Group> {

	@Override
	protected void addChildVisual(IVisualPart<Node, ? extends Node> child,
			int index) {
		getVisual().getChildren().add(index, child.getVisual());
	}

	@Override
	public void addContentChild(Object contentChild, int index) {
		if (!(contentChild instanceof AbstractFXGeometricElement)) {
			throw new IllegalArgumentException(
					"Cannot add content child: wrong type!");
		}
		getContent().getShapeVisuals().add(index,
				(AbstractFXGeometricElement<?>) contentChild);
	}

	@Override
	protected Group createVisual() {
		Group visual = new Group();
		visual.setAutoSizeChildren(false);
		return visual;
	}

	@Override
	public void doRefreshVisual(Group visual) {
		// nothing to do
	}

	@Override
	public FXGeometricModel getContent() {
		return (FXGeometricModel) super.getContent();
	}

	@Override
	public List<Object> getContentChildren() {
		List<Object> objs = new ArrayList<Object>();
		objs.addAll(getContent().getShapeVisuals());
		return objs;
	}

	@Override
	protected void removeChildVisual(IVisualPart<Node, ? extends Node> child,
			int index) {
		getVisual().getChildren().remove(child.getVisual());
	}

	@Override
	public void removeContentChild(Object contentChild, int index) {
		getContent().getShapeVisuals().remove(contentChild);
	}

}
