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
import java.beans.PropertyChangeListener;
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
 * the part. Additionally, a {@link HiddenNeighborsFeedbackPart} is managed by this
 * {@link NodeHidingBehavior}. The {@link HiddenNeighborsFeedbackPart} shows the number
 * of hidden neighbors of the {@link NodeContentPart}.
 *
 * @author mwienand
 *
 */
// Only applicable for NodeContentPart (see #getHost())
public class NodeHidingBehavior extends AbstractHidingBehavior implements PropertyChangeListener {

	private IVisualPart<Node, ? extends Node> hiddenNeighborsPart;

	@Override
	public void activate() {
		super.activate();

		// register for change notifications regarding anchoreds (connections)
		getHost().addPropertyChangeListener(this);

		// create hidden neighbors part if it is already associated with our
		// host
		if (hasHiddenNeighbors(getHost().getContent(), getHidingModel())) {
			createHiddenNeighborPart();
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
	 * Creates the {@link HiddenNeighborsFeedbackPart} that shows the hidden neighbors
	 * of the {@link NodeContentPart} on which this {@link NodeHidingBehavior}
	 * is installed.
	 */
	protected void createHiddenNeighborPart() {
		// TODO: delegate to factory
		hiddenNeighborsPart = new HiddenNeighborsFeedbackPart();
		BehaviorUtils.<Node> addAnchorages(getHost().getRoot(), Collections.singletonList(getHost()),
				Collections.singletonList(hiddenNeighborsPart));
	}

	@Override
	public void deactivate() {
		// remove pruned neighbors part if it is currently associated with our
		// host
		if (hasHiddenNeighbors(getHost().getContent(), getHidingModel())) {
			removeHiddenNeighborPart();
		}

		super.deactivate();
	}

	@Override
	protected boolean determineHiddenStatus() {
		return getHidingModel().isHidden(getHost().getContent());
	}

	@Override
	public NodeContentPart getHost() {
		return (NodeContentPart) super.getHost();
	}

	/**
	 * Returns the {@link HiddenNeighborsFeedbackPart} that is managed by this
	 * {@link NodeHidingBehavior}.
	 *
	 * @return The {@link HiddenNeighborsFeedbackPart} that is managed by this
	 *         {@link NodeHidingBehavior}.
	 */
	// TODO: Rename to getHiddenNeighborsPart
	protected IVisualPart<Node, ? extends Node> getPrunedNeighborsPart() {
		return hiddenNeighborsPart;
	}

	private boolean hasHiddenNeighbors(org.eclipse.gef4.graph.Node node, HidingModel hidingModel) {
		return !hidingModel.getHiddenNeighbors(node).isEmpty();
	}

	@Override
	protected void onHidingModelChange(PropertyChangeEvent event) {
		super.onHidingModelChange(event);

		Set<org.eclipse.gef4.graph.Node> oldHidden = (Set<org.eclipse.gef4.graph.Node>) event.getOldValue();
		Set<org.eclipse.gef4.graph.Node> newHidden = (Set<org.eclipse.gef4.graph.Node>) event.getNewValue();

		// check if we have to show/hide/update the pruned neighbors part
		org.eclipse.gef4.graph.Node content = getHost().getContent();
		Set<org.eclipse.gef4.graph.Node> neighbors = content.getLocalNeighbors();

		if (!containsAny(oldHidden, neighbors) && containsAny(newHidden, neighbors)) {
			createHiddenNeighborPart();
		} else if (containsAny(oldHidden, neighbors) && !containsAny(newHidden, neighbors)) {
			removeHiddenNeighborPart();
		} else {
			// TODO: only necessary when neighbors change
			if (hiddenNeighborsPart != null) {
				updateHiddenNeighborPart();
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (IVisualPart.ANCHOREDS_PROPERTY.equals(event.getPropertyName())) {
			if (hiddenNeighborsPart == null) {
				Set<org.eclipse.gef4.graph.Node> hiddenNeighbors = getHidingModel()
						.getHiddenNeighbors(getHost().getContent());
				if (!hiddenNeighbors.isEmpty()) {
					createHiddenNeighborPart();
				}
			} else {
				updateHiddenNeighborPart();
			}
		}
	}

	/**
	 * Removes the {@link HiddenNeighborsFeedbackPart} that is managed by this
	 * {@link NodeHidingBehavior}.
	 */
	protected void removeHiddenNeighborPart() {
		BehaviorUtils.<Node> removeAnchorages(getHost().getRoot(), Collections.singletonList(getHost()),
				Collections.singletonList(hiddenNeighborsPart));
		hiddenNeighborsPart = null;
	}

	/**
	 * Refreshes the {@link HiddenNeighborsFeedbackPart} that is managed by this
	 * {@link NodeHidingBehavior}.
	 */
	protected void updateHiddenNeighborPart() {
		hiddenNeighborsPart.refreshVisual();
	}

}
