/*******************************************************************************
 * Copyright (c) 2011 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial tests
 *******************************************************************************/
package org.eclipse.gef4.zest.tests;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.viewers.IGraphContentProvider;
import org.eclipse.gef4.zest.core.viewers.IGraphEntityContentProvider;
import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphItem;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.jface.util.DelegatingDragAdapter;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.Assert;

/**
 * Tests for the {@link GraphViewer} class.
 * 
 * @author Fabian Steeg (fsteeg)
 * 
 */
public class GraphViewerTests extends TestCase {

	private GraphViewer viewer;
	private Shell shell;

	/**
	 * Set up the shell and viewer to use in the tests.
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp() {
		shell = new Shell();
		viewer = new GraphViewer(shell, SWT.NONE);
	}

	/**
	 * Create a drop target on a viewer's control and check disposal (see
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=200732)
	 */
	public void testDisposalWithDropTarget() {
		new DropTarget(viewer.getGraphControl(), DND.DROP_MOVE | DND.DROP_COPY);
		shell.dispose();
		Assert.assertTrue("The viewer's graph control should be disposed",
				viewer.getControl().isDisposed());
	}

	/**
	 * Create a drag source on a viewer and check disposal (see
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=334009)
	 */
	public void testDisposalWithDragSource() {
		viewer.addDragSupport(DND.DROP_MOVE,
				new Transfer[] { TextTransfer.getInstance() },
				new DelegatingDragAdapter());
		shell.dispose();
		Assert.assertTrue("The viewer's graph control should be disposed",
				viewer.getControl().isDisposed());
	}

	/**
	 * Test creation of a graph viewer from a graph widget.
	 */
	public void testCreateFromGraph() {
		Graph g = new Graph(shell, SWT.NONE);
		new GraphConnection(g, SWT.NONE, new GraphNode(g, SWT.NONE),
				new GraphNode(g, SWT.NONE));
		GraphViewer v = new GraphViewer(g);
		Assert.assertEquals(2, v.getGraphControl().getNodes().size());
		Assert.assertEquals(1, v.getGraphControl().getConnections().size());
	}

	/**
	 * Try to find an item that cannot be found (see
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=237489)
	 */
	public void testFindGraphItem() {
		Assert.assertNull(
				"If an item cannot be found, the viewer should return null",
				viewer.findGraphItem(new Integer(5)));
	}

	/**
	 * Assert that no invalid selections with null data are produced by the
	 * viewer (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=356449)
	 */
	public void testValidSelection() {
		Graph graph = new Graph(shell, SWT.NONE);
		GraphNode n1 = new GraphNode(graph, SWT.NONE);
		GraphNode n2 = new GraphNode(graph, SWT.NONE);
		GraphConnection c = new GraphConnection(graph, SWT.NONE, n1, n2);
		graph.setSelection(new GraphItem[] { n1, n2, c });
		GraphViewer viewer = new GraphViewer(graph);
		assertEquals("No null data should be in the selection", 0,
				((StructuredSelection) viewer.getSelection()).size());
		n1.setData("1");
		n2.setData("2");
		assertEquals("Other data should be in the selection", 2,
				((StructuredSelection) viewer.getSelection()).size());
	}

	/**
	 * Assert that listeners for post selection events are properly notified by
	 * the viewer (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=366916)
	 */
	public void testPostSelectionListener() {
		final List<SelectionChangedEvent> selected = new ArrayList<SelectionChangedEvent>();
		viewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				selected.add(event);
			}
		});
		viewer.getControl().notifyListeners(SWT.Selection, new Event());
		assertFalse("Post selection listeners should be notified",
				selected.isEmpty());
	}

	/**
	 * Assert that a ViewerFilter filters both nodes and connections when using
	 * an IGraphEntityContentProvider (see
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=381852)
	 */
	public void testViewerFilterWithGraphEntityContentProvider() {
		testViewerFilter(new SampleGraphEntityContentProvider());
	}

	/**
	 * Assert that a ViewerFilter filters both nodes and connections when using
	 * an IGraphContentProvider.
	 */
	public void testViewerFilterWithGraphContentProvider() {
		testViewerFilter(new SampleGraphContentProvider());
	}

	/**
	 * Assert that dynamic layout is disabled by default and that it stays
	 * disabled after changing input, refreshing, or updating the viewer (see
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=390172)
	 */
	public void testViewerRefreshDoesNotApplyLayout() {
		viewer.setLayoutAlgorithm(new SpringLayoutAlgorithm());
		viewer.setContentProvider(new SampleGraphContentProvider());
		assertFalse("Dynamic layout should be disabled by default", viewer
				.getGraphControl().isDynamicLayoutEnabled());
		viewer.setInput(new Object());
		assertFalse("Dynamic layout should be disabled after setting input",
				viewer.getGraphControl().isDynamicLayoutEnabled());
		viewer.refresh(new Object());
		assertFalse("Dynamic layout should be disabled after refresh", viewer
				.getGraphControl().isDynamicLayoutEnabled());
		viewer.update(new Object(), new String[] {});
		assertFalse("Dynamic layout should be disabled after update", viewer
				.getGraphControl().isDynamicLayoutEnabled());
	}

	public void testViewerFilter(IContentProvider contentProvider) {
		viewer.setContentProvider(contentProvider);
		viewer.setInput(new Object());
		assertNodesAndConnections(3, 3);
		viewer.setFilters(new ViewerFilter[] { new SampleBooleanFilter(false) });
		assertNodesAndConnections(0, 0);
		viewer.setFilters(new ViewerFilter[] { new SampleBooleanFilter(true) });
		assertNodesAndConnections(3, 3);
	}

	private void assertNodesAndConnections(int nodes, int connections) {
		assertEquals(nodes, viewer.getGraphControl().getNodes().size());
		assertEquals(connections, viewer.getGraphControl().getConnections()
				.size());
	}

	static class SampleBooleanFilter extends ViewerFilter {
		private final boolean filter;

		SampleBooleanFilter(boolean filter) {
			this.filter = filter;
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			return filter;
		}
	}

	static class SampleGraphContentProvider implements IGraphContentProvider {

		public void dispose() {
		}

		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		}

		public Object getDestination(Object r) {
			if (r.equals("1to2"))
				return "2";
			if (r.equals("2to3"))
				return "3";
			if (r.equals("3to1"))
				return "1";
			return null;
		}

		public Object[] getElements(Object arg0) {
			return new String[] { "1to2", "2to3", "3to1" };
		}

		public Object getSource(Object r) {
			if (r.equals("1to2"))
				return "1";
			if (r.equals("2to3"))
				return "2";
			if (r.equals("3to1"))
				return "3";
			return null;
		}

	}

	static class SampleGraphEntityContentProvider implements
			IGraphEntityContentProvider {

		public Object[] getConnectedTo(Object entity) {
			if (entity.equals("1")) {
				return new Object[] { "2" };
			}
			if (entity.equals("2")) {
				return new Object[] { "3" };
			}
			if (entity.equals("3")) {
				return new Object[] { "2" };
			}
			return null;
		}

		public Object[] getElements(Object inputElement) {
			return new String[] { "1", "2", "3" };
		}

		public double getWeight(Object entity1, Object entity2) {
			return 0;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

}
