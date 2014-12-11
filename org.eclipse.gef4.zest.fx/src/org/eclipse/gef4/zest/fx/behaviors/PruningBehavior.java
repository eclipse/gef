/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.behaviors.BehaviorUtils;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.models.PruningModel;
import org.eclipse.gef4.zest.fx.parts.EdgeContentPart;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;
import org.eclipse.gef4.zest.fx.parts.PrunedNeighborsPart;

import com.google.common.collect.Multiset;

// Only applicable for NodeContentPart
public class PruningBehavior extends AbstractBehavior<Node> implements
		PropertyChangeListener {

	private IVisualPart<Node, ? extends Node> prunedNeighborsPart;
	private boolean isPruned;

	@Override
	public void activate() {
		super.activate();
		PruningModel pruningModel = getPruningModel();

		// register for change notifications
		pruningModel.addPropertyChangeListener(this);

		// query pruned status
		isPruned = pruningModel.isPruned(getHost().getContent());

		// create pruned neighbors part if it is already associated with our
		// host
		if (hasPrunedNeighbors(getHost().getContent(), pruningModel)) {
			createPrunedNeighborPart();
		}
	}

	private boolean containsAny(Set<org.eclipse.gef4.graph.Node> oldPruned,
			Set<org.eclipse.gef4.graph.Node> neighbors) {
		boolean containsAny = false;
		for (org.eclipse.gef4.graph.Node n : neighbors) {
			if (oldPruned.contains(n)) {
				containsAny = true;
				break;
			}
		}
		return containsAny;
	}

	protected void createPrunedNeighborPart() {
		// TODO: delegate to factory
		prunedNeighborsPart = new PrunedNeighborsPart();
		BehaviorUtils.<Node> addAnchorages(getHost().getRoot(),
				Collections.singletonList(getHost()),
				Collections.singletonList(prunedNeighborsPart));
	}

	@Override
	public void deactivate() {
		PruningModel pruningModel = getPruningModel();

		// remove pruned neighbors part if it is currently associated with our
		// host
		if (hasPrunedNeighbors(getHost().getContent(), pruningModel)) {
			removePrunedNeighborPart();
		}

		// unregister
		pruningModel.removePropertyChangeListener(this);
		super.deactivate();
	}

	@Override
	public NodeContentPart getHost() {
		return (NodeContentPart) super.getHost();
	}

	protected IVisualPart<Node, ? extends Node> getPrunedNeighborsPart() {
		return prunedNeighborsPart;
	}

	protected PruningModel getPruningModel() {
		return getHost().getRoot().getViewer().getDomain()
				.<PruningModel> getAdapter(PruningModel.class);
	}

	private boolean hasPrunedNeighbors(org.eclipse.gef4.graph.Node node,
			PruningModel pruningModel) {
		return !pruningModel.getPrunedNeighbors(node).isEmpty();
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(PruningModel.PRUNED_PROPERTY)) {
			// check if we have to prune/unprune the host
			boolean wasPruned = isPruned;
			isPruned = getPruningModel().isPruned(getHost().getContent());

			if (wasPruned && !isPruned) {
				unprune();
			} else if (!wasPruned && isPruned) {
				prune();
			}

			// check if we have to show/hide/update the pruned neighbors part
			Set<org.eclipse.gef4.graph.Node> oldPruned = (Set<org.eclipse.gef4.graph.Node>) event
					.getOldValue();
			Set<org.eclipse.gef4.graph.Node> newPruned = (Set<org.eclipse.gef4.graph.Node>) event
					.getNewValue();

			org.eclipse.gef4.graph.Node content = getHost().getContent();
			Set<org.eclipse.gef4.graph.Node> neighbors = content
					.getLocalNeighbors();

			if (!containsAny(oldPruned, neighbors)
					&& containsAny(newPruned, neighbors)) {
				createPrunedNeighborPart();
			} else if (containsAny(oldPruned, neighbors)
					&& !containsAny(newPruned, neighbors)) {
				removePrunedNeighborPart();
			} else {
				// TODO: only necessary when neighbors change
				if (prunedNeighborsPart != null) {
					updatePrunedNeighborPart();
				}
			}
		}
	}

	protected void prune() {
		// hide visual
		getHost().getVisual().setVisible(false);
		getHost().getVisual().setMouseTransparent(true);

		// hide connections
		Multiset<IVisualPart<Node, ? extends Node>> anchoreds = getHost()
				.getAnchoreds();
		for (IVisualPart<Node, ? extends Node> anchored : anchoreds
				.elementSet()) {
			if (anchored instanceof EdgeContentPart) {
				anchored.getVisual().setVisible(false);
				anchored.getVisual().setMouseTransparent(true);
			}
		}
	}

	protected void removePrunedNeighborPart() {
		BehaviorUtils.<Node> removeAnchorages(getHost().getRoot(),
				Collections.singletonList(getHost()),
				Collections.singletonList(prunedNeighborsPart));
		prunedNeighborsPart = null;
	}

	protected void unprune() {
		// show node
		getHost().getVisual().setVisible(true);
		getHost().getVisual().setMouseTransparent(false);

		// show connections
		Multiset<IVisualPart<Node, ? extends Node>> anchoreds = getHost()
				.getAnchoreds();
		for (IVisualPart<Node, ? extends Node> anchored : anchoreds
				.elementSet()) {
			if (anchored instanceof EdgeContentPart) {
				// retrieve source and target parts
				Edge edge = ((EdgeContentPart) anchored).getContent();
				IContentPart<Node, ? extends Node> sourcePart = getHost()
						.getRoot().getViewer().getContentPartMap()
						.get(edge.getSource());
				IContentPart<Node, ? extends Node> targetPart = getHost()
						.getRoot().getViewer().getContentPartMap()
						.get(edge.getTarget());
				// set connection visible if both (source and target) are
				// visible
				if (sourcePart.getVisual().isVisible()
						&& targetPart.getVisual().isVisible()) {
					anchored.getVisual().setVisible(true);
					anchored.getVisual().setMouseTransparent(false);
				}
			}
		}
	}

	protected void updatePrunedNeighborPart() {
		prunedNeighborsPart.refreshVisual();
	}

}
