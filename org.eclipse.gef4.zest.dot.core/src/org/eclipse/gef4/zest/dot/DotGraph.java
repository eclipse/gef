/*******************************************************************************
 * Copyright (c) 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.zest.dot;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.internal.dot.DotExport;
import org.eclipse.gef4.zest.internal.dot.DotImport;
import org.eclipse.gef4.zest.internal.dot.GraphCreatorInterpreter;
import org.eclipse.swt.widgets.Composite;

/**
 * A Zest graph that can be be built from and exported to the DOT language.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class DotGraph extends Graph {

	/**
	 * @param dot
	 *            The DOT graph (e.g. "graph{1--2}") or snippet (e.g. "1->2")
	 * @param parent
	 *            The parent for the graph
	 * @param style
	 *            The style bits
	 */
	public DotGraph(String dot, Composite parent, int style) {
		super(parent, style);
		new GraphCreatorInterpreter().create(new DotImport(dot).getDotAst(),
				this);
	}

	/**
	 * @param dot
	 *            The DOT graph file (e.g. containing "graph{1--2}")
	 * @param parent
	 *            The parent for the graph
	 * @param style
	 *            The style bits
	 */
	public DotGraph(IFile dot, Composite parent, int style) {
		super(parent, style);
		new GraphCreatorInterpreter().create(new DotImport(dot).getDotAst(),
				this);
	}

	/**
	 * @param dot
	 *            The DOT graph file (e.g. containing "graph{1--2}")
	 * @param parent
	 *            The parent for the graph
	 * @param style
	 *            The style bits
	 */
	public DotGraph(File dot, Composite parent, int style) {
		super(parent, style);
		new GraphCreatorInterpreter().create(new DotImport(dot).getDotAst(),
				this);
	}

	/**
	 * @param parent
	 *            The parent for the graph
	 * @param style
	 *            The style bits
	 */
	public DotGraph(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param dot
	 *            The DOT snippet (e.g. "1->2") to add to this graph
	 * @return A graph equivalent to this graph, including the given DOT snippet
	 */
	public DotGraph add(String dot) {
		new DotImport(dot).into(this);
		return this;
	}

	/**
	 * @return A representation of this graph in the DOT language
	 */
	public String toDot() {
		return new DotExport(this).toDotString();
	}

}
