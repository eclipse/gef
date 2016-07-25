/*******************************************************************************
 * Copyright (c) 2010, 2016 Fabian Steeg and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg - initial API and implementation (see bug #277380)
 *******************************************************************************/
package org.eclipse.gef.dot.examples;

import org.eclipse.gef.dot.internal.DotImport;
import org.eclipse.gef.graph.Graph;

/**
 * @author Fabian Steeg (fsteeg)
 * @author anyssen
 */
public final class DotImportExample {

	public static void main(final String[] args) {
		/* We can create Graphs based on GraphViz Dot files/string */
		Graph graph = new DotImport().importDot("graph { 1--2 ; 1--3 }");
		Graph digraph = new DotImport().importDot("digraph { 1->2 ; 1->3 }");
	}

}
