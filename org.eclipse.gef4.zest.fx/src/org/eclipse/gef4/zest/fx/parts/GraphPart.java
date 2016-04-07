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
package org.eclipse.gef4.zest.fx.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.behaviors.ContentBehavior;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.behaviors.GraphLayoutBehavior;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.util.Pair;

/**
 * The {@link GraphPart} is the controller for a {@link Graph} content object.
 * It starts a layout pass after activation and when its content children
 * change.
 *
 * @author mwienand
 *
 */
// TODO: most of the listeners should probably be moved to GraphLayoutBehavior
public class GraphPart extends AbstractFXContentPart<Group> {

	private ListChangeListener<Object> graphChildrenObserver = new ListChangeListener<Object>() {

		@SuppressWarnings("serial")
		@Override
		public void onChanged(ListChangeListener.Change<? extends Object> c) {
			// synchronize children
			getAdapter(new TypeToken<ContentBehavior<Node>>() {
			}).synchronizeContentChildren(doGetContentChildren());

			// apply layout
			// TODO: this should be done by GraphLayoutBehavior
			applyLayout(true);
		}
	};

	@Override
	protected void addChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		getVisual().getChildren().add(index, child.getVisual());
	}

	private void applyLayout(boolean clean) {
		GraphLayoutBehavior layoutBehavior = getAdapter(GraphLayoutBehavior.class);
		if (layoutBehavior != null) {
			layoutBehavior.applyLayout(clean);
		}
	}

	@Override
	protected Group createVisual() {
		Group visual = new Group();
		visual.setAutoSizeChildren(false);
		return visual;
	}

	@Override
	protected void doActivate() {
		super.doActivate();

		getContent().getNodes().addListener(graphChildrenObserver);
		getContent().getEdges().addListener(graphChildrenObserver);
	}

	@Override
	protected void doDeactivate() {
		getContent().getNodes().removeListener(graphChildrenObserver);
		getContent().getEdges().removeListener(graphChildrenObserver);

		super.doDeactivate();
	}

	@Override
	protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
		return HashMultimap.create();
	}

	@Override
	protected List<? extends Object> doGetContentChildren() {
		List<Object> children = new ArrayList<>();
		// collect visible nodes
		ObservableList<org.eclipse.gef4.graph.Node> nodes = getContent().getNodes();
		ArrayList<org.eclipse.gef4.graph.Node> visibleNodes = new ArrayList<>();
		for (org.eclipse.gef4.graph.Node n : nodes) {
			if (!ZestProperties.getInvisible(n, true)) {
				visibleNodes.add(n);
			}
		}
		// add visible nodes
		children.addAll(visibleNodes);
		// add labels for visible nodes
		for (org.eclipse.gef4.graph.Node n : visibleNodes) {
			if (ZestProperties.getExternalLabel(n) != null) {
				children.add(new Pair<>(n, ZestProperties.ELEMENT_EXTERNAL_LABEL));
			}
		}
		// collect visible edges
		ObservableList<Edge> edges = getContent().getEdges();
		ArrayList<Edge> visibleEdges = new ArrayList<>();
		for (Edge e : edges) {
			if (!ZestProperties.getInvisible(e, true) && !ZestProperties.getInvisible(e.getSource(), true)
					&& !ZestProperties.getInvisible(e.getTarget(), true)) {
				visibleEdges.add(e);
			}
		}
		// add visible edges
		children.addAll(visibleEdges);
		// add labels for visible edges
		for (Edge e : visibleEdges) {
			if (ZestProperties.getLabel(e) != null) {
				children.add(new Pair<>(e, ZestProperties.ELEMENT_LABEL));
			}
			if (ZestProperties.getExternalLabel(e) != null) {
				children.add(new Pair<>(e, ZestProperties.ELEMENT_EXTERNAL_LABEL));
			}
			if (ZestProperties.getSourceLabel(e) != null) {
				children.add(new Pair<>(e, ZestProperties.EDGE_SOURCE_LABEL));
			}
			if (ZestProperties.getTargetLabel(e) != null) {
				children.add(new Pair<>(e, ZestProperties.EDGE_TARGET_LABEL));
			}
		}
		return children;
	}

	@Override
	public void doRefreshVisual(Group visual) {
	}

	@Override
	public Graph getContent() {
		return (Graph) super.getContent();
	}

	@Override
	protected void removeChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		getVisual().getChildren().remove(child.getVisual());
	}
}
