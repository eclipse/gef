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
package org.eclipse.gef4.zest.fx.behaviors;

import javafx.geometry.Bounds;
import javafx.scene.Node;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.zest.fx.layout.GraphEdgeLayout;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.parts.EdgeContentPart;

// only applicable for EdgeContentPart (see #getHost())
public class EdgeLayoutBehavior extends AbstractLayoutBehavior {

	protected GraphEdgeLayout edgeLayout;

	@Override
	protected Graph getGraph() {
		return getHost().getContent().getGraph();
	}

	@Override
	public EdgeContentPart getHost() {
		return (EdgeContentPart) super.getHost();
	}

	@Override
	protected void initializeLayout(GraphLayoutContext glc) {
		edgeLayout = glc
				.getEdgeLayout((Edge) ((IContentPart<Node, ? extends Node>) getHost())
						.getContent());
	}

	@Override
	protected void onBoundsChange(Bounds oldBounds, Bounds newBounds) {
	}

	@Override
	protected void onFlushChanges() {
		if (edgeLayout != null) {
			getHost().refreshVisual();
		}
	}

}
