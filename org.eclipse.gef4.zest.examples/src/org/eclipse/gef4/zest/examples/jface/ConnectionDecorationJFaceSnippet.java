/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *     Zoltan Ujhelyi - update for connectionprovider
 *******************************************************************************/
package org.eclipse.gef4.zest.examples.jface;

import java.util.Iterator;

import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.viewers.IConnectionStyleProvider;
import org.eclipse.gef4.zest.core.viewers.IEntityConnectionStyleProvider;
import org.eclipse.gef4.zest.core.viewers.IGraphContentProvider;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.decoration.AbstractConnectionDecorator;
import org.eclipse.gef4.zest.core.widgets.decoration.IConnectionDecorator;
import org.eclipse.gef4.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This snippet shows how to use the {@link IConnectionRouterStyleProvider}
 * interface to set the connection router for some references.
 * 
 * Based on {@link GraphJFaceSnippet4}.
 * 
 * @author Zoltan Ujhelyi - update for connectionprovider
 * @author Ian Bull - original implementation
 * 
 */
public class ConnectionDecorationJFaceSnippet {

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

	static class MyConnectionRelationLabelProvider extends LabelProvider
			implements IConnectionStyleProvider {
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

		/* Relation-based customization: IConnectionStyleProvider */

		public ConnectionRouter getRouter(Object rel) {
			return null;
		}

		public int getConnectionStyle(Object rel) {
			return SWT.NONE;
		}

		public Color getColor(Object rel) {
			return null;
		}

		public Color getHighlightColor(Object rel) {
			return null;
		}

		public int getLineWidth(Object rel) {
			return -1;
		}

		public IFigure getTooltip(Object entity) {
			return null;
		}

		public IConnectionDecorator getConnectionDecorator(Object rel) {
			return new AbstractConnectionDecorator() {

				public RotatableDecoration createTargetDecoration(
						GraphConnection connection) {
					return null;
				}

				public RotatableDecoration createSourceDecoration(
						GraphConnection connection) {
					PolygonDecoration decoration = new PolygonDecoration();
					PointList decorationPointList = new PointList();
					decorationPointList.addPoint(0, 0);
					decorationPointList.addPoint(-2, 2);
					decorationPointList.addPoint(-4, 0);
					decorationPointList.addPoint(-2, -2);
					decoration.setTemplate(decorationPointList);
					return decoration;
				}
			};
		}

	}

	static class MyEndpointEntityLabelProvider extends LabelProvider implements
			IEntityConnectionStyleProvider {
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

		/* Endpoint-based customization: IEntityConnectionStyleProvider */

		public ConnectionRouter getRouter(Object src, Object dest) {
			System.out.println(src + " -> " + dest);
			if (!(src.equals("Paper") && dest.equals("Rock")))
				return new ManhattanConnectionRouter();
			else
				return null;
		}

		public int getConnectionStyle(Object src, Object dest) {
			return SWT.NONE;
		}

		public Color getColor(Object src, Object dest) {
			return null;
		}

		public Color getHighlightColor(Object src, Object dest) {
			return null;
		}

		public int getLineWidth(Object src, Object dest) {
			return -1;
		}

		public IFigure getTooltip(Object src, Object dest) {
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
		shell.setText("GraphJFaceSnippet2");
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.setSize(400, 400);
		viewer = new GraphViewer(shell, SWT.NONE);
		viewer.setContentProvider(new MyContentProvider());
		viewer.setLabelProvider(new MyConnectionRelationLabelProvider()); // MyEndpointEntityLabelProvider
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
