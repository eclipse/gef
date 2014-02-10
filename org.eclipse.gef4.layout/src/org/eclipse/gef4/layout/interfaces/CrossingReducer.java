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
import java.util.Map;

import org.eclipse.gef4.layout.algorithms.NodeWrapper;

/**
 * 
 * An interface for heuristics that reduces edge crossings.
 * 
 */
public interface CrossingReducer {
	/**
	 * From the given nodes it creates a map of NodeLayouts and NodeWrappers
	 * which contains the layers and indexes of the nodes
	 * 
	 * @param nodes
	 *            List of nodes needed to be organized
	 * @return
	 */
	Map<NodeLayout, NodeWrapper> crossReduction(List<List<NodeWrapper>> nodes);
}
