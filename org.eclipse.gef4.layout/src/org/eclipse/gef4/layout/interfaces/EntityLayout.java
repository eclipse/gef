/*******************************************************************************
 * Copyright (c) 2009-2010 Mateusz Matela and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Mateusz Matela - initial API and implementation
 *               Ian Bull
 ******************************************************************************/
package org.eclipse.gef4.layout.interfaces;

import org.eclipse.gef4.layout.IPropertyStore;

/**
 * A common interface for entities that are displayed on a graph, that is
 * {@link NodeLayout nodes} and {@link SubgraphLayout subgraphs}.
 */
public interface EntityLayout extends IPropertyStore {

	/**
	 * Stores the location of this EntityLayout.
	 */
	public static final String LOCATION_PROPERTY = "location";

	/**
	 * Stores the size of this EntityLayout.
	 */
	public static final String SIZE_PROPERTY = "size";

	/**
	 * Stores the movable state of this EntityLayout.
	 */
	public static final String MOVABLE_PROPERTY = "movable";

	/**
	 * Stores the preferred aspect ratio (width / height) for this EntityLayout.
	 */
	public static final String ASPECT_RATIO_PROPERTY = "aspect-ratio";

	/**
	 * Stores the resizable state of this EntityLayout. A resizable EntityLayout
	 * may be resized by a layout algorithm.
	 */
	public static final String RESIZABLE_PROPERTY = "resizable";

	/**
	 * Returns all entities that are direct successors of this entity. Successor
	 * entities of an unpruned node N are:
	 * <ul>
	 * <li>all unpruned successor nodes of node N</li>
	 * <li>all subgraphs that are <code>GraphEntities</code> and contain at
	 * least one successor node of node N</li>
	 * </ul>
	 * Successor entities of a subgraph S that is a <code>GraphEntity</code>
	 * are:
	 * <ul>
	 * <li>all unpruned nodes that are successor of at least one node from
	 * subgraph S</li>
	 * <li>all subgraphs that are <code>GraphEntities</code> and contain at
	 * least one node that is a successor of at least one node from subgraph S</li>
	 * </ul>
	 * For subgraphs that are not <code>GraphEntities</code> an empty array will
	 * be returned.</br>Entities connected with this node by a bidirectional
	 * connection are considered both successors and predecessors. Any
	 * subsequent changes to the returned array do not affect this node.
	 * 
	 * @return array of successors of this node
	 */
	public EntityLayout[] getSuccessingEntities();

	/**
	 * Returns all entities that are direct predecessors of this entity.
	 * Predecessor entities of an unpruned node A are:
	 * <ul>
	 * <li>all unpruned predecessor nodes of node N</li>
	 * <li>all subgraphs that are <code>GraphEntities</code> and contain at
	 * least one predecessor node of node N</li>
	 * </ul>
	 * Successor entities of a subgraph S that is a <code>GraphEntity</code>
	 * are:
	 * <ul>
	 * <li>all unpruned nodes that are predecessor of at least one node from
	 * subgraph S</li>
	 * <li>all subgraphs that are <code>GraphEntities</code> and contain at
	 * least one node that is a predecessor of at least one node from subgraph S
	 * </li>
	 * </ul>
	 * For subgraphs that are not <code>GraphEntities</code> an empty array will
	 * be returned.</br>Entities connected with this node by a bidirectional
	 * connection are considered both successors and predecessors. Any
	 * subsequent changes to the returned array do not affect this node.
	 * 
	 * @return array of predecessors of this node
	 */
	public EntityLayout[] getPredecessingEntities();

	/**
	 * Returns all graph items that are represented using this single entity.
	 * They are useful when a layout would get information about the graph it
	 * draws.
	 * 
	 * @return an array of graph items
	 */
	public Object[] getItems();

}
