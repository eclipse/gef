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
package org.eclipse.gef4.zest.examples.jface;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef4.fx.nodes.IConnectionDecoration;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.jface.IGraphNodeContentProvider;
import org.eclipse.gef4.zest.fx.jface.IGraphNodeLabelProvider;
import org.eclipse.gef4.zest.fx.jface.ZestContentViewer;
import org.eclipse.gef4.zest.fx.jface.ZestFxJFaceModule;
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

import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

public class JFaceEdgeDecorationExample {

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

	static class CircleHead extends Circle implements IConnectionDecoration {
		public CircleHead() {
			super(5);
		}

		@Override
		public Point getLocalEndPoint() {
			return new Point(0, 0);
		}

		@Override
		public Point getLocalStartPoint() {
			return new Point(0, 0);
		}

		@Override
		public Node getVisual() {
			return this;
		}
	}

	static class DiamondHead extends Polyline implements IConnectionDecoration {
		public DiamondHead() {
			super(15.0, 0.0, 7.5, -7.5, 0.0, 0.0, 7.5, 7.5, 15.0, 0.0);
		}

		@Override
		public Point getLocalEndPoint() {
			return new Point(15, 0);
		}

		@Override
		public Point getLocalStartPoint() {
			return new Point(0, 0);
		}

		@Override
		public Node getVisual() {
			return this;
		}
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
			Map<String, Object> edgeAttributes = new HashMap<String, Object>();
			edgeAttributes.put(ZestProperties.EDGE_SOURCE_DECORATION,
					new CircleHead());
			edgeAttributes.put(ZestProperties.EDGE_TARGET_DECORATION,
					new DiamondHead());
			return edgeAttributes;
		}

		@Override
		public Map<String, Object> getNodeAttributes(Object node) {
			return Collections.emptyMap();
		}

		@Override
		public Map<String, Object> getRootGraphAttributes() {
			return Collections.emptyMap();
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

		viewer = new ZestContentViewer(new ZestFxJFaceModule());
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
