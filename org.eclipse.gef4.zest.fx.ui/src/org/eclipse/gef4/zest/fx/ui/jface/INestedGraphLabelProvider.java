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

/**
 * The {@link INestedGraphLabelProvider} is an extension to the
 * {@link IGraphNodeLabelProvider} that additionally allows the provision of
 * attributes for nested graphs.
 *
 * @author mwienand
 *
 */
public interface INestedGraphLabelProvider extends IGraphNodeLabelProvider {

	/**
	 * Determines the attributes that should be set on a nested graph that is
	 * nested inside the node represented by the given content element. If no
	 * attributes should be set on the graph, either an empty map or
	 * <code>null</code> can be returned.
	 *
	 * @see ZestProperties For an overview of the supported attributes.
	 * @param nestingNode
	 *            A content element representing a nesting node according to the
	 *            {@link INestedGraphContentProvider#hasChildren(Object)}
	 *            method.
	 * @return A mapping from attribute names to values that should be set on
	 *         the graph nested inside the node represented by the given content
	 *         element.
	 */
	public Map<String, Object> getNestedGraphAttributes(Object nestingNode);

}
