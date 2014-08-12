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

	private Point position;

	public FXStaticAnchor(Node anchorage, Point positionInAnchorageLocal) {
		super(anchorage);
		this.position = positionInAnchorageLocal;
	}

	public FXStaticAnchor(Point positionInScene) {
		super(null);
		this.position = positionInScene;
	}

	@Override
	protected void recomputePositions(Node anchored) {
		// in case an anchorage is set, the position is interpreted to be in its
		// local coordinate system, so transform it into scene coordinates
		Node anchorage = getAnchorage();
		Point positionInScene = anchorage == null ? position : JavaFX2Geometry
				.toPoint(anchorage.localToScene(position.x, position.y));
		for (AnchorKey key : getKeys().get(anchored)) {
			positionProperty().put(key, positionInScene);
		}
	}
}
