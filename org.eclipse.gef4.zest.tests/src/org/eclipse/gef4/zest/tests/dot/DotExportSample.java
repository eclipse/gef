/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.zest.tests.dot;

import java.io.File;

import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.gef4.zest.internal.dot.DotExport;
import org.eclipse.gef4.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Zest graph sample input for the Zest-To-Dot transformation. Contains
 * everything that is currently supported by the transformation: node and edge
 * labels, edge styles.
 * <p/>
 * Uses the actual Zest Graph class and populates an instance of that, instead
 * of subclassing the Zest Graph and exporting the subclass (as in the samples
 * used for testing, which are based on Graphs generated using the
 * org.eclipse.gef4.zest.dot.import bundle).
 */
public final class DotExportSample {
	public static void main(final String[] args) {
		Display d = new Display();
		Shell shell = new Shell(d);
		/* Set up a directed Zest graph with a single connection: */
		Graph graph = new Graph(shell, SWT.NONE);
		graph.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		GraphConnection edge = new GraphConnection(graph, SWT.NONE,
				new GraphNode(graph, SWT.NONE, "Node 1"), new GraphNode(graph, //$NON-NLS-1$
						SWT.NONE, "Node 2")); //$NON-NLS-1$
		edge.setText("A dotted edge"); //$NON-NLS-1$
		edge.setLineStyle(SWT.LINE_DOT);
		/* Export the Zest graph to a DOT string or a DOT file: */
		DotExport dotExport = new DotExport(graph);
		System.out.println(dotExport.toDotString());
		dotExport.toDotFile(new File("src-gen/DirectSample.dot")); //$NON-NLS-1$
		/* Show the Zest graph: */
		graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(), true);
		shell.setLayout(new FillLayout());
		shell.setSize(200, 250);
		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}

	private DotExportSample() { /* enforce non-instantiability */
	}
}
