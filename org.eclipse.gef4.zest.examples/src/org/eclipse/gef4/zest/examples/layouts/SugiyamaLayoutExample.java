/*******************************************************************************
 * Copyright 2012, Fabian Steeg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Fabian Steeg
 *******************************************************************************/
package org.eclipse.gef4.zest.examples.layouts;

import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.gef4.zest.layouts.algorithms.SugiyamaLayoutAlgorithm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Sample usage for the {@link SugiyamaLayoutAlgorithm}.
 * 
 * @author Fabian Steeg
 */
public class SugiyamaLayoutExample {

	public static void main(String[] args) {
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);

		Graph g = new Graph(shell, SWT.NONE);
		g.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);

		GraphNode coal = new GraphNode(g, SWT.NONE, "Coal");
		GraphNode ore = new GraphNode(g, SWT.NONE, "Ore");
		GraphNode stone = new GraphNode(g, SWT.NONE, "Stone");
		GraphNode metal = new GraphNode(g, SWT.NONE, "Metal");
		GraphNode concrete = new GraphNode(g, SWT.NONE, "Concrete");
		GraphNode machine = new GraphNode(g, SWT.NONE, "Machine");
		GraphNode building = new GraphNode(g, SWT.NONE, "Building");

		new GraphConnection(g, SWT.NONE, coal, metal);
		new GraphConnection(g, SWT.NONE, coal, concrete);
		new GraphConnection(g, SWT.NONE, metal, machine);
		new GraphConnection(g, SWT.NONE, metal, building);
		new GraphConnection(g, SWT.NONE, concrete, building);
		new GraphConnection(g, SWT.NONE, ore, metal);
		new GraphConnection(g, SWT.NONE, stone, concrete);

		g.setLayoutAlgorithm(new SugiyamaLayoutAlgorithm(), true);

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}

	}
}
