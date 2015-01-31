/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.dot.tests;

import java.io.File;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Graph.Attr;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;
import org.junit.Assert;

/**
 * Util class for different tests.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class DotTestUtils {

	public static final String RESOURCES_TESTS = "resources/"; //$NON-NLS-1$

	private DotTestUtils() { /* Enforce non-instantiability */
	}

	/**
	 * Wipes (does not delete hidden files and files starting with a '.') the
	 * given output folder used for generated files during testing and makes
	 * sure it contains no files with the given extension.
	 * 
	 * @param location
	 *            The folder to wipe of all files with the given extension
	 * @param suffix
	 *            The extension of the files to delete in the given output
	 *            folder
	 */
	public static void wipeOutput(final File location, final String suffix) {
		String[] files = location.list();
		int deleted = 0;
		if (files != null && files.length > 0) {
			for (String file : files) {
				File deletionCandidate = new File(location, file);
				/*
				 * Relying on hidden is not safe on all platforms, so we double
				 * check so that no .cvsignore files etc. are deleted:
				 */
				if (!deletionCandidate.isHidden()
						&& !deletionCandidate.getName().startsWith(".")) { //$NON-NLS-1$
					boolean delete = deletionCandidate.delete();
					if (delete) {
						deleted++;
					}
				}
			}
			int dotFiles = countFilesWithSuffix(location, suffix);
			Assert.assertEquals(
					"Default output directory should contain no files matching the suffix before tests run;", //$NON-NLS-1$
					0, dotFiles);
			System.out.println(String.format("Deleted %s files in %s", deleted, //$NON-NLS-1$
					location));
		}
	}

	private static int countFilesWithSuffix(final File folder,
			final String suffix) {
		String[] list = folder.list();
		int dotFiles = 0;
		for (String name : list) {
			if (name.endsWith(suffix)) {
				dotFiles++;
			}
		}
		return dotFiles;
	}

	public static Graph getLabeledGraph() {
		/* Global settings: */
		Graph.Builder graph = new Graph.Builder()
				.attr(Attr.Key.GRAPH_TYPE.toString(),
						Graph.Attr.Value.GRAPH_DIRECTED).attr(
						Attr.Key.LAYOUT.toString(), new TreeLayoutAlgorithm());

		/* Nodes: */
		Node n1 = new Node.Builder()
				.attr(Attr.Key.ID.toString(), "1").attr(Attr.Key.LABEL.toString(), "one").build(); //$NON-NLS-1$
		Node n2 = new Node.Builder()
				.attr(Attr.Key.ID.toString(), "2").attr(Attr.Key.LABEL.toString(), "two").build(); //$NON-NLS-1$
		Node n3 = new Node.Builder().attr(Attr.Key.ID.toString(), "3").build(); //$NON-NLS-1$
		Node n4 = new Node.Builder().attr(Attr.Key.ID.toString(), "4").build(); //$NON-NLS-1$

		/* Connection from n1 to n2: */
		Edge e1 = new Edge.Builder(n1, n2)
				.attr(Attr.Key.LABEL.toString(), "+1").build(); //$NON-NLS-1$

		/* Connection from n1 to n3: */
		Edge e2 = new Edge.Builder(n1, n3)
				.attr(Attr.Key.LABEL.toString(), "+2").build(); //$NON-NLS-1$

		/* Connection from n3 to n4: */
		Edge e3 = new Edge.Builder(n3, n4).build();

		return graph.nodes(n1, n2, n3, n4).edges(e1, e2, e3).build();
	}

	public static Graph getSampleGraph() {
		/* Global settings: */
		Graph.Builder graph = new Graph.Builder()//
				.attr(Attr.Key.GRAPH_TYPE.toString(),
						Graph.Attr.Value.GRAPH_DIRECTED)//
				.attr(Attr.Key.LAYOUT.toString(), new TreeLayoutAlgorithm());

		/* Nodes: */
		Node n1 = new Node.Builder()
				.attr(Attr.Key.LABEL.toString(), "Node").build(); //$NON-NLS-1$
		Node n2 = new Node.Builder()
				.attr(Attr.Key.LABEL.toString(), "Node").build(); //$NON-NLS-1$
		Node n3 = new Node.Builder()
				.attr(Attr.Key.LABEL.toString(), "Leaf1").build(); //$NON-NLS-1$
		Node n4 = new Node.Builder()
				.attr(Attr.Key.LABEL.toString(), "Leaf2").build(); //$NON-NLS-1$

		/* Connection from n1 to n2: */
		new Edge.Builder(n1, n2)
				.attr(Attr.Key.LABEL.toString(), "Edge")
				.attr(Attr.Key.EDGE_STYLE.toString(),
						Graph.Attr.Value.LINE_DASH).build();

		/* Connection from n2 to n3: */
		Edge e1 = new Edge.Builder(n2, n3)
				.attr(Attr.Key.LABEL.toString(), "Edge")
				.attr(Attr.Key.EDGE_STYLE.toString(),
						Graph.Attr.Value.LINE_DASH).build();

		/* Connection from n2 to n4: */
		Edge e2 = new Edge.Builder(n2, n4)
				.attr(Attr.Key.LABEL.toString(), "Dotted")
				.attr(Attr.Key.EDGE_STYLE.toString(), Graph.Attr.Value.LINE_DOT)
				.build();

		return graph.nodes(n1, n2, n3, n4).edges(e1, e2).build();
	}

	public static Graph getSimpleDiGraph() {

		/* Global settings, here we set the directed property: */
		Graph.Builder graph = new Graph.Builder()
				.attr(Attr.Key.GRAPH_TYPE.toString(),
						Graph.Attr.Value.GRAPH_DIRECTED).attr(
						Attr.Key.LAYOUT.toString(), new TreeLayoutAlgorithm());

		/* Nodes: */
		Node n1 = new Node.Builder().attr(Attr.Key.ID.toString(), "1").build(); //$NON-NLS-1$
		Node n2 = new Node.Builder().attr(Attr.Key.ID.toString(), "2").build(); //$NON-NLS-1$
		Node n3 = new Node.Builder().attr(Attr.Key.ID.toString(), "3").build(); //$NON-NLS-1$ 

		/* Connection from n1 to n2: */
		Edge e1 = new Edge.Builder(n1, n2).build();

		/* Connection from n2 to n3: */
		Edge e2 = new Edge.Builder(n2, n3).build();

		return graph.nodes(n1, n2, n3).edges(e1, e2).build();
	}

	public static Graph getSimpleGraph() {
		/* Set a layout algorithm: */
		Graph.Builder graph = new Graph.Builder().attr(
				Attr.Key.GRAPH_TYPE.toString(),
				Graph.Attr.Value.GRAPH_UNDIRECTED).attr(
				Attr.Key.LAYOUT.toString(), new TreeLayoutAlgorithm());

		/* Set the nodes: */
		Node n1 = new Node.Builder().attr(Attr.Key.ID.toString(), "1").build(); //$NON-NLS-1$
		Node n2 = new Node.Builder().attr(Attr.Key.ID.toString(), "2").build(); //$NON-NLS-1$
		Node n3 = new Node.Builder().attr(Attr.Key.ID.toString(), "3").build(); //$NON-NLS-1$ 

		/* Connection from n1 to n2: */
		Edge e1 = new Edge.Builder(n1, n2).build();

		/* Connection from n1 to n3: */
		Edge e2 = new Edge.Builder(n1, n3).build();

		return graph.nodes(n1, n2, n3).edges(e1, e2).build();
	}

	public static Graph getStyledGraph() {
		/* Global properties: */
		Graph.Builder graph = new Graph.Builder()
				.attr(Attr.Key.GRAPH_TYPE.toString(),
						Graph.Attr.Value.GRAPH_DIRECTED).attr(
						Attr.Key.LAYOUT.toString(), new TreeLayoutAlgorithm());

		/* Nodes: */
		Node n1 = new Node.Builder().attr(Attr.Key.ID.toString(), "1").build(); //$NON-NLS-1$
		Node n2 = new Node.Builder().attr(Attr.Key.ID.toString(), "2").build(); //$NON-NLS-1$
		Node n3 = new Node.Builder().attr(Attr.Key.ID.toString(), "3").build(); //$NON-NLS-1$
		Node n4 = new Node.Builder().attr(Attr.Key.ID.toString(), "4").build(); //$NON-NLS-1$
		Node n5 = new Node.Builder().attr(Attr.Key.ID.toString(), "5").build(); //$NON-NLS-1$

		/* Connection from n1 to n2: */
		Edge e1 = new Edge.Builder(n1, n2).attr(Attr.Key.EDGE_STYLE.toString(),
				Graph.Attr.Value.LINE_DASH).build();

		/* Connection from n2 to n3: */
		Edge e2 = new Edge.Builder(n2, n3).attr(Attr.Key.EDGE_STYLE.toString(),
				Graph.Attr.Value.LINE_DOT).build();

		/* Connection from n3 to n4: */
		Edge e3 = new Edge.Builder(n3, n4).attr(Attr.Key.EDGE_STYLE.toString(),
				Graph.Attr.Value.LINE_DASHDOT).build();

		/* Connection from n3 to n5: */
		Edge e4 = new Edge.Builder(n3, n5).attr(Attr.Key.EDGE_STYLE.toString(),
				Graph.Attr.Value.LINE_DASHDOTDOT).build();

		Edge e5 = new Edge.Builder(n4, n5).attr(Attr.Key.EDGE_STYLE.toString(),
				Graph.Attr.Value.LINE_SOLID).build();

		return graph.nodes(n1, n2, n3, n4, n5).edges(e1, e2, e3, e4, e5)
				.build();
	}
}
