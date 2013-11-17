/*******************************************************************************
 * Copyright (c) 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.graph;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef4.graph.internal.dot.DotImport;
import org.eclipse.gef4.graph.internal.dot.GraphCreatorInterpreter;
import org.eclipse.gef4.graph.internal.dot.export.DotExport;

/**
 * A Zest graph that can be be built from and exported to the DOT language.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class DotGraph extends Graph {

	/**
	 * @param dot
	 *            The DOT graph (e.g. "graph{1--2}") or snippet (e.g. "1->2")
	 * 
	 */
	public DotGraph(String dot) {
		super();
		new GraphCreatorInterpreter().create(new DotImport(dot).getDotAst(),
				this);
	}

	/**
	 * @param dot
	 *            The DOT graph file (e.g. containing "graph{1--2}")
	 * 
	 */
	public DotGraph(IFile dot) {
		super();
		new GraphCreatorInterpreter().create(new DotImport(dot).getDotAst(),
				this);
	}

	/**
	 * @param dot
	 *            The DOT graph file (e.g. containing "graph{1--2}")
	 * 
	 */
	public DotGraph(File dot) {
		super();
		new GraphCreatorInterpreter().create(new DotImport(dot).getDotAst(),
				this);
	}

	/**
	
	 */
	public DotGraph() {
		super();
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
