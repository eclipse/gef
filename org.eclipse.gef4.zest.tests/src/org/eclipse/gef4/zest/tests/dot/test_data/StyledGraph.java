/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.zest.tests.dot.test_data;

import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.gef4.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Zest graph sample input for the Zest-To-Dot transformation demonstrating edge
 * styles support.
 */
public class StyledGraph extends Graph {
	/**
	 * {@link Graph#Graph(Composite, int)}
	 * 
	 * @param parent
	 *            The parent
	 * @param style
	 *            The style bits
	 */
	public StyledGraph(final Composite parent, final int style) {
		super(parent, style);

		/* Global properties: */
		setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		setLayoutAlgorithm(new TreeLayoutAlgorithm(), true);

		/* Nodes: */
		GraphNode n1 = new GraphNode(this, SWT.NONE, "1"); //$NON-NLS-1$
		GraphNode n2 = new GraphNode(this, SWT.NONE, "2"); //$NON-NLS-1$
		GraphNode n3 = new GraphNode(this, SWT.NONE, "3"); //$NON-NLS-1$
		GraphNode n4 = new GraphNode(this, SWT.NONE, "4"); //$NON-NLS-1$
		GraphNode n5 = new GraphNode(this, SWT.NONE, "5"); //$NON-NLS-1$

		/* Connection from n1 to n2: */
		GraphConnection n1n2 = new GraphConnection(this, SWT.NONE, n1, n2);
		n1n2.setLineStyle(SWT.LINE_DASH);

		/* Connection from n2 to n3: */
		GraphConnection n2n3 = new GraphConnection(this, SWT.NONE, n2, n3);
		n2n3.setLineStyle(SWT.LINE_DOT);

		/* Connection from n3 to n4: */
		GraphConnection n3n4 = new GraphConnection(this, SWT.NONE, n3, n4);
		n3n4.setLineStyle(SWT.LINE_DASHDOT);

		/* Connection from n3 to n5: */
		GraphConnection n3n5 = new GraphConnection(this, SWT.NONE, n3, n5);
		n3n5.setLineStyle(SWT.LINE_SOLID);

	}

	/**
	 * Displays this graph in a shell.
	 * 
	 * @param args
	 *            Not used
	 */
	public static void main(final String[] args) {
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setText(StyledGraph.class.getSimpleName());
		shell.setLayout(new FillLayout());
		shell.setSize(200, 250);
		new StyledGraph(shell, SWT.NONE);
		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}
}
