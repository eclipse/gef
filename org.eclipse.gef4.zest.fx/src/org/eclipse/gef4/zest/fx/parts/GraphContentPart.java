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

import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;

public class GraphContentPart extends AbstractFXContentPart {

	private Group group = new Group();

	{
		group.setAutoSizeChildren(false);
	}

	@Override
	public void doRefreshVisual() {
		// nothing to do
	}

	@Override
	public List<Object> getContentChildren() {
		List<Object> children = new ArrayList<Object>();
		children.addAll(((Graph) getContent()).getNodes());
		children.addAll(((Graph) getContent()).getEdges());
		return children;
	}

	@Override
	public Node getVisual() {
		return group;
	}

}
