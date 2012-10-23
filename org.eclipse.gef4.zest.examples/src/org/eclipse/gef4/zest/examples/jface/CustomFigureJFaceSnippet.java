/*******************************************************************************
 * Copyright (c) 2011 Simon Templer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon Templer - example for using custom figures based on
 *        ILabeledFigure/IStyleableFigure, associated to bug 335136  
 *******************************************************************************/
package org.eclipse.gef4.zest.examples.jface;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.gef4.zest.core.viewers.EntityConnectionData;
import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.viewers.IFigureProvider;
import org.eclipse.gef4.zest.core.viewers.IGraphEntityContentProvider;
import org.eclipse.gef4.zest.core.widgets.ILabeledFigure;
import org.eclipse.gef4.zest.core.widgets.IStyleableFigure;
import org.eclipse.gef4.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Example demonstrating the use of a custom figure that implements
 * {@link ILabeledFigure} and {@link IStyleableFigure}.<br>
 * <br>
 * The custom figure will be configured according to the label provider and
 * highlighted when it is selected.
 */
public class CustomFigureJFaceSnippet {

	/**
	 * Custom figure shaped like a rectangle.
	 */
	public static class RectLabelFigure extends RectangleFigure implements
			ILabeledFigure, IStyleableFigure {

		private Label label;

		private Color borderColor;

		public RectLabelFigure() {
			super();

			setLayoutManager(new GridLayout(1, true));

			label = new Label();
			add(label);
			GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true);
			setConstraint(label, gd);
		}

		protected void outlineShape(Graphics graphics) {
			graphics.setForegroundColor(borderColor);

			super.outlineShape(graphics);
		}

		public void setBorderColor(Color borderColor) {
			this.borderColor = borderColor;
		}

		public void setBorderWidth(int borderWidth) {
			setLineWidth(borderWidth);
		}

		public void setText(String text) {
			label.setText(text);
			adjustSize();
		}

		public String getText() {
			return label.getText();
		}

		public void setIcon(Image icon) {
			label.setIcon(icon);
			adjustSize();
		}

		public Image getIcon() {
			return label.getIcon();
		}

		protected void adjustSize() {
			setSize(getPreferredSize());
		}

	}

	static class MyContentProvider implements IGraphEntityContentProvider {

		public Object[] getConnectedTo(Object entity) {
			if (entity.equals("One")) {
				return new Object[] { "Two" };
			}
			if (entity.equals("Two")) {
				return new Object[] { "Three" };
			}
			if (entity.equals("Three")) {
				return new Object[] { "One" };
			}
			return null;
		}

		public Object[] getElements(Object inputElement) {
			return new String[] { "One", "Two", "Three" };
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	static class MyLabelProvider extends LabelProvider implements
			IFigureProvider {
		final Image image = Display.getDefault().getSystemImage(
				SWT.ICON_INFORMATION);

		public Image getImage(Object element) {
			if (element instanceof EntityConnectionData) {
				return null;
			}

			return image;
		}

		public String getText(Object element) {
			if (element instanceof EntityConnectionData) {
				return null;
			}

			return element.toString();
		}

		public IFigure getFigure(Object element) {
			// use a custom figure
			return new RectLabelFigure();
		}

	}

	static GraphViewer viewer = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setText("CustomFigureJFaceSnippet");
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.setSize(400, 400);
		viewer = new GraphViewer(shell, SWT.NONE);
		viewer.setContentProvider(new MyContentProvider());
		viewer.setLabelProvider(new MyLabelProvider());
		viewer.setLayoutAlgorithm(new SpringLayoutAlgorithm());
		viewer.setInput(new Object());
		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}
}