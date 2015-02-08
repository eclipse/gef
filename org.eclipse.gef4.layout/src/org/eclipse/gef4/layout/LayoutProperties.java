/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
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
import org.eclipse.gef4.layout.interfaces.ConnectionLayout;
import org.eclipse.gef4.layout.interfaces.EntityLayout;
import org.eclipse.gef4.layout.interfaces.LayoutContext;
import org.eclipse.gef4.layout.interfaces.NodeLayout;
import org.eclipse.gef4.layout.interfaces.SubgraphLayout;

public class LayoutProperties {

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

	/**
	 * Stores the visibility state of the entity.
	 */
	public static final String VISIBLE_PROPERTY = "visible";
	public static final Boolean DEFAULT_VISIBLE = true;

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

	/**
	 * Constant for top-down direction (default).
	 */
	public static final int DIRECTION_TOP_DOWN = 1;

	/**
	 * Constant for bottom-up direction.
	 */
	public static final int DIRECTION_BOTTOM_UP = 2;

	/**
	 * Constant for direction from left to right.
	 */
	public static final int DIRECTION_LEFT_RIGHT = 3;

	/**
	 * Constant for direction from right to left.
	 */
	public static final int DIRECTION_RIGHT_LEFT = 4;

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

	public static Rectangle getBounds(LayoutContext graph) {
		Object bounds = graph.getProperty(BOUNDS_PROPERTY);
		if (bounds instanceof Rectangle) {
			return ((Rectangle) bounds).getCopy();
		}
		return DEFAULT_BOUNDS.getCopy();
	}

	public static void setBounds(LayoutContext graph, Rectangle bounds) {
		graph.setProperty(BOUNDS_PROPERTY, bounds);
	}

	public static Boolean isBoundsExpandable(LayoutContext graph) {
		Object boundsExpandable = graph.getProperty(BOUNDS_EXPANDABLE_PROPERTY);
		if (boundsExpandable instanceof Boolean) {
			return (Boolean) boundsExpandable;
		}
		return DEFAULT_BOUNDS_EXPANDABLE;
	}

	public static void setBoundsExpandable(LayoutContext graph,
			boolean boundsExpandable) {
		graph.setProperty(BOUNDS_EXPANDABLE_PROPERTY, boundsExpandable);
	}

	public static Boolean isDynamicLayoutEnables(LayoutContext graph) {
		Object dynamicLayoutEnabled = graph
				.getProperty(DYNAMIC_LAYOUT_ENABLED_PROPERTY);
		if (dynamicLayoutEnabled instanceof Boolean) {
			return (Boolean) dynamicLayoutEnabled;
		}
		return DEFAULT_DYNAMIC_LAYOUT_ENABLED;
	}

	public static void setDynamicLayoutEnabled(LayoutContext graph,
			boolean dynamicLayoutEnabled) {
		graph.setProperty(DYNAMIC_LAYOUT_ENABLED_PROPERTY, dynamicLayoutEnabled);
	}

	public static Point getLocation(EntityLayout entity) {
		Object location = entity.getProperty(LOCATION_PROPERTY);
		if (location instanceof Point) {
			return ((Point) location).getCopy();
		}
		return DEFAULT_LOCATION.getCopy();
	}

	public static Dimension getSize(EntityLayout entity) {
		Object size = entity.getProperty(SIZE_PROPERTY);
		if (size instanceof Dimension) {
			return ((Dimension) size).getCopy();
		}
		return DEFAULT_SIZE.getCopy();
	}

	public static Double getPreferredAspectRatio(EntityLayout entity) {
		Object ar = entity.getProperty(ASPECT_RATIO_PROPERTY);
		if (ar instanceof Double) {
			return (Double) ar;
		}
		return DEFAULT_ASPECT_RATIO;
	}

	public static Boolean isResizable(EntityLayout entity) {
		Object resizable = entity.getProperty(RESIZABLE_PROPERTY);
		if (resizable instanceof Boolean) {
			return (Boolean) resizable;
		}
		return DEFAULT_RESIZABLE;
	}

	public static Boolean isMovable(EntityLayout entity) {
		Object movable = entity.getProperty(MOVABLE_PROPERTY);
		if (movable instanceof Boolean) {
			return (Boolean) movable;
		}
		return DEFAULT_MOVABLE;
	}

	public static void setLocation(EntityLayout entity, double x, double y) {
		if (Double.isNaN(x)) {
			x = 0;
		}
		if (Double.isNaN(y)) {
			y = 0;
		}
		entity.setProperty(LOCATION_PROPERTY, new Point(x, y));
	}

	public static void setSize(EntityLayout entity, double w, double h) {
		if (Double.isNaN(w)) {
			w = 0;
		}
		if (Double.isNaN(h)) {
			h = 0;
		}
		entity.setProperty(SIZE_PROPERTY, new Dimension(w, h));
	}

	public static void setResizable(EntityLayout entity, boolean resizable) {
		entity.setProperty(RESIZABLE_PROPERTY, resizable);
	}

	public static Boolean isMinimized(NodeLayout node) {
		Object minimized = node.getProperty(MINIMIZED_PROPERTY);
		if (minimized instanceof Boolean) {
			return (Boolean) minimized;
		}
		return DEFAULT_MINIMIZED;
	}

	public static void setMinimized(NodeLayout node, boolean minimized) {
		node.setProperty(MINIMIZED_PROPERTY, minimized);
	}

	public static Boolean isPrunable(NodeLayout node) {
		Object prunable = node.getProperty(PRUNABLE_PROPERTY);
		if (prunable instanceof Boolean) {
			return (Boolean) prunable;
		}
		return DEFAULT_PRUNABLE;
	}

	public static Boolean isPruned(NodeLayout node) {
		return node.getSubgraph() != null;
	}

	public static void setPrunable(NodeLayout node, boolean prunable) {
		node.setProperty(PRUNABLE_PROPERTY, prunable);
	}

	public static Boolean isDirected(ConnectionLayout edge) {
		Object directed = edge.getProperty(DIRECTED_PROPERTY);
		if (directed instanceof Boolean) {
			return (Boolean) directed;
		}
		return DEFAULT_DIRECTED;
	}

	public static Boolean isVisible(ConnectionLayout edge) {
		Object visible = edge.getProperty(VISIBLE_PROPERTY);
		if (visible instanceof Boolean) {
			return (Boolean) visible;
		}
		return DEFAULT_VISIBLE;
	}

	public static Boolean isVisible(EntityLayout entity) {
		Object visible = entity.getProperty(VISIBLE_PROPERTY);
		if (visible instanceof Boolean) {
			return (Boolean) visible;
		}
		return DEFAULT_VISIBLE;
	}

	public static Double getWeight(ConnectionLayout edge) {
		Object weight = edge.getProperty(WEIGHT_PROPERTY);
		if (weight instanceof Double) {
			return (Double) weight;
		}
		return DEFAULT_WEIGHT;
	}

	public static void setDirected(ConnectionLayout edge, boolean directed) {
		edge.setProperty(DIRECTED_PROPERTY, directed);
	}

	public static void setVisible(ConnectionLayout edge, boolean visible) {
		edge.setProperty(VISIBLE_PROPERTY, visible);
	}

	public static void setWeight(ConnectionLayout edge, double weight) {
		edge.setProperty(WEIGHT_PROPERTY, weight);
	}

	public static void setDirectionDependant(SubgraphLayout subgraph,
			boolean isDirectionDependant) {
		subgraph.setProperty(DIRECTION_DEPENDANT_PROPERTY, isDirectionDependant);
	}

	// TODO: ensure valid direction by using an enum
	public static void setDirection(SubgraphLayout subgraph, int direction) {
		subgraph.setProperty(DIRECTION_PROPERTY, direction);
	}

	public static Boolean isDirectionDependant(SubgraphLayout subgraph) {
		Object directionDependant = subgraph
				.getProperty(DIRECTION_DEPENDANT_PROPERTY);
		if (directionDependant instanceof Boolean) {
			return (Boolean) directionDependant;
		}
		return DEFAULT_DIRECTION_DEPENDANT;
	}

	public static Integer getDirection(SubgraphLayout subgraph) {
		Object direction = subgraph.getProperty(DIRECTION_PROPERTY);
		if (direction instanceof Integer) {
			return (Integer) direction;
		}
		return DEFAULT_DIRECTION;
	}

}
