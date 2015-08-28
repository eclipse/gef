/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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
package org.eclipse.gef4.zest.fx.behaviors;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.parts.EdgeContentPart;

// only applicable for EdgeContentPart (see #getHost())
public class EdgeLayoutBehavior extends AbstractLayoutBehavior {

	@Override
	protected GraphLayoutContext getGraphLayoutContext() {
		IContentPart<Node, ? extends Node> graphPart = getHost().getRoot().getViewer().getContentPartMap()
				.get(getHost().getContent().getGraph());
		return graphPart.getAdapter(GraphLayoutContext.class);
	}

	@Override
	public EdgeContentPart getHost() {
		return (EdgeContentPart) super.getHost();
	}

	@Override
	protected void postLayout() {
		getHost().refreshVisual();
	}

	@Override
	protected void preLayout() {
	}

}
