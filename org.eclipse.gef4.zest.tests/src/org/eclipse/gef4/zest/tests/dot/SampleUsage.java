/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.zest.tests.dot;

import org.eclipse.gef4.zest.dot.DotGraph;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/**
 * API sample usage.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class SampleUsage {
	@Test
	public void sampleUsage() {
		/* A Dot graph is a Zest graph that can be created from DOT. */
		Shell shell = new Shell();
		/* The DOT input can be given as a String, File or IFile: */
		DotGraph graph = new DotGraph("digraph{1->2}", shell, SWT.NONE);
		/* The Dot graph can be modified using DOT snippets: */
		graph.add("2->3").add("2->4");
		/* The snippets can contain DOT node and edge attributes: */
		graph.add("node[label=zested]; edge[style=dashed]; 3->5; 4->6");
		/* Export to a DOT string: */
		String dot = graph.toDot();
		/* See some of the results: */
		System.out.println(graph);
		System.out.println(dot);
		// open(shell);
	}

	@SuppressWarnings("unused")
	/* Unused for running tests */
	private void open(final Shell shell) {
		shell.setText(DotGraph.class.getSimpleName());
		shell.setLayout(new FillLayout());
		shell.setSize(600, 300);
		shell.open();
		Display display = shell.getDisplay();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
		display.dispose();
	}
}
