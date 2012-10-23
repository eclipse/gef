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

import java.util.Iterator;

import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.viewers.IGraphContentProvider;
import org.eclipse.gef4.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class GraphJFaceSnippet4 {
	static class MyContentProvider implements IGraphContentProvider {

		public Object getDestination(Object rel) {
			if ("Rock2Paper".equals(rel)) {
				return "Rock";
			} else if ("Paper2Scissors".equals(rel)) {
				return "Paper";
			} else if ("Scissors2Rock".equals(rel)) {
				return "Scissors";
			}
			return null;
		}

		public Object[] getElements(Object input) {
			return new Object[] { "Rock2Paper", "Paper2Scissors",
					"Scissors2Rock" };
		}

		public Object getSource(Object rel) {
			if ("Rock2Paper".equals(rel)) {
				return "Paper";
			} else if ("Paper2Scissors".equals(rel)) {
				return "Scissors";
			} else if ("Scissors2Rock".equals(rel)) {
				return "Rock";
			}
			return null;
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

		public Image getImage(Object element) {
			if (element.equals("Rock") || element.equals("Paper")
					|| element.equals("Scissors")) {
				return image;
			}
			return null;
		}

		public String getText(Object element) {
			return element.toString();
		}

	}

	static GraphViewer viewer = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setText("GraphJFaceSnippet2");
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.setSize(400, 400);
		viewer = new GraphViewer(shell, SWT.NONE);
		viewer.setContentProvider(new MyContentProvider());
		viewer.setLabelProvider(new MyLabelProvider());
		viewer.setLayoutAlgorithm(new SpringLayoutAlgorithm());
		viewer.setInput(new Object());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				System.out.println("Selection Changed: "
						+ selectionToString((StructuredSelection) event
								.getSelection()));
			}

			private String selectionToString(StructuredSelection selection) {
				StringBuffer stringBuffer = new StringBuffer();
				Iterator iterator = selection.iterator();
				boolean first = true;
				while (iterator.hasNext()) {
					if (first) {
						first = false;
					} else {
						stringBuffer.append(" : ");
					}
					stringBuffer.append(iterator.next());
				}
				return stringBuffer.toString();
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
