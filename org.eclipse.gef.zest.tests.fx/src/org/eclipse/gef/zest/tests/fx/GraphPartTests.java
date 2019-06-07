/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
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
package org.eclipse.gef.zest.tests.fx;

import static org.junit.Assert.assertEquals;

import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.mvc.tests.fx.rules.FXApplicationThreadRule;
import org.eclipse.gef.zest.fx.parts.GraphPart;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javafx.collections.ObservableList;

public class GraphPartTests {

	/**
	 * Ensure all tests are executed on the JavaFX application thread (and the
	 * JavaFX toolkit is properly initialized).
	 */
	@Rule
	public FXApplicationThreadRule fxApplicationThreadRule = new FXApplicationThreadRule();
	private GraphPart graphPart;
	private Node source;
	private Node target;
	private Edge edge;

	@Before
	public void setup_GraphPart() {
		graphPart = new GraphPart();
		graphPart.setContent(new Graph());
		source = new Node();
		graphPart.addContentChild(source, 0);
		target = new Node();
		graphPart.addContentChild(target, 0);
		edge = new Edge(source, target);
		graphPart.addContentChild(edge, 2);
	}

	@Test
	public void test_addContentChild() {
		ObservableList<Node> nodes = graphPart.getContent().getNodes();
		ObservableList<Edge> edges = graphPart.getContent().getEdges();
		assertEquals(target, nodes.get(0));
		assertEquals(source, nodes.get(1));
		assertEquals(edge, edges.get(0));

		ObservableList<Object> cc = graphPart.getContentChildrenUnmodifiable();
		assertEquals(target, cc.get(0));
		assertEquals(source, cc.get(1));
		assertEquals(edge, cc.get(2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_addContentChild_invalid() {
		graphPart.addContentChild(new Object(), 0);
	}

	@Test
	public void test_removeContentChild() {
		ObservableList<Object> cc = graphPart.getContentChildrenUnmodifiable();
		assertEquals(3, cc.size());

		graphPart.removeContentChild(edge);
		assertEquals(2, cc.size());
		assertEquals(target, cc.get(0));
		assertEquals(source, cc.get(1));

		graphPart.removeContentChild(target);
		assertEquals(1, cc.size());
		assertEquals(source, cc.get(0));

		graphPart.removeContentChild(source);
		assertEquals(0, cc.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_removeContentChild_invalid() {
		graphPart.removeContentChild(new Object());
	}
}
