/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
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
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.fx.operations.BendContentOperation;
import org.eclipse.gef.mvc.fx.operations.BendVisualOperation;
import org.eclipse.gef.mvc.fx.parts.IBendableContentPart;
import org.eclipse.gef.mvc.fx.parts.IBendableContentPart.BendPoint;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.policies.BendConnectionPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.scene.Node;

/**
 * Triggers a normalization of the control points of all content parts that
 * support {@link BendConnectionPolicy} and are anchored to the host of this
 * policy.
 */
public class ConnectedSupport extends IAdaptable.Bound.Impl<IViewer> {

	private List<BendVisualOperation> operations = new ArrayList<>();

	private IVisualPart<?>[] parts;
	private BendConnectionPolicy[] policies;
	private boolean[] wasRefresh;

	/**
	 * Aborts the normalization, i.e. calls
	 * {@link BendConnectionPolicy#rollback()} and restores refreshing of
	 * visuals for each target part.
	 */
	public void abort() {
		if (parts != null) {
			for (int i = 0; i < parts.length; i++) {
				policies[i].rollback();
				parts[i].setRefreshVisual(wasRefresh[i]);
			}
		}
		parts = null;
		policies = null;
		wasRefresh = null;

		abortHints();
	}

	private void abortHints() {
		try {
			for (BendVisualOperation op : operations) {
				op.undo(null, null);
			}
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Commits the normalization, i.e. calls
	 * {@link BendConnectionPolicy#commit()} and restores refreshing of visuals
	 * for each target part.
	 */
	public void commit() {
		if (parts != null) {
			for (int i = 0; i < parts.length; i++) {
				policies[i].normalize();
				try {
					parts[i].getViewer().getDomain()
							.execute(policies[i].commit(), null);
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				parts[i].setRefreshVisual(wasRefresh[i]);
			}
		}
		parts = null;
		policies = null;
		wasRefresh = null;

		commitHints();
	}

	private void commitHints() {
		for (BendVisualOperation op : operations) {
			try {
				getAdaptable().getDomain().execute(op, null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			BendContentOperation bendContentOperation = new BendContentOperation(
					op.getPart(), op.getInitialBendPoints(),
					op.getFinalBendPoints());
			try {
				getAdaptable().getDomain().execute(bendContentOperation, null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Initializes this support class so that it can be used to normalize the
	 * Connections that are anchored to the given anchorages.
	 *
	 * @param anchorages
	 *            anchorages
	 */
	public void init(List<? extends IVisualPart<? extends Node>> anchorages) {
		List<IVisualPart<? extends Node>> targetParts = new ArrayList<>();
		for (IVisualPart<? extends Node> anchorage : anchorages) {
			for (IVisualPart<? extends Node> anchored : anchorage
					.getAnchoredsUnmodifiable()) {
				if (anchorages.contains(anchored)
						|| targetParts.contains(anchored)) {
					// skip anchorages and duplicates
					continue;
				}
				if (anchored instanceof IContentPart) {
					BendConnectionPolicy bendConnectionPolicy = anchored
							.getAdapter(BendConnectionPolicy.class);
					if (bendConnectionPolicy != null
							&& !targetParts.contains(anchored)) {
						// normalize all anchoreds with a BendConnectionPolicy
						targetParts.add(anchored);
					}
				}
			}
		}

		parts = targetParts.toArray(new IVisualPart[] {});
		wasRefresh = new boolean[parts.length];
		policies = new BendConnectionPolicy[parts.length];
		for (int i = 0; i < parts.length; i++) {
			wasRefresh[i] = parts[i].isRefreshVisual();
			parts[i].setRefreshVisual(false);
			policies[i] = parts[i].getAdapter(BendConnectionPolicy.class);
			policies[i].init();
			policies[i].move(new Point(), new Point());
		}

		// collect all anchored connections and put them into the sets for parts
		// with movable hints
		Set<IVisualPart<? extends Node>> isTarget = Collections
				.newSetFromMap(new IdentityHashMap<>());
		isTarget.addAll(anchorages);
		List<IBendableContentPart<? extends Node>> startHintMovable = new ArrayList<>();
		List<IBendableContentPart<? extends Node>> endHintMovable = new ArrayList<>();
		List<IBendableContentPart<? extends Node>> hintsMovable = new ArrayList<>();
		for (IVisualPart<? extends Node> targetPart : anchorages) {
			for (IVisualPart<? extends Node> anchored : targetPart
					.getAnchoredsUnmodifiable()) {
				if (anchored instanceof IBendableContentPart) {
					IBendableContentPart<? extends Node> bendable = (IBendableContentPart<? extends Node>) anchored;
					List<BendPoint> bendPoints = bendable
							.getContentBendPoints();
					if (bendPoints == null || bendPoints.isEmpty()) {
						continue;
					}
					if (bendPoints.get(0).isAttached()) {
						Object contentAnchorage = bendPoints.get(0)
								.getContentAnchorage();
						IContentPart<? extends Node> anchoragePart = getAdaptable()
								.getContentPartMap().get(contentAnchorage);
						if (isTarget.contains(anchoragePart)) {
							startHintMovable.add(bendable);
						}
					}
					if (bendPoints.get(bendPoints.size() - 1).isAttached()) {
						Object contentAnchorage = bendPoints
								.get(bendPoints.size() - 1)
								.getContentAnchorage();
						IContentPart<? extends Node> anchoragePart = getAdaptable()
								.getContentPartMap().get(contentAnchorage);
						if (isTarget.contains(anchoragePart)) {
							endHintMovable.add(bendable);
						}
					}
					if (startHintMovable.contains(anchored)
							&& endHintMovable.contains(anchored)
							&& !isTarget.contains(anchored)) {
						hintsMovable.add(
								(IBendableContentPart<? extends Node>) anchored);
					}
				}
			}
		}

		initHints(hintsMovable);
	}

	private void initHints(
			Collection<? extends IBendableContentPart<? extends Node>> hintsMovable) {
		this.operations.clear();
		for (IBendableContentPart<? extends Node> p : hintsMovable) {
			this.operations.add(new BendVisualOperation(p));
		}
	}

	/**
	 * Normalizes the target parts, i.e. calls
	 * {@link BendConnectionPolicy#normalize()} for each target part.
	 */
	public void normalizeConnected() {
		if (parts != null) {
			for (int i = 0; i < parts.length; i++) {
				policies[i].move(new Point(), new Point());
				policies[i].normalize();
			}
		}
	}

	/**
	 *
	 * @param delta
	 *            a
	 */
	public void relocateHints(Dimension delta) {
		for (BendVisualOperation op : operations) {
			List<BendPoint> relocatedBendPoints = new ArrayList<>();
			for (BendPoint bp : op.getInitialBendPoints()) {
				if (bp.isAttached()) {
					relocatedBendPoints
							.add(new BendPoint(bp.getContentAnchorage(),
									bp.getPosition().getTranslated(delta)));
				} else {
					relocatedBendPoints.add(new BendPoint(
							bp.getPosition().getTranslated(delta)));
				}
			}
			op.setFinalBendPoints(relocatedBendPoints);
			try {
				op.execute(null, null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
}
