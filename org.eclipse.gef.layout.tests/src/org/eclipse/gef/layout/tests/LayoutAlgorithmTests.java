/*******************************************************************************
 * Copyright (c) 2009, 2017 Fabian Steeg and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Fabian Steeg - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.layout.tests;

import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.layout.algorithms.TreeLayoutHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests involving the {@link ILayoutAlgorithm} interface.
 *
 * @author Fabian Steeg (fsteeg)
 *
 */
public class LayoutAlgorithmTests {

	/* Use a private subclass to access protected members: */
	private static class TestNode extends TreeLayoutHelper.TreeNode {
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

	/**
	 * Test issues with TreeLayoutObserver.TreeNode#isAncestorOf for tree nodes
	 * that are their own descendants, using the protected addChild() method
	 * (see http://bugs.eclipse.org/412446)
	 */
	@Test
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
	@Test
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
}
