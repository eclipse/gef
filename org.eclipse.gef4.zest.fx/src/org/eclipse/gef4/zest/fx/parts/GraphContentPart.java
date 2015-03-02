/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.parts;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXHoverPolicy;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class GraphContentPart extends AbstractFXContentPart<Group> {

	/**
	 * A property change event is fired as soon as {@link #activate()
	 * activation} is finished.
	 */
	public static final String ACTIVATION_COMPLETE_PROPERTY = "activationComplete";

	/**
	 * This layout attribute determines if an element (node/edge) is irrelevant
	 * for laying out, i.e. it should be filtered before laying out.
	 */
	public static final String ATTR_LAYOUT_IRRELEVANT = "layoutIrrelevant";

	public GraphContentPart() {
		// we set the hover policy adapter here to disable hovering this part
		// TODO: move to NoHoverPolicy
		setAdapter(AdapterKey.get(AbstractFXHoverPolicy.class),
				new AbstractFXHoverPolicy() {
					@Override
					public void hover(MouseEvent e) {
					}
				});
	}

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
	protected void doActivate() {
		super.doActivate();
		pcs.firePropertyChange(ACTIVATION_COMPLETE_PROPERTY, false, true);
	}

	@Override
	public void doRefreshVisual(Group visual) {
		// nothing to do
	}

	@Override
	public Graph getContent() {
		return (Graph) super.getContent();
	}

	@Override
	public List<Object> getContentChildren() {
		List<Object> children = new ArrayList<Object>();
		children.addAll(getContent().getNodes());
		children.addAll(getContent().getEdges());
		return children;
	}

	@Override
	protected void removeChildVisual(IVisualPart<Node, ? extends Node> child,
			int index) {
		getVisual().getChildren().remove(child.getVisual());
	}

}
