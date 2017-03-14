/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.policies.BendConnectionPolicy;

import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * Triggers a normalization of the control points of all content parts that
 * support {@link BendConnectionPolicy} and are anchored to the host of this
 * policy.
 *
 * @author mwienand
 *
 */
public class NormalizeConnectedOnDragHandler extends AbstractHandler
		implements IOnDragHandler {

	private Set<IVisualPart<? extends Node>> targetParts;
	private boolean invalidGesture = false;

	@Override
	public void abortDrag() {
		if (invalidGesture) {
			return;
		}

		for (IVisualPart<? extends Node> part : targetParts) {
			rollback(part.getAdapter(BendConnectionPolicy.class));
			restoreRefreshVisuals(part);
		}
		targetParts = null;
	}

	/**
	 * Determines the target parts for this policy.
	 *
	 * @return The {@link IVisualPart} that should be considered as target
	 *         parts.
	 */
	protected Set<IVisualPart<? extends Node>> determineTargetParts() {
		Set<IVisualPart<? extends Node>> targetParts = Collections
				.newSetFromMap(
						new IdentityHashMap<IVisualPart<? extends Node>, Boolean>());
		for (IVisualPart<? extends Node> anchored : getHost()
				.getAnchoredsUnmodifiable()) {
			if (anchored instanceof IContentPart) {
				BendConnectionPolicy bendConnectionPolicy = anchored
						.getAdapter(BendConnectionPolicy.class);
				if (bendConnectionPolicy != null
						&& !targetParts.contains(anchored)) {
					targetParts.add(anchored);
				}
			}
		}

		// filter out selected
		SelectionModel selectionModel = getHost().getRoot().getViewer()
				.getAdapter(SelectionModel.class);
		Iterator<IVisualPart<? extends Node>> it = targetParts.iterator();
		while (it.hasNext()) {
			IVisualPart<? extends Node> part = it.next();
			if (part instanceof IContentPart && selectionModel
					.isSelected((IContentPart<? extends Node>) part)) {
				it.remove();
			}
		}
		return targetParts;
	}

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		if (invalidGesture) {
			return;
		}

		for (IVisualPart<? extends Node> part : targetParts) {
			part.getAdapter(BendConnectionPolicy.class).normalize();
		}
	}

	@Override
	public void endDrag(MouseEvent e, Dimension delta) {
		if (invalidGesture) {
			return;
		}
		for (IVisualPart<? extends Node> part : targetParts) {
			commit(part.getAdapter(BendConnectionPolicy.class));
			restoreRefreshVisuals(part);
		}
		targetParts = null;
	}

	@Override
	public void hideIndicationCursor() {
	}

	/**
	 * Returns <code>true</code> if the given {@link MouseEvent} should trigger
	 * normalization. Otherwise returns <code>false</code>. Per default always
	 * returns <code>true</code> if there are target parts.
	 *
	 * @param event
	 *            The {@link MouseEvent} in question.
	 * @return <code>true</code> to indicate that the given {@link MouseEvent}
	 *         should trigger normalization, otherwise <code>false</code>.
	 */
	protected boolean isNormalize(MouseEvent event) {
		return !targetParts.isEmpty();
	}

	@Override
	public boolean showIndicationCursor(KeyEvent event) {
		return false;
	}

	@Override
	public boolean showIndicationCursor(MouseEvent event) {
		return false;
	}

	@Override
	public void startDrag(MouseEvent e) {
		targetParts = determineTargetParts();

		invalidGesture = !isNormalize(e);
		if (invalidGesture) {
			return;
		}

		for (IVisualPart<? extends Node> part : targetParts) {
			storeAndDisableRefreshVisuals(part);
			init(part.getAdapter(BendConnectionPolicy.class));
		}
	}

}
