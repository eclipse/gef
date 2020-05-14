/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.providers;

import java.util.List;

import org.eclipse.gef.mvc.fx.handlers.SnapToGrid;
import org.eclipse.gef.mvc.fx.models.SnappingModel.SnappingLocation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;

import javafx.geometry.Orientation;
import javafx.scene.Node;

/**
 * The {@link TopLeftSnappingLocationProvider} returns the top left corner of
 * the bounds as the only snapping location. It can be used, for example, to
 * provide locations for {@link SnapToGrid}.
 */
public class TopLeftSnappingLocationProvider
		extends BoundsSnappingLocationProvider {

	@Override
	public List<SnappingLocation> getHorizontalSnappingLocations(
			IContentPart<? extends Node> part) {
		return getSnappingLocations(part, Orientation.HORIZONTAL, 0d);
	}

	@Override
	public List<SnappingLocation> getVerticalSnappingLocations(
			IContentPart<? extends Node> part) {
		return getSnappingLocations(part, Orientation.VERTICAL, 0d);
	}
}
