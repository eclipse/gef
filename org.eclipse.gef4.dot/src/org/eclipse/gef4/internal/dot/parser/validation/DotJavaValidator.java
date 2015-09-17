/*******************************************************************************
 * Copyright (c) 2009, 2014 Fabian Steeg and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg - initial API and implementation (see bug #277380)
 *******************************************************************************/

package org.eclipse.gef4.internal.dot.parser.validation;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef4.internal.dot.parser.dot.DotGraph;
import org.eclipse.gef4.internal.dot.parser.dot.DotPackage;
import org.eclipse.gef4.internal.dot.parser.dot.EdgeOp;
import org.eclipse.gef4.internal.dot.parser.dot.EdgeRhsNode;
import org.eclipse.gef4.internal.dot.parser.dot.EdgeRhsSubgraph;
import org.eclipse.gef4.internal.dot.parser.dot.GraphType;
import org.eclipse.xtext.validation.Check;

/**
 * Provides DOT-specific validation rules.
 * 
 * @author anyssen
 *
 */
public class DotJavaValidator extends AbstractDotJavaValidator {

	/**
	 * Ensures that within {@link EdgeRhsNode}, '->' is used in directed graphs,
	 * while '--' is used in undirected graphs.
	 * 
	 * @param edgeRhsNode
	 *            The EdgeRhsNode to validate.
	 */
	@Check
	public void checkEdgeOpCorrespondsToGraphType(EdgeRhsNode edgeRhsNode) {
		checkEdgeOpCorrespondsToGraphType(edgeRhsNode.getOp(),
				getDotGraph(edgeRhsNode).getType());
	}

	/**
	 * Ensures that within {@link EdgeRhsSubgraph} '->' is used in directed
	 * graphs, while '--' is used in undirected graphs.
	 * 
	 * @param edgeRhsSubgraph
	 *            The EdgeRhsSubgraph to validate.
	 */
	@Check
	public void checkEdgeOpCorrespondsToGraphType(
			EdgeRhsSubgraph edgeRhsSubgraph) {
		checkEdgeOpCorrespondsToGraphType(edgeRhsSubgraph.getOp(),
				getDotGraph(edgeRhsSubgraph).getType());
	}

	private void checkEdgeOpCorrespondsToGraphType(EdgeOp edgeOp,
			GraphType graphType) {
		boolean edgeDirected = edgeOp.equals(EdgeOp.DIRECTED);
		boolean graphDirected = graphType.equals(GraphType.DIGRAPH);
		if (graphDirected && !edgeDirected) {
			error("EdgeOp '--' may only be used in undirected graphs.",
					DotPackage.eINSTANCE.getEdgeRhs_Op());

		} else if (!graphDirected && edgeDirected) {
			error("EdgeOp '->' may only be used in directed graphs.",
					DotPackage.eINSTANCE.getEdgeRhs_Op());
		}
	}

	private DotGraph getDotGraph(EObject eObject) {
		EObject container = eObject.eContainer();
		while (container != null && !(container instanceof DotGraph)) {
			container = container.eContainer();
		}
		return (DotGraph) container;
	}

}
