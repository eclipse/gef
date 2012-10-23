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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.viewers.IGraphContentProvider;
import org.eclipse.gef4.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * This snippet uses a very simple file format to read a graph. Edges are listed
 * on a new line in a file as such: a calls b b calls c c calld d
 * 
 * The content provider creates an edge for each line in the file and names the
 * sources and destination from the line.
 * 
 * 
 * @author Ian Bull
 * 
 */
public class GraphJFaceSnippet3 {

	public static final String graph = "a calls b\n" + "a calls c\n"
			+ "b calld d\n" + "b calls e\n" + "c calls f\n" + "c calls g\n"
			+ "d calls h\n" + "d calls i\n" + "e calls j\n" + "e calls k\n"
			+ "f calls l\n" + "f calls m\n";

	static class SimpleGraphContentProvider implements IGraphContentProvider {

		private StringTokenizer graph;

		public Object getDestination(Object rel) {
			String string = (String) rel;
			String[] parts = string.split(" ");
			return parts[2];
		}

		public Object[] getElements(Object input) {
			ArrayList listOfEdges = new ArrayList();
			while (graph.hasMoreTokens()) {
				listOfEdges.add(graph.nextToken());
			}
			return listOfEdges.toArray();
		}

		public Object getSource(Object rel) {
			String string = (String) rel;
			String[] parts = string.split(" ");
			return parts[0];
		}

		public double getWeight(Object connection) {
			return 0;
		}

		public void dispose() {

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput != null) {
				graph = new StringTokenizer((String) newInput, "\n");
			}
		}

	}

	public static void main(String[] args) throws IOException {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Simple Graph File Format");

		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setFilterNames(new String[] { "Simple Graph Files (*.sgf)",
				"All Files (*.*)" });
		dialog.setFilterExtensions(new String[] { "*.sgf", "*.*" }); // Windows
																		// wild
																		// cards

		String directory = System.getProperty("user.dir")
				+ "/src/org/eclipse/gef4/zest/tests/jface/SimpleGraph.sgf"; // eclipse/zest/examples/jface/";
		System.out.println(directory);
		dialog.setFilterPath(directory);
		// dialog.setFilterPath(System.getProperty("user.dir") +
		// "src/org/eclipse/gef4/zest/examples/jface/"); //Windows path

		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.setSize(400, 400);
		GraphViewer viewer = null;

		viewer = new GraphViewer(shell, SWT.NONE);
		viewer.setContentProvider(new SimpleGraphContentProvider());
		viewer.setLabelProvider(new LabelProvider());
		viewer.setLayoutAlgorithm(new RadialLayoutAlgorithm());

		shell.open();
		String fileName = dialog.open();

		if (fileName == null) {
			// use the sample graph
			viewer.setInput(graph);
		} else {
			FileReader fileReader = new FileReader(new File(fileName));
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			while (bufferedReader.ready()) {
				stringBuffer.append(bufferedReader.readLine() + "\n");
			}
			viewer.setInput(stringBuffer.toString());
		}

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}
