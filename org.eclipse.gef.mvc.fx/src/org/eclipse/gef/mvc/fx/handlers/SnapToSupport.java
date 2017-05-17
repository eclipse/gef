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
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.common.collections.CollectionUtils;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.fx.behaviors.SnappingBehavior;
import org.eclipse.gef.mvc.fx.models.SnappingModel;
import org.eclipse.gef.mvc.fx.models.SnappingModel.SnappingLocation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.providers.ISnappingLocationProvider;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;

/**
 * The {@link SnapToSupport} can be used to perform snapping during manipulation
 * of an {@link IContentPart}. The {@link ISnapToStrategy}s are queried from the
 * {@link SnappingModel}. If there are {@link ISnapToStrategy}s that are always
 * active, then these are run exclusively, in the order present. Only if there
 * are no "always-enabled" {@link ISnapToStrategy}s, then all
 * {@link ISnapToStrategy}s contribute to snapping by providing
 * {@link SnappingLocation}s for the {@link SnappingLocation}s of the currently
 * snapped part. The closest {@link SnappingLocation} is used as the snapping
 * target. All {@link SnappingLocation}s that match the final position are put
 * into the {@link SnappingModel} so that {@link SnappingBehavior} can provide
 * feedback for them.
 */
public class SnapToSupport extends IAdaptable.Bound.Impl<IViewer> {

	/**
	 * Globally stores the {@link SnapToGrid} strategy.
	 */
	public static final SnapToGrid SNAP_TO_GRID_STRATEGY = new SnapToGrid();

	/**
	 * Globally stores the {@link SnapToGeometry} strategy.
	 */
	public static final SnapToGeometry SNAP_TO_GEOMETRY_STRATEGY = new SnapToGeometry();

	private ObservableList<ISnapToStrategy> supportedStrategies = CollectionUtils
			.observableArrayList();

	// /**
	// * Name of the property storing supported strategies.
	// */
	// public static final String SUPPORTED_STRATEGIES_PROPERTY =
	// "supportedSnapToStrategies";
	// private ObservableList<ISnapToStrategy> supportedStrategiesUnmodifiable =
	// FXCollections
	// .unmodifiableObservableList(supportedStrategies);
	// private ReadOnlyListWrapper<ISnapToStrategy> supportedStrategiesProperty
	// = new ReadOnlyListWrapperEx<>(
	// this, SUPPORTED_STRATEGIES_PROPERTY,
	// supportedStrategiesUnmodifiable);

	private IContentPart<? extends Node> snappedPart;
	private List<ISnapToStrategy> applicableSnapToStrategies = new ArrayList<>();
	private Map<ISnapToStrategy, List<SnappingLocation>> hSourceLocations = new IdentityHashMap<>();
	private Map<ISnapToStrategy, List<SnappingLocation>> vSourceLocations = new IdentityHashMap<>();

	/**
	 *
	 */
	public SnapToSupport() {
	}

	/**
	 * @param snapTos
	 *            The {@link ISnapToStrategy ISnapToStrategies} for this
	 *            {@link SnapToSupport}.
	 */
	public SnapToSupport(ISnapToStrategy... snapTos) {
		supportedStrategies.addAll(Arrays.asList(snapTos));
	}

	private Dimension determineMinimum(ISnapToStrategy snapper,
			List<SnappingLocation> locs, Dimension delta) {
		Dimension min = null;
		double minLenSq = 0d;
		if (locs != null) {
			for (SnappingLocation sl : locs) {
				double d = sl.getOrientation() == Orientation.HORIZONTAL
						? delta.width : delta.height;
				Dimension snapDelta = snapper.snap(sl.getOrientation(),
						sl.getPositionInScene() + d);
				if (snapDelta != null) {
					double lenSq = snapDelta.width * snapDelta.width
							+ snapDelta.height * snapDelta.height;
					if (min == null || lenSq < minLenSq) {
						min = snapDelta;
						minLenSq = lenSq;
					}
				}
			}
		}
		return min;
	}

	/**
	 * Returns a {@link ReadOnlyListProperty} of supported
	 * {@link ISnapToStrategy}.
	 *
	 * @return A {@link ReadOnlyListProperty} of supported
	 *         {@link ISnapToStrategy}.
	 */
	public ObservableList<ISnapToStrategy> getSupportedStrategies() {
		return supportedStrategies;
	}

	private List<SnappingLocation> getTranslated(
			Map<ISnapToStrategy, List<SnappingLocation>> locs, Dimension delta,
			Dimension snapDelta) {
		List<SnappingLocation> translated = new ArrayList<>();
		for (Entry<ISnapToStrategy, List<SnappingLocation>> e : locs
				.entrySet()) {
			for (SnappingLocation sl : e.getValue()) {
				double p = sl.getPositionInScene();
				double np = sl.getOrientation() == Orientation.HORIZONTAL
						? p + delta.width + snapDelta.width
						: p + delta.height + snapDelta.height;
				translated.add(new SnappingLocation(sl.getPart(),
						sl.getOrientation(), np));
			}
		}
		return translated;
	}

	/**
	 * <ol>
	 * <li>Iterate applicable snappers.
	 * <li>Iterate snapped locations.
	 * <li>Snap SL + Delta => S-Delta.
	 * <li>Record SL with min. S-Delta.
	 * <li>Translate snapped locations.
	 * <li>Iterate all snappers.
	 * <li>Find matching snapper SLs.
	 * <li>Update SnappingModel.
	 * <li>Compute composite delta.
	 * </ol>
	 *
	 * @param delta
	 *            The {@link Dimension} representing a translation that should
	 *            be applied to the currently snapped part.
	 * @return A {@link Dimension} representing a translation that should be
	 *         applied additional to the given delta {@link Dimension} in order
	 *         to place the snapped part at a snapping location (if snapped).
	 */
	public Dimension snap(Dimension delta) {
		// 1. iterate applicable snappers
		Dimension totalHMin = null;
		Dimension totalVMin = null;
		for (ISnapToStrategy strategy : applicableSnapToStrategies) {
			// 2. + 3. iterate hsl and vsl; snap SL + Delta => S-Delta
			Dimension hmin = determineMinimum(strategy,
					hSourceLocations.get(strategy), delta);
			Dimension vmin = determineMinimum(strategy,
					vSourceLocations.get(strategy), delta);

			// 4. keep track of minimum
			if (totalHMin == null) {
				if (hmin != null && Math.abs(hmin.width) < strategy
						.getMaximumSnappingDistance()) {
					totalHMin = hmin;
				}
			} else if (hmin != null
					&& Math.abs(hmin.width) < Math.abs(totalHMin.width)) {
				totalHMin = hmin;
			}
			if (totalVMin == null) {
				if (vmin != null && Math.abs(vmin.height) < strategy
						.getMaximumSnappingDistance()) {
					totalVMin = vmin;
				}
			} else if (vmin != null
					&& Math.abs(vmin.height) < Math.abs(totalVMin.height)) {
				totalVMin = vmin;
			}
		}

		// 5. translate snapped locations
		List<SnappingLocation> hTranslated = totalHMin == null
				? Collections.emptyList()
				: getTranslated(hSourceLocations, delta, totalHMin);
		List<SnappingLocation> vTranslated = totalVMin == null
				? Collections.emptyList()
				: getTranslated(vSourceLocations, delta, totalVMin);

		// 6. iterate all snappers
		List<SnappingLocation> matchingHSLs = new ArrayList<>();
		List<SnappingLocation> matchingVSLs = new ArrayList<>();
		for (ISnapToStrategy snapper : supportedStrategies) {
			List<SnappingLocation> horizontalSnappingLocations = snapper
					.getHorizontalTargetLocations();
			List<SnappingLocation> verticalSnappingLocations = snapper
					.getVerticalTargetLocations();

			// 7. find matching SLs for translated source SLs
			for (SnappingLocation mySL : hTranslated) {
				for (SnappingLocation sl : horizontalSnappingLocations) {
					if (mySL.getPositionInScene() == sl.getPositionInScene()) {
						matchingHSLs.add(sl);
					}
				}
			}
			for (SnappingLocation mySL : vTranslated) {
				for (SnappingLocation sl : verticalSnappingLocations) {
					if (mySL.getPositionInScene() == sl.getPositionInScene()) {
						matchingVSLs.add(sl);
					}
				}
			}
		}

		// 8. update SnappingModel
		SnappingModel snappingModel = snappedPart.getViewer()
				.getAdapter(SnappingModel.class);
		List<SnappingLocation> feedbackLocs = new ArrayList<>();
		feedbackLocs.addAll(matchingHSLs);
		feedbackLocs.addAll(matchingVSLs);
		snappingModel.setSnappingLocations(feedbackLocs);

		// 9. compute composite delta
		Dimension composite = new Dimension(0, 0);
		if (totalHMin != null) {
			composite.setWidth(totalHMin.width);
		}
		if (totalVMin != null) {
			composite.setHeight(totalVMin.height);
		}
		return composite;
	}

	/**
	 * Initializes
	 *
	 * @param snappedPart
	 *            The {@link IContentPart} that might be snapped.
	 */
	public void startSnapping(IContentPart<? extends Node> snappedPart) {
		if (snappedPart == null) {
			throw new IllegalArgumentException("snappedPart may not be null");
		}

		this.snappedPart = snappedPart;

		// determine providers for strategies
		applicableSnapToStrategies.clear();
		hSourceLocations.clear();
		vSourceLocations.clear();

		for (ISnapToStrategy strategy : supportedStrategies) {
			ISnappingLocationProvider slocProvider = snappedPart
					.getAdapter(AdapterKey.get(ISnappingLocationProvider.class,
							strategy.getSourceLocationProviderRole()));
			if (slocProvider != null) {
				applicableSnapToStrategies.add(strategy);
				strategy.setSnappedPart(snappedPart);
				List<SnappingLocation> hsls = slocProvider
						.getHorizontalSnappingLocations(snappedPart);
				hSourceLocations.put(strategy, new ArrayList<>(hsls));
				List<SnappingLocation> vsls = slocProvider
						.getVerticalSnappingLocations(snappedPart);
				vSourceLocations.put(strategy, new ArrayList<>(vsls));
			} else {
				strategy.setSnappedPart(null);
			}
		}

		SnappingModel snappingModel = snappedPart.getViewer()
				.getAdapter(SnappingModel.class);
		snappingModel.setSnappingLocations(
				Collections.<SnappingLocation> emptyList());
	}

	/**
	 *
	 * @param snappedPart
	 *            The snapped {@link IContentPart}
	 * @param snappableLocationInScene
	 *            The location that is manipulated
	 */
	public void startSnapping(IContentPart<? extends Node> snappedPart,
			Point snappableLocationInScene) {
		if (snappedPart == null) {
			throw new IllegalArgumentException("snappedPart may not be null");
		}

		this.snappedPart = snappedPart;

		// determine providers for strategies
		applicableSnapToStrategies.clear();
		hSourceLocations.clear();
		vSourceLocations.clear();

		SnappingLocation sourceH = new SnappingLocation(snappedPart,
				Orientation.HORIZONTAL, snappableLocationInScene.x);
		SnappingLocation sourceV = new SnappingLocation(snappedPart,
				Orientation.VERTICAL, snappableLocationInScene.y);

		for (ISnapToStrategy strategy : supportedStrategies) {
			applicableSnapToStrategies.add(strategy);
			strategy.setSnappedPart(snappedPart);
			hSourceLocations.put(strategy,
					new ArrayList<>(Arrays.asList(sourceH)));
			vSourceLocations.put(strategy,
					new ArrayList<>(Arrays.asList(sourceV)));
		}

		SnappingModel snappingModel = snappedPart.getViewer()
				.getAdapter(SnappingModel.class);
		snappingModel.setSnappingLocations(
				Collections.<SnappingLocation> emptyList());
	}

	/**
	 * Clears the snapping locations and the SnapToLocationModel.
	 */
	public void stopSnapping() {
		if (snappedPart != null) {
			SnappingModel snappingModel = snappedPart.getViewer()
					.getAdapter(SnappingModel.class);
			snappingModel.setSnappingLocations(
					Collections.<SnappingLocation> emptyList());
			snappedPart = null;
		}
		applicableSnapToStrategies.clear();
		hSourceLocations.clear();
		vSourceLocations.clear();
	}
}
