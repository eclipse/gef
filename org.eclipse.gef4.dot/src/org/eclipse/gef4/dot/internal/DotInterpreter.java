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

package org.eclipse.gef4.dot.internal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef4.dot.internal.parser.dot.AttrList;
import org.eclipse.gef4.dot.internal.parser.dot.AttrStmt;
import org.eclipse.gef4.dot.internal.parser.dot.Attribute;
import org.eclipse.gef4.dot.internal.parser.dot.AttributeType;
import org.eclipse.gef4.dot.internal.parser.dot.DotGraph;
import org.eclipse.gef4.dot.internal.parser.dot.EdgeRhsNode;
import org.eclipse.gef4.dot.internal.parser.dot.EdgeStmtNode;
import org.eclipse.gef4.dot.internal.parser.dot.GraphType;
import org.eclipse.gef4.dot.internal.parser.dot.NodeId;
import org.eclipse.gef4.dot.internal.parser.dot.NodeStmt;
import org.eclipse.gef4.dot.internal.parser.dot.Stmt;
import org.eclipse.gef4.dot.internal.parser.dot.Subgraph;
import org.eclipse.gef4.dot.internal.parser.dot.util.DotSwitch;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;

/**
 * Create a {@link Graph} instance from a DOT string by interpreting the AST of
 * the parsed DOT.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class DotInterpreter extends DotSwitch<Object> {

	private Map<String, Node> nodes;
	private Graph.Builder graph;
	private String globalEdgeStyle;
	private String globalEdgeLabel;
	private String globalNodeLabel;
	private String currentEdgeStyleValue;
	private String currentEdgeLabelValue;
	private String currentEdgeSourceNodeId;
	private boolean createConnection;

	/**
	 * @param dotAst
	 *            The DOT abstract synstx tree (AST) to interpret
	 * @return A graph instance for the given DOT AST
	 */
	public Graph interpret(DotAst dotAst) {
		return interpret(dotAst,
				new Graph.Builder().attr(DotProperties.GRAPH_LAYOUT,
						DotProperties.GRAPH_LAYOUT_DEFAULT));
	}

	private Graph interpret(DotAst dotAst, Graph.Builder graph) {
		if (dotAst.errors().size() > 0) {
			throw new IllegalArgumentException(
					String.format("Could not create graph: %s", dotAst //$NON-NLS-1$
							.errors().toString()));
		}
		this.graph = graph;
		nodes = new HashMap<>();
		TreeIterator<Object> contents = EcoreUtil
				.getAllProperContents(dotAst.resource(), false);
		while (contents.hasNext()) {
			doSwitch((EObject) contents.next());
		}
		return graph.build();
	}

	@Override
	public Object caseDotGraph(DotGraph object) {
		createGraph(object);
		return super.caseDotGraph(object);
	}

	@Override
	public Object caseAttribute(Attribute object) {
		/*
		 * Convenience for common 'rankdir=LR' attribute: use
		 * TreeLayoutAlgorithm.LEFT_RIGHT if nothing else is specified
		 */
		if (DotProperties.GRAPH_RANKDIR.equals(object.getName())) {
			String value = object.getValue();
			if (value == null)
				value = "";
			value = value.toLowerCase();
			boolean lr = DotProperties.GRAPH_RANKDIR_LR.equals(value);
			boolean td = DotProperties.GRAPH_RANKDIR_TD.equals(value);
			graph.attr(DotProperties.GRAPH_LAYOUT,
					DotProperties.GRAPH_LAYOUT_DOT);
			graph.attr(DotProperties.GRAPH_RANKDIR,
					lr ? DotProperties.GRAPH_RANKDIR_LR
							: td ? DotProperties.GRAPH_RANKDIR_TD
									: DotProperties.GRAPH_RANKDIR_DEFAULT);
		}
		return super.caseAttribute(object);
	}

	@Override
	public Object caseAttrStmt(AttrStmt object) {
		createAttributes(object);
		return super.caseAttrStmt(object);
	}

	@Override
	public Object caseNodeStmt(NodeStmt object) {
		createNode(object);
		return super.caseNodeStmt(object);
	}

	@Override
	public Object caseEdgeStmtNode(EdgeStmtNode object) {
		currentEdgeLabelValue = getAttributeValue(object,
				DotProperties.EDGE_LABEL);
		currentEdgeStyleValue = getAttributeValue(object,
				DotProperties.EDGE_STYLE);
		return super.caseEdgeStmtNode(object);
	}

	@Override
	public Object caseNodeId(NodeId object) {
		if (!createConnection) {
			currentEdgeSourceNodeId = escaped(object.getName());
		} else {
			String targetNodeId = escaped(object.getName());
			if (currentEdgeSourceNodeId != null && targetNodeId != null) {
				addConnectionTo(targetNodeId);
				// current target node may be source for next EdgeRHS
				currentEdgeSourceNodeId = targetNodeId;
			}
			createConnection = false;
		}
		return super.caseNodeId(object);
	}

	private void addConnectionTo(String targetNodeId) {
		Edge.Builder graphConnection = new Edge.Builder(
				node(currentEdgeSourceNodeId), node(targetNodeId));
		/* Set the optional label, if set in the DOT input: */
		if (currentEdgeLabelValue != null) {
			graphConnection.attr(DotProperties.EDGE_LABEL,
					currentEdgeLabelValue);
		} else if (globalEdgeLabel != null) {
			graphConnection.attr(DotProperties.EDGE_LABEL, globalEdgeLabel);
		}
		/* Set the optional style, if set in the DOT input and supported: */
		String currentEdgeStyleLc = new String(
				currentEdgeStyleValue == null ? "" : currentEdgeStyleValue)
						.toLowerCase();
		String globalEdgeStyleLc = new String(
				globalEdgeStyle == null ? "" : globalEdgeStyle).toLowerCase();
		if (!DotProperties.EDGE_STYLE_VOID.equals(currentEdgeStyleLc)
				&& supported(currentEdgeStyleLc,
						DotProperties.EDGE_STYLE_VALUES)) {
			// if an explicit local style is set, use it
			graphConnection.attr(DotProperties.EDGE_STYLE, currentEdgeStyleLc);
		} else if (!DotProperties.EDGE_STYLE_VOID.equals(globalEdgeStyleLc)
				&& supported(globalEdgeStyleLc,
						DotProperties.EDGE_STYLE_VALUES)) {
			// if an explicit global style is set, use it
			graphConnection.attr(DotProperties.EDGE_STYLE, globalEdgeStyleLc);
		}
		graph.edges(graphConnection.buildEdge());
	}

	private boolean supported(String value, Set<String> vals) {
		if (value == null) {
			return false;
		}
		return vals.contains(value);
	}

	@Override
	public Object caseEdgeRhsNode(EdgeRhsNode object) {
		// Set the flag for the node_id case handled above
		createConnection = true;
		return super.caseEdgeRhsNode(object);
	}

	@Override
	public Object caseSubgraph(Subgraph object) {
		return super.caseSubgraph(object);
	}

	// private implementation of the cases above

	private void createGraph(DotGraph object) {
		graph.attr(DotProperties.GRAPH_LAYOUT,
				DotProperties.GRAPH_LAYOUT_DEFAULT);
		GraphType graphType = object.getType();
		graph.attr(DotProperties.GRAPH_TYPE,
				graphType == GraphType.DIGRAPH
						? DotProperties.GRAPH_TYPE_DIRECTED
						: DotProperties.GRAPH_TYPE_UNDIRECTED);
	}

	private void createAttributes(final AttrStmt attrStmt) {
		// TODO: Verify that the global values are retrieved from edge/node
		// attributes. Maybe they are retrieved from graph attributes, and it
		// should really be GRAPH_EDGE_STYLE.
		AttributeType type = attrStmt.getType();
		switch (type) {
		case EDGE: {
			globalEdgeStyle = getAttributeValue(attrStmt,
					DotProperties.EDGE_STYLE);
			globalEdgeLabel = getAttributeValue(attrStmt,
					DotProperties.EDGE_LABEL);
			break;
		}
		case NODE: {
			globalNodeLabel = getAttributeValue(attrStmt,
					DotProperties.NODE_LABEL);
			break;
		}
		case GRAPH: {
			for (AttrList al : attrStmt.getAttrLists()) {
				for (Attribute a : al.getAttributes()) {
					graph.attr(a.getName(), a.getValue());
				}
			}
			String graphLayout = getAttributeValue(attrStmt,
					DotProperties.GRAPH_LAYOUT);
			if (graphLayout != null) {
				String graphLayoutLc = new String(graphLayout).toLowerCase();
				if (!supported(graphLayoutLc,
						DotProperties.GRAPH_LAYOUT_VALUES)) {
					throw new IllegalArgumentException(
							"Unknown layout algorithm <" + graphLayoutLc
									+ ">.");
				}
				graph.attr(DotProperties.GRAPH_LAYOUT, graphLayoutLc);
			}
			break;
		}
		}
	}

	private void createNode(final NodeStmt nodeStatement) {
		String nodeId = escaped(nodeStatement.getNode().getName());

		Node node;
		if (nodes.containsKey(nodeId)) {
			node = nodes.get(nodeId);
		} else {
			node = new Node.Builder().attr(DotProperties.NODE_ID, nodeId)
					.buildNode();
		}

		String label = getAttributeValue(nodeStatement,
				DotProperties.NODE_LABEL);
		if (label != null) {
			DotProperties.setLabel(node, label);
		} else if (globalNodeLabel != null) {
			DotProperties.setLabel(node, globalNodeLabel);
		}

		String pos = getAttributeValue(nodeStatement, DotProperties.NODE_POS);
		if (pos != null) {
			DotProperties.setPos(node, pos);
		}

		if (!nodes.containsKey(nodeId)) {
			nodes.put(nodeId, node);
			graph = graph.nodes(node);
		}
	}

	private Node node(String id) {
		if (!nodes.containsKey(id)) { // undeclared node, as in "graph{1->2}"
			Node node = new Node.Builder()
					.attr(DotProperties.NODE_LABEL,
							globalNodeLabel != null ? globalNodeLabel : id)
					.attr(DotProperties.NODE_ID, id).buildNode();
			nodes.put(id, node);
			graph = graph.nodes(node);
		}
		return nodes.get(id);
	}

	/**
	 * @param eStatementObject
	 *            The statement object, e.g. the object corresponding to
	 *            "node[label="hi"]"
	 * @param attributeName
	 *            The name of the attribute to get the value for, e.g. "label"
	 * @return The value of the given attribute, e.g. "hi"
	 */
	private String getAttributeValue(final Stmt eStatementObject,
			final String attributeName) {
		Iterator<EObject> nodeContents = eStatementObject.eContents()
				.iterator();
		while (nodeContents.hasNext()) {
			EObject nodeContentElement = nodeContents.next();
			if (nodeContentElement instanceof AttrList) {
				Iterator<EObject> attributeContents = nodeContentElement
						.eContents().iterator();
				while (attributeContents.hasNext()) {
					EObject next = attributeContents.next();
					if (next instanceof Attribute) {
						Attribute attributeElement = (Attribute) next;
						if (attributeElement.getName().equals(attributeName)) {
							return escaped(attributeElement.getValue());
						}
					}
				}
			}
		}
		return null;
	}

	private String escaped(String id) {
		return id
				/* In DOT, an ID can be quoted... */
				.replaceAll("^\"|\"$", "") //$NON-NLS-1$//$NON-NLS-2$
				/*
				 * ...and may contain escaped quotes, see footnote on
				 * http://www.graphviz.org/doc/info/lang.html
				 */
				.replaceAll("\\\\\"", "\""); //$NON-NLS-1$//$NON-NLS-2$
	}
}