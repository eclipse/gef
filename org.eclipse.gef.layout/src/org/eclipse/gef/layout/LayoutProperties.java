/*******************************************************************************
 * Copyright (c) 2013, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.layout;

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;

/**
 * {@link LayoutProperties} contains all properties which can be accessed on
 * {@link Graph}, {@link Edge}, {@link Node}, their default values, as well as
 * static accessor methods for those properties.
 */
public class LayoutProperties {

	/**
	 * Stores the bounds in which the graph elements can be placed.
	 */
	public static final String BOUNDS_PROPERTY = "layout_bounds";

	/**
	 * Stores the location of this EntityLayout.
	 */
	public static final String LOCATION_PROPERTY = "layout_location";

	/**
	 * Stores the size of this EntityLayout.
	 */
	public static final String SIZE_PROPERTY = "layout_size";

	/**
	 * Stores the movable state of this EntityLayout.
	 */
	public static final String MOVABLE_PROPERTY = "layout_movable";

	/**
	 * Defines the default value for the {@link #MOVABLE_PROPERTY}.
	 */
	public static final Boolean DEFAULT_MOVABLE = true;

	/**
	 * Stores the resizable state of this EntityLayout. A resizable EntityLayout
	 * may be resized by a layout algorithm.
	 */
	public static final String RESIZABLE_PROPERTY = "layout_resizable";

	/**
	 * Defines the default value for the {@link #RESIZABLE_PROPERTY}.
	 */
	public static final Boolean DEFAULT_RESIZABLE = true;

	/**
	 * Stores the preferred aspect ratio (width / height) for this EntityLayout.
	 */
	public static final String ASPECT_RATIO_PROPERTY = "layout_aspect-ratio";

	/**
	 * Defines the default value for the {@link #ASPECT_RATIO_PROPERTY}.
	 */
	public static final Double DEFAULT_ASPECT_RATIO = 0d;

	/**
	 * Stores a weight for this connection.
	 */
	public static final String WEIGHT_PROPERTY = "layout_weight";

	/**
	 * Defines the default value for the {@link #WEIGHT_PROPERTY}.
	 */
	public static final Double DEFAULT_WEIGHT = 0d;

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
	public static final String DIRECTION_PROPERTY = "layout_direction";

	/**
	 * Defines the default value for the {@link #DIRECTION_PROPERTY}.
	 */
	public static final Integer DEFAULT_DIRECTION = DIRECTION_TOP_DOWN;

	/**
	 * Returns the value of the {@link #BOUNDS_PROPERTY} of the given
	 * {@link LayoutContext}.
	 * 
	 * @param graph
	 *            The {@link Graph} whose {@link #BOUNDS_PROPERTY} is read.
	 * @return The value of the {@link #BOUNDS_PROPERTY} of the given
	 *         {@link Graph}.
	 */
	public static Rectangle getBounds(Graph graph) {
		return (Rectangle) graph.getAttributes().get(BOUNDS_PROPERTY);
	}

	/**
	 * Sets the value of the {@link #BOUNDS_PROPERTY} of the given
	 * {@link LayoutContext} to the given value.
	 * 
	 * @param graph
	 *            The {@link Graph} whose {@link #BOUNDS_PROPERTY} is changed.
	 * @param bounds
	 *            The new value for the {@link #BOUNDS_PROPERTY} of the given
	 *            {@link Graph}.
	 */
	public static void setBounds(Graph graph, Rectangle bounds) {
		graph.getAttributes().put(BOUNDS_PROPERTY, bounds);
	}

	/**
	 * Returns the value of the {@link #LOCATION_PROPERTY} of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} whose {@link #LOCATION_PROPERTY} is read.
	 * @return The value of the {@link #LOCATION_PROPERTY} of the given
	 *         {@link Node}.
	 */
	public static Point getLocation(Node node) {
		return (Point) node.getAttributes().get(LOCATION_PROPERTY);
	}

	/**
	 * Returns the value of the {@link #SIZE_PROPERTY} of the given {@link Node}
	 * .
	 * 
	 * @param node
	 *            The {@link Node} whose {@link #SIZE_PROPERTY} is read.
	 * @return The value of the {@link #SIZE_PROPERTY} of the given {@link Node}
	 *         .
	 */
	public static Dimension getSize(Node node) {
		return (Dimension) node.getAttributes().get(SIZE_PROPERTY);
	}

	/**
	 * Returns the value of the {@link #ASPECT_RATIO_PROPERTY} of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} whose {@link #ASPECT_RATIO_PROPERTY} is read.
	 * @return The value of the {@link #ASPECT_RATIO_PROPERTY} of the given
	 *         {@link Node}.
	 */
	public static Double getPreferredAspectRatio(Node node) {
		Object ar = node.getAttributes().get(ASPECT_RATIO_PROPERTY);
		if (ar instanceof Double) {
			return (Double) ar;
		}
		return DEFAULT_ASPECT_RATIO;
	}

	/**
	 * Returns the value of the {@link #RESIZABLE_PROPERTY} of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} whose {@link #RESIZABLE_PROPERTY} is read.
	 * @return The value of the {@link #RESIZABLE_PROPERTY} of the given
	 *         {@link Node}.
	 */
	public static Boolean isResizable(Node node) {
		Object resizable = node.getAttributes().get(RESIZABLE_PROPERTY);
		if (resizable instanceof Boolean) {
			return (Boolean) resizable;
		}
		return DEFAULT_RESIZABLE;
	}

	/**
	 * Returns the value of the {@link #MOVABLE_PROPERTY} of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} whose {@link #MOVABLE_PROPERTY} is read.
	 * @return The value of the {@link #MOVABLE_PROPERTY} of the given
	 *         {@link Node}.
	 */
	public static Boolean isMovable(Node node) {
		Object movable = node.getAttributes().get(MOVABLE_PROPERTY);
		if (movable instanceof Boolean) {
			return (Boolean) movable;
		}
		return DEFAULT_MOVABLE;
	}

	/**
	 * Sets the value of the {@link #LOCATION_PROPERTY} of the given
	 * {@link Node} to the given value.
	 * 
	 * @param node
	 *            The {@link Node} whose {@link #LOCATION_PROPERTY} is changed.
	 * @param location
	 *            The new location for the {@link #LOCATION_PROPERTY} of the
	 *            given {@link Node}.
	 */
	public static void setLocation(Node node, Point location) {
		node.getAttributes().put(LOCATION_PROPERTY, location);
	}

	/**
	 * Sets the value of the {@link #SIZE_PROPERTY} of the given {@link Node} to
	 * the given value.
	 * 
	 * @param node
	 *            The {@link Node} whose {@link #SIZE_PROPERTY} is changed.
	 * @param size
	 *            The new size for the {@link #SIZE_PROPERTY} of the given
	 *            {@link Node}.
	 */
	public static void setSize(Node node, Dimension size) {
		node.getAttributes().put(SIZE_PROPERTY, size);
	}

	/**
	 * Sets the value of the {@link #RESIZABLE_PROPERTY} of the given
	 * {@link Node} to the given value.
	 * 
	 * @param node
	 *            The {@link Node} whose {@link #RESIZABLE_PROPERTY} is changed.
	 * @param resizable
	 *            The new value for the {@link #RESIZABLE_PROPERTY} of the given
	 *            {@link Node}.
	 */
	public static void setResizable(Node node, boolean resizable) {
		node.getAttributes().put(RESIZABLE_PROPERTY, resizable);
	}

	/**
	 * Returns the value of the {@link #WEIGHT_PROPERTY} of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} whose {@link #WEIGHT_PROPERTY} is read.
	 * @return The value of the {@link #WEIGHT_PROPERTY} of the given
	 *         {@link Edge}.
	 */
	public static Double getWeight(Edge edge) {
		Object weight = edge.getAttributes().get(WEIGHT_PROPERTY);
		if (weight instanceof Double) {
			return (Double) weight;
		}
		return DEFAULT_WEIGHT;
	}

	/**
	 * Sets the value of the {@link #WEIGHT_PROPERTY} of the given {@link Edge}
	 * to the given value.
	 * 
	 * @param edge
	 *            The {@link Edge} whose {@link #WEIGHT_PROPERTY} is changed.
	 * @param weight
	 *            The new value for the {@link #WEIGHT_PROPERTY} of the given
	 *            {@link Edge}.
	 */
	public static void setWeight(Edge edge, double weight) {
		edge.getAttributes().put(WEIGHT_PROPERTY, weight);
	}

}
