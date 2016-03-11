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
	private String currentEdgeStyle;
	private String currentEdgeLabel;
	private String currentEdgeSourceNodeId;
	private String currentEdgePos;
	private boolean createEdge;
	private String currentEdgeXLabel;
	private String currentEdgeXlp;
	private String currentEdgeLp;
	private String currentEdgeTailLabel;
	private String currentEdgeHeadLabel;
	private String currentEdgeHeadLp;
	private String currentEdgeTailLp;

	/**
	 * @param dotAst
	 *            The DOT abstract synstx tree (AST) to interpret
	 * @return A graph instance for the given DOT AST
	 */
	public Graph interpret(DotAst dotAst) {
		return interpret(dotAst,
				new Graph.Builder().attr(DotAttributes.GRAPH_LAYOUT,
						DotAttributes.GRAPH_LAYOUT_DEFAULT));
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
		if (DotAttributes.GRAPH_RANKDIR.equals(object.getName())) {
			String value = object.getValue();
			if (value == null)
				value = "";
			value = value.toLowerCase();
			boolean lr = DotAttributes.GRAPH_RANKDIR_LR.equals(value);
			boolean td = DotAttributes.GRAPH_RANKDIR_TD.equals(value);
			graph.attr(DotAttributes.GRAPH_LAYOUT,
					DotAttributes.GRAPH_LAYOUT_DOT);
			graph.attr(DotAttributes.GRAPH_RANKDIR,
					lr ? DotAttributes.GRAPH_RANKDIR_LR
							: td ? DotAttributes.GRAPH_RANKDIR_TD
									: DotAttributes.GRAPH_RANKDIR_DEFAULT);
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
		currentEdgeLabel = getAttributeValue(object, DotAttributes.EDGE_LABEL);
		currentEdgeLp = getAttributeValue(object, DotAttributes.EDGE_LP);
		currentEdgeXLabel = getAttributeValue(object,
				DotAttributes.EDGE_XLABEL);
		currentEdgeXlp = getAttributeValue(object, DotAttributes.EDGE_XLP);
		currentEdgeStyle = getAttributeValue(object, DotAttributes.EDGE_STYLE);
		currentEdgePos = getAttributeValue(object, DotAttributes.EDGE_POS);
		currentEdgeHeadLabel = getAttributeValue(object,
				DotAttributes.EDGE_HEADLABEL);
		currentEdgeHeadLp = getAttributeValue(object,
				DotAttributes.EDGE_HEAD_LP);
		currentEdgeTailLabel = getAttributeValue(object,
				DotAttributes.EDGE_TAILLABEL);
		currentEdgeTailLp = getAttributeValue(object,
				DotAttributes.EDGE_TAIL_LP);
		return super.caseEdgeStmtNode(object);
	}

	@Override
	public Object caseNodeId(NodeId object) {
		if (!createEdge) {
			currentEdgeSourceNodeId = escaped(object.getName());
		} else {
			String targetNodeId = escaped(object.getName());
			if (currentEdgeSourceNodeId != null && targetNodeId != null) {
				createEdge(targetNodeId);
				// current target node may be source for next EdgeRHS
				currentEdgeSourceNodeId = targetNodeId;
			}
			createEdge = false;
		}
		return super.caseNodeId(object);
	}

	private void createEdge(String targetNodeId) {
		Edge.Builder edgeBuilder = new Edge.Builder(
				node(currentEdgeSourceNodeId), node(targetNodeId));
		// label
		if (currentEdgeLabel != null) {
			edgeBuilder.attr(DotAttributes.EDGE_LABEL, currentEdgeLabel);
		} else if (globalEdgeLabel != null) {
			edgeBuilder.attr(DotAttributes.EDGE_LABEL, globalEdgeLabel);
		}

		// external label (xlabel)
		if (currentEdgeXLabel != null) {
			edgeBuilder.attr(DotAttributes.EDGE_XLABEL, currentEdgeXLabel);
		}

		// head label (headllabel)
		if (currentEdgeHeadLabel != null) {
			edgeBuilder.attr(DotAttributes.EDGE_HEADLABEL,
					currentEdgeHeadLabel);
		}

		// tail label (taillabel)
		if (currentEdgeTailLabel != null) {
			edgeBuilder.attr(DotAttributes.EDGE_TAILLABEL,
					currentEdgeTailLabel);
		}

		// style
		String currentEdgeStyleLc = new String(
				currentEdgeStyle == null ? "" : currentEdgeStyle).toLowerCase();
		String globalEdgeStyleLc = new String(
				globalEdgeStyle == null ? "" : globalEdgeStyle).toLowerCase();
		if (!DotAttributes.EDGE_STYLE_VOID.equals(currentEdgeStyleLc)
				&& supported(currentEdgeStyleLc,
						DotAttributes.EDGE_STYLE_VALUES)) {
			// if an explicit local style is set, use it
			edgeBuilder.attr(DotAttributes.EDGE_STYLE, currentEdgeStyleLc);
		} else if (!DotAttributes.EDGE_STYLE_VOID.equals(globalEdgeStyleLc)
				&& supported(globalEdgeStyleLc,
						DotAttributes.EDGE_STYLE_VALUES)) {
			// if an explicit global style is set, use it
			edgeBuilder.attr(DotAttributes.EDGE_STYLE, globalEdgeStyleLc);
		}

		// position (pos)
		if (currentEdgePos != null) {
			edgeBuilder.attr(DotAttributes.EDGE_POS, currentEdgePos);
		}
		// label position (lp)
		if (currentEdgeLp != null) {
			edgeBuilder.attr(DotAttributes.EDGE_LP, currentEdgeLp);
		}

		// external label position (xlp)
		if (currentEdgeXlp != null) {
			edgeBuilder.attr(DotAttributes.EDGE_XLP, currentEdgeXlp);
		}

		// head label position (head_lp)
		if (currentEdgeHeadLp != null) {
			edgeBuilder.attr(DotAttributes.EDGE_HEAD_LP, currentEdgeHeadLp);
		}

		// tail label position (tail_lp)
		if (currentEdgeTailLp != null) {
			edgeBuilder.attr(DotAttributes.EDGE_TAIL_LP, currentEdgeTailLp);
		}

		graph.edges(edgeBuilder.buildEdge());
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
		createEdge = true;
		return super.caseEdgeRhsNode(object);
	}

	@Override
	public Object caseSubgraph(Subgraph object) {
		return super.caseSubgraph(object);
	}

	// private implementation of the cases above

	private void createGraph(DotGraph object) {
		graph.attr(DotAttributes.GRAPH_LAYOUT,
				DotAttributes.GRAPH_LAYOUT_DEFAULT);
		GraphType graphType = object.getType();
		graph.attr(DotAttributes.GRAPH_TYPE,
				graphType == GraphType.DIGRAPH
						? DotAttributes.GRAPH_TYPE_DIRECTED
						: DotAttributes.GRAPH_TYPE_UNDIRECTED);
	}

	private void createAttributes(final AttrStmt attrStmt) {
		// TODO: Verify that the global values are retrieved from edge/node
		// attributes. Maybe they are retrieved from graph attributes, and it
		// should really be GRAPH_EDGE_STYLE.
		AttributeType type = attrStmt.getType();
		switch (type) {
		case EDGE: {
			globalEdgeStyle = getAttributeValue(attrStmt,
					DotAttributes.EDGE_STYLE);
			globalEdgeLabel = getAttributeValue(attrStmt,
					DotAttributes.EDGE_LABEL);
			break;
		}
		case NODE: {
			globalNodeLabel = getAttributeValue(attrStmt,
					DotAttributes.NODE_LABEL);
			break;
		}
		case GRAPH: {
			for (AttrList al : attrStmt.getAttrLists()) {
				for (Attribute a : al.getAttributes()) {
					graph.attr(a.getName(), a.getValue());
				}
			}
			String graphLayout = getAttributeValue(attrStmt,
					DotAttributes.GRAPH_LAYOUT);
			if (graphLayout != null) {
				String graphLayoutLc = new String(graphLayout).toLowerCase();
				if (!supported(graphLayoutLc,
						DotAttributes.GRAPH_LAYOUT_VALUES)) {
					throw new IllegalArgumentException(
							"Unknown layout algorithm <" + graphLayoutLc
									+ ">.");
				}
				graph.attr(DotAttributes.GRAPH_LAYOUT, graphLayoutLc);
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
			node = new Node.Builder().attr(DotAttributes.NODE_ID, nodeId)
					.buildNode();
		}

		String label = getAttributeValue(nodeStatement,
				DotAttributes.NODE_LABEL);
		if (label != null) {
			DotAttributes.setLabel(node, label);
		} else if (globalNodeLabel != null) {
			DotAttributes.setLabel(node, globalNodeLabel);
		}

		String xLabel = getAttributeValue(nodeStatement,
				DotAttributes.NODE_XLABEL);
		if (xLabel != null) {
			DotAttributes.setXLabel(node, xLabel);
		}

		String pos = getAttributeValue(nodeStatement, DotAttributes.NODE_POS);
		if (pos != null) {
			DotAttributes.setPos(node, pos);
		}

		String xlp = getAttributeValue(nodeStatement, DotAttributes.NODE_XLP);
		if (xlp != null) {
			DotAttributes.setXlp(node, xlp);
		}

		String width = getAttributeValue(nodeStatement,
				DotAttributes.NODE_WIDTH);
		if (width != null) {
			DotAttributes.setWidth(node, width);
		}

		String height = getAttributeValue(nodeStatement,
				DotAttributes.NODE_HEIGHT);
		if (height != null) {
			DotAttributes.setHeight(node, height);
		}

		if (!nodes.containsKey(nodeId)) {
			nodes.put(nodeId, node);
			graph = graph.nodes(node);
		}
	}

	private Node node(String id) {
		if (!nodes.containsKey(id)) { // undeclared node, as in "graph{1->2}"
			Node node = new Node.Builder()
					.attr(DotAttributes.NODE_LABEL,
							globalNodeLabel != null ? globalNodeLabel : id)
					.attr(DotAttributes.NODE_ID, id).buildNode();
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