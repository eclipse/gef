/*******************************************************************************
 * Copyright (c) 2011 Zoltan Ujhelyi. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Zoltan Ujhelyi - initial implementation
 *******************************************************************************/
package org.eclipse.gef4.zest.examples.jface;

import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This snippet shows how to use the ITreeContentProvider to build a graph.
 */
public class GraphJFaceSnippet9 {

	static class MyContentProvider implements ITreeContentProvider {

		private static final String n1 = "First", n2 = "Second", n3 = "Third",
				n4 = "Fourth";

		public Object[] getElements(Object inputElement) {
			return new String[] { n1 };
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getChildren(Object parentElement) {
			if (parentElement.equals(n1)) {
				return new Object[] { n2, n3 };
			}
			if (parentElement.equals(n2)) {
				return new Object[] { n4 };
			}
			return null;
		}

		public Object getParent(Object element) {
			if (element.equals(n2)) {
				return n1;
			} else if (element.equals(n3)) {
				return n1;
			} else if (element.equals(n4)) {
				return new Object[] { n2 };
			}
			return null;
		}

		public boolean hasChildren(Object element) {
			return element.equals("First") || element.equals("Second");
		}
	}

	static class MyLabelProvider extends LabelProvider {
		final Image image = Display.getDefault().getSystemImage(
				SWT.ICON_WARNING);

		public Image getImage(Object element) {
			if (element instanceof String) {
				return image;
			}
			return null;
		}

		public String getText(Object element) {
			if (element instanceof String) {
				return element.toString();
			}
			return null;
		}

	}

	static GraphViewer viewer = null;

	public static void main(String[] args) {
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.setSize(400, 400);
		Button button = new Button(shell, SWT.PUSH);
		button.setText("Reload");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				viewer.setInput(new Object());
			}
		});
		viewer = new GraphViewer(shell, SWT.NONE);
		viewer.setContentProvider(new MyContentProvider());
		viewer.setLabelProvider(new MyLabelProvider());
		viewer.setLayoutAlgorithm(new TreeLayoutAlgorithm());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				System.out.println("Selection changed: "
						+ (event.getSelection()));
			}
		});
		viewer.setInput(new Object());
		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}
}
