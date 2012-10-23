/*******************************************************************************
 * Copyright (c) 2011 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial implementation
 *******************************************************************************/
package org.eclipse.gef4.zest.examples.jface;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.viewers.IGraphEntityContentProvider;
import org.eclipse.gef4.zest.core.viewers.ISelfStyleProvider;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
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
 * Shows curved edges.
 * 
 * @author Fabian Steeg
 * 
 */
public class GraphJFaceSnippet8 {

	static class MyContentProvider implements IGraphEntityContentProvider {

		public Object[] getConnectedTo(Object entity) {
			if (entity.equals("First")) {
				return new Object[] { "First", "Second" };
			}
			if (entity.equals("Second")) {
				return new Object[] { "Third" };
			}
			if (entity.equals("Third")) {
				return new Object[] { "Second" };
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

	static class MyLabelProvider extends LabelProvider implements
			ISelfStyleProvider {

		public Image getImage(Object element) {
			return null;
		}

		public String getText(Object element) {
			return element.toString();
		}

		public void selfStyleConnection(Object element,
				GraphConnection connection) {
			connection.setLineStyle(SWT.LINE_CUSTOM);
			PolylineConnection pc = (PolylineConnection) connection
					.getConnectionFigure();
			pc.setLineDash(new float[] { 4 });
			pc.setTargetDecoration(createDecoration(ColorConstants.white));
			pc.setSourceDecoration(createDecoration(ColorConstants.green));
		}

		private PolygonDecoration createDecoration(final Color color) {
			PolygonDecoration decoration = new PolygonDecoration() {
				protected void fillShape(Graphics g) {
					g.setBackgroundColor(color);
					super.fillShape(g);
				}
			};
			decoration.setScale(10, 5);
			decoration.setBackgroundColor(color);
			return decoration;
		}

		public void selfStyleNode(Object element, GraphNode node) {
		}

	}

	static GraphViewer viewer = null;

	public static void main(String[] args) {
		Display d = new Display();
		Shell shell = new Shell(d);
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
