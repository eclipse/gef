/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC,
 * Canada. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: The Chisel Group, University of Victoria
 ******************************************************************************/
package org.eclipse.gef4.zest.examples.jface;

import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.viewers.IGraphContentProvider;
import org.eclipse.gef4.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This snippet shows how the refresh works on a Zest viewer.
 */
public class GraphJFaceSnippet5 {

	static class MyContentProvider implements IGraphContentProvider {

		Object[] elements = new Object[] { "Rock2Paper", "Paper2Scissors",
				"Scissors2Rock" };

		public Object getDestination(Object rel) {
			if ("Rock2Paper".equals(rel)) {
				return "Rock";
			} else if ("Paper2Scissors".equals(rel)
					|| "Scissors2Paper".equals(rel)) {
				return "Paper";
			} else if ("Scissors2Rock".equals(rel)) {
				return "Scissors";
			}
			return null;
		}

		public Object[] getElements(Object input) {
			return elements;
		}

		public Object getSource(Object rel) {
			if ("Rock2Paper".equals(rel)) {
				return "Paper";
			} else if ("Paper2Scissors".equals(rel)
					|| "Scissors2Paper".equals(rel)) {
				return "Scissors";
			} else if ("Scissors2Rock".equals(rel)) {
				return "Rock";
			}
			return null;
		}

		public void setElements(Object[] elements) {
			this.elements = elements;
		}

		public double getWeight(Object connection) {
			return 0;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	static class MyLabelProvider extends LabelProvider {
		final Image image = Display.getDefault().getSystemImage(
				SWT.ICON_WARNING);

		public MyLabelProvider() {

		}

		public String getText(Object element) {
			return element.toString();
		}

		public Image getImage(Object element) {
			return image;
		}
	}

	static GraphViewer viewer = null;
	private static MyContentProvider contentProvider;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setText("GraphJFaceSnippet2");
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.setSize(400, 400);
		Composite parent = new Composite(shell, SWT.NONE);
		parent.setLayout(new GridLayout(2, false));
		buildViewer(parent);
		buildButton(parent);
		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}

	}

	private static void buildButton(Composite parent) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText("Refresh");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				contentProvider.setElements(new Object[] { "Rock2Paper",
						"Scissors2Paper", "Scissors2Rock" });
				viewer.refresh();
			}
		});
	}

	private static void buildViewer(Composite parent) {
		viewer = new GraphViewer(parent, SWT.NONE);
		viewer.getGraphControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		contentProvider = new MyContentProvider();
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new MyLabelProvider());
		viewer.setLayoutAlgorithm(new SpringLayoutAlgorithm());
		viewer.setInput(new Object());
	}
}
