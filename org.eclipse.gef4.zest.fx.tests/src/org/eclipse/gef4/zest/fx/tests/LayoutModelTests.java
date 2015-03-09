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
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.models.LayoutModel;
import org.junit.Test;

public class LayoutModelTests {

	/**
	 * Tests whether the mapping from Graph to LayoutContext works as intended,
	 * i.e. graphs are differentiated by their object identity.
	 */
	@Test
	public void test_graphContextMapping() {
		LayoutModel layoutModel = new LayoutModel();
		assertNull(layoutModel.getLayoutContext(null));
		Graph graph = new Graph();
		assertNull(layoutModel.getLayoutContext(graph));
		GraphLayoutContext graphLayoutContext = new GraphLayoutContext(graph);
		// assign the context to the empty graph in the model
		layoutModel.setLayoutContext(graph, graphLayoutContext);
		assertEquals(graphLayoutContext, layoutModel.getLayoutContext(graph));
		// add attribute
		graph.getAttrs().put("test", "attr");
		assertEquals(graphLayoutContext, layoutModel.getLayoutContext(graph));
		// add nodes
		Node n = new Node();
		n.setGraph(graph);
		Node m = new Node();
		m.setGraph(graph);
		graph.getNodes().add(n);
		graph.getNodes().add(m);
		assertEquals(graphLayoutContext, layoutModel.getLayoutContext(graph));
		// test some other graph
		assertNull(layoutModel.getLayoutContext(new Graph()));
	}

}
