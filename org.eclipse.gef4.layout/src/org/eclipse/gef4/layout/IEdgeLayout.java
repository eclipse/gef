/*******************************************************************************
 * Copyright (c) 2009, 2015 Mateusz Matela and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Mateusz Matela - initial API and implementation
 *               Ian Bull
 ******************************************************************************/
package org.eclipse.gef4.layout;

import org.eclipse.gef4.common.attributes.IAttributeStore;

/**
 * An {@link IEdgeLayout} represents a connection between two
 * {@link INodeLayout}s, i.e. it represents an edge of a graph within the layout
 * model.
 */
public interface IEdgeLayout extends IAttributeStore {

	/**
	 * @return source node
	 */
	public INodeLayout getSource();

	/**
	 * @return target node
	 */
	public INodeLayout getTarget();

}
