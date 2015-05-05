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

import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.Point;

import javafx.scene.Node;

/**
 * An {@link FXStaticAnchor} provides a position for each {@link AnchorKey},
 * based on a reference position relative to the anchorage {@link Node}, to
 * which the {@link FXStaticAnchor} is bound, or based on a (global) static
 * reference position in case the {@link FXStaticAnchor} is unbound.
 *
 * @author mwienand
 * @author anyssen
 */
public class FXStaticAnchor extends AbstractFXAnchor {

	// TODO: expose reference position as a property, and make sure positions
	// are re-computed when anchorage or reference position changes.
	private Point referencePosition;

	/**
	 * Creates an {@link FXStaticAnchor} that is bound to the provided
	 * anchorage. It will used the passed in reference position (in the local
	 * coordinate system of the anchorage {@link Node}) to compute positions
	 * (see {@link #positionProperty()}) for all attached {@link AnchorKey}s (in
	 * the local coordinate system of the attached {@link AnchorKey}'s
	 * {@link Node}).
	 * <p>
	 * In case the anchorage {@link Node} or any of its ancestors are changed in
	 * a way that will affect the position, the {@link #positionProperty()} will
	 * be updated.
	 *
	 * @param anchorage
	 *            The anchorage {@link Node} to bind this {@link FXStaticAnchor}
	 *            to.
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
	protected Point computePosition(AnchorKey key) {
		// in case an anchorage is set, the position is interpreted to be in its
		// local coordinate system, so transform it into scene coordinates
		Node anchored = key.getAnchored();
		Node anchorage = getAnchorage();
		Point positionInScene = anchorage == null ? referencePosition
				: JavaFX2Geometry.toPoint(anchorage.localToScene(
						referencePosition.x, referencePosition.y));
		Point positionInAnchoredLocal = JavaFX2Geometry.toPoint(
				anchored.sceneToLocal(positionInScene.x, positionInScene.y));
		return positionInAnchoredLocal;
	}

	@Override
	public String toString() {
		return "FXStaticAnchor[referencePosition = " + referencePosition + "]";
	}

}
