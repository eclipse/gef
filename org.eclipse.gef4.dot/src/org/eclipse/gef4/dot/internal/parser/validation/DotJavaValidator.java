/*******************************************************************************
 * Copyright (c) 2009, 2014 Fabian Steeg and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg    - intial Xtext generation (see bug #277380)
 *     Alexander Nyßen - initial implementation
 *
 *******************************************************************************/

package org.eclipse.gef4.dot.internal.parser.validation;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef4.dot.internal.DotProperties;
import org.eclipse.gef4.dot.internal.parser.conversion.DotTerminalConverters;
import org.eclipse.gef4.dot.internal.parser.dot.AttrStmt;
import org.eclipse.gef4.dot.internal.parser.dot.Attribute;
import org.eclipse.gef4.dot.internal.parser.dot.AttributeType;
import org.eclipse.gef4.dot.internal.parser.dot.DotGraph;
import org.eclipse.gef4.dot.internal.parser.dot.DotPackage;
import org.eclipse.gef4.dot.internal.parser.dot.EdgeOp;
import org.eclipse.gef4.dot.internal.parser.dot.EdgeRhsNode;
import org.eclipse.gef4.dot.internal.parser.dot.EdgeRhsSubgraph;
import org.eclipse.gef4.dot.internal.parser.dot.EdgeStmtNode;
import org.eclipse.gef4.dot.internal.parser.dot.EdgeStmtSubgraph;
import org.eclipse.gef4.dot.internal.parser.dot.GraphType;
import org.eclipse.gef4.dot.internal.parser.dot.NodeStmt;
import org.eclipse.gef4.dot.internal.parser.dot.Subgraph;
import org.eclipse.xtext.validation.Check;

/**
 * Provides DOT-specific validation rules.
 * 
 * @author anyssen
 *
 */
public class DotJavaValidator extends AbstractDotJavaValidator {

	/**
	 * Error code for invalid edge 'style' attribute value. Used to bind quick
	 * fixes.
	 */
	public static final String ATTRIBUTE__INVALID_VALUE__EDGE_STYLE = "ATTRIBUTE__INVALID_VALUE__EDGE_STYLE";

	/**
	 * Checks that within an {@link Attribute} only valid attribute values are
	 * used (dependent on context, in which the attribute is specified).
	 * 
	 * @param attribute
	 *            The {@link Attribute} to validate.
	 */
	@Check
	public void checkValidAttributeValue(Attribute attribute) {
		if (isEdgeAttribute(attribute)
				&& DotProperties.EDGE_STYLE.equals(attribute.getName())) {
			// 'style' can also be used for nodes or clusters, so we have to
			// check the context as well
			String unquotedValue = DotTerminalConverters
					.unquote(attribute.getValue());
			if (!DotProperties.EDGE_STYLE_VALUES.contains(unquotedValue)) {
				// provide (issue) code and data for quickfix
				error("Style '" + unquotedValue
						+ "' is not a valid DOT style for Edge.",
						DotPackage.eINSTANCE.getAttribute_Value(),
						ATTRIBUTE__INVALID_VALUE__EDGE_STYLE, unquotedValue);
			}
		}
	}

	/**
	 * Checks whether the given {@link Attribute} is used in the context of a
	 * node. That is, it is either nested below an {@link NodeStmt} or used
	 * within an {@link AttrStmt} of type {@link AttributeType#NODE}.
	 * 
	 * @param attribute
	 *            The {@link Attribute} to test.
	 * @return <code>true</code> if the {@link Attribute} is used in the context
	 *         of an node, <code>false</code> otherwise.
	 */
	public static boolean isNodeAttribute(Attribute attribute) {
		// attribute nested below EdgeStmtNode or EdgeStmtSubgraph
		if (getAncestorOfType(attribute, NodeStmt.class) != null) {
			return true;
		}
		// global AttrStmt with AttributeType 'node'
		AttrStmt attrStmt = getAncestorOfType(attribute, AttrStmt.class);
		return attrStmt != null
				&& AttributeType.NODE.equals(attrStmt.getType());
	}

	/**
	 * Checks whether the given {@link Attribute} is used in the context of a
	 * subgraph.
	 * 
	 * @param attribute
	 *            The {@link Attribute} to test.
	 * @return <code>true</code> if the {@link Attribute} is used in the context
	 *         of subgraph, <code>false</code> otherwise.
	 */
	public static boolean isSubgraphAttribute(Attribute attribute) {
		if (isEdgeAttribute(attribute) || isNodeAttribute(attribute)) {
			return false;
		}
		// attribute nested below Subgraph
		return getAncestorOfType(attribute, Subgraph.class) != null;
	}

	/**
	 * Checks whether the given {@link Attribute} is used in the context of a
	 * top-level graph.
	 * 
	 * @param attribute
	 *            The {@link Attribute} to test.
	 * @return <code>true</code> if the {@link Attribute} is used in the context
	 *         of a top-level graph, <code>false</code> otherwise.
	 */
	public static boolean isRootGraphAttribute(Attribute attribute) {
		// attribute nested below EdgeStmtNode or EdgeStmtSubgraph
		if (isEdgeAttribute(attribute) || isNodeAttribute(attribute)
				|| isSubgraphAttribute(attribute)) {
			return false;
		}
		return true;
	}

	/**
	 * Checks whether the given {@link Attribute} is used in the context of an
	 * edge. That is, it is either nested below an {@link EdgeStmtNode} or an
	 * {@link EdgeStmtSubgraph}, or used within an {@link AttrStmt} of type
	 * {@link AttributeType#EDGE}.
	 * 
	 * @param attribute
	 *            The {@link Attribute} to test.
	 * @return <code>true</code> if the {@link Attribute} is used in the context
	 *         of an edge, <code>false</code> otherwise.
	 */
	public static boolean isEdgeAttribute(Attribute attribute) {
		// attribute nested below EdgeStmtNode or EdgeStmtSubgraph
		if (getAncestorOfType(attribute, EdgeStmtNode.class) != null
				|| getAncestorOfType(attribute,
						EdgeStmtSubgraph.class) != null) {
			return true;
		}
		// global AttrStmt with AttributeType 'edge'
		AttrStmt attrStmt = getAncestorOfType(attribute, AttrStmt.class);
		return attrStmt != null
				&& AttributeType.EDGE.equals(attrStmt.getType());
	}

	/**
	 * Ensures that within {@link EdgeRhsNode}, '-&gt;' is used in directed
	 * graphs, while '--' is used in undirected graphs.
	 * 
	 * @param edgeRhsNode
	 *            The EdgeRhsNode to validate.
	 */
	@Check
	public void checkEdgeOpCorrespondsToGraphType(EdgeRhsNode edgeRhsNode) {
		checkEdgeOpCorrespondsToGraphType(edgeRhsNode.getOp(),
				getAncestorOfType(edgeRhsNode, DotGraph.class).getType());
	}

	/**
	 * Ensures that within {@link EdgeRhsSubgraph} '-&gt;' is used in directed
	 * graphs, while '--' is used in undirected graphs.
	 * 
	 * @param edgeRhsSubgraph
	 *            The EdgeRhsSubgraph to validate.
	 */
	@Check
	public void checkEdgeOpCorrespondsToGraphType(
			EdgeRhsSubgraph edgeRhsSubgraph) {
		checkEdgeOpCorrespondsToGraphType(edgeRhsSubgraph.getOp(),
				getAncestorOfType(edgeRhsSubgraph, DotGraph.class).getType());
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

	@SuppressWarnings("unchecked")
	private static <T extends EObject> T getAncestorOfType(EObject eObject,
			Class<T> type) {
		EObject container = eObject.eContainer();
		while (container != null
				&& !type.isAssignableFrom(container.getClass())) {
			container = container.eContainer();
		}
		return (T) container;
	}

}
