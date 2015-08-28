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

import javafx.scene.Node;

import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.behaviors.BehaviorUtils;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.models.HidingModel;
import org.eclipse.gef4.zest.fx.parts.HiddenNeighborsPart;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

/*
 * TODO: Rename HidingBehavior to NodeHidingBehavior and let it extend AbstractHidingBehavior. (Bugzilla #466851)
 */

// Only applicable for NodeContentPart (seee #getHost())
public class HidingBehavior extends AbstractBehavior<Node>implements PropertyChangeListener {

	private IVisualPart<Node, ? extends Node> hiddenNeighborsPart;
	private boolean isHidden;

	@Override
	public void activate() {
		super.activate();

		// register for change notifications regarding hidden nodes
		HidingModel hidingModel = getHidingModel();
		hidingModel.addPropertyChangeListener(this);

		// register for change notifications regarding anchoreds (connections)
		getHost().addPropertyChangeListener(this);

		// query "hidden" state
		isHidden = hidingModel.isHidden(getHost().getContent());

		// create hidden neighbors part if it is already associated with our
		// host
		if (hasHiddenNeighbors(getHost().getContent(), hidingModel)) {
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

	protected void createHiddenNeighborPart() {
		// TODO: delegate to factory
		hiddenNeighborsPart = new HiddenNeighborsPart();
		BehaviorUtils.<Node> addAnchorages(getHost().getRoot(), Collections.singletonList(getHost()),
				Collections.singletonList(hiddenNeighborsPart));
	}

	@Override
	public void deactivate() {
		HidingModel hidingModel = getHidingModel();

		// remove pruned neighbors part if it is currently associated with our
		// host
		if (hasHiddenNeighbors(getHost().getContent(), hidingModel)) {
			removeHiddenNeighborPart();
		}

		// unregister
		hidingModel.removePropertyChangeListener(this);
		super.deactivate();
	}

	protected HidingModel getHidingModel() {
		return getHost().getRoot().getViewer().<HidingModel> getAdapter(HidingModel.class);
	}

	@Override
	public NodeContentPart getHost() {
		return (NodeContentPart) super.getHost();
	}

	protected IVisualPart<Node, ? extends Node> getPrunedNeighborsPart() {
		return hiddenNeighborsPart;
	}

	private boolean hasHiddenNeighbors(org.eclipse.gef4.graph.Node node, HidingModel hidingModel) {
		return !hidingModel.getHiddenNeighbors(node).isEmpty();
	}

	protected void hide() {
		// hide visual
		getHost().getVisual().setVisible(false);
		getHost().getVisual().setMouseTransparent(true);
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (HidingModel.HIDDEN_PROPERTY.equals(event.getPropertyName())) {
			// check if we have to prune/unprune the host
			boolean wasHidden = isHidden;
			isHidden = getHidingModel().isHidden(getHost().getContent());

			if (wasHidden && !isHidden) {
				show();
			} else if (!wasHidden && isHidden) {
				hide();
			}

			// check if we have to show/hide/update the pruned neighbors part
			Set<org.eclipse.gef4.graph.Node> oldHidden = (Set<org.eclipse.gef4.graph.Node>) event.getOldValue();
			Set<org.eclipse.gef4.graph.Node> newHidden = (Set<org.eclipse.gef4.graph.Node>) event.getNewValue();

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
		} else if (IVisualPart.ANCHOREDS_PROPERTY.equals(event.getPropertyName())) {
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

	protected void removeHiddenNeighborPart() {
		BehaviorUtils.<Node> removeAnchorages(getHost().getRoot(), Collections.singletonList(getHost()),
				Collections.singletonList(hiddenNeighborsPart));
		hiddenNeighborsPart = null;
	}

	protected void show() {
		// show node
		getHost().getVisual().setVisible(true);
		getHost().getVisual().setMouseTransparent(false);
	}

	protected void updateHiddenNeighborPart() {
		hiddenNeighborsPart.refreshVisual();
	}

}
