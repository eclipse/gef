/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 * Note: Parts of this class have been transferred from org.eclipse.gef.zest.examples.jface.GraphJFaceSnippet1
 *
 *******************************************************************************/
package org.eclipse.gef.zest.examples.jface;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.gef.zest.fx.jface.IGraphAttributesProvider;
import org.eclipse.gef.zest.fx.jface.IGraphContentProvider;
import org.eclipse.gef.zest.fx.jface.ZestContentViewer;
import org.eclipse.gef.zest.fx.jface.ZestFxJFaceModule;
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

import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

public class JFaceEdgeDecorationExample {

	static class MyContentProvider implements IGraphContentProvider {
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

		public Object[] getAdjacentNodes(Object entity) {
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

		@Override
		public Object[] getNestedGraphNodes(Object node) {
			return null;
		}

		@Override
		public boolean hasNestedGraph(Object node) {
			return false;
		}
	}

	static class CircleHead extends Circle {
		public CircleHead() {
			super(5);
		}
	}

	static class DiamondHead extends Polygon {
		public DiamondHead() {
			super(15.0, 0.0, 7.5, -7.5, 0.0, 0.0, 7.5, 7.5, 15.0, 0.0);
		}
	}

	static class MyLabelProvider extends LabelProvider
			implements IGraphAttributesProvider {
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
			Map<String, Object> edgeAttributes = new HashMap<>();
			edgeAttributes.put(ZestProperties.SOURCE_DECORATION__E,
					new CircleHead());
			edgeAttributes.put(ZestProperties.TARGET_DECORATION__E,
					new DiamondHead());
			edgeAttributes.put(ZestProperties.CURVE_CSS_STYLE__E,
					"-fx-stroke: red;");
			return edgeAttributes;
		}

		@Override
		public Map<String, Object> getNodeAttributes(Object node) {
			return Collections.emptyMap();
		}

		@Override
		public Map<String, Object> getGraphAttributes() {
			return Collections.emptyMap();
		}

		@Override
		public Map<String, Object> getNestedGraphAttributes(
				Object nestingNode) {
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
