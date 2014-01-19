/*******************************************************************************
 * Copyright (c) 2010, 2014 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.zest.tests.dot;

import static org.junit.Assert.assertEquals;

import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.internal.dot.ZestGraph;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the {@link DotGraph} class.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class TestDotGraph {

	@Test
	public void sampleUsage() {
		Shell shell = new Shell();
		Graph dotGraph = new Graph("digraph{1->2}");
		assertNodesEdgesCount(2, 1, new ZestGraph(shell, SWT.NONE, dotGraph));
		dotGraph = dotGraph.withDot("node[label=zested]; 2->3; 2->4"); //$NON-NLS-1$
		assertNodesEdgesCount(4, 3, new ZestGraph(shell, SWT.NONE, dotGraph));
		dotGraph = dotGraph.withDot("edge[style=dashed]; 3->5; 4->6"); //$NON-NLS-1$
		assertNodesEdgesCount(6, 5, new ZestGraph(shell, SWT.NONE, dotGraph));
		// open(shell);
	}

	@Test
	public void graphAttributesToDataMapping() {
		String dotInput = "digraph{ graph[key1=graph_value1 key2=graph_value2]; 1->2 }";
		ZestGraph graph = new ZestGraph(new Shell(), SWT.NONE, new Graph(
				dotInput));
		assertEquals("graph_value1", graph.getGraph().getData("key1"));
		assertEquals("graph_value2", graph.getGraph().getData("key2"));
	}

	private void assertNodesEdgesCount(int n, int e, ZestGraph zestGraph) {
		Assert.assertEquals(n, zestGraph.getNodes().size());
		Assert.assertEquals(e, zestGraph.getConnections().size());
	}

	static void open(final Shell shell) {
		shell.setText("Testing"); //$NON-NLS-1$
		shell.setLayout(new FillLayout());
		shell.setSize(200, 250);
		shell.open();
		while (!shell.isDisposed()) {
			while (!shell.getDisplay().readAndDispatch()) {
				shell.getDisplay().sleep();
			}
		}
	}
}
