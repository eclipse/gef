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
import org.eclipse.gef4.layout.interfaces.EntityLayout;
import org.eclipse.gef4.layout.interfaces.NodeLayout;

public class PropertiesHelper {

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

	// /**
	// * Stores the pruned state of this NodeLayout. A NodeLayout is pruned if
	// it
	// * is contained by a subgraph.
	// */
	// public static final String PRUNED_PROPERTY = "pruned";
	// public static final Boolean DEFAULT_PRUNED = false;

	public static Point getLocation(EntityLayout entity) {
		Object location = entity.getProperty(LOCATION_PROPERTY);
		if (location instanceof Point) {
			return (Point) location;
		}
		return DEFAULT_LOCATION;
	}

	public static Dimension getSize(EntityLayout entity) {
		Object size = entity.getProperty(SIZE_PROPERTY);
		if (size instanceof Dimension) {
			return (Dimension) size;
		}
		return DEFAULT_SIZE;
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
		entity.setProperty(LOCATION_PROPERTY, new Point(x, y));
	}

	public static void setSize(EntityLayout entity, double w, double h) {
		entity.setProperty(SIZE_PROPERTY, new Dimension(w, h));
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

	// public static Boolean isPruned(NodeLayout node) {
	// Object pruned = node.getProperty(PRUNED_PROPERTY);
	// if (pruned instanceof Boolean) {
	// return (Boolean) pruned;
	// }
	// return DEFAULT_PRUNED;
	// }
	public static Boolean isPruned(NodeLayout node) {
		return node.getSubgraph() != null;
	}

	public static void setPrunable(NodeLayout node, boolean prunable) {
		node.setProperty(PRUNABLE_PROPERTY, prunable);
	}

}
