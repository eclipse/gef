package org.mati.zest.examples;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.widgets.DAGExpandCollapseManager;
import org.eclipse.zest.core.widgets.DefaultSubgraph;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.DirectedGraphLayoutAlgorithm;

public class DAGExample {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Create the shell
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setText("GraphSnippet1");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 10;
		shell.setLayout(gridLayout);
		shell.setSize(500, 500);

		final Graph g = new Graph(shell, SWT.NONE);
		// LayoutAlgorithm algorithm = new CompositeLayoutAlgorithm(
		// new LayoutAlgorithm[] { new DirectedGraphLayoutAlgorithm(),
		// new HorizontalShiftAlgorithm() });
		LayoutAlgorithm algorithm = new DirectedGraphLayoutAlgorithm();

		g.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 10, 10));
		g.setSize(500, 500);

		g
				.setSubgraphFactory(new DefaultSubgraph.PrunedSuccessorsSubgraphFactory());
		g.setLayoutAlgorithm(algorithm, false);
		g.setExpandCollapseManager(new DAGExpandCollapseManager());

		GraphNode root = new GraphNode(g, SWT.NONE, "Root");
		GraphNode a = new GraphNode(g, SWT.NONE, "A");
		GraphNode b = new GraphNode(g, SWT.NONE, "B");
		GraphNode c = new GraphNode(g, SWT.NONE, "C");
		GraphNode e = new GraphNode(g, SWT.NONE, "D");

		GraphNode f = new GraphNode(g, SWT.NONE, "E");
		GraphNode h = new GraphNode(g, SWT.NONE, "F");

		new GraphConnection(g, ZestStyles.CONNECTIONS_DIRECTED, root, a);
		new GraphConnection(g, ZestStyles.CONNECTIONS_DIRECTED, root, b);
		new GraphConnection(g, ZestStyles.CONNECTIONS_DIRECTED, root, c);
		new GraphConnection(g, ZestStyles.CONNECTIONS_DIRECTED, a, c);
		new GraphConnection(g, ZestStyles.CONNECTIONS_DIRECTED, c, e);

		new GraphConnection(g, ZestStyles.CONNECTIONS_DIRECTED, e, f);
		new GraphConnection(g, ZestStyles.CONNECTIONS_DIRECTED, e, h);
		new GraphConnection(g, ZestStyles.CONNECTIONS_DIRECTED, root, h);


		
		hookMenu(g);

		final Button buttonTopDown = new Button(shell, SWT.FLAT);
		buttonTopDown.setText("TOP_DOWN");

		final Button buttonBottomUp = new Button(shell, SWT.FLAT);
		buttonBottomUp.setText("BOTTOM_UP");
		buttonBottomUp.setLayoutData(new GridData());

		final Button buttonLeftRight = new Button(shell, SWT.FLAT);
		buttonLeftRight.setText("LEFT_RIGHT");

		final Button buttonRightLeft = new Button(shell, SWT.FLAT);
		buttonRightLeft.setText("RIGHT_LEFT");

		SelectionAdapter buttonListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				g.applyLayout();
			}
		};
		buttonTopDown.addSelectionListener(buttonListener);
		buttonBottomUp.addSelectionListener(buttonListener);
		buttonLeftRight.addSelectionListener(buttonListener);
		buttonRightLeft.addSelectionListener(buttonListener);
		


		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}

	private static void hookMenu(final Graph g) {
		MenuManager menuMgr = new MenuManager("#PopupMenu");

		Action expandAction = new Action() {
			public void run() {
				List selection = g.getSelection();
				if (!selection.isEmpty()) {
					GraphNode selected = (GraphNode) selection.get(0);
					g.setExpanded((GraphNode) selected, true);
				}
			}
		};
		expandAction.setText("expand");
		menuMgr.add(expandAction);

		Action collapseAction = new Action() {
			public void run() {
				List selection = g.getSelection();
				if (!selection.isEmpty()) {
					GraphItem selected = (GraphItem) selection.get(0);
					g.setExpanded((GraphNode) selected, false);
				}
			}
		};
		collapseAction.setText("collapse");
		menuMgr.add(collapseAction);

		g.setMenu(menuMgr.createContextMenu(g));
	}
}
