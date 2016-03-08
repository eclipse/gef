/*******************************************************************************
 * Copyright (c) 2009, 2015 Fabian Steeg and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg - initial API and implementation (see bug #277380)
 *******************************************************************************/
package org.eclipse.gef4.dot.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.eclipse.gef4.dot.internal.DotExport;
import org.eclipse.gef4.dot.internal.DotAttributes;
import org.eclipse.gef4.graph.Graph;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for the {@link DotExport} class.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class DotExportTests extends DotTemplateTests {

	public static final File OUTPUT = new File("output"); //$NON-NLS-1$

	@BeforeClass
	public static void wipe() {
		DotTestUtils.wipeOutput(DotExportTests.OUTPUT, ".dot"); //$NON-NLS-1$
		if (!DotExportTests.OUTPUT.exists()) {
			DotExportTests.OUTPUT.mkdirs();
		}
	}

	@Override
	protected void testDotGeneration(final Graph graph) {
		/*
		 * The DotExport class wraps the simple DotTemplate class, so when we
		 * test DotExport, we also run the test in the test superclass:
		 */
		super.testDotGeneration(graph);

		/* DotExport adds stripping of blank lines and file output: */
		DotExport dotExport = new DotExport(graph);
		String dot = dotExport.toDotString();
		assertNoBlankLines(dot);

		File file = new File(OUTPUT, new DotExport(graph).toString() + ".dot"); //$NON-NLS-1$
		dotExport.toDotFile(file);
		Assert.assertTrue("Generated file " + file.getName() + " must exist!", //$NON-NLS-1$
				file.exists());
		String dotRead = read(file);
		Assert.assertTrue(
				"DOT file output representation must contain simple class name of Zest input!", //$NON-NLS-1$
				dotRead.contains(graph.getClass().getSimpleName()));
		Assert.assertEquals("File output and String output should be equal;", //$NON-NLS-1$
				dot, dotRead);

	}

	// TODO: move test to zest, as the mapping should be done there
	// /** Test mapping of GEF4 layouts to Graphviz layouts. */
	// @Test
	// public void layoutToGraphvizLayoutMapping() {
	// Graph.Builder graph = new Graph.Builder();
	// graph.attr(DotAttributes.GRAPH_LAYOUT, new TreeLayoutAlgorithm());
	// assertTrue("TreeLayout -> 'dot'", new DotExport(graph.build())
	// .toDotString().contains("graph[layout=dot]"));
	// graph.attr(DotAttributes.GRAPH_LAYOUT, new RadialLayoutAlgorithm());
	// assertTrue("RadialLayout -> 'twopi'", new DotExport(graph.build())
	// .toDotString().contains("graph[layout=twopi]"));
	// graph.attr(DotAttributes.GRAPH_LAYOUT, new GridLayoutAlgorithm());
	// assertTrue("GridLayout -> 'osage'", new DotExport(graph.build())
	// .toDotString().contains("graph[layout=osage]"));
	// graph.attr(DotAttributes.GRAPH_LAYOUT, new SpringLayoutAlgorithm());
	// assertTrue("SpringLayout, small -> 'fdp'", new DotExport(graph.build())
	// .toDotString().contains("graph[layout=fdp]"));
	// for (int i = 0; i < 100; i++) {
	// graph.nodes(new Node.Builder().build());
	// }
	// assertTrue(
	// "SpringLayout, large -> 'sfdp'",
	// new DotExport(graph.build()).toDotString().contains(
	// "graph[layout=sfdp]"));
	// }

	/** Test setting layout algorithms. */
	@Test
	public void layoutToGraphvizLayoutMapping() {
		Graph.Builder graph = new Graph.Builder();
		graph.attr(DotAttributes.GRAPH_LAYOUT, DotAttributes.GRAPH_LAYOUT_DOT);
		assertTrue("'dot'", new DotExport(graph.build()).toDotString()
				.contains("graph[layout=dot]"));
		graph.attr(DotAttributes.GRAPH_LAYOUT,
				DotAttributes.GRAPH_LAYOUT_TWOPI);
		assertTrue("'twopi'", new DotExport(graph.build()).toDotString()
				.contains("graph[layout=twopi]"));
		graph.attr(DotAttributes.GRAPH_LAYOUT,
				DotAttributes.GRAPH_LAYOUT_OSAGE);
		assertTrue("'osage'", new DotExport(graph.build()).toDotString()
				.contains("graph[layout=osage]"));
		graph.attr(DotAttributes.GRAPH_LAYOUT, DotAttributes.GRAPH_LAYOUT_FDP);
		assertTrue("'fdp'", new DotExport(graph.build()).toDotString()
				.contains("graph[layout=fdp]"));
		graph.attr(DotAttributes.GRAPH_LAYOUT, DotAttributes.GRAPH_LAYOUT_SFDP);
		assertTrue("'sfdp'", new DotExport(graph.build()).toDotString()
				.contains("graph[layout=sfdp]"));
	}

	private void assertNoBlankLines(final String dot) {
		Scanner scanner = new Scanner(dot);
		while (scanner.hasNextLine()) {
			if (scanner.nextLine().trim().equals("")) { //$NON-NLS-1$
				Assert.fail("Resulting DOT should contain no blank lines;"); //$NON-NLS-1$
			}
		}
		scanner.close();
	}

	private String read(final File file) {
		try {
			Scanner scanner = new Scanner(file);
			StringBuilder builder = new StringBuilder();
			while (scanner.hasNextLine()) {
				builder.append(scanner.nextLine() + "\n"); //$NON-NLS-1$
			}
			scanner.close();
			return builder.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
