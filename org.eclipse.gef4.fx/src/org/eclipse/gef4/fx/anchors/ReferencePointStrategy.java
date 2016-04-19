/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.anchors;

import java.util.Collections;
import java.util.Set;

import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.planar.Point;

import javafx.scene.Node;

/**
 * A computation strategy that is based on a single reference point.
 *
 * @author anyssen
 *
 */
public class ReferencePointStrategy extends AbstractComputationStrategy {

	@Override
	public Point computePositionInScene(Node anchorage, Node anchored,
			Set<Parameter<?>> parameters) {
		Point referencePointInAnchorageLocal = getParameter(parameters,
				AnchorageReferencePosition.class).get();
		return FX2Geometry.toPoint(
				anchorage.localToScene(referencePointInAnchorageLocal.x,
						referencePointInAnchorageLocal.y));
	}

	@Override
	public Set<Class<? extends Parameter<?>>> getRequiredParameters() {
		return Collections.<Class<? extends Parameter<?>>> singleton(
				AnchorageReferencePosition.class);
	}

}
