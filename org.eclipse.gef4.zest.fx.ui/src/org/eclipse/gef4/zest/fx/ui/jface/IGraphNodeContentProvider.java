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
 * Note: Parts of this class have been transferred from org.eclipse.gef4.zest.core.viewers.IGraphEntityContentProvider
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.ui.jface;

import org.eclipse.jface.viewers.IContentProvider;

public interface IGraphNodeContentProvider extends IContentProvider {

	/**
	 * Returns all model objects which represent nodes which are connected to
	 * the node represented by the given model object.
	 *
	 * @param node
	 *            A model object representing a graph node.
	 * @return All model objects which represent nodes which are connected to
	 *         the node represented by the given model object.
	 */
	public Object[] getConnectedTo(Object node);

	/**
	 * Returns all model objects which represent nodes on the first level of the
	 * graph.
	 *
	 * @return All model objects which represent nodes on the first level of the
	 *         graph.
	 */
	public Object[] getNodes();

}
