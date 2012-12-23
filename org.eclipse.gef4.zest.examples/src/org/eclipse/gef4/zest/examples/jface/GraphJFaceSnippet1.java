/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package org.eclipse.gef4.zest.examples.jface;

import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.viewers.IGraphEntityContentProvider;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.gef4.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.jface.viewers.ISelectionChangedListener;
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
 * This snippet shows how to use the IGraphEntityContentProvider to build a
 * graph.
 * 
 * @author Ian Bull
 * 
 */
public class GraphJFaceSnippet1 {

	/**
	 * The Content Provider
	 * 
	 * @author irbull
	 * 
	 */
	static class MyContentProvider implements IGraphEntityContentProvider {

		public Object[] getConnectedTo(Object entity) {
			if (entity.equals("First")) {
				return new Object[] { "Second" };
			}
			if (entity.equals("Second")) {
				return new Object[] { "Third" };
			}
			if (entity.equals("Third")) {
				return new Object[] { "First" };
			}
			return null;
		}

		public Object[] getElements(Object inputElement) {
			return new String[] { "First", "Second", "Third" };
		}

		public double getWeight(Object entity1, Object entity2) {
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

	/**
	 * @param args
	 */
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
		viewer = new GraphViewer(shell, ZestStyles.NONE);
		viewer.getGraphControl().setAnimationEnabled(true);
		viewer.setContentProvider(new MyContentProvider());
		viewer.setLabelProvider(new MyLabelProvider());
		viewer.setLayoutAlgorithm(new SpringLayoutAlgorithm());
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
