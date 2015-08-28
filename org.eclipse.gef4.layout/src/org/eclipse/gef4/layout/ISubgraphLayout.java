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

/**
 * An interface for subgraphs in layout. A subgraph is a set of pruned nodes
 * that will be displayed as one element. A subgraph must contain at least one
 * node (empty subgraphs will be removed from its context). Every node can
 * belong to at most one subgraph.
 */
public interface ISubgraphLayout extends IEntityLayout {

	/**
	 * Returns all the nodes belonging to this subgraph. Replacing elements in
	 * the returned array does not affect this subgraph.
	 * 
	 * @return array of nodes
	 */
	public INodeLayout[] getNodes();

	/**
	 * Returns the number of nodes pruned into this subgraph.
	 * 
	 * @return The number of nodes pruned into this subgraph.
	 */
	public int countNodes();

	/**
	 * Adds nodes to this subgraph. If given nodes already belong to another
	 * subgraph, they are first removed from them.
	 * 
	 * @param nodes
	 *            array of nodes to add
	 */
	public void addNodes(INodeLayout[] nodes);

	/**
	 * Removes nodes from this subgraph.
	 * 
	 * @param nodes
	 *            array of nodes to remove
	 */
	public void removeNodes(INodeLayout[] nodes);

}
