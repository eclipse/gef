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
package org.eclipse.gef4.mvc.fx.policies;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.policies.AbstractResizeRelocateAnchoragesOnHandleDragPolicy;

public class FXResizeRelocateAnchoragesOnHandleDragPolicy extends
		AbstractResizeRelocateAnchoragesOnHandleDragPolicy<Node> {

	private ReferencePoint refPoint;

	public FXResizeRelocateAnchoragesOnHandleDragPolicy(ReferencePoint refPoint) {
		this.refPoint = refPoint;
	}

	@Override
	protected Rectangle getVisualBounds(IContentPart<Node> contentPart) {
		if (contentPart == null) {
			throw new IllegalArgumentException("contentPart may not be null!");
		}
		return JavaFX2Geometry.toRectangle(contentPart.getVisual()
				.localToScene(contentPart.getVisual().getBoundsInLocal()));
	}

	@Override
	protected ReferencePoint getReferencePoint() {
		return refPoint;
	}

}
