/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC,
 * Canada. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * 		The Chisel Group, University of Victoria
 * 		Zoltan Ujhelyi - update for connection router
 ******************************************************************************/
package org.eclipse.gef4.zest.examples.swt;

import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This snippet shows how to update the default connection router - modified
 * from PaintSnippet example.
 * 
 * @author Zoltan Ujhelyi
 * @author Ian Bull - initial commit
 * 
 */
public class ManhattanLayoutGraphSnippet {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Create the shell
		final Display d = new Display();
		final Shell shell = new Shell(d);
		shell.setText("GraphSnippet1");
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);

		Button b = new Button(shell, SWT.PUSH);
		b.setText("Change Router");

		final Graph g = new Graph(shell, SWT.NONE);

		GraphNode n = new GraphNode(g, SWT.NONE, "Paper");
		GraphNode n2 = new GraphNode(g, SWT.NONE, "Rock");
		GraphNode n3 = new GraphNode(g, SWT.NONE, "Scissors");
		new GraphConnection(g, SWT.NONE, n, n2);
		new GraphConnection(g, SWT.NONE, n2, n3);
		new GraphConnection(g, SWT.NONE, n3, n);
		g.setLayoutAlgorithm(new SpringLayoutAlgorithm(), true);

		b.addSelectionListener(new SelectionAdapter() {
			ConnectionRouter router = null;

			public void widgetSelected(SelectionEvent e) {
				if (router == null) {
					router = new ManhattanConnectionRouter();
				} else {
					router = null;
				}
				g.setRouter(router);
			}

		});

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}
}
