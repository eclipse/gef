/*******************************************************************************
 * Copyright (c) 2009, 2011 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/

package org.eclipse.gef4.zest.internal.dot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphContainer;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.core.widgets.IContainer;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.gef4.zest.internal.dot.DotMessages;
import org.eclipse.gef4.zest.internal.dot.DotAst.Layout;
import org.eclipse.gef4.zest.internal.dot.DotAst.Style;
import org.eclipse.gef4.zest.internal.dot.parser.dot.AList;
import org.eclipse.gef4.zest.internal.dot.parser.dot.AttrList;
import org.eclipse.gef4.zest.internal.dot.parser.dot.AttrStmt;
import org.eclipse.gef4.zest.internal.dot.parser.dot.Attribute;
import org.eclipse.gef4.zest.internal.dot.parser.dot.AttributeType;
import org.eclipse.gef4.zest.internal.dot.parser.dot.EdgeRhsNode;
import org.eclipse.gef4.zest.internal.dot.parser.dot.EdgeStmtNode;
import org.eclipse.gef4.zest.internal.dot.parser.dot.GraphType;
import org.eclipse.gef4.zest.internal.dot.parser.dot.MainGraph;
import org.eclipse.gef4.zest.internal.dot.parser.dot.NodeId;
import org.eclipse.gef4.zest.internal.dot.parser.dot.NodeStmt;
import org.eclipse.gef4.zest.internal.dot.parser.dot.Stmt;
import org.eclipse.gef4.zest.internal.dot.parser.dot.Subgraph;
import org.eclipse.gef4.zest.internal.dot.parser.dot.util.DotSwitch;
import org.eclipse.gef4.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Create a Zest graph instance from a DOT string by interpreting the AST of the
 * parsed DOT.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class GraphCreatorInterpreter extends DotSwitch<Object> {

	private Map<String, GraphNode> nodes;
	private Graph graph;
	private String globalEdgeStyle;
	private String globalEdgeLabel;
	private String globalNodeLabel;
	private String currentEdgeStyleValue;
	private String currentEdgeLabelValue;
	private String currentEdgeSourceNodeId;
	private GraphContainer currentSubgraph;
	private boolean gotSource;

	public Graph create(Composite parent, int style, DotAst dotAst) {
		return create(dotAst, new Graph(parent, style));
	}

	public Graph create(DotAst dotAst, Graph graph) {
		if (dotAst.errors().size() > 0) {
			throw new IllegalArgumentException(String.format(
					DotMessages.GraphCreatorInterpreter_0 + ": %s", dotAst //$NON-NLS-1$
							.errors().toString()));
		}
		this.graph = graph;
		nodes = new HashMap<String, GraphNode>();
		TreeIterator<Object> contents = EcoreUtil.getAllProperContents(
				dotAst.resource(), false);
		while (contents.hasNext()) {
			doSwitch((EObject) contents.next());
		}
		layoutSubgraph();
		return graph;
	}

	@Override
	public Object caseMainGraph(MainGraph object) {
		createGraph(object);
		return super.caseMainGraph(object);
	}

	@Override
	public Object caseAttribute(Attribute object) {
		/*
		 * Convenience for common 'rankdir=LR' attribute: use
		 * TreeLayoutAlgorithm.LEFT_RIGHT if nothing else is specified
		 */
		if (object.getName().equals("rankdir") //$NON-NLS-1$
				&& object.getValue().equals("LR")) { //$NON-NLS-1$
			TreeLayoutAlgorithm algorithm = new TreeLayoutAlgorithm(
					TreeLayoutAlgorithm.LEFT_RIGHT);
			currentParentGraph().setLayoutAlgorithm(algorithm, true);
		} else if (currentSubgraph != null && object.getName().equals("label")) { //$NON-NLS-1$
			currentSubgraph.setText(object.getValue());
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
		currentEdgeLabelValue = getAttributeValue(object, "label"); //$NON-NLS-1$
		currentEdgeStyleValue = getAttributeValue(object, "style"); //$NON-NLS-1$
		return super.caseEdgeStmtNode(object);
	}

	@Override
	public Object caseNodeId(NodeId object) {
		if (!gotSource) {
			gotSource = true;
		} else {
			String targetNodeId = escaped(object.getName());
			if (currentEdgeSourceNodeId != null && targetNodeId != null) {
				addConnectionTo(targetNodeId);
			}
			gotSource = false;
		}
		/* Current node is potential source in case next is caseEdgeRhsNode: */
		currentEdgeSourceNodeId = escaped(object.getName());
		return super.caseNodeId(object);
	}

	private void addConnectionTo(String targetNodeId) {
		GraphConnection graphConnection = new GraphConnection(graph, SWT.NONE,
				node(currentEdgeSourceNodeId), node(targetNodeId));
		/* Set the optional label, if set in the DOT input: */
		if (currentEdgeLabelValue != null) {
			graphConnection.setText(currentEdgeLabelValue);
		} else if (globalEdgeLabel != null) {
			graphConnection.setText(globalEdgeLabel);
		}
		/* Set the optional style, if set in the DOT input: */
		if (currentEdgeStyleValue != null) {
			Style v = Enum.valueOf(Style.class,
					currentEdgeStyleValue.toUpperCase());
			graphConnection.setLineStyle(v.style);
		} else if (globalEdgeStyle != null) {
			Style v = Enum.valueOf(Style.class, globalEdgeStyle.toUpperCase());
			graphConnection.setLineStyle(v.style);
		}
	}

	@Override
	public Object caseEdgeRhsNode(EdgeRhsNode object) {
		// Set the flag for the node_id case handled above
		gotSource = true;
		return super.caseEdgeRhsNode(object);
	}

	@Override
	public Object caseSubgraph(Subgraph object) {
		createSubgraph(object);
		return super.caseSubgraph(object);
	}

	// private implementation of the cases above

	private void createSubgraph(Subgraph object) {
		/*
		 * Graphviz DOT naming convention for cluster subgraphs, see
		 * http://www.graphviz.org/doc/info/lang.html
		 */
		if (object.getName() != null && object.getName().startsWith("cluster")) { //$NON-NLS-1$
			layoutSubgraph();
			currentSubgraph = new GraphContainer(graph, SWT.NONE);
			currentSubgraph.setLayoutAlgorithm(new TreeLayoutAlgorithm(), true);
		}
	}

	private void layoutSubgraph() {
		if (currentSubgraph != null) {
			currentSubgraph.applyLayout();
			currentSubgraph.open(false);
			/*
			 * TODO do this only after the end of each subgraph if possible, and
			 * set subgraph to null to have subsequent nodes added to the parent
			 * graph (currently subsequent nodes are in latest subgraph).
			 */
		}
	}

	private IContainer currentParentGraph() {
		return currentSubgraph != null ? currentSubgraph : graph;
	}

	private void createGraph(MainGraph object) {
		graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(), true);
		GraphType graphType = object.getType();
		graph.setConnectionStyle(graphType == GraphType.DIGRAPH ? ZestStyles.CONNECTIONS_DIRECTED
				: ZestStyles.CONNECTIONS_SOLID);
	}

	private void createAttributes(final AttrStmt attrStmt) {
		AttributeType type = attrStmt.getType();
		switch (type) {
		case EDGE: {
			globalEdgeStyle = getAttributeValue(attrStmt, "style"); //$NON-NLS-1$
			globalEdgeLabel = getAttributeValue(attrStmt, "label"); //$NON-NLS-1$
			break;
		}
		case NODE: {
			globalNodeLabel = getAttributeValue(attrStmt, "label"); //$NON-NLS-1$
			break;
		}
		case GRAPH: {
			for (AList a : attrStmt.getAttributes().get(0).getA_list()) {
				graph.setData(a.getName(), a.getValue());
			}
			String graphLayout = getAttributeValue(attrStmt, "layout"); //$NON-NLS-1$
			if (graphLayout != null) {
				Layout layout = Enum.valueOf(Layout.class,
						graphLayout.toUpperCase());
				currentParentGraph().setLayoutAlgorithm(layout.algorithm, true);
			}
			break;
		}
		}
	}

	private void createNode(final NodeStmt nodeStatement) {
		String nodeId = escaped(nodeStatement.getName());
		GraphNode node = nodes.containsKey(nodeId) ? nodes.get(nodeId)
				: new GraphNode(currentParentGraph(), SWT.NONE, nodeId);
		node.setText(nodeId);
		node.setData(nodeId);
		String value = getAttributeValue(nodeStatement, "label"); //$NON-NLS-1$
		if (value != null) {
			node.setText(value);
		} else if (globalNodeLabel != null) {
			node.setText(globalNodeLabel);
		}
		nodes.put(nodeId, node);
	}

	private GraphNode node(String id) {
		if (!nodes.containsKey(id)) { // undeclared node, as in "graph{1->2}"
			GraphNode node = new GraphNode(currentParentGraph(), SWT.NONE,
					globalNodeLabel != null ? globalNodeLabel : id);
			node.setData(id);
			nodes.put(id, node);
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
					if (next instanceof AList) {
						AList attributeElement = (AList) next;
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