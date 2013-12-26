package org.eclipse.gef4.zest.examples.layouts;

import java.util.List;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;
import org.eclipse.gef4.zest.core.widgets.DAGExpandCollapseManager;
import org.eclipse.gef4.zest.core.widgets.DefaultSubgraph;
import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphItem;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
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

public class TreeLayoutExample {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Create the shell
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setText("TreeLayoutExample");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 10;
		shell.setLayout(gridLayout);
		shell.setSize(600, 500);

		final Graph g = new Graph(shell, ZestStyles.NONE);
		final TreeLayoutAlgorithm algorithm = new TreeLayoutAlgorithm();
		g.setSubgraphFactory(new DefaultSubgraph.PrunedSuccessorsSubgraphFactory());
		g.setLayoutAlgorithm(algorithm, false);

		g.setExpandCollapseManager(new DAGExpandCollapseManager(false));
		g.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 10, 10));
		g.setSize(500, 500);
		g.applyLayout();

		GraphNode root = new GraphNode(g, SWT.NONE, "Root");

		GraphNode lastNode = null;
		for (int i = 0; i < 3; i++) {
			GraphNode n = new GraphNode(g, SWT.NONE, "1 - " + i);
			if (lastNode != null)
				new GraphConnection(g, SWT.NONE, n, lastNode).setDirected(true);
			for (int j = 0; j < 1; j++) {
				GraphNode n2 = new GraphNode(g, SWT.NONE, "2 - " + j);
				GraphConnection c = new GraphConnection(g, SWT.NONE, n, n2);
				c.setWeight(-1);
				c.setDirected(true);
				lastNode = n2;
			}

			new GraphConnection(g, SWT.NONE, root, n).setDirected(true);
		}

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
				if (e.widget == buttonTopDown)
					algorithm.setDirection(TreeLayoutAlgorithm.TOP_DOWN);
				if (e.widget == buttonBottomUp)
					algorithm.setDirection(TreeLayoutAlgorithm.BOTTOM_UP);
				if (e.widget == buttonLeftRight)
					algorithm.setDirection(TreeLayoutAlgorithm.LEFT_RIGHT);
				if (e.widget == buttonRightLeft)
					algorithm.setDirection(TreeLayoutAlgorithm.RIGHT_LEFT);

				g.applyLayout();
			}
		};
		buttonTopDown.addSelectionListener(buttonListener);
		buttonBottomUp.addSelectionListener(buttonListener);
		buttonLeftRight.addSelectionListener(buttonListener);
		buttonRightLeft.addSelectionListener(buttonListener);

		final Button resizeButton = new Button(shell, SWT.CHECK);
		resizeButton.setText("Resize");
		resizeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				algorithm.setResizing(resizeButton.getSelection());
			}
		});

		final Button nodeSpaceButton = new Button(shell, SWT.CHECK);
		nodeSpaceButton.setText("Set node space");
		nodeSpaceButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				algorithm.setNodeSpace(nodeSpaceButton.getSelection() ? new Dimension(
						100, 100) : null);
			}
		});

		final Button animateButton = new Button(shell, SWT.CHECK);
		animateButton.setText("Animate");
		animateButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				g.setAnimationEnabled(animateButton.getSelection());
			}
		});

		shell.pack();
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
