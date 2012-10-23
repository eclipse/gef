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
import org.eclipse.gef4.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.junit.Test;

/**
 * Tests for the {@link DotImport} class with graphs using different layout
 * algorithms.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class TestLayoutDotImport {
	/**
	 * Test execution of File-based DOT-to-Zest transformations for a graph
	 * using the Zest {@link TreeLayoutAlgorithm}.
	 */
	@Test
	public void treeLayout() {
		importFrom(new File(RESOURCES_TESTS + "layout_tree_graph.dot")); //$NON-NLS-1$
	}

	/**
	 * Test execution of File-based DOT-to-Zest transformations for a graph
	 * using the Zest {@link SpringLayoutAlgorithm}.
	 */
	@Test
	public void springLayout() {
		importFrom(new File(RESOURCES_TESTS + "layout_spring_graph.dot")); //$NON-NLS-1$
	}

	/**
	 * Test execution of File-based DOT-to-Zest transformations for a graph
	 * using the Zest {@link RadialLayoutAlgorithm}.
	 */
	@Test
	public void radialLayout() {
		importFrom(new File(RESOURCES_TESTS + "layout_radial_graph.dot")); //$NON-NLS-1$
	}

	/**
	 * Test execution of File-based DOT-to-Zest transformations for a graph
	 * using the Zest {@link GridLayoutAlgorithm}.
	 */
	@Test
	public void gridLayout() {
		importFrom(new File(RESOURCES_TESTS + "layout_grid_graph.dot")); //$NON-NLS-1$
	}

}
