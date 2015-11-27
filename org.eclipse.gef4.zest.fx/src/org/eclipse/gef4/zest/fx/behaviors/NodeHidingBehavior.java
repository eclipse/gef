/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.behaviors;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.Set;

import org.eclipse.gef4.mvc.behaviors.BehaviorUtils;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.models.HidingModel;
import org.eclipse.gef4.zest.fx.parts.HiddenNeighborsFeedbackPart;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

import javafx.scene.Node;

/**
 * The {@link NodeHidingBehavior} is specific to {@link NodeContentPart}. It
 * registers listeners on the {@link HidingModel} upon activation. When the
 * {@link HidingModel} changes, the hidden status of the {@link NodeContentPart}
 * is determined. If the hidden status of the part changed, either
 * {@link #hide()} or {@link #show()} will be called, respectively, to hide/show
 * the part. Additionally, a {@link HiddenNeighborsFeedbackPart} is managed by
 * this {@link NodeHidingBehavior}. The {@link HiddenNeighborsFeedbackPart}
 * shows the number of hidden neighbors of the {@link NodeContentPart}.
 *
 * @author mwienand
 *
 */
// Only applicable for NodeContentPart (see #getHost())
public class NodeHidingBehavior extends AbstractHidingBehavior {

	private IVisualPart<Node, ? extends Node> hiddenNeighborsFeedbackPart;

	@Override
	public void activate() {
		super.activate();

		// create hidden neighbors part if it is already associated with our
		// host
		if (getHidingModel().hasHiddenNeighbors(getHost())) {
			createHiddenNeighborsFeedbackPart();
		}
	}

	private boolean containsAny(Set<org.eclipse.gef4.graph.Node> hidden, Set<org.eclipse.gef4.graph.Node> neighbors) {
		boolean containsAny = false;
		for (org.eclipse.gef4.graph.Node n : neighbors) {
			if (hidden.contains(n)) {
				containsAny = true;
				break;
			}
		}
		return containsAny;
	}

	/**
	 * Creates the {@link HiddenNeighborsFeedbackPart} that shows the hidden
	 * neighbors of the {@link NodeContentPart} on which this
	 * {@link NodeHidingBehavior} is installed.
	 */
	protected void createHiddenNeighborsFeedbackPart() {
		if (hiddenNeighborsFeedbackPart == null) {
			// TODO: delegate to factory, ensure adaptable context is updated
			// before
			hiddenNeighborsFeedbackPart = new HiddenNeighborsFeedbackPart();
		}
		BehaviorUtils.<Node> addAnchorages(getHost().getRoot(), Collections.singletonList(getHost()),
				Collections.singletonList(hiddenNeighborsFeedbackPart));
	}

	@Override
	public void deactivate() {
		// remove hidden neighbors part if it is currently associated with our
		// host
		if (getHidingModel().hasHiddenNeighbors(getHost())) {
			removeHiddenNeighborsFeedbackPart();
		}

		super.deactivate();
	}

	@Override
	protected boolean determineHiddenStatus() {
		return getHidingModel().isHidden(getHost().getContent());
	}

	/**
	 * Returns the {@link HiddenNeighborsFeedbackPart} that is managed by this
	 * {@link NodeHidingBehavior}.
	 *
	 * @return The {@link HiddenNeighborsFeedbackPart} that is managed by this
	 *         {@link NodeHidingBehavior}.
	 */
	protected IVisualPart<Node, ? extends Node> getHiddenNeighborsFeedbackPart() {
		return hiddenNeighborsFeedbackPart;
	}

	@Override
	public NodeContentPart getHost() {
		return (NodeContentPart) super.getHost();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onHidingModelChange(PropertyChangeEvent event) {
		super.onHidingModelChange(event);

		Set<org.eclipse.gef4.graph.Node> oldHidden = (Set<org.eclipse.gef4.graph.Node>) event.getOldValue();
		Set<org.eclipse.gef4.graph.Node> newHidden = (Set<org.eclipse.gef4.graph.Node>) event.getNewValue();

		// check if we have to show/hide/update the pruned neighbors part
		org.eclipse.gef4.graph.Node content = getHost().getContent();
		Set<org.eclipse.gef4.graph.Node> neighbors = content.getLocalNeighbors();

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
		BehaviorUtils.<Node> removeAnchorages(getHost().getRoot(), Collections.singletonList(getHost()),
				Collections.singletonList(hiddenNeighborsFeedbackPart));
	}

	/**
	 * Refreshes the {@link HiddenNeighborsFeedbackPart} that is managed by this
	 * {@link NodeHidingBehavior}.
	 */
	protected void updateHiddenNeighborsFeedbackPart() {
		hiddenNeighborsFeedbackPart.refreshVisual();
	}

}
