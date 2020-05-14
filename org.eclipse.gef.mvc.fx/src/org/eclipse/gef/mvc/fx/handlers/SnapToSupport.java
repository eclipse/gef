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

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.mvc.fx.behaviors.SnappingBehavior;
import org.eclipse.gef.mvc.fx.models.SnappingModel;
import org.eclipse.gef.mvc.fx.models.SnappingModel.SnappingLocation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.providers.ISnappingLocationProvider;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

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

	private IContentPart<? extends Node> snappedPart;
	private List<ISnapToStrategy> supportedSnapToStrategies = new ArrayList<>();
	private List<ISnapToStrategy> applicableSnapToStrategies = new ArrayList<>();
	private Map<ISnapToStrategy, List<SnappingLocation>> hSourceLocations = new IdentityHashMap<>();
	private Map<ISnapToStrategy, List<SnappingLocation>> vSourceLocations = new IdentityHashMap<>();

	/**
	 * Constructs a new {@link SnapToSupport}.
	 */
	public SnapToSupport() {
	}

	/**
	 * Clear snapping feedback.
	 */
	public void clearSnappingFeedback() {
		// XXX: SnappingModel is only altered during interaction, therefore,
		// we do not need to carry these changes out via operations.
		SnappingModel snappingModel = getSnappingModel();
		if (snappingModel != null) {
			snappingModel.setSnappingLocations(
					Collections.<SnappingLocation> emptyList());
		}
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

	private SnappingModel getSnappingModel() {
		return getAdaptable().getAdapter(SnappingModel.class);
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

	private void initializeLocations(
			Map<ISnapToStrategy, List<SnappingLocation>> horizontalSourceSnappingLocations,
			Map<ISnapToStrategy, List<SnappingLocation>> verticalSourceSnappingLocations) {
		// clear state
		applicableSnapToStrategies.clear();
		hSourceLocations.clear();
		vSourceLocations.clear();

		// compute applicable strategies and corresponding source locations
		for (ISnapToStrategy strategy : supportedSnapToStrategies) {
			if (horizontalSourceSnappingLocations.containsKey(strategy)
					|| verticalSourceSnappingLocations.containsKey(strategy)) {
				applicableSnapToStrategies.add(strategy);
				strategy.setSnappedPart(snappedPart);
				List<SnappingLocation> hssls = horizontalSourceSnappingLocations
						.get(strategy);
				if (hssls != null && !hssls.isEmpty()) {
					hSourceLocations.put(strategy, new ArrayList<>(hssls));
				}
				List<SnappingLocation> vssls = verticalSourceSnappingLocations
						.get(strategy);
				if (vssls != null && !vssls.isEmpty()) {
					vSourceLocations.put(strategy, new ArrayList<>(vssls));
				}
			}
		}

		// clear snapping model
		// XXX: SnappingModel is only altered during interaction, therefore, we
		// do not need to carry these changes out via operations.
		SnappingModel snappingModel = getSnappingModel();
		if (snappingModel != null) {
			snappingModel.setSnappingLocations(
					Collections.<SnappingLocation> emptyList());
		}
	}

	private void initializePartAndStrategies(
			IContentPart<? extends Node> snappedPart) {
		// check that snapped part is given
		if (snappedPart == null) {
			throw new IllegalArgumentException("snappedPart may not be null");
		}

		// save snapped part
		this.snappedPart = snappedPart;

		// save snap-to strategies
		supportedSnapToStrategies.clear();
		SnappingModel snappingModel = getSnappingModel();
		if (snappingModel != null) {
			supportedSnapToStrategies
					.addAll(snappingModel.snapToStrategiesProperty());
		}
	}

	/**
	 * <ol>
	 * <li>Iterate only the applicable strategies.
	 * <li>Iterate source locations.
	 * <li>Snap source location + Delta to get the snap-delta.
	 * <li>Record location with minimal snap-delta.
	 * <li>Translate snapped locations.
	 * <li>Iterate all (i.e. supported) strategies.
	 * <li>Find matching snapping locations.
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
		for (ISnapToStrategy snapper : supportedSnapToStrategies) {
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
		// XXX: SnappingModel is only altered during interaction, therefore,
		// we do not need to carry these changes out via operations.
		SnappingModel snappingModel = getSnappingModel();
		if (snappingModel != null) {
			List<SnappingLocation> feedbackLocs = new ArrayList<>();
			feedbackLocs.addAll(matchingHSLs);
			feedbackLocs.addAll(matchingVSLs);
			snappingModel.setSnappingLocations(feedbackLocs);
		}

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
	 * Initializes this {@link SnapToSupport} for performing snapping of the
	 * given {@link IContentPart}. The source snapping locations for the
	 * individual {@link ISnapToStrategy ISnapToStrategies} are determined by
	 * corresponding {@link ISnappingLocationProvider}s that are registered as
	 * adapters at the given snapped part.
	 *
	 * @param snappedPart
	 *            The {@link IContentPart} that might be snapped.
	 */
	public void startSnapping(IContentPart<? extends Node> snappedPart) {
		initializePartAndStrategies(snappedPart);

		// collect source locations using providers
		Map<ISnapToStrategy, List<SnappingLocation>> hsrc = new IdentityHashMap<>();
		Map<ISnapToStrategy, List<SnappingLocation>> vsrc = new IdentityHashMap<>();
		for (ISnapToStrategy strategy : supportedSnapToStrategies) {
			ISnappingLocationProvider slocProvider = snappedPart
					.getAdapter(AdapterKey.get(ISnappingLocationProvider.class,
							strategy.getSourceLocationProviderRole()));
			if (slocProvider != null) {
				List<SnappingLocation> hssls = slocProvider
						.getHorizontalSnappingLocations(snappedPart);
				List<SnappingLocation> vssls = slocProvider
						.getVerticalSnappingLocations(snappedPart);
				hsrc.put(strategy, new ArrayList<>(hssls));
				vsrc.put(strategy, new ArrayList<>(vssls));
			}
		}

		initializeLocations(hsrc, vsrc);
	}

	/**
	 * Initializes this {@link SnapToSupport} for performing snapping of the
	 * given {@link IContentPart}. The given snapping locations are used for all
	 * {@link ISnapToStrategy ISnapToStrategies}.
	 *
	 * @param snappedPart
	 *            The snapped part.
	 * @param snappingLocations
	 *            The snapping locations for the part.
	 */
	public void startSnapping(IContentPart<? extends Node> snappedPart,
			List<SnappingLocation> snappingLocations) {
		initializePartAndStrategies(snappedPart);

		// build source snapping location configuration for all supported
		// strategies
		Map<ISnapToStrategy, List<SnappingLocation>> hsrc = new IdentityHashMap<>();
		Map<ISnapToStrategy, List<SnappingLocation>> vsrc = new IdentityHashMap<>();

		List<SnappingLocation> hssls = new ArrayList<>();
		List<SnappingLocation> vssls = new ArrayList<>();
		for (SnappingLocation sl : snappingLocations) {
			if (sl.getOrientation() == Orientation.HORIZONTAL) {
				hssls.add(sl);
			} else {
				vssls.add(sl);
			}
		}

		for (ISnapToStrategy strategy : supportedSnapToStrategies) {
			hsrc.put(strategy, new ArrayList<>(hssls));
			vsrc.put(strategy, new ArrayList<>(vssls));
		}

		initializeLocations(hsrc, vsrc);
	}

	/**
	 * Initializes this {@link SnapToSupport} for performing snapping of the
	 * given {@link IContentPart}, considering only the supplied horizontal and
	 * vertical {@link SnappingLocation}s.
	 *
	 * @param snappedPart
	 *            The {@link IContentPart} that might be snapped.
	 * @param horizontalSourceSnappingLocations
	 *            Maps from {@link ISnapToStrategy} to horizontal source
	 *            {@link SnappingLocation}s.
	 * @param verticalSourceSnappingLocations
	 *            Maps from {@link ISnapToStrategy} to vertical source
	 *            {@link SnappingLocation}s.
	 */
	public void startSnapping(IContentPart<? extends Node> snappedPart,
			Map<ISnapToStrategy, List<SnappingLocation>> horizontalSourceSnappingLocations,
			Map<ISnapToStrategy, List<SnappingLocation>> verticalSourceSnappingLocations) {
		initializePartAndStrategies(snappedPart);
		initializeLocations(horizontalSourceSnappingLocations,
				verticalSourceSnappingLocations);
	}

	/**
	 * Clears the snapping locations and the SnapToLocationModel.
	 */
	public void stopSnapping() {
		if (snappedPart != null) {
			// XXX: SnappingModel is only altered during interaction, therefore,
			// we do not need to carry these changes out via operations.
			SnappingModel snappingModel = getSnappingModel();
			if (snappingModel != null) {
				snappingModel.setSnappingLocations(
						Collections.<SnappingLocation> emptyList());
			}
			snappedPart = null;
		}
		supportedSnapToStrategies.clear();
		applicableSnapToStrategies.clear();
		hSourceLocations.clear();
		vSourceLocations.clear();
	}
}
