/*******************************************************************************
 * Copyright (c) 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial tests
 *******************************************************************************/
package org.eclipse.gef4.zest.tests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphItem;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

/**
 * Selection-related tests for the {@link Graph} class.
 * 
 * @author Fabian Steeg (fsteeg)
 * 
 */
public class GraphSelectionTests extends TestCase {

	private static final int STYLE = SWT.NONE;

	private GraphNode[] nodes;

	private Graph graph;

	protected void setUp() throws Exception {
		graph = new Graph(new Shell(), STYLE);
		nodes = new GraphNode[] { new GraphNode(graph, STYLE),
				new GraphNode(graph, STYLE) };
		new GraphConnection(graph, STYLE, nodes[0], nodes[1]);
	}

	public void testSetSelectionGetSelection() {
		graph.setSelection(new GraphNode[] {});
		assertEquals(0, graph.getSelection().size());
		graph.setSelection(nodes);
		assertEquals(2, graph.getSelection().size());
	}

	public void testSelectAllGetSelection() {
		graph.selectAll();
		assertEquals(2, graph.getSelection().size());
	}

	public void testAddSelectionListenerEventIdentity() {
		final List selectionEvents = new ArrayList();
		graph.addSelectionListener(setupListener(selectionEvents));
		graph.addSelectionListener(setupListener(selectionEvents));
		Event event = new Event();
		event.widget = nodes[0];
		graph.notifyListeners(SWT.Selection, event);
		assertEquals("Two listeners should receive one event each", 2,
				selectionEvents.size());
		assertEquals("Two listeners should receive the same event",
				selectionEvents.get(0), selectionEvents.get(1));
	}

	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=314710
	 */
	public void testSelectedNodeDisposal() {
		graph.setSelection(nodes);
		nodes[0].dispose();
		graph.layout();
		assertEquals(
				"Disposing a selected node should remove it from the selection",
				1, graph.getSelection().size());
	}

	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=320281
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=218148#c6
	 */
	public void testAddSelectionListenerSetSelection() {
		final List selectionEvents = new ArrayList();
		graph.addSelectionListener(setupListener(selectionEvents));
		graph.setSelection(nodes);
		assertEquals("Programmatic selection should not trigger events", 0,
				selectionEvents.size());
		for (int i = 0; i < nodes.length; i++) {
			GraphNode node = nodes[i];
			assertTrue("Programmatic selection should select nodes",
					node.isSelected());
		}
		graph.setSelection(new GraphNode[] { nodes[0] });
		for (int i = 1; i < nodes.length; i++) {
			GraphNode node = nodes[i];
			assertFalse(
					"Changing the selection should deselect the nodes selected before",
					node.isSelected());
		}
	}

	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=320281
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=322054
	 */
	public void testAddSelectionListenerSelectAll() {
		final List selectionEvents = new ArrayList();
		graph.addSelectionListener(setupListener(selectionEvents));
		graph.selectAll();
		assertEquals("Programmatic selection should not trigger events", 0,
				selectionEvents.size());
		for (Iterator iterator = graph.getNodes().iterator(); iterator
				.hasNext();) {
			GraphNode node = (GraphNode) iterator.next();
			assertTrue("Programmatic selection should set nodes selected",
					node.isSelected());
		}
	}

	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=320281
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=218148#c6
	 */
	public void testAddSelectionListenerNotifyListeners() {
		final List selectionEvents = new ArrayList();
		graph.addSelectionListener(setupListener(selectionEvents));
		graph.setSelection(nodes);
		Event event = new Event();
		event.widget = graph;
		graph.notifyListeners(SWT.Dispose, event);
		assertEquals("Non-selection events should not be received", 0,
				selectionEvents.size());
		graph.notifyListeners(SWT.Selection, event);
		assertEquals("Selection events should be received", 1,
				selectionEvents.size());
	}

	public void testClearGraphCheckSelection() throws Exception {
		graph.setSelection(nodes);
		assertEquals(2, graph.getSelection().size());
		graph.clear();
		assertEquals(0, graph.getNodes().size());
		assertEquals(0, graph.getConnections().size());
		assertEquals(0, graph.getSelection().size());
		setUp();
		assertEquals(2, graph.getNodes().size());
		assertEquals(1, graph.getConnections().size());
	}

	/** https://bugs.eclipse.org/bugs/show_bug.cgi?id=405871 */
	public void testVisualNodeSelection() {
		Shell shell = graph.getShell();
		shell.setLayout(new FillLayout());
		shell.open();
		while (!shell.isDisposed()) {
			GraphItem node = (GraphItem) graph.getNodes().get(0);
			graph.setSelection(new GraphItem[] { node });
			if (shell.getDisplay().readAndDispatch()) {
				shell.close();
			}
		}
	}

	private SelectionListener setupListener(final List events) {
		return new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				events.add(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
	}
}
