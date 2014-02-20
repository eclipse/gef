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

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.anchors.IAnchor;
import org.eclipse.gef4.mvc.behaviors.AbstractZoomBehavior;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricModel;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;
import org.eclipse.gef4.mvc.policies.IDragPolicy;
import org.eclipse.gef4.mvc.policies.IPinchSpreadPolicy;

public class FXGeometricModelPart extends AbstractFXContentPart {
	
	private Group g;

	public FXGeometricModelPart() {
		g = new Group();
		g.setAutoSizeChildren(false);
	}

	@Override
	public Node getVisual() {
		return g;
	}

	@Override
	public void refreshVisual() {
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
	public void attachVisualToAnchorageVisual(Node anchorageVisual, IAnchor<Node> anchor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void detachVisualFromAnchorageVisual(Node anchorageVisual, IAnchor<Node> anchor) {
		// TODO Auto-generated method stub

	}

	@Override
	protected IAnchor<Node> getAnchor(IVisualPart<Node> anchored) {
		// TODO Auto-generated method stub
		return null;
	}

}
