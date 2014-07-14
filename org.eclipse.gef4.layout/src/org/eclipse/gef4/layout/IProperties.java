/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.layout;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;

public interface IProperties {

	// graphs

	/**
	 * Stores the bounds in which the graph elements can be placed.
	 */
	public static final String BOUNDS_PROPERTY = "bounds";
	public static final Rectangle DEFAULT_BOUNDS = new Rectangle();

	/**
	 * Indicates whether an algorithm is allowed to place graph elements outside
	 * of the bounds.
	 */
	public static final String BOUNDS_EXPANDABLE_PROPERTY = "bounds-expandable";
	public static final Boolean DEFAULT_BOUNDS_EXPANDABLE = false;

	/**
	 * Indicates whether an algorithm is allowed to prune nodes to subgraphs.
	 */
	public static final String PRUNING_ENABLED_PROPERTY = "pruning-enabled";
	public static final Boolean DEFAULT_PRUNING_ENABLED = true;

	/**
	 * Indicates if layout algorithms can work in the background, reacting to
	 * events/animation.
	 */
	public static final String DYNAMIC_LAYOUT_ENABLED_PROPERTY = "dynamic-layout-enabled";
	public static final Boolean DEFAULT_DYNAMIC_LAYOUT_ENABLED = true;

	// entities

	/**
	 * Stores the location of this EntityLayout.
	 */
	public static final String LOCATION_PROPERTY = "location";
	public static final Point DEFAULT_LOCATION = new Point();

	/**
	 * Stores the size of this EntityLayout.
	 */
	public static final String SIZE_PROPERTY = "size";
	public static final Dimension DEFAULT_SIZE = new Dimension();

	/**
	 * Stores the movable state of this EntityLayout.
	 */
	public static final String MOVABLE_PROPERTY = "movable";
	public static final Boolean DEFAULT_MOVABLE = true;

	/**
	 * Stores the resizable state of this EntityLayout. A resizable EntityLayout
	 * may be resized by a layout algorithm.
	 */
	public static final String RESIZABLE_PROPERTY = "resizable";
	public static final Boolean DEFAULT_RESIZABLE = true;

	/**
	 * Stores the preferred aspect ratio (width / height) for this EntityLayout.
	 */
	public static final String ASPECT_RATIO_PROPERTY = "aspect-ratio";
	public static final Double DEFAULT_ASPECT_RATIO = 0d;

	// nodes

	/**
	 * Stores the minimized state of this NodeLayout. A minimized NodeLayout
	 * resizes its visual to (0, 0). When it is unminimized, it resizes it back
	 * to its previous dimension. Note that a NodeLayout can be minimized even
	 * if it is not resizable.
	 */
	public static final String MINIMIZED_PROPERTY = "minimized";
	public static final Boolean DEFAULT_MINIMIZED = false;

	/**
	 * Stores the prunable state of this NodeLayout. A prunable NodeLayout may
	 * be pruned to a subgraph.
	 */
	public static final String PRUNABLE_PROPERTY = "prunable";
	public static final Boolean DEFAULT_PRUNABLE = true;

	// connections

	/**
	 * Stores the visibility state of this connection.
	 */
	public static final String VISIBLE_PROPERTY = "visible";
	public static final Boolean DEFAULT_VISIBLE = true;

	/**
	 * Stores a weight for this connection.
	 */
	public static final String WEIGHT_PROPERTY = "weight";
	public static final Double DEFAULT_WEIGHT = 0d;

	/**
	 * Stores a weight for this connection.
	 */
	public static final String DIRECTED_PROPERTY = "directed";
	public static final Boolean DEFAULT_DIRECTED = true;

	// subgraphs

	/**
	 * Constant for top-down direction (default).
	 */
	public final int DIRECTION_TOP_DOWN = 1;

	/**
	 * Constant for bottom-up direction.
	 */
	public final int DIRECTION_BOTTOM_UP = 2;

	/**
	 * Constant for direction from left to right.
	 */
	public final int DIRECTION_LEFT_RIGHT = 3;

	/**
	 * Constant for direction from right to left.
	 */
	public final int DIRECTION_RIGHT_LEFT = 4;

	/**
	 * Stores the direction of this subgraph.
	 */
	public static final String DIRECTION_PROPERTY = "direction";
	public static final Integer DEFAULT_DIRECTION = DIRECTION_TOP_DOWN;

	/**
	 * Indicates whether this subgraph is visualized differently depending on
	 * its .
	 */
	public static final String DIRECTION_DEPENDANT_PROPERTY = "direction-dependant";
	public static final Boolean DEFAULT_DIRECTION_DEPENDANT = true;

	/**
	 * Specifies if the subgraph is visualized as a particular object on the
	 * graph. If a subgraph is not a graph entity, it means that it will not be
	 * visible, so all methods related to location, size and direction should be
	 * ignored.
	 */
	public static final String IS_GRAPH_ENTITY_PROPERTY = "is-graph-entity";
	public static final Boolean DEFAULT_IS_GRAPH_ENTITY = true;

}
