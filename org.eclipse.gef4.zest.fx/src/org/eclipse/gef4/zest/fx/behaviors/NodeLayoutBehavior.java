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
import javafx.scene.transform.Affine;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.layout.LayoutProperties;
import org.eclipse.gef4.layout.interfaces.NodeLayout;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.layout.GraphNodeLayout;
import org.eclipse.gef4.zest.fx.models.HidingModel;
import org.eclipse.gef4.zest.fx.parts.HiddenNeighborsPart;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;

public class NodeLayoutBehavior extends AbstractLayoutBehavior {

	public static Class<FXResizeRelocatePolicy> RESIZE_RELOCATE_POLICY_KEY = FXResizeRelocatePolicy.class;

	protected GraphNodeLayout nodeLayout;

	private ChangeListener<Boolean> visibilityChangeListener = new ChangeListener<Boolean>() {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable,
				Boolean oldValue, Boolean newValue) {
			if (oldValue.booleanValue() != newValue.booleanValue()) {
				onVisibilityChanged(oldValue, newValue);
			}
		}
	};

	private PropertyChangeListener anchoredChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (IVisualPart.ANCHOREDS_PROPERTY.equals(evt.getPropertyName())) {
				@SuppressWarnings("unchecked")
				Multiset<IVisualPart<Node, ? extends Node>> oldAnchoreds = (Multiset<IVisualPart<Node, ? extends Node>>) evt
						.getOldValue();
				@SuppressWarnings("unchecked")
				Multiset<IVisualPart<Node, ? extends Node>> newAnchoreds = (Multiset<IVisualPart<Node, ? extends Node>>) evt
						.getNewValue();
				onAnchoredChange(oldAnchoreds, newAnchoreds);
			}
		}
	};

	private PropertyChangeListener pruningAnchorageChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (IVisualPart.ANCHORAGES_PROPERTY.equals(evt.getPropertyName())) {
				@SuppressWarnings("unchecked")
				SetMultimap<IVisualPart<Node, ? extends Node>, String> oldPruningAnchorages = (SetMultimap<IVisualPart<Node, ? extends Node>, String>) evt
						.getOldValue();
				@SuppressWarnings("unchecked")
				SetMultimap<IVisualPart<Node, ? extends Node>, String> newPruningAnchorages = (SetMultimap<IVisualPart<Node, ? extends Node>, String>) evt
						.getNewValue();
				onPruningAnchorageChange(oldPruningAnchorages,
						newPruningAnchorages);
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

	public void adaptLayoutInformation(NodeLayout nodeLayout) {
		FXResizeRelocatePolicy policy = getHost().getAdapter(
				RESIZE_RELOCATE_POLICY_KEY);
		if (policy != null) {
			Node visual = getHost().getVisual();
			Bounds layoutBounds = visual.getLayoutBounds();
			FXTransformPolicy txPolicy = getHost().getAdapter(
					FXTransformPolicy.class);
			Affine transform = txPolicy.getNodeTransform();
			double x = transform.getTx();
			double y = transform.getTy();
			double w = layoutBounds.getWidth();
			double h = layoutBounds.getHeight();

			Point location = LayoutProperties.getLocation(nodeLayout);
			Dimension size = LayoutProperties.getSize(nodeLayout);

			// location is the center of the node, therefore we subtract half
			// width/height from it
			double dx = location.x - size.width / 2 - x;
			double dy = location.y - size.height / 2 - y;
			double dw = size.width - w;
			double dh = size.height - h;

			policy.init();
			policy.performResizeRelocate(dx, dy, dw, dh);
			IUndoableOperation operation = policy.commit();
			if (operation != null) {
				try {
					operation.execute(new NullProgressMonitor(), null);
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void deactivate() {
		super.deactivate();
		getHost().getVisual().visibleProperty()
				.removeListener(visibilityChangeListener);
		getHost().removePropertyChangeListener(anchoredChangeListener);
	}

	@Override
	protected Graph getGraph() {
		return getHost().getContent().getGraph();
	}

	@Override
	public NodeContentPart getHost() {
		return (NodeContentPart) super.getHost();
	}

	@Override
	protected void initializeLayout(GraphLayoutContext glc) {
		// find node layout
		nodeLayout = glc
				.getNodeLayout((org.eclipse.gef4.graph.Node) ((IContentPart<Node, ? extends Node>) getHost())
						.getContent());
		if (nodeLayout == null) {
			// XXX: Why are we still living?
			return;
		}

		// initialize layout information
		getHost().refreshVisual();
		provideLayoutInformation(nodeLayout);
	}

	protected void onAnchoredChange(
			Multiset<IVisualPart<Node, ? extends Node>> oldAnchoreds,
			Multiset<IVisualPart<Node, ? extends Node>> newAnchoreds) {
		if (nodeLayout != null) {
			HiddenNeighborsPart oldSubgraphPart = null;
			for (IVisualPart<Node, ? extends Node> oldAnchored : oldAnchoreds) {
				if (oldAnchored instanceof HiddenNeighborsPart) {
					oldSubgraphPart = (HiddenNeighborsPart) oldAnchored;
					break;
				}
			}

			HiddenNeighborsPart newSubgraphPart = null;
			for (IVisualPart<Node, ? extends Node> newAnchored : newAnchoreds) {
				if (newAnchored instanceof HiddenNeighborsPart) {
					newSubgraphPart = (HiddenNeighborsPart) newAnchored;
					break;
				}
			}

			if (oldSubgraphPart != null && newSubgraphPart == null) {
				oldSubgraphPart
						.removePropertyChangeListener(pruningAnchorageChangeListener);
				provideLayoutInformation(nodeLayout);
			} else if (oldSubgraphPart == null && newSubgraphPart != null) {
				newSubgraphPart
						.addPropertyChangeListener(pruningAnchorageChangeListener);
			}
		}
	}

	@Override
	protected void onBoundsChange(Bounds oldBounds, Bounds newBounds) {
		if (nodeLayout != null) {
			provideLayoutInformation(nodeLayout);
		}
	}

	@Override
	protected void onFlushChanges() {
		if (nodeLayout != null) {
			adaptLayoutInformation(nodeLayout);
			getHost().refreshVisual();
		}
	}

	protected void onPruningAnchorageChange(
			SetMultimap<IVisualPart<Node, ? extends Node>, String> oldPruningAnchorages,
			SetMultimap<IVisualPart<Node, ? extends Node>, String> newPruningAnchorages) {
		boolean hostWasAnchorage = oldPruningAnchorages.containsKey(getHost());
		boolean hostIsAnchorage = newPruningAnchorages.containsKey(getHost());
		if (!hostWasAnchorage && hostIsAnchorage) {
			provideLayoutInformation(nodeLayout);
		}
	}

	protected void onVisibilityChanged(Boolean wasVisible, Boolean isVisible) {
		if (nodeLayout != null) {
			provideLayoutInformation(nodeLayout);
		}
	}

	public void provideLayoutInformation(NodeLayout nodeLayout) {
		Node visual = getHost().getVisual();
		Bounds hostBounds = visual.getLayoutBounds();
		double minx = hostBounds.getMinX();
		double miny = hostBounds.getMinY();
		double maxx = hostBounds.getMaxX();
		double maxy = hostBounds.getMaxY();
		// union node bounds with bounds of feedback visuals
		for (IVisualPart<Node, ? extends Node> anchored : getHost()
				.getAnchoreds()) {
			if (!(anchored instanceof IFeedbackPart)) {
				continue;
			}
			Node anchoredVisual = anchored.getVisual();
			Bounds anchoredBounds = anchoredVisual.getLayoutBounds();
			Bounds anchoredBoundsInHost = visual.sceneToLocal(anchoredVisual
					.localToScene(anchoredBounds));
			minx = Math.min(minx, anchoredBoundsInHost.getMinX());
			miny = Math.min(miny, anchoredBoundsInHost.getMinY());
			maxx = Math.max(maxx, anchoredBoundsInHost.getMaxX());
			maxy = Math.max(maxy, anchoredBoundsInHost.getMaxY());
		}

		FXTransformPolicy txPolicy = getHost().getAdapter(
				FXTransformPolicy.class);
		Affine transform = txPolicy.getNodeTransform();

		LayoutProperties.setLocation(nodeLayout, transform.getTx() + minx,
				transform.getTy() + miny);
		LayoutProperties.setSize(nodeLayout, maxx - minx, maxy - miny);
		LayoutProperties.setResizable(nodeLayout, visual.isResizable());

		Object wasHidden = nodeLayout.getProperty(HidingModel.HIDDEN_PROPERTY);
		if (visual.isVisible() == (wasHidden == null || wasHidden instanceof Boolean
				&& (Boolean) wasHidden)) {
			nodeLayout.setProperty(HidingModel.HIDDEN_PROPERTY,
					!visual.isVisible());
		}
	}
}
