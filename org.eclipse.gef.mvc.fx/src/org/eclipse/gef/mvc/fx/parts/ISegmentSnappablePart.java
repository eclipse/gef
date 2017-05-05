/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import java.util.List;

import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.mvc.fx.models.SnappingModel.SnappingLocation;

import javafx.geometry.Orientation;

/**
 * The {@link ISegmentSnappablePart} is an {@link ISnappablePart} that provides
 * that start/end locations of its segments as its snapping locations.
 *
 * @param <V>
 *            Specifies the visual type.
 */
public interface ISegmentSnappablePart<V extends Connection>
		extends ISnappablePart<V> {

	@Override
	default List<SnappingLocation> getHorizontalSnappingLocations() {
		return SnappingUtil.computeSnappingLocations(this,
				Orientation.HORIZONTAL);
	}

	@Override
	default List<SnappingLocation> getVerticalSnappingLocations() {
		return SnappingUtil.computeSnappingLocations(this,
				Orientation.VERTICAL);
	}
}
