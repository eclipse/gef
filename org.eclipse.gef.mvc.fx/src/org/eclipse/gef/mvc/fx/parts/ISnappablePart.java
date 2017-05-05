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
 * @param <V>
 *            The visual type parameter.
 */
public interface ISnappablePart<V extends Node> extends IContentPart<V> {

	/**
	 * Returns a list of horizontal {@link SnappingLocation}s for this part.
	 *
	 * @return a list of horizontal {@link SnappingLocation}s for this part.
	 */
	default public List<SnappingLocation> getHorizontalSnappingLocations() {
		return SnappingUtil.computeSnappingLocations(this,
				Orientation.HORIZONTAL, 0d, 0.5d, 1d);
	}

	/**
	 * Returns a list of vertical {@link SnappingLocation}s for this part.
	 *
	 * @return a list of vertical {@link SnappingLocation}s for this part.
	 */
	default public List<SnappingLocation> getVerticalSnappingLocations() {
		return SnappingUtil.computeSnappingLocations(this, Orientation.VERTICAL,
				0d, 0.5d, 1d);
	}
}
