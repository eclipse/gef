/*******************************************************************************
 * Copyright (c) 2009, 2015 Fabian Steeg and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg - initial API and implementation (see bug #277380)
 *******************************************************************************/

package org.eclipse.gef4.dot;

import java.io.File;
import java.util.Scanner;

import org.eclipse.gef4.dot.internal.DotFileUtils;
import org.eclipse.gef4.dot.internal.DotTemplate;
import org.eclipse.gef4.graph.Graph;

/**
 * Utilities to exportDOT files or strings from Graph instances containing
 * attributes defined in {@link DotProperties}.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class DotExport {

	private String dotString;
	private String graphName = "Unnamed" + System.currentTimeMillis(); //$NON-NLS-1$

	/**
	 * @param graph
	 *            The Zest graph to export to DOT
	 */
	public DotExport(final Graph graph) {
		this.dotString = graphToDot(graph);
		this.graphName = graph.getClass().getSimpleName();
	}

	/**
	 * @param dotString
	 *            The DOT graph to export to an image
	 */
	public DotExport(String dotString) {
		this.dotString = dotString;
	}

	/**
	 * Export a Zest Graph to a DOT string.
	 * 
	 * @return The DOT representation of the given Zest graph
	 */
	public String toDotString() {
		return dotString;
	}

	/**
	 * Export a Zest Graph to a DOT file.
	 * 
	 * @param destination
	 *            The file to store the DOT export
	 * @return The given file
	 */
	public File toDotFile(final File destination) {
		DotFileUtils.write(dotString, destination);
		return destination;
	}

	@Override
	public String toString() {
		/* The exact name 'Graph' is not valid for rendering with Graphviz: */
		return graphName.equals("Graph") ? "Dot" + graphName //$NON-NLS-1$//$NON-NLS-2$
				: graphName;
	}

	private static String graphToDot(final Graph graph) {
		String raw = new DotTemplate().generate(graph);
		raw = removeBlankLines(raw);
		return raw;
	}

	private static String removeBlankLines(final String raw) {
		Scanner scanner = new Scanner(raw);
		StringBuilder builder = new StringBuilder();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (!line.trim().equals("")) { //$NON-NLS-1$
				builder.append(line + "\n"); //$NON-NLS-1$
			}
		}
		scanner.close();
		return builder.toString();
	}

}
