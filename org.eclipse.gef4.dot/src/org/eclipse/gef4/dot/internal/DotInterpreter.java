/*******************************************************************************
 * Copyright (c) 2009, 2016 Fabian Steeg and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg                - initial API and implementation (bug #277380)                     
 *     Alexander Nyßen (itemis AG) - several refactorings and additions (bugs #487081, #489793)
 *     Tamas Miklossy  (itemis AG) - support for arrowType edge decorations (bug #477980)
 *                                   
 *******************************************************************************/

package org.eclipse.gef4.dot.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.eclipse.gef4.dot.internal.parser.dot.util.DotSwitch;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Graph.Builder;
import org.eclipse.gef4.graph.Node;

/**
 * Create a {@link Graph} instance from a DOT string by interpreting the AST of
 * the parsed DOT.
 * 
 * @author Fabian Steeg (fsteeg)
 * @author Alexander Nyßen (anyssen)
 */
public final class DotInterpreter extends DotSwitch<Object> {

	private Builder graphBuilder;
	private Map<String, Node> nodesByName = new HashMap<>();

	private Map<String, String> globalGraphAttributes = new HashMap<>();
	private Map<String, String> globalNodeAttributes = new HashMap<>();
	private Map<String, String> globalEdgeAttributes = new HashMap<>();

	private boolean createEdge;
	private String currentArrowHead;
	private String currentArrowTail;
	private String currentArrowSize;
	private String currentEdgeDirection;
	private String currentEdgeStyle;
	private String currentEdgeLabel;
	private String currentEdgeSourceNodeName;
	private String currentEdgePos;
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
			// clear global attributes, which only hold for each respective
			// graph
			globalGraphAttributes.clear();
			globalNodeAttributes.clear();
			globalEdgeAttributes.clear();

			// create a new graph builder and clear the nodes map
			graphBuilder = new Graph.Builder();
			nodesByName.clear();

			// process all contents (nodes, edges, attributes)
			TreeIterator<Object> contents = EcoreUtil
					.getAllProperContents(dotGraph, false);
			while (contents.hasNext()) {
				doSwitch((EObject) contents.next());
			}

			// process the graph last, so we can initialize attributes of the
			// created graph object rather than using the builder; we can thus
			// ensure attribute values get properly validated.
			Graph g = (Graph) doSwitch(dotGraph);
			if (g != null) {
				graphs.add(g);
			}
		}
		return graphs;
	}

	@Override
	public Object caseDotGraph(DotGraph dotGraph) {
		// name (meta-attribute)
		String name = escaped(dotGraph.getName());
		if (name != null) {
			graphBuilder.attr(DotAttributes._NAME__GNE, name);
		}

		// type (meta-attribute)
		GraphType graphType = dotGraph.getType();
		graphBuilder.attr(DotAttributes._TYPE__G,
				GraphType.GRAPH.equals(graphType)
						? DotAttributes._TYPE__G__GRAPH
						: DotAttributes._TYPE__G__DIGRAPH);
		Graph graph = graphBuilder.build();

		// layout
		String layout = getAttributeValue(dotGraph, DotAttributes.LAYOUT__G);
		if (layout != null) {
			DotAttributes.setLayout(graph, layout);
		} else if (globalGraphAttributes.containsKey(DotAttributes.LAYOUT__G)) {
			DotAttributes.setLayout(graph,
					globalGraphAttributes.get(DotAttributes.LAYOUT__G));
		}
		// rankdir
		String rankdir = getAttributeValue(dotGraph, DotAttributes.RANKDIR__G);
		if (rankdir != null) {
			DotAttributes.setRankdir(graph, rankdir);
		} else if (globalGraphAttributes
				.containsKey(DotAttributes.RANKDIR__G)) {
			DotAttributes.setRankdir(graph,
					globalGraphAttributes.get(DotAttributes.RANKDIR__G));
		}
		return graph;
	}

	@Override
	public Object caseAttrStmt(AttrStmt attrStmt) {
		AttributeType type = attrStmt.getType();
		switch (type) {
		case EDGE: {
			// global edge attributes
			for (AttrList al : attrStmt.getAttrLists()) {
				for (Attribute a : al.getAttributes()) {
					globalEdgeAttributes.put(a.getName(),
							escaped(a.getValue()));
				}
			}
			break;
		}
		case NODE: {
			// global node attributes
			for (AttrList al : attrStmt.getAttrLists()) {
				for (Attribute a : al.getAttributes()) {
					globalNodeAttributes.put(a.getName(),
							escaped(a.getValue()));
				}
			}
			break;
		}
		case GRAPH: {
			// global graph attributes
			for (AttrList al : attrStmt.getAttrLists()) {
				for (Attribute a : al.getAttributes()) {
					globalGraphAttributes.put(a.getName(),
							escaped(a.getValue()));
				}
			}
			break;
		}
		}
		return super.caseAttrStmt(attrStmt);
	}

	@Override
	public Object caseNodeStmt(NodeStmt nodeStmt) {
		// name (from grammar definition, not attribute)
		Node node = node(escaped(nodeStmt.getNode().getName()));

		// id
		String id = getAttributeValue(nodeStmt, DotAttributes.ID__GNE);
		if (id != null) {
			DotAttributes.setId(node, id);
		}

		// label
		String label = getAttributeValue(nodeStmt, DotAttributes.LABEL__GNE);
		if (label != null) {
			DotAttributes.setLabel(node, label);
		}

		// xlabel
		String xLabel = getAttributeValue(nodeStmt, DotAttributes.XLABEL__NE);
		if (xLabel != null) {
			DotAttributes.setXLabel(node, xLabel);
		}

		// pos
		String pos = getAttributeValue(nodeStmt, DotAttributes.POS__NE);
		if (pos != null) {
			DotAttributes.setPos(node, pos);
		}

		// xlp
		String xlp = getAttributeValue(nodeStmt, DotAttributes.XLP__NE);
		if (xlp != null) {
			DotAttributes.setXlp(node, xlp);
		}

		// width
		String width = getAttributeValue(nodeStmt, DotAttributes.WIDTH__N);
		if (width != null) {
			DotAttributes.setWidth(node, width);
		}

		// height
		String height = getAttributeValue(nodeStmt, DotAttributes.HEIGHT__N);
		if (height != null) {
			DotAttributes.setHeight(node, height);
		}
		return super.caseNodeStmt(nodeStmt);
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
		currentArrowTail = getAttributeValue(object,
				DotAttributes.ARROWTAIL__E);
		currentArrowSize = getAttributeValue(object,
				DotAttributes.ARROWSIZE__E);
		currentEdgeDirection = getAttributeValue(object, DotAttributes.DIR__E);
		return super.caseEdgeStmtNode(object);
	}

	@Override
	public Object caseNodeId(NodeId object) {
		if (!createEdge) {
			currentEdgeSourceNodeName = escaped(object.getName());
		} else {
			String targetNodeName = escaped(object.getName());
			if (currentEdgeSourceNodeName != null && targetNodeName != null) {
				edge(currentEdgeSourceNodeName, currentEdgeOp, targetNodeName);
				// current target node may be source for next EdgeRHS
				currentEdgeSourceNodeName = targetNodeName;
			}
			createEdge = false;
		}
		return super.caseNodeId(object);
	}

	private void edge(String sourceNodeName, String edgeOp,
			String targetNodeName) {
		Edge edge = new Edge.Builder(node(sourceNodeName), node(targetNodeName))
				.attr(DotAttributes._NAME__GNE,
						sourceNodeName + edgeOp + targetNodeName)
				.buildEdge();

		// id
		if (currentEdgeId != null) {
			DotAttributes.setId(edge, currentEdgeId);
		}

		// label
		if (currentEdgeLabel != null) {
			DotAttributes.setLabel(edge, currentEdgeLabel);
		} else if (globalEdgeAttributes.containsKey(DotAttributes.LABEL__GNE)) {
			DotAttributes.setLabel(edge,
					globalEdgeAttributes.get(DotAttributes.LABEL__GNE));
		}

		// external label (xlabel)
		if (currentEdgeXLabel != null) {
			DotAttributes.setXLabel(edge, currentEdgeXLabel);
		} else if (globalEdgeAttributes.containsKey(DotAttributes.XLABEL__NE)) {
			DotAttributes.setXLabel(edge,
					globalEdgeAttributes.get(DotAttributes.XLABEL__NE));
		}

		// head label (headllabel)
		if (currentEdgeHeadLabel != null) {
			DotAttributes.setHeadLabel(edge, currentEdgeHeadLabel);
		} else if (globalEdgeAttributes
				.containsKey(DotAttributes.HEADLABEL__E)) {
			DotAttributes.setHeadLabel(edge,
					globalEdgeAttributes.get(DotAttributes.HEADLABEL__E));
		}

		// tail label (taillabel)
		if (currentEdgeTailLabel != null) {
			DotAttributes.setTailLabel(edge, currentEdgeTailLabel);
		} else if (globalEdgeAttributes
				.containsKey(DotAttributes.TAILLABEL__E)) {
			DotAttributes.setTailLabel(edge,
					globalEdgeAttributes.get(DotAttributes.TAILLABEL__E));
		}

		// style
		if (currentEdgeStyle != null) {
			DotAttributes.setStyle(edge, currentEdgeStyle);
		} else if (globalEdgeAttributes.containsKey(DotAttributes.STYLE__E)) {
			DotAttributes.setStyle(edge,
					globalEdgeAttributes.get(DotAttributes.STYLE__E));
		}

		// arrow head
		if (currentArrowHead != null) {
			DotAttributes.setArrowHead(edge, currentArrowHead);
		} else if (globalEdgeAttributes
				.containsKey(DotAttributes.ARROWHEAD__E)) {
			DotAttributes.setArrowHead(edge,
					globalEdgeAttributes.get(DotAttributes.ARROWHEAD__E));
		}

		// arrow tail
		if (currentArrowTail != null) {
			DotAttributes.setArrowTail(edge, currentArrowTail);
		} else if (globalEdgeAttributes
				.containsKey(DotAttributes.ARROWTAIL__E)) {
			DotAttributes.setArrowTail(edge,
					globalEdgeAttributes.get(DotAttributes.ARROWTAIL__E));
		}

		// arrow size
		if (currentArrowSize != null) {
			DotAttributes.setArrowSize(edge, currentArrowSize);
		} else if (globalEdgeAttributes
				.containsKey(DotAttributes.ARROWSIZE__E)) {
			DotAttributes.setArrowSize(edge,
					globalEdgeAttributes.get(DotAttributes.ARROWSIZE__E));
		}

		// direction
		if (currentEdgeDirection != null) {
			DotAttributes.setDir(edge, currentEdgeDirection);
		} else if (globalEdgeAttributes.containsKey(DotAttributes.DIR__E)) {
			DotAttributes.setDir(edge,
					globalEdgeAttributes.get(DotAttributes.DIR__E));
		}

		// position (pos)
		if (currentEdgePos != null) {
			DotAttributes.setPos(edge, currentEdgePos);
		}
		// label position (lp)
		if (currentEdgeLp != null) {
			DotAttributes.setLp(edge, currentEdgeLp);
		}

		// external label position (xlp)
		if (currentEdgeXlp != null) {
			DotAttributes.setXlp(edge, currentEdgeXlp);
		}

		// head label position (head_lp)
		if (currentEdgeHeadLp != null) {
			DotAttributes.setHeadLp(edge, currentEdgeHeadLp);
		}

		// tail label position (tail_lp)
		if (currentEdgeTailLp != null) {
			DotAttributes.setTailLp(edge, currentEdgeTailLp);
		}

		graphBuilder.edges(edge);
	}

	@Override
	public Object caseEdgeRhsNode(EdgeRhsNode object) {
		// Set the flag for the node_id case handled above
		createEdge = true;
		currentEdgeOp = object.getOp().getLiteral();
		return super.caseEdgeRhsNode(object);
	}

	private Node node(String nodeName) {
		if (!nodesByName.containsKey(nodeName)) {
			Node node = new Node.Builder()
					.attr(DotAttributes._NAME__GNE, nodeName).buildNode();
			graphBuilder.nodes(node);
			nodesByName.put(nodeName, node);

			// evaluate global attributes
			if (globalNodeAttributes.containsKey(DotAttributes.LABEL__GNE)) {
				DotAttributes.setLabel(node,
						globalNodeAttributes.get(DotAttributes.LABEL__GNE));
			}
			if (globalNodeAttributes.containsKey(DotAttributes.XLABEL__NE)) {
				DotAttributes.setXLabel(node,
						globalNodeAttributes.get(DotAttributes.XLABEL__NE));
			}
			if (globalNodeAttributes.containsKey(DotAttributes.WIDTH__N)) {
				DotAttributes.setWidth(node,
						globalNodeAttributes.get(DotAttributes.WIDTH__N));
			}
			if (globalNodeAttributes.containsKey(DotAttributes.HEIGHT__N)) {
				DotAttributes.setHeight(node,
						globalNodeAttributes.get(DotAttributes.HEIGHT__N));
			}
		}
		return nodesByName.get(nodeName);
	}

	private String getAttributeValue(final DotGraph graph, final String name) {
		for (Stmt stmt : graph.getStmts()) {
			String value = null;
			if (stmt instanceof AttrStmt) {
				value = getAttributeValue((AttrStmt) stmt, name);
			} else if (stmt instanceof Attribute) {
				value = getAttributeValue((Attribute) stmt, name);
			}
			if (value != null) {
				return value;
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