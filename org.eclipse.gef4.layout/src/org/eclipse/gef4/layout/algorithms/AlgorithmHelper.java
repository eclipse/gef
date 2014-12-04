/*******************************************************************************
 * Copyright (c) 2005-2010 The Chisel Group and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: The Chisel Group - initial API and implementation
 *               Mateusz Matela 
 *               Ian Bull
 ******************************************************************************/
package org.eclipse.gef4.layout.algorithms;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.layout.LayoutProperties;
import org.eclipse.gef4.layout.interfaces.EntityLayout;

public class AlgorithmHelper {

	public static int MIN_NODE_SIZE = 8;

	public static double PADDING_PERCENT = 0.8;

	/**
	 * Fits given entities within given bounds, preserving their relative
	 * locations.
	 * 
	 * @param entities
	 * @param destinationBounds
	 * @param resize
	 */
	public static void fitWithinBounds(EntityLayout[] entities,
			Rectangle destinationBounds, boolean resize) {
		Rectangle startingBounds = getLayoutBounds(entities, false);
		double sizeScale = Math.min(destinationBounds.getWidth()
				/ startingBounds.getWidth(), destinationBounds.getHeight()
				/ startingBounds.getHeight());
		if (entities.length == 1) {
			fitSingleEntity(entities[0], destinationBounds, resize);
			return;
		}
		for (int i = 0; i < entities.length; i++) {
			EntityLayout entity = entities[i];
			Dimension size = LayoutProperties.getSize(entity);
			if (LayoutProperties.isMovable(entity)) {
				Point location = LayoutProperties.getLocation(entity);
				double percentX = (location.x - startingBounds.getX())
						/ (startingBounds.getWidth());
				double percentY = (location.y - startingBounds.getY())
						/ (startingBounds.getHeight());

				if (resize && LayoutProperties.isResizable(entity)) {
					size.width *= sizeScale;
					size.height *= sizeScale;
					LayoutProperties.setSize(entity, size.width, size.height);
				}

				location.x = destinationBounds.getX() + size.width / 2
						+ percentX
						* (destinationBounds.getWidth() - size.width);
				location.y = destinationBounds.getY() + size.height / 2
						+ percentY
						* (destinationBounds.getHeight() - size.height);
				LayoutProperties.setLocation(entity, location.x, location.y);
			} else if (resize && LayoutProperties.isResizable(entity)) {
				LayoutProperties.setSize(entity, size.width * sizeScale,
						size.height * sizeScale);
			}
		}
	}

	private static void fitSingleEntity(EntityLayout entity,
			Rectangle destinationBounds, boolean resize) {
		if (LayoutProperties.isMovable(entity)) {
			LayoutProperties.setLocation(entity, destinationBounds.getX()
					+ destinationBounds.getWidth() / 2,
					destinationBounds.getY() + destinationBounds.getHeight()
							/ 2);
		}
		if (resize && LayoutProperties.isResizable(entity)) {
			double width = destinationBounds.getWidth();
			double height = destinationBounds.getHeight();
			double preferredAspectRatio = LayoutProperties
					.getPreferredAspectRatio(entity);
			if (preferredAspectRatio > 0) {
				Dimension fixedSize = fixAspectRatio(width, height,
						preferredAspectRatio);
				LayoutProperties.setSize(entity, fixedSize.width,
						fixedSize.height);
			} else {
				LayoutProperties.setSize(entity, width, height);
			}
		}
	}

	/**
	 * Resizes the nodes so that they have a maximal area without overlapping
	 * each other, with additional empty space of 20% of node's width (or
	 * height, if bigger). It does nothing if there's less than two nodes.
	 * 
	 * @param entities
	 */
	public static void maximizeSizes(EntityLayout[] entities) {
		if (entities.length > 1) {
			Dimension minDistance = getMinimumDistance(entities);
			double nodeSize = Math.max(minDistance.width, minDistance.height)
					* PADDING_PERCENT;
			double width = nodeSize;
			double height = nodeSize;
			for (int i = 0; i < entities.length; i++) {
				EntityLayout entity = entities[i];
				if (LayoutProperties.isResizable(entity)) {
					double preferredRatio = LayoutProperties
							.getPreferredAspectRatio(entity);
					if (preferredRatio > 0) {
						Dimension fixedSize = fixAspectRatio(width, height,
								preferredRatio);
						LayoutProperties.setSize(entity, fixedSize.width,
								fixedSize.height);
					} else {
						LayoutProperties.setSize(entity, width, height);
					}
				}
			}
		}
	}

	private static Dimension fixAspectRatio(double width, double height,
			double preferredRatio) {
		double actualRatio = width / height;
		if (actualRatio > preferredRatio) {
			width = height * preferredRatio;
			if (width < MIN_NODE_SIZE) {
				width = MIN_NODE_SIZE;
				height = width / preferredRatio;
			}
		}
		if (actualRatio < preferredRatio) {
			height = width / preferredRatio;
			if (height < MIN_NODE_SIZE) {
				height = MIN_NODE_SIZE;
				width = height * preferredRatio;
			}
		}
		return new Dimension(width, height);
	}

	/**
	 * Find the bounds in which the nodes are located. Using the bounds against
	 * the real bounds of the screen, the nodes can proportionally be placed
	 * within the real bounds. The bounds can be determined either including the
	 * size of the nodes or not. If the size is not included, the bounds will
	 * only be guaranteed to include the center of each node.
	 */
	public static Rectangle getLayoutBounds(EntityLayout[] entities,
			boolean includeNodeSize) {
		double rightSide = Double.NEGATIVE_INFINITY;
		double bottomSide = Double.NEGATIVE_INFINITY;
		double leftSide = Double.POSITIVE_INFINITY;
		double topSide = Double.POSITIVE_INFINITY;
		for (int i = 0; i < entities.length; i++) {
			EntityLayout entity = entities[i];
			Point location = LayoutProperties.getLocation(entity);
			Dimension size = LayoutProperties.getSize(entity);
			if (includeNodeSize) {
				leftSide = Math.min(location.x - size.width / 2, leftSide);
				topSide = Math.min(location.y - size.height / 2, topSide);
				rightSide = Math.max(location.x + size.width / 2, rightSide);
				bottomSide = Math.max(location.y + size.height / 2, bottomSide);
			} else {
				leftSide = Math.min(location.x, leftSide);
				topSide = Math.min(location.y, topSide);
				rightSide = Math.max(location.x, rightSide);
				bottomSide = Math.max(location.y, bottomSide);
			}
		}
		return new Rectangle(leftSide, topSide, rightSide - leftSide,
				bottomSide - topSide);
	}

	/**
	 * minDistance is the closest that any two points are together. These two
	 * points become the center points for the two closest nodes, which we wish
	 * to make them as big as possible without overlapping. This will be the
	 * maximum of minDistanceX and minDistanceY minus a bit, lets say 20%
	 * 
	 * We make the recommended node size a square for convenience.
	 * 
	 * <pre>
	 *    _______
	 *   |       | 
	 *   |       |
	 *   |   +   |
	 *   |   |\  |
	 *   |___|_\_|_____
	 *       | | \     |
	 *       | |  \    |
	 *       +-|---+   |
	 *         |       |
	 *         |_______|
	 * </pre>
	 * 
	 * 
	 */
	public static Dimension getMinimumDistance(EntityLayout[] entities) {
		Dimension horAndVertdistance = new Dimension(Double.MAX_VALUE,
				Double.MAX_VALUE);
		double minDistance = Double.MAX_VALUE;

		// TODO: Very Slow!
		for (int i = 0; i < entities.length; i++) {
			Point location1 = LayoutProperties.getLocation(entities[i]);
			for (int j = i + 1; j < entities.length; j++) {
				Point location2 = LayoutProperties.getLocation(entities[j]);
				double distanceX = location1.x - location2.x;
				double distanceY = location1.y - location2.y;
				double distance = distanceX * distanceX + distanceY * distanceY;

				if (distance < minDistance) {
					minDistance = distance;
					horAndVertdistance.width = Math.abs(distanceX);
					horAndVertdistance.height = Math.abs(distanceY);
				}
			}
		}
		return horAndVertdistance;
	}
}
