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
import org.eclipse.gef4.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Minimal Zest graph sample input for the Zest-To-Dot transformation.
 */
public class SimpleGraph extends Graph {
	/**
	 * {@link Graph#Graph(Composite, int)}
	 * 
	 * @param parent
	 *            The parent
	 * @param style
	 *            The style bits
	 */
	public SimpleGraph(final Composite parent, final int style) {
		super(parent, style);

		/* Set a layout algorithm: */
		setLayoutAlgorithm(new TreeLayoutAlgorithm(), true);

		/* Set the nodes: */
		GraphNode n1 = new GraphNode(this, SWT.NONE, "1"); //$NON-NLS-1$
		GraphNode n2 = new GraphNode(this, SWT.NONE, "2"); //$NON-NLS-1$
		GraphNode n3 = new GraphNode(this, SWT.NONE, "3"); //$NON-NLS-1$

		/* Connection from n1 to n2: */
		new GraphConnection(this, SWT.NONE, n1, n2);

		/* Connection from n1 to n3: */
		new GraphConnection(this, SWT.NONE, n1, n3);

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
		shell.setText(SimpleGraph.class.getSimpleName());
		shell.setLayout(new FillLayout());
		shell.setSize(200, 250);
		new SimpleGraph(shell, SWT.NONE);
		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}
}
