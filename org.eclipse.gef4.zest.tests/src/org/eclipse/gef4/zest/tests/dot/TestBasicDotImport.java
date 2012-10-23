/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.zest.tests.dot;

import static org.eclipse.gef4.zest.tests.dot.DotImportTestUtils.RESOURCES_TESTS;
import static org.eclipse.gef4.zest.tests.dot.DotImportTestUtils.importFrom;

import java.io.File;

import org.eclipse.gef4.zest.internal.dot.DotImport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/**
 * Tests for the {@link DotImport} class.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class TestBasicDotImport {

	/**
	 * Sample graph summarizing all that is currently supported in the DOT
	 * input.
	 */
	@Test
	public void sampleGraph() {
		importFrom(new File(RESOURCES_TESTS + "sample_input.dot")); //$NON-NLS-1$
	}

	/**
	 * Basic directed graph.
	 */
	@Test
	public void basicGraph() {
		importFrom(new File(RESOURCES_TESTS + "basic_directed_graph.dot")); //$NON-NLS-1$
	}

	/**
	 * Test execution of File-based DOT-to-Zest transformations for a simple
	 * directed graph.
	 */
	@Test
	public void directedGraph() {
		importFrom(new File(RESOURCES_TESTS + "simple_digraph.dot")); //$NON-NLS-1$
	}

	/**
	 * Test execution of File-based DOT-to-Zest transformations for a simple
	 * undirected graph.
	 */
	@Test
	public void undirectedGraph() {
		importFrom(new File(RESOURCES_TESTS + "simple_graph.dot")); //$NON-NLS-1$
	}

	/**
	 * Test execution of File-based DOT-to-Zest transformations for a labeled
	 * graph.
	 */
	@Test
	public void labeledGraph() {
		importFrom(new File(RESOURCES_TESTS + "labeled_graph.dot")); //$NON-NLS-1$
	}

	/**
	 * Test execution of File-based DOT-to-Zest transformations for a graph
	 * using style attributes for edges.
	 */
	@Test
	public void styledGraph() {
		importFrom(new File(RESOURCES_TESTS + "styled_graph.dot")); //$NON-NLS-1$
	}

	/**
	 * Test execution of File-based DOT-to-Zest transformations for a graph
	 * using global node attributes.
	 */
	@Test
	public void globalNodeGraph() {
		importFrom(new File(RESOURCES_TESTS + "global_node_graph.dot")); //$NON-NLS-1$
	}

	/**
	 * Test execution of File-based DOT-to-Zest transformations for a graph
	 * using global edge attributes.
	 */
	@Test
	public void globalEdgeGraph() {
		importFrom(new File(RESOURCES_TESTS + "global_edge_graph.dot")); //$NON-NLS-1$
	}

	/**
	 * Test execution of File-based DOT-to-Zest transformations for a graph
	 * using statement-like attributes (e.g. rankdir=LR)
	 */
	@Test
	public void attributesGraph() {
		importFrom(new File(RESOURCES_TESTS + "attributes_graph.dot")); //$NON-NLS-1$
	}

	/**
	 * Test error handling for invalid graph syntax (instance import).
	 */
	@Test(expected = IllegalArgumentException.class)
	public void faultyGraphInstance() {
		new DotImport("graph Sample{").newGraphInstance(new Shell(), SWT.NONE);
	}

	/**
	 * Test error handling for invalid graph syntax (subclass import).
	 */
	@Test(expected = IllegalArgumentException.class)
	public void faultyGraphClass() {
		new DotImport("graph Sample{").newGraphInstance(new Shell(), SWT.NONE);
	}
}
