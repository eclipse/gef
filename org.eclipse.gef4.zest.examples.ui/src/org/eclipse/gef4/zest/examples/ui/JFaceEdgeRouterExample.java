/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 * Note: Parts of this class have been transferred from org.eclipse.gef4.zest.examples.jface.GraphJFaceSnippet1
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.examples.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.fx.nodes.IFXConnectionRouter;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.ui.jface.IGraphNodeContentProvider;
import org.eclipse.gef4.zest.fx.ui.jface.IGraphNodeLabelProvider;
import org.eclipse.gef4.zest.fx.ui.jface.ZestContentViewer;
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

public class JFaceEdgeRouterExample {

	static class MyContentProvider implements IGraphNodeContentProvider {
		private Object input;

		private static String first() {
			return "First";
		}

		private static String second() {
			return "Second";
		}

		private static String third() {
			return "Third";
		}

		@Override
		public Object[] getNodes() {
			if (input == null) {
				return new Object[] {};
			}
			return new Object[] { first(), second(), third() };
		}

		public Object[] getConnectedTo(Object entity) {
			if (entity.equals(first())) {
				return new Object[] { second() };
			}
			if (entity.equals(second())) {
				return new Object[] { third() };
			}
			if (entity.equals(third())) {
				return new Object[] { first() };
			}
			return null;
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput,
				Object newInput) {
			input = newInput;
		}
	}

	protected static IFXConnectionRouter getManhattenRouter() {
		return new IFXConnectionRouter() {
			@Override
			public ICurve routeConnection(Point[] points) {
				if (points == null || points.length < 2) {
					return new org.eclipse.gef4.geometry.planar.Polyline(0, 0,
							0, 0);
				}
				List<Point> manhattenPoints = new ArrayList<Point>();
				Point start = points[0];
				Point end = points[points.length - 1];
				Point mid = start.getTranslated(end).getScaled(0.5);
				boolean isHorizontal = Math.abs(end.x - start.x) > Math
						.abs(end.y - start.y);

				manhattenPoints.add(start);
				if (isHorizontal) {
					manhattenPoints.add(new Point(mid.x, start.y));
					manhattenPoints.add(new Point(mid.x, mid.y));
					manhattenPoints.add(new Point(mid.x, end.y));
				} else {
					manhattenPoints.add(new Point(start.x, mid.y));
					manhattenPoints.add(new Point(mid.x, mid.y));
					manhattenPoints.add(new Point(end.x, mid.y));
				}
				manhattenPoints.add(end);

				return new org.eclipse.gef4.geometry.planar.Polyline(
						manhattenPoints.toArray(new Point[] {}));
			}
		};
	}

	static class MyLabelProvider extends LabelProvider
			implements IGraphNodeLabelProvider {
		public Image getImage(Object element) {
			return Display.getCurrent().getSystemImage(SWT.ICON_WARNING);
		}

		public String getText(Object element) {
			if (element instanceof String) {
				return element.toString();
			}
			return null;
		}

		@Override
		public Map<String, Object> getEdgeAttributes(Object sourceNode,
				Object targetNode) {
			return Collections.singletonMap(ZestProperties.EDGE_ROUTER,
					(Object) getManhattenRouter());
		}

		@Override
		public Map<String, Object> getNodeAttributes(Object node) {
			return null;
		}

		@Override
		public Map<String, Object> getRootGraphAttributes() {
			return null;
		}
	}

	static ZestContentViewer viewer = null;

	public static void main(String[] args) {
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.setSize(400, 400);
		Button button = new Button(shell, SWT.PUSH);
		button.setText("Reload");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				viewer.setInput(null);
				viewer.setInput(new Object());
			}
		});

		viewer = new ZestContentViewer();
		viewer.createControl(shell, SWT.NONE);
		viewer.setContentProvider(new MyContentProvider());
		viewer.setLabelProvider(new MyLabelProvider());
		viewer.setLayoutAlgorithm(new SpringLayoutAlgorithm());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				System.out.println(
						"Selection changed: " + (event.getSelection()));
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
