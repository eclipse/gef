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
package org.eclipse.gef.mvc.fx.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.mvc.fx.models.SnappingModel.SnappingLocation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.parts.PartUtils;
import org.eclipse.gef.mvc.fx.providers.ISnappingLocationProvider;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.scene.Node;

/**
 * The {@link AbstractSnapTo} class provides basic functionality for an
 * {@link ISnapToStrategy} implementation, i.e. storage for the
 * {@link SnappingLocation}s and the currently snapped part, and filtering of
 * {@link IContentPart}s to determine the snapping-relevant parts.
 */
public abstract class AbstractSnapTo implements ISnapToStrategy {

	/**
	 * The default value for the {@link #getMaximumSnappingDistance()} property.
	 */
	public static final double MAX_SNAPPING_DISTANCE_DEFAULT = 15d;

	private double maxSnappingDistance = MAX_SNAPPING_DISTANCE_DEFAULT;
	private IContentPart<? extends Node> snappedPart;
	private List<SnappingLocation> xLocations = new ArrayList<>();
	private List<SnappingLocation> yLocations = new ArrayList<>();

	/**
	 * Determines the horizontal {@link SnappingLocation}s for the given
	 * {@link IContentPart}. By default, the part, and all its parent hierarchy
	 * up to the root part, is asked for an {@link ISnappingLocationProvider}
	 * that is used to query the {@link SnappingLocation}s for the given
	 * {@link IContentPart}.
	 *
	 * @param rp
	 *            The {@link IContentPart} for which to determine the horizontal
	 *            {@link SnappingLocation}s.
	 * @return The horizontal {@link SnappingLocation}s for the given
	 *         {@link IContentPart}.
	 */
	protected Collection<? extends SnappingLocation> determineHorizontalSnappingLocations(
			IContentPart<? extends Node> rp) {
		ISnappingLocationProvider snappingLocationProvider = getSnappingLocationProvider(
				rp);
		if (snappingLocationProvider == null) {
			return Collections.emptyList();
		}
		return snappingLocationProvider.getHorizontalSnappingLocations(rp);
	}

	/**
	 * Returns the default maximum snapping distance for this
	 * {@link ISnapToStrategy}.
	 *
	 * @return The default maximum snapping distance for this
	 *         {@link ISnapToStrategy}.
	 */
	protected double determineMaximumSnappingDistance() {
		return MAX_SNAPPING_DISTANCE_DEFAULT;
	}

	/**
	 * Determines the vertical {@link SnappingLocation}s for the given
	 * {@link IContentPart}. By default, the part, and all its parent hierarchy
	 * up to the root part, is asked for an {@link ISnappingLocationProvider}
	 * that is used to query the {@link SnappingLocation}s for the given
	 * {@link IContentPart}.
	 *
	 * @param rp
	 *            The {@link IContentPart} for which to determine the vertical
	 *            {@link SnappingLocation}s.
	 * @return The vertical {@link SnappingLocation}s for the given
	 *         {@link IContentPart}.
	 */
	protected Collection<? extends SnappingLocation> determineVerticalSnappingLocations(
			IContentPart<? extends Node> rp) {
		ISnappingLocationProvider snappingLocationProvider = getSnappingLocationProvider(
				rp);
		if (snappingLocationProvider == null) {
			return Collections.emptyList();
		}
		return snappingLocationProvider.getVerticalSnappingLocations(rp);
	}

	@Override
	public List<SnappingLocation> getHorizontalSnappingLocations() {
		return xLocations;
	}

	@Override
	public double getMaximumSnappingDistance() {
		return maxSnappingDistance;
	}

	@Override
	public IContentPart<? extends Node> getSnappedPart() {
		return snappedPart;
	}

	private ISnappingLocationProvider getSnappingLocationProvider(
			IContentPart<? extends Node> part) {
		String role = getSnappingLocationProviderRole();
		IVisualPart<? extends Node> current = part;
		ISnappingLocationProvider slp = null;
		if (role != null) {
			while (slp == null && current != null) {
				slp = current.getAdapter(
						AdapterKey.get(ISnappingLocationProvider.class, role));
				current = current.getParent();
			}
		}
		return slp;
	}

	/**
	 * Returns the role for the adapter binding providing the
	 * {@link ISnappingLocationProvider} for this {@link ISnapToStrategy}.
	 *
	 * @return the role for the adapter binding providing the
	 *         {@link ISnappingLocationProvider} for this
	 *         {@link ISnapToStrategy}.
	 */
	protected abstract String getSnappingLocationProviderRole();

	@Override
	public List<SnappingLocation> getVerticalSnappingLocations() {
		return yLocations;
	}

	/**
	 * Determines if the given {@link IContentPart} should participate in
	 * snapping. This callback method is called for all parts implementing
	 * {@link IContentPart} within the {@link IViewer} of the currently snapped
	 * part, except for the currently snapped part.
	 *
	 * By default, all {@link IContentPart}s are considered for snapping, i.e.
	 * this method returns <code>true</code> regardless of its input.
	 *
	 * @param part
	 *            The {@link IContentPart} for which snapping participation is
	 *            evaluated.
	 * @return <code>true</code> to indicate that the given {@link IContentPart}
	 *         should participate in snapping, <code>false</code> otherwise.
	 */
	protected boolean isRelevant(IContentPart<? extends Node> part) {
		return true;
	}

	@Override
	public void setMaximumSnappingDistance(double distance) {
		maxSnappingDistance = distance;
	}

	@Override
	public void setSnappedPart(IContentPart<? extends Node> snappedPart) {
		this.snappedPart = snappedPart;
		xLocations.clear();
		yLocations.clear();
		if (snappedPart != null) {
			List<IContentPart<? extends Node>> relevantParts = PartUtils
					.filterParts(
							snappedPart.getViewer().getContentPartMap()
									.values(),
							(p) -> p != snappedPart && p instanceof IContentPart
									&& isRelevant(
											(IContentPart<? extends Node>) p));
			for (IContentPart<? extends Node> rp : relevantParts) {
				xLocations.addAll(determineHorizontalSnappingLocations(rp));
				yLocations.addAll(determineVerticalSnappingLocations(rp));
			}
			setMaximumSnappingDistance(determineMaximumSnappingDistance());
		}
	}
}
