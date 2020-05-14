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
 * Note: Parts of this class have been transferred from org.eclipse.gef.zest.core.viewers.IGraphEntityContentProvider
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.jface;

import org.eclipse.jface.viewers.IContentProvider;

/**
 * The {@link IGraphContentProvider} mediates between the
 * {@link ZestContentViewer} and a content model. It provides content elements
 * which represent nodes, edges between nodes, and nested nodes.
 *
 * @author mwienand
 *
 */
public interface IGraphContentProvider extends IContentProvider {

	/**
	 * Returns all content elements which represent nodes that are adjacent to
	 * the node represented by the given content element, i.e. determines the
	 * targets of all edges that start at the given node. If the given content
	 * element does not have any outgoing edges, then either an empty array or
	 * <code>null</code> is returned.
	 *
	 * @param node
	 *            A model object representing a graph node.
	 * @return All model objects which represent nodes which are adjacent to the
	 *         node represented by the given model object.
	 */
	public Object[] getAdjacentNodes(Object node);

	/**
	 * Returns the content elements representing the nodes within the graph that
	 * is nested inside the node represented by the given content element. If
	 * the node does not contain a nested graph, either an empty array or
	 * <code>null</code> is returned.
	 *
	 * @param node
	 *            A content element that represents a node.
	 * @return The content elements representing the nodes within the graph that
	 *         is nested inside the node represented by the given content
	 *         element.
	 */
	public Object[] getNestedGraphNodes(Object node);

	/**
	 * Returns all content elements which represent nodes on the first level of
	 * the graph. If no nodes exist, either an empty array or <code>null</code>
	 * is returned.
	 *
	 * @return All content elements which represent nodes on the first level of
	 *         the graph.
	 */
	public Object[] getNodes();

	/**
	 * Determines whether the node represented by the given content element has
	 * nested children.
	 *
	 * @param node
	 *            A content element that represents a node, according to the
	 *            {@link #getNodes()} method.
	 * @return <code>true</code> when the node represented by the given content
	 *         element has nested children, otherwise <code>false</code>.
	 */
	public boolean hasNestedGraph(Object node);

}
