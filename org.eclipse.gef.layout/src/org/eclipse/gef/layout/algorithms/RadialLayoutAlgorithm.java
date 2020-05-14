/*******************************************************************************
 * Copyright (c) 2005, 2017 The Chisel Group and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Casey Best, Ian Bull, Rob Lintern (The Chisel Group) - initial API and implementation
 *               Mateusz Matela - "Tree Views for Zest" contribution, Google Summer of Code 2009
 *               Matthias Wienand (itemis AG) - refactorings
 ******************************************************************************/
package org.eclipse.gef.layout.algorithms;

import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.layout.LayoutProperties;

/**
 * This layout will take the given entities, apply a tree layout to them, and
 * then display the tree in a circular fashion with the roots in the center.
 * 
 * @author Casey Best
 * @author Ian Bull
 * @author Rob Lintern
 * @author Mateusz Matela
 * @author mwienand
 */
public class RadialLayoutAlgorithm implements ILayoutAlgorithm {

	private static final double MAX_DEGREES = Math.PI * 2;
	private double startDegree = 0;
	private double endDegree = MAX_DEGREES;
	private boolean resize = false;
	private TreeLayoutAlgorithm treeLayout = new TreeLayoutAlgorithm();

	/**
	 * Default constructor.
	 */
	public RadialLayoutAlgorithm() {
	}

	public void applyLayout(LayoutContext layoutContext, boolean clean) {
		if (!clean)
			return;
		treeLayout.internalApplyLayout(layoutContext);
		Node[] entities = layoutContext.getNodes();
		Rectangle bounds = LayoutProperties.getBounds(layoutContext.getGraph())
				.getCopy();
		computeRadialPositions(entities, bounds);
		if (resize)
			AlgorithmHelper.maximizeSizes(entities);
		int insets = 4;
		bounds.setX(bounds.getX() + insets);
		bounds.setY(bounds.getY() + insets);
		bounds.setWidth(bounds.getWidth() - 2 * insets);
		bounds.setHeight(bounds.getHeight() - 2 * insets);
		AlgorithmHelper.fitWithinBounds(entities, bounds, resize);
	}

	private void computeRadialPositions(Node[] entities, Rectangle bounds) {
		Rectangle layoutBounds = AlgorithmHelper.getLayoutBounds(entities,
				false);
		layoutBounds.setX(bounds.getX());
		layoutBounds.setWidth(bounds.getWidth());
		for (int i = 0; i < entities.length; i++) {
			Point location = LayoutProperties.getLocation(entities[i]);
			if (layoutBounds.getWidth() == 0 || layoutBounds.getHeight() == 0) {
				location.x = 0;
				location.y = 0;
			} else {
				double percenttheta = (location.x - layoutBounds.getX())
						/ layoutBounds.getWidth();
				double distance = (location.y - layoutBounds.getY())
						/ layoutBounds.getHeight();
				double theta = startDegree
						+ Math.abs(endDegree - startDegree) * percenttheta;
				location.x = distance * Math.cos(theta);
				location.y = distance * Math.sin(theta);
			}
			LayoutProperties.setLocation(entities[i], location);
		}
	}

	/**
	 * Set the range the radial layout will use when
	 * {@link #applyLayout(LayoutContext, boolean)} is called. Both values must
	 * be in radians.
	 * 
	 * @param startDegree
	 *            The start angle for this algorithm (in degree).
	 * @param endDegree
	 *            The end angle for this algorithm (in degree).
	 */
	// TODO: Could use GEF Geometry Angle instead
	public void setRangeToLayout(double startDegree, double endDegree) {
		this.startDegree = startDegree;
		this.endDegree = endDegree;
	}

	/**
	 * 
	 * @return true if this algorithm is set to resize elements
	 */
	public boolean isResizing() {
		return resize;
	}

	/**
	 * 
	 * @param resizing
	 *            true if this algorithm should resize elements (default is
	 *            false)
	 */
	public void setResizing(boolean resizing) {
		resize = resizing;
		treeLayout.setResizing(resize);
	}
}
