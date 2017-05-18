/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
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
import java.util.List;

import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.policies.BendConnectionPolicy;

import javafx.scene.Node;

/**
 * Triggers a normalization of the control points of all content parts that
 * support {@link BendConnectionPolicy} and are anchored to the host of this
 * policy.
 */
public class NormalizeConnectedSupport {

	/*
	 * As a support class, normalizeConnected() should be provided as API to use
	 * by IHandler implementations. However, normalization of the connected
	 * parts needs to be coupled with the interaction, i.e. it needs to be
	 * reverted if the interaction is aborted, and it needs to be performed
	 * multiple times during interaction (single initialization and commit are
	 * desirable).
	 *
	 * Ergo, it does not really make sense to wrap the functionality in a
	 * support class. Using an IHandler instead seems like the natural choice
	 * given the functionality is coupled with interaction.
	 *
	 * In spite of this, given the assumption that a single (leaf) handler
	 * should be sufficient for processing input events, that single handler
	 * should be able to perform the normalization on its own, without the need
	 * for another handler that runs simultaneously.
	 *
	 * Moreover, as a support class, the time and context of execution is
	 * precisely specified by the handlers that use it, i.e. so that the handler
	 * can make assumptions about the state of its anchored connections.
	 */

	private IVisualPart<?>[] parts;
	private BendConnectionPolicy[] policies;
	private boolean[] wasRefresh;

	/**
	 * Aborts the normalization, i.e. calls
	 * {@link BendConnectionPolicy#rollback()} and restores refreshing of
	 * visuals for each target part.
	 */
	public void abortNormalization() {
		if (parts != null) {
			for (int i = 0; i < parts.length; i++) {
				policies[i].rollback();
				parts[i].setRefreshVisual(wasRefresh[i]);
			}
		}
		parts = null;
		policies = null;
		wasRefresh = null;
	}

	/**
	 * Commits the normalization, i.e. calls
	 * {@link BendConnectionPolicy#commit()} and restores refreshing of visuals
	 * for each target part.
	 */
	public void commitNormalization() {
		if (parts != null) {
			for (int i = 0; i < parts.length; i++) {
				policies[i].commit();
				parts[i].setRefreshVisual(wasRefresh[i]);
			}
		}
		parts = null;
		policies = null;
		wasRefresh = null;
	}

	/**
	 * Initializes this support class so that it can be used to normalize the
	 * Connections that are anchored to the given anchorages.
	 *
	 * @param anchorages
	 *            anchorages
	 */
	public void initNormalizationForAnchoredsOf(
			List<? extends IVisualPart<? extends Node>> anchorages) {
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
		}
	}

	/**
	 * Normalizes the target parts, i.e. calls
	 * {@link BendConnectionPolicy#normalize()} for each target part.
	 */
	public void normalizeAnchoreds() {
		for (IVisualPart<? extends Node> part : parts) {
			part.getAdapter(BendConnectionPolicy.class).normalize();
		}
	}
}
