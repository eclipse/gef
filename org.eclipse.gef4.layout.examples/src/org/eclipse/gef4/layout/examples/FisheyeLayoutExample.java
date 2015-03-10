/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 * Note: Parts of this class have been transferred from org.eclipse.gef4.zest.examples.layout.FisheyeGraphSnippet
 *
 *******************************************************************************/
package org.eclipse.gef4.layout.examples;

import javafx.application.Application;

import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.algorithms.GridLayoutAlgorithm;
import org.eclipse.gef4.zest.examples.AbstractZestExample;
import org.eclipse.gef4.zest.fx.ZestProperties;

public class FisheyeLayoutExample extends AbstractZestExample {

	public static void main(String[] args) {
		Application.launch(args);
	}

	public FisheyeLayoutExample() {
		super("GEF4 Layout - Fisheye Example");
	}

	@Override
	protected Graph createGraph() {
		// TODO: images for info, warn, error
		Graph g = new Graph();
		for (int i = 0; i < 80; i++) {
			Node n1 = n(g, LABEL, "Information", ZestProperties.NODE_FISHEYE,
					true);
			Node n2 = n(g, LABEL, "Warning", ZestProperties.NODE_FISHEYE, true);
			Node n3 = n(g, LABEL, "Error", ZestProperties.NODE_FISHEYE, true);
			e(g, n1, n2, LABEL, "");
			e(g, n2, n3, LABEL, "");
		}
		g.getAttrs()
				.put(ZestProperties.GRAPH_LAYOUT, new GridLayoutAlgorithm());
		return g;
	}

	@Override
	protected int getStageHeight() {
		return 500;
	}

	@Override
	protected int getStageWidth() {
		return 500;
	}

}
