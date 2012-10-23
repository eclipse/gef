/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC,
 * Canada. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: The Chisel Group, University of Victoria
 ******************************************************************************/
package org.eclipse.gef4.zest.examples.swt;

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
 * 
 * This snippet shows how to create a curved connection using Zest. Each time
 * the Button is clicked, the curve changes.
 * 
 * @author Ian Bull
 * 
 */
public class GraphSnippet10 {

	public static void main(String[] args) {
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setText("GraphSnippet1");
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);

		final Graph g = new Graph(shell, SWT.NONE);

		GraphNode n = new GraphNode(g, SWT.NONE, "Paper");
		n.setBorderColor(org.eclipse.draw2d.ColorConstants.yellow);
		n.setBorderWidth(3);
		GraphNode n2 = new GraphNode(g, SWT.NONE, "Rock");
		GraphNode n3 = new GraphNode(g, SWT.NONE, "Scissors");
		final GraphConnection connection = new GraphConnection(g, SWT.NONE, n,
				n2);
		connection.setLineWidth(3);
		new GraphConnection(g, SWT.NONE, n2, n3);
		new GraphConnection(g, SWT.NONE, n3, n);
		g.setLayoutAlgorithm(new SpringLayoutAlgorithm(), true);

		Button button = new Button(shell, SWT.PUSH);
		button.setText("Change Curve");
		button.addSelectionListener(new SelectionAdapter() {
			int count = 0;

			public void widgetSelected(SelectionEvent e) {
				count = ++count % 16;
				if (count > 8) {
					connection.setCurveDepth((-16 + count) * 10);
				} else {
					connection.setCurveDepth(count * 10);
				}
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
