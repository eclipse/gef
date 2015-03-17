/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.ui.jface;

import java.util.Map;

import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.jface.viewers.ILabelProvider;

public interface IGraphNodeLabelProvider extends ILabelProvider {

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
	 *            {@link IGraphNodeContentProvider#getConnectedTo(Object)}
	 *            method.
	 * @param targetNode
	 *            A content element representing the target node of an edge,
	 *            according to the
	 *            {@link IGraphNodeContentProvider#getConnectedTo(Object)}
	 *            method.
	 * @return A mapping from attribute names to values that should be set on
	 *         the specified edge.
	 */
	public Map<String, Object> getEdgeAttributes(Object sourceNode,
			Object targetNode);

	/**
	 * Determines the attributes that should be set on the node represented by
	 * the given content element. If no attributes should be set on the node,
	 * either an empty map or <code>null</code> can be returned.
	 *
	 * @see ZestProperties
	 * @param node
	 *            A content element representing a node according to the
	 *            {@link IGraphNodeContentProvider#getNodes()} method.
	 * @return A mapping from attribute names to values that should be set on
	 *         the node represented by the given content element.
	 */
	public Map<String, Object> getNodeAttributes(Object node);

	/**
	 * Determines the attributes that should be set on the root graph. If no
	 * attributes should be set on the root graph, either an empty map or
	 * <code>null</code> can be returned.
	 *
	 * @see ZestProperties
	 * @return A mapping from attribute names to values that should be set on
	 *         the root graph.
	 */
	public Map<String, Object> getRootGraphAttributes();

}
