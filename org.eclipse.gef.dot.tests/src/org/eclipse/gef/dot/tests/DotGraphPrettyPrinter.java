/*******************************************************************************
 * Copyright (c) 2018, 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #518417)
 *     Zoey Prigge    (itemis AG) - internal API change prettyPrinting attributes
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;

import com.google.common.base.Strings;

import javafx.beans.property.ReadOnlyMapProperty;

/**
 * A Pretty Printer providing formatted string representations (with line
 * separation and indentation) for {@link Graph}, {@link Node} and {@link Edge}
 * objects.
 *
 */
class DotGraphPrettyPrinter {

	private String indent;
	private String lineSeparator;

	private Map<Node, String> nodeToIdMapper;

	/**
	 * Creates a Pretty Printer with the default settings.
	 */
	public DotGraphPrettyPrinter() {
		this("\t", System.lineSeparator());
	}

	/**
	 * Creates a Pretty Printer with the given indent and lineSeparator
	 * characters.
	 *
	 * @param indent
	 *            characters to use for indenting.
	 * @param lineSeparator
	 *            characters to use for line separation.
	 */
	public DotGraphPrettyPrinter(String indent, String lineSeparator) {
		this.indent = indent;
		this.lineSeparator = lineSeparator;
		this.nodeToIdMapper = new HashMap<>();
	}

	/**
	 * Calculates the position of the given {@link Edge} within the containing
	 * {@link Graph}.
	 *
	 * @param edge
	 *            The {@link Edge} for which to return the position within the
	 *            containing {@link Graph}.
	 *
	 * @return The (1-based) position of the given {@link Edge} within the
	 *         containing {@link Graph} or -1 if the edge is not contained in
	 *         any {@link Graph}.
	 */
	protected int getPosition(Edge edge) {
		Graph graph = edge.getGraph();
		if (graph == null) {
			return -1;
		}
		// the position starts with 1 (not with 0)
		return graph.getEdges().indexOf(edge) + 1;
	}

	/**
	 * Calculates the position of the given {@link Node} within the containing
	 * {@link Graph}.
	 *
	 * @param node
	 *            The {@link Node} for which to return the position within the
	 *            containing {@link Graph}.
	 *
	 * @return The (1-based) position of the given {@link Node} within the
	 *         containing {@link Graph} or -1 if the node is not contained in
	 *         any {@link Graph}.
	 */
	protected int getPosition(Node node) {
		Graph graph = node.getGraph();
		if (graph == null) {
			return -1;
		}
		// the position starts with 1 (not with 0)
		return graph.getNodes().indexOf(node) + 1;
	}

	/**
	 * Creates a formatted string representation of a given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} for which to create a formatted string
	 *            representation.
	 * @return The formatted string representation (with line separation and
	 *         indentation) of the given {@link Edge}.
	 */
	public String prettyPrint(Edge edge) {
		return prettyPrint(edge, "", "");
	}

	/**
	 * Creates a formatted string representation of a given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} for which to create a formatted string
	 *            representation.
	 * @param startIndent
	 *            The indentation to use when creating the formatted string
	 *            representation.
	 * @param positionPrefix
	 *            The prefix to prepend the edge position
	 * @return The formatted string representation (with line separation and
	 *         indentation) of the given {@link Edge}.
	 */
	protected String prettyPrint(Edge edge, String startIndent,
			String positionPrefix) {
		StringBuilder sb = new StringBuilder();

		sb.append(startIndent);
		sb.append("Edge");
		int position = getPosition(edge);
		if (position != -1) {
			sb.append(positionPrefix);
			sb.append(position);
		}

		sb.append(String.format(" from Node%s to Node%s {",
				nodeToIdMapper.get(edge.getSource()),
				nodeToIdMapper.get(edge.getTarget())));
		sb.append(lineSeparator);

		sb.append(prettyPrint(edge.attributesProperty(), startIndent + indent));

		sb.append(startIndent);
		sb.append("}");
		sb.append(lineSeparator);

		return sb.toString();
	}

	/**
	 * Creates a formatted string representation of a given {@link Graph}.
	 *
	 * @param graph
	 *            The {@link Graph} for which to create a formatted string
	 *            representation.
	 * @return The formatted string representation (with line separation and
	 *         indentation) of the given {@link Graph}.
	 */
	public String prettyPrint(Graph graph) {
		return prettyPrint(graph, "", "");
	}

	/**
	 * Creates a formatted string representation of a given {@link Graph}.
	 *
	 * @param graph
	 *            The {@link Graph} for which to create a formatted string
	 *            representation.
	 * @param startIndent
	 *            The indentation to use when creating the formatted string
	 *            representation.
	 * @param positionPrefix
	 *            The prefix to prepend the graph position
	 * @return The formatted string representation (with line separation and
	 *         indentation) of the given {@link Graph}.
	 */
	protected String prettyPrint(Graph graph, String startIndent,
			String positionPrefix) {
		StringBuilder sb = new StringBuilder();

		sb.append(startIndent);
		sb.append("Graph {");
		sb.append(lineSeparator);

		sb.append(
				prettyPrint(graph.attributesProperty(), startIndent + indent));

		for (Node node : graph.getNodes()) {
			sb.append(prettyPrint(node, startIndent + indent, positionPrefix));
		}
		for (Edge edge : graph.getEdges()) {
			sb.append(prettyPrint(edge, startIndent + indent, positionPrefix));
		}
		sb.append(startIndent);
		sb.append("}");
		sb.append(lineSeparator);

		return sb.toString();
	}

	/**
	 * Creates a formatted string representation of a given {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} for which to create a formatted string
	 *            representation.
	 * @return The formatted string representation (with line separation and
	 *         indentation) of the given {@link Node}.
	 */
	public String prettyPrint(Node node) {
		return prettyPrint(node, "", "");
	}

	/**
	 * Creates a formatted string representation of a given {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} for which to create a formatted string
	 *            representation.
	 * @param startIndent
	 *            The indentation to use when creating the formatted string
	 *            representation.
	 * @param positionPrefix
	 *            The prefix to prepend the node position.
	 * @return The formatted string representation (with line separation and
	 *         indentation) of the given {@link Node}.
	 */
	protected String prettyPrint(Node node, String startIndent,
			String positionPrefix) {
		StringBuilder sb = new StringBuilder();

		sb.append(startIndent);
		sb.append("Node");
		int position = getPosition(node);
		String nodeId = "";
		if (position != -1) {
			nodeId = positionPrefix + position;
			sb.append(nodeId);
		}
		nodeToIdMapper.put(node, nodeId);
		sb.append(" {");
		sb.append(lineSeparator);

		sb.append(prettyPrint(node.attributesProperty(), startIndent + indent));

		Graph nestedGraph = node.getNestedGraph();
		if (nestedGraph != null) {
			String newPositionPrefix = positionPrefix + position + ".";
			String nestedGraphText = prettyPrint(nestedGraph,
					startIndent + indent, newPositionPrefix);
			sb.append(nestedGraphText);
		}
		sb.append(startIndent);
		sb.append("}");
		sb.append(lineSeparator);

		return sb.toString();
	}

	/**
	 * Creates a formatted string representation of a given attributesProperty.
	 *
	 * @param node
	 *            The attributesProperty for which to create a formatted string
	 *            representation.
	 * @param startIndent
	 *            The indentation to use when creating the formatted string
	 *            representation.
	 * @return The formatted string representation (with line separation and
	 *         indentation) of the given attributesProperty.
	 */
	protected String prettyPrint(
			ReadOnlyMapProperty<String, Object> attributesProperty,
			String startIndent) {
		StringBuilder sb = new StringBuilder();

		TreeMap<String, Object> sortedAttributes = new TreeMap<>();
		sortedAttributes.putAll(attributesProperty);
		for (String attrKey : sortedAttributes.keySet()) {
			Object attrValue = attributesProperty.get(attrKey);
			sb.append(prettyPrint(attrKey, attrValue, startIndent));
		}

		return sb.toString();
	}

	/**
	 * Creates a formatted string representation of a given attribute with a
	 * startIndent.
	 *
	 * @param attrKey
	 *            The key of the attribute for which to create a formatted
	 *            string representation.
	 * @param attrValue
	 *            The value of the attribute for which to create a formatted
	 *            string representation.
	 * @param startIndent
	 *            The indentation to use when creating the formatted string
	 *            representation.
	 * @return The formatted string representation (with line separation and
	 *         indentation) of the given attribute.
	 */
	protected String prettyPrint(String attrKey, Object attrValue,
			String startIndent) {
		StringBuilder sb = new StringBuilder();
		sb.append(startIndent);
		sb.append(prettyPrint(attrKey, attrValue));
		sb.append(lineSeparator);
		return sb.toString();
	}

	/**
	 * Creates a formatted string representation of a given attribute
	 * (unindented on single line).
	 *
	 * @param attrKey
	 *            The key of the attribute for which to create a formatted
	 *            string representation.
	 * @param attrValue
	 *            The value of the attribute for which to create a formatted
	 *            string representation.
	 * @return The formatted unindented, single-line string representation of
	 *         the given attribute.
	 */
	protected String prettyPrint(String attrKey, Object attrValue) {
		String result = attrKey + " :";
		String attrValueText = attrValue.toString();
		if (!Strings.isNullOrEmpty(attrValueText)) {
			result += " " + attrValueText;
		}
		return result;
	}

	/**
	 * @return The characters to use for indentation.
	 */
	public String getIndent() {
		return indent;
	}

	/**
	 * @return The characters to use for line separation.
	 */
	public String getLineSeparator() {
		return lineSeparator;
	}
}
