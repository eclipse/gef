/*******************************************************************************
 * Copyright (c) 2009, 2015 Fabian Steeg and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg                - initial API and implementation 
 *                                   (bug #277380)
 *     Alexander Nyßen (itemis AG) - several refactorings and additions 
 *                                   (bug #487081, 489793)
 *     Tamas Miklossy (itemis AG)  - Add support for arrowType edge decorations (bug #477980)                              
 *******************************************************************************/

package org.eclipse.gef4.dot.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef4.dot.internal.parser.dot.AttrList;
import org.eclipse.gef4.dot.internal.parser.dot.AttrStmt;
import org.eclipse.gef4.dot.internal.parser.dot.Attribute;
import org.eclipse.gef4.dot.internal.parser.dot.AttributeType;
import org.eclipse.gef4.dot.internal.parser.dot.DotAst;
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
 * @author Alexander Nyßen (anyssen)
 */
public final class DotInterpreter extends DotSwitch<Object> {

	private Map<String, Node> nodes;
	private Graph.Builder graph;
	private String globalEdgeStyle;
	private String globalEdgeLabel;
	private String globalNodeLabel;
	private String currentArrowHead;
	private String currentEdgeStyle;
	private String currentEdgeLabel;
	private String currentEdgeSourceNodeName;
	private String currentEdgePos;
	private boolean createEdge;
	private String currentEdgeXLabel;
	private String currentEdgeXlp;
	private String currentEdgeLp;
	private String currentEdgeTailLabel;
	private String currentEdgeHeadLabel;
	private String currentEdgeHeadLp;
	private String currentEdgeTailLp;
	private String currentEdgeId;
	private String currentEdgeOp;

	/**
	 * @param dotAst
	 *            The DOT abstract syntax tree (AST) to interpret
	 * @return A graph instance for the given DOT AST
	 */
	public List<Graph> interpret(DotAst dotAst) {
		List<Graph> graphs = new ArrayList<>();
		for (DotGraph dotGraph : dotAst.getGraphs()) {
			Graph g = interpret(dotGraph, new Graph.Builder());
			if (g != null) {
				graphs.add(g);
			}
		}
		return graphs;
	}

	private Graph interpret(DotGraph dotGraph, Graph.Builder graph) {
		this.graph = graph;
		nodes = new HashMap<>();
		doSwitch(dotGraph);
		TreeIterator<Object> contents = EcoreUtil.getAllProperContents(dotGraph,
				false);
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
		if (DotAttributes.RANKDIR__G.equals(object.getName())) {
			String value = object.getValue();
			if (value == null)
				value = "";
			value = value.toLowerCase();
			boolean lr = DotAttributes.RANKDIR__G__LR.equals(value);
			boolean td = DotAttributes.RANKDIR__G__TD.equals(value);
			graph.attr(DotAttributes.LAYOUT_G, DotAttributes.LAYOUT__G__DOT);
			graph.attr(DotAttributes.RANKDIR__G,
					lr ? DotAttributes.RANKDIR__G__LR
							: td ? DotAttributes.RANKDIR__G__TD
									: DotAttributes.RANKDIR__G__DEFAULT);
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
		currentEdgeId = getAttributeValue(object, DotAttributes.ID__GNE);
		currentEdgeLabel = getAttributeValue(object, DotAttributes.LABEL__GNE);
		currentEdgeLp = getAttributeValue(object, DotAttributes.LP__E);
		currentEdgeXLabel = getAttributeValue(object, DotAttributes.XLABEL__NE);
		currentEdgeXlp = getAttributeValue(object, DotAttributes.XLP__NE);
		currentEdgeStyle = getAttributeValue(object, DotAttributes.STYLE__E);
		currentEdgePos = getAttributeValue(object, DotAttributes.POS__NE);
		currentEdgeHeadLabel = getAttributeValue(object,
				DotAttributes.HEADLABEL__E);
		currentEdgeHeadLp = getAttributeValue(object, DotAttributes.HEAD_LP__E);
		currentEdgeTailLabel = getAttributeValue(object,
				DotAttributes.TAILLABEL__E);
		currentEdgeTailLp = getAttributeValue(object, DotAttributes.TAIL_LP__E);
		currentArrowHead = getAttributeValue(object,
				DotAttributes.ARROWHEAD__E);
		return super.caseEdgeStmtNode(object);
	}

	@Override
	public Object caseNodeId(NodeId object) {
		if (!createEdge) {
			currentEdgeSourceNodeName = escaped(object.getName());
		} else {
			String targetNodeId = escaped(object.getName());
			if (currentEdgeSourceNodeName != null && targetNodeId != null) {
				createEdge(targetNodeId);
				// current target node may be source for next EdgeRHS
				currentEdgeSourceNodeName = targetNodeId;
			}
			createEdge = false;
		}
		return super.caseNodeId(object);
	}

	private void createEdge(String targetNodeName) {
		Edge.Builder edgeBuilder = new Edge.Builder(
				node(currentEdgeSourceNodeName), node(targetNodeName));

		// name (always set)
		String name = currentEdgeSourceNodeName + currentEdgeOp
				+ targetNodeName;
		edgeBuilder.attr(DotAttributes._NAME__GNE, name);

		// id
		if (currentEdgeId != null) {
			edgeBuilder.attr(DotAttributes.ID__GNE, currentEdgeId);
		}

		// label
		if (currentEdgeLabel != null) {
			edgeBuilder.attr(DotAttributes.LABEL__GNE, currentEdgeLabel);
		} else if (globalEdgeLabel != null) {
			edgeBuilder.attr(DotAttributes.LABEL__GNE, globalEdgeLabel);
		}

		// external label (xlabel)
		if (currentEdgeXLabel != null) {
			edgeBuilder.attr(DotAttributes.XLABEL__NE, currentEdgeXLabel);
		}

		// head label (headllabel)
		if (currentEdgeHeadLabel != null) {
			edgeBuilder.attr(DotAttributes.HEADLABEL__E, currentEdgeHeadLabel);
		}

		// tail label (taillabel)
		if (currentEdgeTailLabel != null) {
			edgeBuilder.attr(DotAttributes.TAILLABEL__E, currentEdgeTailLabel);
		}

		// style
		String currentEdgeStyleLc = new String(
				currentEdgeStyle == null ? "" : currentEdgeStyle).toLowerCase();
		String globalEdgeStyleLc = new String(
				globalEdgeStyle == null ? "" : globalEdgeStyle).toLowerCase();
		if (!DotAttributes.STYLE__E__VOID.equals(currentEdgeStyleLc)
				&& supported(currentEdgeStyleLc,
						DotAttributes.STYLE__E__VALUES)) {
			// if an explicit local style is set, use it
			edgeBuilder.attr(DotAttributes.STYLE__E, currentEdgeStyleLc);
		} else if (!DotAttributes.STYLE__E__VOID.equals(globalEdgeStyleLc)
				&& supported(globalEdgeStyleLc,
						DotAttributes.STYLE__E__VALUES)) {
			// if an explicit global style is set, use it
			edgeBuilder.attr(DotAttributes.STYLE__E, globalEdgeStyleLc);
		}

		// position (pos)
		if (currentEdgePos != null) {
			edgeBuilder.attr(DotAttributes.POS__NE, currentEdgePos);
		}
		// label position (lp)
		if (currentEdgeLp != null) {
			edgeBuilder.attr(DotAttributes.LP__E, currentEdgeLp);
		}

		// external label position (xlp)
		if (currentEdgeXlp != null) {
			edgeBuilder.attr(DotAttributes.XLP__NE, currentEdgeXlp);
		}

		// head label position (head_lp)
		if (currentEdgeHeadLp != null) {
			edgeBuilder.attr(DotAttributes.HEAD_LP__E, currentEdgeHeadLp);
		}

		// tail label position (tail_lp)
		if (currentEdgeTailLp != null) {
			edgeBuilder.attr(DotAttributes.TAIL_LP__E, currentEdgeTailLp);
		}

		// arrow head
		if (currentArrowHead != null) {
			edgeBuilder.attr(DotAttributes.ARROWHEAD__E, currentArrowHead);
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
		currentEdgeOp = object.getOp().getLiteral();
		return super.caseEdgeRhsNode(object);
	}

	@Override
	public Object caseSubgraph(Subgraph object) {
		return super.caseSubgraph(object);
	}

	private void createGraph(DotGraph dotGraph) {
		// name (meta-attribute)
		String name = escaped(dotGraph.getName());
		if (name != null) {
			graph.attr(DotAttributes._NAME__GNE, name);
		}

		// type (meta-attribute)
		GraphType graphType = dotGraph.getType();
		graph.attr(DotAttributes._TYPE__G,
				GraphType.GRAPH.equals(graphType)
						? DotAttributes._TYPE__G__GRAPH
						: DotAttributes._TYPE__G__DIGRAPH);

		// layout
		String layout = getAttributeValue(dotGraph, DotAttributes.LAYOUT_G);
		if (layout != null) {
			graph.attr(DotAttributes.LAYOUT_G, layout);
		}
	}

	private void createAttributes(final AttrStmt attrStmt) {
		// TODO: Verify that the global values are retrieved from edge/node
		// attributes. Maybe they are retrieved from graph attributes, and it
		// should really be GRAPH_EDGE_STYLE.
		AttributeType type = attrStmt.getType();
		switch (type) {
		case EDGE: {
			globalEdgeStyle = getAttributeValue(attrStmt,
					DotAttributes.STYLE__E);
			globalEdgeLabel = getAttributeValue(attrStmt,
					DotAttributes.LABEL__GNE);
			break;
		}
		case NODE: {
			globalNodeLabel = getAttributeValue(attrStmt,
					DotAttributes.LABEL__GNE);
			break;
		}
		case GRAPH: {
			for (AttrList al : attrStmt.getAttrLists()) {
				for (Attribute a : al.getAttributes()) {
					graph.attr(a.getName(), a.getValue());
				}
			}
			String graphLayout = getAttributeValue(attrStmt,
					DotAttributes.LAYOUT_G);
			if (graphLayout != null) {
				String graphLayoutLc = new String(graphLayout).toLowerCase();
				if (!supported(graphLayoutLc,
						DotAttributes.LAYOUT__G__VALUES)) {
					throw new IllegalArgumentException(
							"Unknown layout algorithm <" + graphLayoutLc
									+ ">.");
				}
				graph.attr(DotAttributes.LAYOUT_G, graphLayoutLc);
			}
			break;
		}
		}
	}

	private void createNode(final NodeStmt nodeStatement) {
		// name (from grammar definition, not attribute)
		String nodeName = escaped(nodeStatement.getNode().getName());
		Node node;
		if (nodes.containsKey(nodeName)) {
			node = nodes.get(nodeName);
		} else {
			node = new Node.Builder().attr(DotAttributes._NAME__GNE, nodeName)
					.buildNode();
		}

		// id
		String id = getAttributeValue(nodeStatement, DotAttributes.ID__GNE);
		if (id != null) {
			DotAttributes.setId(node, id);
		}

		// label
		String label = getAttributeValue(nodeStatement,
				DotAttributes.LABEL__GNE);
		if (label != null) {
			DotAttributes.setLabel(node, label);
		} else if (globalNodeLabel != null) {
			DotAttributes.setLabel(node, globalNodeLabel);
		}

		// xlabel
		String xLabel = getAttributeValue(nodeStatement,
				DotAttributes.XLABEL__NE);
		if (xLabel != null) {
			DotAttributes.setXLabel(node, xLabel);
		}

		// pos
		String pos = getAttributeValue(nodeStatement, DotAttributes.POS__NE);
		if (pos != null) {
			DotAttributes.setPos(node, pos);
		}

		// xlp
		String xlp = getAttributeValue(nodeStatement, DotAttributes.XLP__NE);
		if (xlp != null) {
			DotAttributes.setXlp(node, xlp);
		}

		// width
		String width = getAttributeValue(nodeStatement, DotAttributes.WIDTH__N);
		if (width != null) {
			DotAttributes.setWidth(node, width);
		}

		// height
		String height = getAttributeValue(nodeStatement,
				DotAttributes.HEIGHT__N);
		if (height != null) {
			DotAttributes.setHeight(node, height);
		}

		// TODO: do we have to perform containment check here??
		if (!nodes.containsKey(nodeName)) {
			nodes.put(nodeName, node);
			graph = graph.nodes(node);
		}
	}

	private Node node(String id) {
		if (!nodes.containsKey(id)) { // undeclared node, as in "graph{1->2}"
			Node node = new Node.Builder()
					.attr(DotAttributes.LABEL__GNE,
							globalNodeLabel != null ? globalNodeLabel : id)
					.attr(DotAttributes._NAME__GNE, id).buildNode();
			nodes.put(id, node);
			graph = graph.nodes(node);
		}
		return nodes.get(id);
	}

	private String getAttributeValue(final DotGraph graph, final String name) {
		for (Stmt stmt : graph.getStmts()) {
			if (stmt instanceof AttrStmt) {
				return getAttributeValue((AttrStmt) stmt, name);
			} else if (stmt instanceof Attribute) {
				return getAttributeValue((Attribute) stmt, name);
			}
		}
		return null;
	}

	/**
	 * @param stmt
	 *            The {@link Stmt} object, e.g. the object corresponding to
	 *            "node[label="hi"]"
	 * @param name
	 *            The name of the attribute to get the value for, e.g. "label"
	 * @return The value of the given attribute, e.g. "hi"
	 */
	private String getAttributeValue(final NodeStmt stmt, final String name) {
		return getAttributeValue(stmt.getAttrLists(), name);
	}

	/**
	 * Returns the value of the first attribute with the give name or
	 * <code>null</code> if no attribute could be found.
	 * 
	 * @param attrLists
	 *            The {@link AttrList}s to search.
	 * @param name
	 *            The name of the attribute whose value is to be retrieved.
	 * @return The attribute value or <code>null</code> in case the attribute
	 *         could not be found.
	 */
	private String getAttributeValue(List<AttrList> attrLists,
			final String name) {
		for (AttrList attrList : attrLists) {
			String value = getAttributeValue(attrList, name);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	private String getAttributeValue(AttrStmt attrStmt, String name) {
		return getAttributeValue(attrStmt.getAttrLists(), name);
	}

	private String getAttributeValue(EdgeStmtNode edgeStmtNode, String name) {
		return getAttributeValue(edgeStmtNode.getAttrLists(), name);
	}

	private String getAttributeValue(AttrList attrList, final String name) {
		Iterator<EObject> attributeContents = attrList.eContents().iterator();
		while (attributeContents.hasNext()) {
			EObject next = attributeContents.next();
			if (next instanceof Attribute) {
				String value = getAttributeValue((Attribute) next, name);
				if (value != null) {
					return value;
				}
			}
		}
		return null;
	}

	private String getAttributeValue(Attribute attribute, final String name) {
		if (attribute.getName().equals(name)) {
			return escaped(attribute.getValue());
		}
		return null;
	}

	private String escaped(String id) {
		if (id == null) {
			return null;
		}
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