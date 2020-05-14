/******************************************************************************
 * Copyright (c) 2011, 2016 Stephan Schwiebert and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stephan Schwiebert - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef.cloudio.examples.ui.snippets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.gef.cloudio.internal.ui.ICloudLabelProvider;
import org.eclipse.gef.cloudio.internal.ui.TagCloud;
import org.eclipse.gef.cloudio.internal.ui.TagCloudViewer;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This snippet demonstrates how to create a {@link TagCloudViewer}, how to use
 * label- and content-providers and how to add an
 * {@link ISelectionChangedListener}.
 * 
 * @author sschwieb
 *
 */
public class TagCloudViewerSnippet {

	static class CustomLabelProvider extends BaseLabelProvider implements ICloudLabelProvider {

		private Font font;

		public CustomLabelProvider(Font font) {
			this.font = font;
		}

		@Override
		public String getLabel(Object element) {
			return element.toString();
		}

		@Override
		public double getWeight(Object element) {
			return Math.random();
		}

		@Override
		public Color getColor(Object element) {
			return Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
		}

		@Override
		public FontData[] getFontData(Object element) {
			return font.getFontData();
		}

		@Override
		public float getAngle(Object element) {
			return (float) (-90 + Math.random() * 180);
		}

		@Override
		public String getToolTip(Object element) {
			return element.toString();
		}

	}

	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		TagCloud cloud = new TagCloud(shell, SWT.NONE);

		final TagCloudViewer viewer = new TagCloudViewer(cloud);

		// A simple content provider for a list of elements
		viewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			@Override
			public Object[] getElements(Object inputElement) {
				return ((List<?>) inputElement).toArray();
			}

		});

		// A simple label provider (see above)
		viewer.setLabelProvider(new CustomLabelProvider(cloud.getFont()));

		// Demo of an selection listener
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
				System.out.println("Selection: " + selection);
			}
		});

		// Demo data
		List<String> data = new ArrayList<>();
		data.add("Hello");
		data.add("World");
		data.add("Hello Cloudio");

		shell.setBounds(50, 50, 300, 300);
		cloud.setBounds(0, 0, shell.getBounds().width, shell.getBounds().height);

		// Set the input of the viewer
		viewer.setInput(data);

		// Set initial selection:
		viewer.setSelection(new StructuredSelection(Arrays.asList("Hello Cloudio")));

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}