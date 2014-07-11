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

import org.eclipse.gef4.geometry.planar.Point;

/**
 * A {@link FXStaticAnchor} provides a static position per anchor link.
 * 
 * @author mwienand
 * 
 */
public class FXStaticAnchor extends AbstractFXAnchor {

	public FXStaticAnchor(AnchorKey key, Point position) {
		this(null, null, key, position);
	}

	public FXStaticAnchor(Node anchorage, RootNodeProvider rootNodeProvider,
			AnchorKey key, Point position) {
		super(anchorage, rootNodeProvider);
		positionProperty().put(key, position);
	}

	@Override
	protected void recomputePositions() {
		// nothing to compute (*static* anchor)
	}

}
