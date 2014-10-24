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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.layout.GraphNodeLayout;
import org.eclipse.gef4.zest.fx.parts.PrunedNeighborsSubgraphPart;
import org.eclipse.gef4.zest.fx.policies.NodeLayoutPolicy;

import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;

public class NodeLayoutBehavior extends AbstractLayoutBehavior {

	public static Class<NodeLayoutPolicy> LAYOUT_POLICY_KEY = NodeLayoutPolicy.class;

	protected GraphNodeLayout nodeLayout;

	private ChangeListener<Boolean> visibilityChangeListener = new ChangeListener<Boolean>() {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable,
				Boolean oldValue, Boolean newValue) {
			onVisibilityChanged(oldValue, newValue);
		}
	};

	private PropertyChangeListener anchoredChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (IVisualPart.ANCHOREDS_PROPERTY.equals(evt.getPropertyName())) {
				@SuppressWarnings("unchecked")
				Multiset<IVisualPart<Node>> oldAnchoreds = (Multiset<IVisualPart<Node>>) evt
						.getOldValue();
				@SuppressWarnings("unchecked")
				Multiset<IVisualPart<Node>> newAnchoreds = (Multiset<IVisualPart<Node>>) evt
						.getNewValue();
				onAnchoredChange(oldAnchoreds, newAnchoreds);
			}
		}
	};

	private PropertyChangeListener subgraphAnchorageChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (IVisualPart.ANCHORAGES_PROPERTY.equals(evt.getPropertyName())) {
				@SuppressWarnings("unchecked")
				SetMultimap<IVisualPart<Node>, String> oldSubgraphAnchorages = (SetMultimap<IVisualPart<Node>, String>) evt
						.getOldValue();
				@SuppressWarnings("unchecked")
				SetMultimap<IVisualPart<Node>, String> newSubgraphAnchorages = (SetMultimap<IVisualPart<Node>, String>) evt
						.getNewValue();
				onSubgraphAnchorageChange(oldSubgraphAnchorages,
						newSubgraphAnchorages);
			}
		}
	};

	public NodeLayoutBehavior() {
	}

	@Override
	public void activate() {
		super.activate();
		getHost().getVisual().visibleProperty()
				.addListener(visibilityChangeListener);
		getHost().addPropertyChangeListener(anchoredChangeListener);
	}

	@Override
	public void deactivate() {
		super.deactivate();
		getHost().getVisual().visibleProperty()
				.removeListener(visibilityChangeListener);
		getHost().removePropertyChangeListener(anchoredChangeListener);
	}

	@Override
	protected void initializeLayout(GraphLayoutContext glc) {
		// find node layout
		nodeLayout = glc
				.getNodeLayout((org.eclipse.gef4.graph.Node) ((IContentPart<Node>) getHost())
						.getContent());
		getHost().refreshVisual();
		// initialize layout information
		getHost().getAdapter(LAYOUT_POLICY_KEY).provideLayoutInformation(
				nodeLayout);
	}

	protected void onAnchoredChange(Multiset<IVisualPart<Node>> oldAnchoreds,
			Multiset<IVisualPart<Node>> newAnchoreds) {
		if (nodeLayout != null) {
			PrunedNeighborsSubgraphPart oldSubgraphPart = null;
			for (IVisualPart<Node> oldAnchored : oldAnchoreds) {
				if (oldAnchored instanceof PrunedNeighborsSubgraphPart) {
					oldSubgraphPart = (PrunedNeighborsSubgraphPart) oldAnchored;
					break;
				}
			}

			PrunedNeighborsSubgraphPart newSubgraphPart = null;
			for (IVisualPart<Node> newAnchored : newAnchoreds) {
				if (newAnchored instanceof PrunedNeighborsSubgraphPart) {
					newSubgraphPart = (PrunedNeighborsSubgraphPart) newAnchored;
					break;
				}
			}

			if (oldSubgraphPart != null && newSubgraphPart == null) {
				oldSubgraphPart
						.removePropertyChangeListener(subgraphAnchorageChangeListener);
				getHost().getAdapter(LAYOUT_POLICY_KEY)
						.provideLayoutInformation(nodeLayout);
			} else if (oldSubgraphPart == null && newSubgraphPart != null) {
				newSubgraphPart
						.addPropertyChangeListener(subgraphAnchorageChangeListener);
			}
		}
	}

	@Override
	protected void onBoundsChange(Bounds oldBounds, Bounds newBounds) {
		if (nodeLayout != null) {
			getHost().getAdapter(LAYOUT_POLICY_KEY).provideLayoutInformation(
					nodeLayout);
		}
	}

	@Override
	protected void onFlushChanges() {
		if (nodeLayout != null) {
			getHost().getAdapter(LAYOUT_POLICY_KEY).adaptLayoutInformation(
					nodeLayout);
			getHost().refreshVisual();
		}
	}

	protected void onSubgraphAnchorageChange(
			SetMultimap<IVisualPart<Node>, String> oldSubgraphAnchorages,
			SetMultimap<IVisualPart<Node>, String> newSubgraphAnchorages) {
		boolean hostWasAnchorage = oldSubgraphAnchorages.containsKey(getHost());
		boolean hostIsAnchorage = newSubgraphAnchorages.containsKey(getHost());
		if (!hostWasAnchorage && hostIsAnchorage) {
			getHost().getAdapter(LAYOUT_POLICY_KEY).provideLayoutInformation(
					nodeLayout);
		}
	}

	protected void onVisibilityChanged(Boolean wasVisible, Boolean isVisible) {
		if (nodeLayout != null) {
			getHost().getAdapter(LAYOUT_POLICY_KEY).provideLayoutInformation(
					nodeLayout);
		}
	}

}
