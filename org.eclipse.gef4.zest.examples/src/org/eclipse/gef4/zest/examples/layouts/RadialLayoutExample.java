package org.eclipse.gef4.zest.examples.layouts;

import org.eclipse.gef4.layout.LayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.RadialLayoutAlgorithm;
import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class RadialLayoutExample {
	public static void main(String[] args) {
		// Create the shell
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setText("GraphSnippet1");
		shell.setLayout(new FillLayout());
		shell.setSize(500, 500);

		final Graph g = new Graph(shell, SWT.NONE);
		g.setSize(500, 500);
		GraphNode root = new GraphNode(g, SWT.NONE, "Root");
		for (int i = 0; i < 3; i++) {
			GraphNode n = new GraphNode(g, SWT.NONE, "1 - " + i);
			for (int j = 0; j < 3; j++) {
				GraphNode n2 = new GraphNode(g, SWT.NONE, "2 - " + j);
				new GraphConnection(g, SWT.NONE, n, n2).setWeight(-1);
			}
			new GraphConnection(g, SWT.NONE, root, n);
		}

		final LayoutAlgorithm layoutAlgorithm = new RadialLayoutAlgorithm();

		g.setLayoutAlgorithm(layoutAlgorithm, true);
		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}
}
