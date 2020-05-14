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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.mvc.fx.models.SnappingModel.SnappingLocation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;

import javafx.scene.Node;

/**
 * The {@link ISnappingLocationProvider} is used to determine
 * {@link SnappingLocation}s for an {@link IContentPart}.
 */
public interface ISnappingLocationProvider {

	/**
	 * Combines the given {@link ISnappingLocationProvider}s by wrapping them in
	 * a new {@link ISnappingLocationProvider} that returns a combination of all
	 * {@link SnappingLocation}s that are returned by the individual providers.
	 *
	 * @param providers
	 *            The {@link ISnappingLocationProvider}s to combine.
	 * @return A new {@link ISnappingLocationProvider} that represents the
	 *         combination of the given providers.
	 */
	public static ISnappingLocationProvider union(
			List<ISnappingLocationProvider> providers) {
		return new ISnappingLocationProvider() {
			@Override
			public List<SnappingLocation> getHorizontalSnappingLocations(
					IContentPart<? extends Node> part) {
				List<SnappingLocation> hsls = new ArrayList<>();
				for (ISnappingLocationProvider p : providers) {
					hsls.addAll(p.getHorizontalSnappingLocations(part));
				}
				return hsls;
			}

			@Override
			public List<SnappingLocation> getVerticalSnappingLocations(
					IContentPart<? extends Node> part) {
				List<SnappingLocation> vsls = new ArrayList<>();
				for (ISnappingLocationProvider p : providers) {
					vsls.addAll(p.getVerticalSnappingLocations(part));
				}
				return vsls;
			}
		};
	}

	/**
	 * Returns the horizontal {@link SnappingLocation}s for the given
	 * {@link IContentPart}.
	 *
	 * @param part
	 *            The {@link IContentPart} for which to compute the
	 *            {@link SnappingLocation}s.
	 * @return A {@link List} of all horizontal {@link SnappingLocation}s for
	 *         the given {@link IContentPart}.
	 */
	public List<SnappingLocation> getHorizontalSnappingLocations(
			IContentPart<? extends Node> part);

	/**
	 * Returns the vertical {@link SnappingLocation}s for the given
	 * {@link IContentPart}.
	 *
	 * @param part
	 *            The {@link IContentPart} for which to compute the
	 *            {@link SnappingLocation}s.
	 * @return A {@link List} of all vertical {@link SnappingLocation}s for the
	 *         given {@link IContentPart}.
	 */
	public List<SnappingLocation> getVerticalSnappingLocations(
			IContentPart<? extends Node> part);
}
