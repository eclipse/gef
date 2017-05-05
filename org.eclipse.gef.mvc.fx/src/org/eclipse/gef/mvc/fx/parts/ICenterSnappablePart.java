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

import org.eclipse.gef.mvc.fx.models.SnappingModel.SnappingLocation;

import javafx.geometry.Orientation;
import javafx.scene.Node;

/**
 * The {@link ICenterSnappablePart} is an {@link ISnappablePart} that provides
 * its center position as the horizontal and vertical snapping locations, per
 * default.
 *
 * @param <V>
 *            The visual type parameter.
 */
public interface ICenterSnappablePart<V extends Node>
		extends ISnappablePart<V> {

	@Override
	default List<SnappingLocation> getHorizontalSnappingLocations() {
		return SnappingUtil.computeSnappingLocations(this,
				Orientation.HORIZONTAL, 0.5d);
	}

	@Override
	public default java.util.List<SnappingLocation> getVerticalSnappingLocations() {
		return SnappingUtil.computeSnappingLocations(this, Orientation.VERTICAL,
				0.5d);
	}

}
