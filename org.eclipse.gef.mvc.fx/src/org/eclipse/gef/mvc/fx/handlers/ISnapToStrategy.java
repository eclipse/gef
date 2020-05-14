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
package org.eclipse.gef.mvc.fx.handlers;

import java.util.List;

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.mvc.fx.models.SnappingModel.SnappingLocation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.providers.ISnappingLocationProvider;

import javafx.geometry.Orientation;
import javafx.scene.Node;

/**
 * The {@link ISnapToStrategy} interface defines how snapping is performed. In
 * order to allow arbitrary snapping, horizontal and vertical positions are
 * snapped individually. Therefore, an ISnapTo implementation needs to implement
 * the snapping algorithm for an {@link Orientation} and a position within the
 * coordinate system of the scene.
 *
 * Moreover, snapping does not need to yield a {@link SnappingLocation}, but can
 * be expressed using a {@link Dimension} that provides the offset to the
 * snapping location within the coordinate system of the scene.
 *
 * In order to be able to generate snapping feedback, however, the snapping
 * locations need to be accessible, so that the snapping location for the final
 * position can be highlighted.
 */
public interface ISnapToStrategy {

	/**
	 * Returns the horizontal {@link SnappingLocation}s that are applicable for
	 * the currently snapped part.
	 *
	 * @return The horizontal {@link SnappingLocation}s for the currently
	 *         snapped part.
	 */
	public List<SnappingLocation> getHorizontalTargetLocations();

	/**
	 * Returns the maximum snapping distance for this {@link ISnapToStrategy}.
	 *
	 * @return The maximum snapping distance for this {@link ISnapToStrategy}.
	 */
	public double getMaximumSnappingDistance();

	/**
	 * Returns the currently snapped {@link IContentPart}, or <code>null</code>
	 * if no part is currently snapped.
	 *
	 * @return The currently snapped {@link IContentPart}, or <code>null</code>
	 *         if no part is currently snapped.
	 */
	public IContentPart<? extends Node> getSnappedPart();

	/**
	 * Returns the role used for binding the {@link ISnappingLocationProvider}
	 * that provides the {@link SnappingLocation}s that can be snapped during
	 * interaction.
	 *
	 * @return The role that is used to bind the
	 *         {@link ISnappingLocationProvider} that provides the source
	 *         {@link SnappingLocation}s.
	 */
	public String getSourceLocationProviderRole();

	/**
	 * Returns the vertical {@link SnappingLocation}s that are applicable for
	 * the currently snapped part.
	 *
	 * @return The vertical {@link SnappingLocation}s for the currently snapped
	 *         part.
	 */
	public List<SnappingLocation> getVerticalTargetLocations();

	/**
	 * Initiates snapping of the given {@link IContentPart}.
	 *
	 * @param snappedPart
	 *            The {@link IContentPart} we prepare snapping for.
	 */
	public void setSnappedPart(IContentPart<? extends Node> snappedPart);

	/**
	 * Returns a {@link Dimension} that represents the offset to a snapping
	 * location that is closest to the given position (within the coordinate
	 * system of the scene).
	 *
	 * @param orientation
	 *            The {@link Orientation} for snapping.
	 * @param positionInScene
	 *            The position coordinate within the coordinate system of the
	 *            scene.
	 * @return A {@link Dimension} that represents the offset to a snapping
	 *         location.
	 */
	public Dimension snap(Orientation orientation, double positionInScene);
}
