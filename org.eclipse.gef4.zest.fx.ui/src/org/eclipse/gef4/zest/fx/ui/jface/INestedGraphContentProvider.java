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
 * Note: Parts of this class have been transferred from org.eclipse.gef4.zest.core.viewers.INestedContentProvider
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.ui.jface;

/**
 * The {@link INestedGraphContentProvider} extends the
 * {@link IGraphNodeContentProvider} with parent-child-relations. You can use
 * this to express nested graphs.
 *
 * @author mwienand
 *
 */
public interface INestedGraphContentProvider extends IGraphNodeContentProvider {

	/**
	 * Returns the nested children of the node represented by the given content
	 * element. If the node does not have nested children, either an empty array
	 * or <code>null</code> is returned.
	 *
	 * @param node
	 *            A content element that represents a node, according to the
	 *            {@link #getNodes()} method.
	 * @return The nested children of the node represented by the given content
	 *         element.
	 */
	public Object[] getChildren(Object node);

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
	public boolean hasChildren(Object node);

}
