/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.jface;

import java.util.Map;

import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.jface.viewers.ILabelProvider;

/**
 * The {@link IGraphAttributesProvider} can be used to provide arbitrary
 * attributes for nodes, edges, the root graph, and any nested graphs.
 *
 * @author mwienand
 *
 */
public interface IGraphAttributesProvider extends ILabelProvider {

	/**
	 * Determines the attributes that should be set on the edge with the
	 * specified source and target content elements. If no attributes should be
	 * set on the edge, either an empty map or <code>null</code> can be
	 * returned.
	 *
	 * @see ZestProperties For an overview of the supported attributes.
	 * @param sourceNode
	 *            A content element representing the source node of an edge,
	 *            according to the
	 *            {@link IGraphContentProvider#getAdjacentNodes(Object)} method.
	 * @param targetNode
	 *            A content element representing the target node of an edge,
	 *            according to the
	 *            {@link IGraphContentProvider#getAdjacentNodes(Object)} method.
	 * @return A mapping from attribute names to values that should be set on
	 *         the specified edge.
	 */
	public Map<String, Object> getEdgeAttributes(Object sourceNode, Object targetNode);

	/**
	 * Determines the attributes that should be set on the root graph. If no
	 * attributes should be set on the root graph, either an empty map or
	 * <code>null</code> can be returned.
	 *
	 * @see ZestProperties
	 * @return A mapping from attribute names to values that should be set on
	 *         the root graph.
	 */
	public Map<String, Object> getGraphAttributes();

	/**
	 * Determines the attributes that should be set on a nested graph that is
	 * nested inside the node represented by the given content element. If no
	 * attributes should be set on the graph, either an empty map or
	 * <code>null</code> can be returned.
	 *
	 * @see ZestProperties For an overview of the supported attributes.
	 * @param nestingNode
	 *            A content element representing a nesting node according to the
	 *            {@link IGraphContentProvider#hasNestedGraph(Object)} method.
	 * @return A mapping from attribute names to values that should be set on
	 *         the graph nested inside the node represented by the given content
	 *         element.
	 */
	public Map<String, Object> getNestedGraphAttributes(Object nestingNode);

	/**
	 * Determines the attributes that should be set on the node represented by
	 * the given content element. If no attributes should be set on the node,
	 * either an empty map or <code>null</code> can be returned.
	 *
	 * @see ZestProperties
	 * @param node
	 *            A content element representing a node according to the
	 *            {@link IGraphContentProvider#getNodes()} method.
	 * @return A mapping from attribute names to values that should be set on
	 *         the node represented by the given content element.
	 */
	public Map<String, Object> getNodeAttributes(Object node);

}
