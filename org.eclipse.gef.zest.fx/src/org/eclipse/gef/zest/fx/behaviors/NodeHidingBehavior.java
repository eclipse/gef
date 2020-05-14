/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.behaviors;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.zest.fx.models.HidingModel;
import org.eclipse.gef.zest.fx.parts.HiddenNeighborsFeedbackPart;
import org.eclipse.gef.zest.fx.parts.NodePart;

import javafx.collections.SetChangeListener;
import javafx.scene.Node;

/**
 * The {@link NodeHidingBehavior} is specific to {@link NodePart}. It registers
 * listeners on the {@link HidingModel} upon activation. When the
 * {@link HidingModel} changes, the hidden status of the {@link NodePart} is
 * determined. If the hidden status of the part changed, either {@link #hide()}
 * or {@link #show()} will be called, respectively, to hide/show the part.
 * Additionally, a {@link HiddenNeighborsFeedbackPart} is managed by this
 * {@link NodeHidingBehavior}. The {@link HiddenNeighborsFeedbackPart} shows the
 * number of hidden neighbors of the {@link NodePart}.
 *
 * @author mwienand
 *
 */
// Only applicable for NodePart (see #getHost())
public class NodeHidingBehavior extends AbstractHidingBehavior {

	private IVisualPart<? extends Node> hiddenNeighborsFeedbackPart;

	private boolean containsAny(Set<org.eclipse.gef.graph.Node> hidden, Set<org.eclipse.gef.graph.Node> neighbors) {
		boolean containsAny = false;
		for (org.eclipse.gef.graph.Node n : neighbors) {
			if (hidden.contains(n)) {
				containsAny = true;
				break;
			}
		}
		return containsAny;
	}

	/**
	 * Creates the {@link HiddenNeighborsFeedbackPart} that shows the hidden
	 * neighbors of the {@link NodePart} on which this
	 * {@link NodeHidingBehavior} is installed.
	 */
	protected void createHiddenNeighborsFeedbackPart() {
		if (hiddenNeighborsFeedbackPart == null) {
			// TODO: delegate to factory, ensure adaptable context is updated
			// before
			hiddenNeighborsFeedbackPart = new HiddenNeighborsFeedbackPart();
		}
		addAnchoreds(Collections.singletonList(getHost()), Collections.singletonList(hiddenNeighborsFeedbackPart));
	}

	@Override
	protected boolean determineHiddenStatus() {
		return getHidingModel().isHidden(getHost().getContent());
	}

	@Override
	protected void doActivate() {
		super.doActivate();
		// create hidden neighbors part if it is already associated with our
		// host
		if (getHidingModel().hasHiddenNeighbors(getHost())) {
			createHiddenNeighborsFeedbackPart();
		}
	}

	@Override
	protected void doDeactivate() {
		// remove hidden neighbors part if it is currently associated with our
		// host
		if (getHidingModel().hasHiddenNeighbors(getHost())) {
			removeHiddenNeighborsFeedbackPart();
		}
		super.doDeactivate();
	}

	/**
	 * Returns the {@link HiddenNeighborsFeedbackPart} that is managed by this
	 * {@link NodeHidingBehavior}.
	 *
	 * @return The {@link HiddenNeighborsFeedbackPart} that is managed by this
	 *         {@link NodeHidingBehavior}.
	 */
	protected IVisualPart<? extends Node> getHiddenNeighborsFeedbackPart() {
		return hiddenNeighborsFeedbackPart;
	}

	@Override
	public NodePart getHost() {
		return (NodePart) super.getHost();
	}

	@Override
	protected void onHidingModelChange(SetChangeListener.Change<? extends org.eclipse.gef.graph.Node> change) {
		super.onHidingModelChange(change);

		Set<org.eclipse.gef.graph.Node> newHidden = new HashSet<>(change.getSet());
		Set<org.eclipse.gef.graph.Node> oldHidden = new HashSet<>(change.getSet());
		oldHidden.remove(change.getElementAdded());
		oldHidden.add(change.getElementRemoved());

		// check if we have to show/hide/update the pruned neighbors part
		org.eclipse.gef.graph.Node content = getHost().getContent();
		Set<org.eclipse.gef.graph.Node> neighbors = content.getNeighbors();

		if (!containsAny(oldHidden, neighbors) && containsAny(newHidden, neighbors)) {
			createHiddenNeighborsFeedbackPart();
		} else if (containsAny(oldHidden, neighbors) && !containsAny(newHidden, neighbors)) {
			removeHiddenNeighborsFeedbackPart();
		} else {
			// TODO: only necessary when neighbors change
			if (hiddenNeighborsFeedbackPart != null) {
				updateHiddenNeighborsFeedbackPart();
			}
		}
	}

	/**
	 * Removes the {@link HiddenNeighborsFeedbackPart} that is managed by this
	 * {@link NodeHidingBehavior}.
	 */
	protected void removeHiddenNeighborsFeedbackPart() {
		removeAnchoreds(Collections.singletonList(getHost()), Collections.singletonList(hiddenNeighborsFeedbackPart));
	}

	/**
	 * Refreshes the {@link HiddenNeighborsFeedbackPart} that is managed by this
	 * {@link NodeHidingBehavior}.
	 */
	protected void updateHiddenNeighborsFeedbackPart() {
		hiddenNeighborsFeedbackPart.refreshVisual();
	}

}
