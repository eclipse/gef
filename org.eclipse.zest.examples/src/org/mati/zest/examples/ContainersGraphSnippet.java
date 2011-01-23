/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC,
 * Canada. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: The Chisel Group, University of Victoria
 ******************************************************************************/
package org.mati.zest.examples;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

/**
 * 
 * This snippet shows how to use graph containers.
 * 
 * @author Ian Bull
 * 
 */
public class ContainersGraphSnippet {

	public static void main(String[] args) {
		final Display d = new Display();
		Shell shell = new Shell(d);
		shell.setText("GraphSnippet11");
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);

		final Graph g = new Graph(shell, SWT.NONE);

		Image zx = new Image(d, "zxsnow.png");
		IFigure tooltip = new Figure();
		tooltip.setBorder(new MarginBorder(5, 5, 5, 5));
		FlowLayout layout = new FlowLayout(false);
		layout.setMajorSpacing(3);
		layout.setMinorAlignment(3);
		tooltip.setLayoutManager(new FlowLayout(false));
		tooltip.add(new ImageFigure(zx));
		tooltip.add(new Label("Name: Chris Aniszczyk"));
		tooltip.add(new Label("Location: Austin, Texas"));

		GraphContainer c1 = new GraphContainer(g, SWT.NONE);
		c1.setText("Canada");
		GraphContainer c2 = new GraphContainer(g, SWT.NONE);
		c2.setText("USA");

		GraphNode n1 = new GraphNode(c1, SWT.NONE, "Ian B.");
		n1.setSize(200, 100);
		GraphNode n2 = new GraphNode(c2, SWT.NONE, "Chris A.");
		n2.setTooltip(tooltip);

		GraphConnection connection = new GraphConnection(g,
				ZestStyles.CONNECTIONS_DIRECTED, n1, n2);
		connection.setCurveDepth(-30);
		GraphConnection connection2 = new GraphConnection(g,
				ZestStyles.CONNECTIONS_DIRECTED, n2, n1);
		connection2.setCurveDepth(-30);

		g.setLayoutAlgorithm(new SpringLayoutAlgorithm(), true);

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
		zx.dispose();
	}

}
