/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.anchors;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.Point;

/**
 * A {@link FXStaticAnchor} provides a static position per anchor link.
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class FXStaticAnchor extends AbstractFXAnchor {

	// TODO: expose reference position as a property, and make sure positions
	// are re-compute when anchorage or reference position changes.
	private Point referencePosition;

	/**
	 * Creates an {@link FXStaticAnchor} that is bound to the provided
	 * anchorage. It will provide the passed in position (in the local
	 * coordinate system of the anchorage {@link Node}) for all attached
	 * {@link AnchorKey}s (i.e anchored {@link Node}s), after having transformed
	 * them into scene coordinates. In case the anchorage {@link Node} or any of
	 * its ancestors are changed in a way that will affect the position, the
	 * {@link #positionProperty()} will be updated.
	 *
	 * @param anchorage
	 * @param referencePositionInAnchorageLocal
	 *            The position within the local coordinate space of the
	 *            anchorage {@link Node}, which is used to compute the position
	 *            (in scene coordinates) for all attached {@link AnchorKey}s.
	 */
	public FXStaticAnchor(Node anchorage,
			Point referencePositionInAnchorageLocal) {
		super(anchorage);
		this.referencePosition = referencePositionInAnchorageLocal;
	}

	/**
	 * Creates an {@link FXStaticAnchor} that is not bound to an anchorage
	 * {@link Node} and will always provide the passed in position (in scene
	 * coordinates) for all attached {@link AnchorKey}s (i.e. anchored
	 * {@link Node}s).
	 *
	 * @param referencePositionInScene
	 *            The position in scene coordinates to be provided for all
	 *            attached {@link AnchorKey}s.
	 */
	public FXStaticAnchor(Point referencePositionInScene) {
		super(null);
		this.referencePosition = referencePositionInScene;
	}

	@Override
	protected void recomputePositions(Node anchored) {
		// in case an anchorage is set, the position is interpreted to be in its
		// local coordinate system, so transform it into scene coordinates
		Node anchorage = getAnchorage();
		Point positionInScene = anchorage == null ? referencePosition
				: JavaFX2Geometry.toPoint(anchorage.localToScene(
						referencePosition.x, referencePosition.y));
		for (AnchorKey key : getKeys().get(anchored)) {
			positionProperty().put(key, positionInScene);
		}
	}
}
