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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.scene.Node;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.behaviors.BehaviorUtils;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.models.SubgraphModel;
import org.eclipse.gef4.zest.fx.parts.EdgeContentPart;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;
import org.eclipse.gef4.zest.fx.parts.PrunedNeighborsSubgraphPart;

import com.google.common.collect.Multiset;

// Only applicable for NodeContentPart
public class SubgraphBehavior extends AbstractBehavior<Node> implements
		PropertyChangeListener {

	private IVisualPart<Node> subgraphPart;
	private boolean isPruned;

	@Override
	public void activate() {
		super.activate();
		SubgraphModel subgraphModel = getSubgraphModel();

		// register for change notifications
		subgraphModel.addPropertyChangeListener(this);

		// query pruned status
		isPruned = subgraphModel.isPruned(getHost());

		// create subgraph if it is already associated with our host
		if (subgraphModel.isSubgraphAssociated(getHost())) {
			createSubgraph();
		}
	}

	protected void createSubgraph() {
		// TODO: delegate to factory
		subgraphPart = new PrunedNeighborsSubgraphPart();
		BehaviorUtils.<Node> addAnchorages(getHost().getRoot(),
				Collections.singletonList(getHost()),
				Collections.singletonList(subgraphPart));
	}

	@Override
	public void deactivate() {
		SubgraphModel subgraphModel = getSubgraphModel();

		// remove subgraph if it is currently associated with our host
		if (subgraphModel.isSubgraphAssociated(getHost())) {
			removeSubgraph();
		}

		// unregister
		subgraphModel.removePropertyChangeListener(this);
		super.deactivate();
	}

	@Override
	public NodeContentPart getHost() {
		return (NodeContentPart) super.getHost();
	}

	protected SubgraphModel getSubgraphModel() {
		return getHost().getRoot().getViewer().getDomain()
				.<SubgraphModel> getAdapter(SubgraphModel.class);
	}

	protected IVisualPart<Node> getSubgraphPart() {
		return subgraphPart;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(SubgraphModel.SUBGRAPHS_PROPERTY)) {
			// check if we have to prune/unprune the host
			boolean wasPruned = isPruned;
			isPruned = getSubgraphModel().isPruned(getHost());

			if (wasPruned && !isPruned) {
				unprune();
			} else if (!wasPruned && isPruned) {
				prune();
			}

			// check if we have to show/hide a subgraph
			Map.Entry<NodeContentPart, Set<NodeContentPart>> oldEntry = (Entry<NodeContentPart, Set<NodeContentPart>>) event
					.getOldValue();
			if (oldEntry != null && oldEntry.getKey() != getHost()) {
				return;
			}

			Map.Entry<NodeContentPart, Set<NodeContentPart>> newEntry = (Entry<NodeContentPart, Set<NodeContentPart>>) event
					.getNewValue();
			if (newEntry != null && newEntry.getKey() != getHost()) {
				return;
			}

			if (oldEntry == null) {
				createSubgraph();
			} else if (newEntry == null || newEntry.getValue().isEmpty()) {
				removeSubgraph();
			} else {
				updateSubgraph();
			}
		}
	}

	protected void prune() {
		// hide visual
		getHost().getVisual().setVisible(false);
		getHost().getVisual().setMouseTransparent(true);

		// hide connections
		Multiset<IVisualPart<Node>> anchoreds = getHost().getAnchoreds();
		for (IVisualPart<Node> anchored : anchoreds.elementSet()) {
			if (anchored instanceof EdgeContentPart) {
				anchored.getVisual().setVisible(false);
				anchored.getVisual().setMouseTransparent(true);
			}
		}
	}

	protected void removeSubgraph() {
		BehaviorUtils.<Node> removeAnchorages(getHost().getRoot(),
				Collections.singletonList(getHost()),
				Collections.singletonList(subgraphPart));
		subgraphPart = null;
	}

	protected void unprune() {
		// show node
		getHost().getVisual().setVisible(true);
		getHost().getVisual().setMouseTransparent(false);

		// show connections
		Multiset<IVisualPart<Node>> anchoreds = getHost().getAnchoreds();
		for (IVisualPart<Node> anchored : anchoreds.elementSet()) {
			if (anchored instanceof EdgeContentPart) {
				// retrieve source and target parts
				Edge edge = ((EdgeContentPart) anchored).getContent();
				IContentPart<Node> sourcePart = getHost().getRoot().getViewer()
						.getContentPartMap().get(edge.getSource());
				IContentPart<Node> targetPart = getHost().getRoot().getViewer()
						.getContentPartMap().get(edge.getTarget());
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

	protected void updateSubgraph() {
		// assert(subgraphPart != null);
		subgraphPart.refreshVisual();
	}

}
