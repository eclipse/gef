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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * This snippet shows how to create a curved connection using Zest.
 * 
 * @author Ian Bull
 * 
 */
public class GraphSnippet11 {

	public static void createConnection(Graph g, GraphNode n1, GraphNode n2,
			Color color, int curve) {
		GraphConnection connection = new GraphConnection(g, SWT.NONE, n1, n2);
		connection.setLineColor(color);
		connection.setCurveDepth(curve);
		connection.setLineWidth(1);
	}

	public static void main(String[] args) {
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setText("GraphSnippet11");
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);

		final Graph g = new Graph(shell, SWT.NONE);
		GraphNode n = new GraphNode(g, SWT.NONE, "Node 1");
		GraphNode n2 = new GraphNode(g, SWT.NONE, "Node 2");
		createConnection(g, n, n2, ColorConstants.darkGreen, 20);
		createConnection(g, n, n2, ColorConstants.darkGreen, -20);
		createConnection(g, n, n2, ColorConstants.darkBlue, 40);
		createConnection(g, n, n2, ColorConstants.darkBlue, -40);
		createConnection(g, n, n2, ColorConstants.darkGray, 60);
		createConnection(g, n, n2, ColorConstants.darkGray, -60);
		createConnection(g, n, n2, ColorConstants.black, 0);
		g.setLayoutAlgorithm(new SpringLayoutAlgorithm(), true);

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}

}
