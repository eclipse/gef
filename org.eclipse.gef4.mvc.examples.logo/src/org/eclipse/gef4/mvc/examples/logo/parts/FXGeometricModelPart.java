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
package org.eclipse.gef4.mvc.examples.logo.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.mvc.examples.logo.model.AbstractFXGeometricElement;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricModel;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import javafx.scene.Group;
import javafx.scene.Node;

public class FXGeometricModelPart extends AbstractFXContentPart<Group> {

	@Override
	protected void addChildVisual(IVisualPart<Node, ? extends Node> child,
			int index) {
		getVisual().getChildren().add(index, child.getVisual());
	}

	@Override
	protected Group createVisual() {
		Group visual = new Group();
		visual.setAutoSizeChildren(false);
		return visual;
	}

	@Override
	public void doAddContentChild(Object contentChild, int index) {
		if (!(contentChild instanceof AbstractFXGeometricElement)) {
			throw new IllegalArgumentException(
					"Cannot add content child: wrong type!");
		}

		getContent().getShapeVisuals().add(index,
				(AbstractFXGeometricElement<?>) contentChild);
	}

	@Override
	public void doRefreshVisual(Group visual) {
		// nothing to do
	}

	@Override
	protected void doRemoveContentChild(Object contentChild) {
		getContent().getShapeVisuals().remove(contentChild);
	}

	@Override
	public FXGeometricModel getContent() {
		return (FXGeometricModel) super.getContent();
	}

	@Override
	public SetMultimap<? extends Object, String> getContentAnchorages() {
		return HashMultimap.create();
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

}
