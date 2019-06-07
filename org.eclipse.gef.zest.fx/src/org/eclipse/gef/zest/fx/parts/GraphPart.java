/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
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
package org.eclipse.gef.zest.fx.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.mvc.fx.parts.AbstractContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.zest.fx.ZestProperties;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

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
public class GraphPart extends AbstractContentPart<Group> {

	private ListChangeListener<Object> graphChildrenObserver = new ListChangeListener<Object>() {

		@Override
		public void onChanged(ListChangeListener.Change<? extends Object> c) {
			// synchronize children (triggered through content children)
			refreshContentChildren();
			refreshVisual();
		}
	};

	@Override
	protected void doActivate() {
		super.doActivate();
		getContent().getNodes().addListener(graphChildrenObserver);
		getContent().getEdges().addListener(graphChildrenObserver);
	}

	@Override
	protected void doAddChildVisual(IVisualPart<? extends Node> child, int index) {
		getVisual().getChildren().add(index, child.getVisual());
	}

	@Override
	protected void doAddContentChild(Object contentChild, int index) {
		if (contentChild instanceof org.eclipse.gef.graph.Node) {
			org.eclipse.gef.graph.Node node = (org.eclipse.gef.graph.Node) contentChild;
			getContent().getNodes().add(index, node);
		} else if (contentChild instanceof org.eclipse.gef.graph.Edge) {
			org.eclipse.gef.graph.Edge edge = (org.eclipse.gef.graph.Edge) contentChild;
			getContent().getEdges().add(index - getContent().getNodes().size(), edge);
		} else {
			throw new IllegalArgumentException(
					"Cannot add content child from graph: expected either Node or Edge, but got " + contentChild
							+ " instead.");
		}
	}

	@Override
	protected Group doCreateVisual() {
		Group visual = new Group();
		visual.setAutoSizeChildren(false);
		return visual;
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
		ObservableList<org.eclipse.gef.graph.Node> nodes = getContent().getNodes();
		ArrayList<org.eclipse.gef.graph.Node> visibleNodes = new ArrayList<>();
		for (org.eclipse.gef.graph.Node n : nodes) {
			if (!Boolean.TRUE.equals(ZestProperties.getInvisible(n))) {
				visibleNodes.add(n);
			}
		}
		// add visible nodes
		children.addAll(visibleNodes);
		// add labels for visible nodes
		for (org.eclipse.gef.graph.Node n : visibleNodes) {
			if (ZestProperties.getExternalLabel(n) != null) {
				children.add(new Pair<>(n, ZestProperties.EXTERNAL_LABEL__NE));
			}
		}
		// collect visible edges
		ObservableList<Edge> edges = getContent().getEdges();
		ArrayList<Edge> visibleEdges = new ArrayList<>();
		for (Edge e : edges) {
			if (!Boolean.TRUE.equals(ZestProperties.getInvisible(e)) && e.getSource() != null
					&& !Boolean.TRUE.equals(ZestProperties.getInvisible(e.getSource())) && e.getTarget() != null
					&& !Boolean.TRUE.equals(ZestProperties.getInvisible(e.getTarget()))) {
				visibleEdges.add(e);
			}
		}
		// add visible edges
		children.addAll(visibleEdges);
		// add labels for visible edges
		for (Edge e : visibleEdges) {
			if (ZestProperties.getLabel(e) != null) {
				children.add(new Pair<>(e, ZestProperties.LABEL__NE));
			}
			if (ZestProperties.getExternalLabel(e) != null) {
				children.add(new Pair<>(e, ZestProperties.EXTERNAL_LABEL__NE));
			}
			if (ZestProperties.getSourceLabel(e) != null) {
				children.add(new Pair<>(e, ZestProperties.SOURCE_LABEL__E));
			}
			if (ZestProperties.getTargetLabel(e) != null) {
				children.add(new Pair<>(e, ZestProperties.TARGET_LABEL__E));
			}
		}
		return children;
	}

	@Override
	public void doRefreshVisual(Group visual) {
	}

	@Override
	protected void doRemoveChildVisual(IVisualPart<? extends Node> child, int index) {
		getVisual().getChildren().remove(child.getVisual());
	}

	@Override
	protected void doRemoveContentChild(Object contentChild) {
		if (contentChild instanceof org.eclipse.gef.graph.Node) {
			org.eclipse.gef.graph.Node node = (org.eclipse.gef.graph.Node) contentChild;
			getContent().getNodes().remove(node);
		} else if (contentChild instanceof org.eclipse.gef.graph.Edge) {
			org.eclipse.gef.graph.Edge edge = (org.eclipse.gef.graph.Edge) contentChild;
			getContent().getEdges().remove(edge);
		} else {
			throw new IllegalArgumentException(
					"Cannot remove content child to graph: expected either Node or Edge, but got " + contentChild
							+ " instead.");
		}
	}

	@Override
	public Graph getContent() {
		return (Graph) super.getContent();
	}

	@Override
	public boolean isSelectable() {
		return false;
	}
}
