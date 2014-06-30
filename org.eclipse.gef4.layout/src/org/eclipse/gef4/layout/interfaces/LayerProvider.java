/*******************************************************************************
 * Copyright (c) 2014 Adam Kovacs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:     
 *    Adam Kovacs - initial implementation
 *******************************************************************************/
package org.eclipse.gef4.layout.interfaces;

import java.util.List;

import org.eclipse.gef4.layout.algorithms.NodeWrapper;

/**
 * 
 * An interface for creating layers. Interface for parameterizable layering
 * heuristics.
 * 
 */
public interface LayerProvider {

	/**
	 * Creating layers of the nodes and makes it possible to assign layers to
	 * those nodes.
	 * 
	 * @param nodes
	 *            List of all the nodes that needs to be organized
	 * @return
	 */
	List<List<NodeWrapper>> calculateLayers(List<NodeLayout> nodes);
}
