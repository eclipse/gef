/*******************************************************************************
 * Copyright (c) 2011-2014 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial tests
 *******************************************************************************/
package org.eclipse.gef4.dot.tests;

import junit.framework.TestCase;

import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.LayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.GridLayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.TreeLayoutObserver;
import org.eclipse.gef4.layout.interfaces.LayoutContext;
import org.junit.Assert;

/**
 * Tests involving the {@link LayoutAlgorithm} interface.
 * 
 * @author Fabian Steeg (fsteeg)
 * 
 */
public class LayoutAlgorithmTests extends TestCase {

	/**
	 * Access items laid out in a custom layout algorithm (see
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=337144)
	 */
	public void testCustomLayoutSimpleItemAccess() {
		Node node = new Node.Builder().build();
		Graph.Builder graph = new Graph.Builder().nodes(node);
		graph.attr(Graph.Attr.LAYOUT.toString(), new LayoutAlgorithm() {
			public void setLayoutContext(LayoutContext context) {
				Object[] all = context.getEntities()[0].getItems();
				Object[] nodes = context.getNodes()[0].getItems();
				Assert.assertEquals(1, all.length);
				Assert.assertEquals(1, nodes.length);
				Assert.assertTrue(
						"All entity items should be wrapped in a Node[]",
						all instanceof Node[]);
				Assert.assertTrue(
						"Node entity items should be wrapped in a Node[]",
						nodes instanceof Node[]);
				Assert.assertTrue("All entity items should be Node instances",
						all[0] instanceof Node);
				Assert.assertTrue("Node entity items should be Node instances",
						nodes[0] instanceof Node);
			}

			public void applyLayout(boolean clean) {
			}
		});
	}

	/**
	 * Attempt to reproduce an infinite loop with GridLayoutAlgorithm on an
	 * empty graph (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=382791)
	 */
	public void testGridLayoutAlgorithmEmptyGraph() {
		Graph.Builder graph = new Graph.Builder();
		graph.attr(Graph.Attr.LAYOUT.toString(), new GridLayoutAlgorithm());
		Assert.assertEquals(GridLayoutAlgorithm.class, graph.build().getAttrs()
				.get(Graph.Attr.LAYOUT.toString()).getClass());
	}

	/**
	 * Test issues with TreeLayoutObserver.TreeNode#isAncestorOf for tree nodes
	 * that are their own descendants, using the protected addChild() method
	 * (see http://bugs.eclipse.org/412446)
	 */
	public void testTreeLayoutObserverTreeNodeIsAncestorOfAdded() {
		TestNode node1 = new TestNode();
		Assert.assertTrue(node1.isAncestorOf(node1));
		node1.addDescendant(node1);
		Assert.assertTrue(node1.isAncestorOf(node1));
		TestNode node2 = new TestNode();
		node1.addDescendant(node2);
		Assert.assertTrue(node1.isAncestorOf(node2));
		Assert.assertFalse(node2.isAncestorOf(node1));
		TestNode node3 = new TestNode();
		TestNode node4 = new TestNode();
		node4.addDescendant(node4);
		Assert.assertFalse(node3.isAncestorOf(node4));
		Assert.assertFalse(node4.isAncestorOf(node3));
	}

	/**
	 * Test issues with TreeLayoutObserver.TreeNode#isAncestorOf for tree nodes
	 * that are their own descendants, linking protected members directly (see
	 * http://bugs.eclipse.org/412446)
	 */
	public void testTreeLayoutObserverTreeNodeIsAncestorOfLinked() {
		TestNode node1 = new TestNode();
		Assert.assertTrue(node1.isAncestorOf(node1));
		node1.linkDescendant(node1);
		Assert.assertTrue(node1.isAncestorOf(node1));
		TestNode node2 = new TestNode();
		node1.linkDescendant(node2);
		Assert.assertTrue(node1.isAncestorOf(node2));
		Assert.assertFalse(node2.isAncestorOf(node1));
		TestNode node3 = new TestNode();
		TestNode node4 = new TestNode();
		node4.linkDescendant(node4);
		Assert.assertFalse(node3.isAncestorOf(node4));
		Assert.assertFalse(node4.isAncestorOf(node3));
	}

	/* Use a private subclass to access protected members: */
	private static class TestNode extends TreeLayoutObserver.TreeNode {
		protected TestNode() {
			super(null, null);
		}

		void addDescendant(TestNode descendant) {
			addChild(descendant);
			precomputeTree();
		}

		public void linkDescendant(TestNode descendant) {
			descendant.parent = this;
			descendant.depth = this.depth + 1;
		}
	}
}
