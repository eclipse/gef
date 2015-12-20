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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.common.attributes.IAttributeStore;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.layout.ILayoutAlgorithm;
import org.eclipse.gef4.layout.ILayoutContext;
import org.eclipse.gef4.mvc.behaviors.ContentBehavior;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.util.Pair;

/**
 * The {@link GraphContentPart} is the controller for a {@link Graph} content
 * object. It fires two special property changes:
 * <ul>
 * <li>{@link #ACTIVATION_COMPLETE_PROPERTY}
 * <li>{@link #SYNC_COMPLETE_PROPERTY}
 * </ul>
 *
 * @author mwienand
 *
 */
public class GraphContentPart extends AbstractFXContentPart<Group> {

	/**
	 * A property change event is fired as soon as {@link #activate()
	 * activation} has finished.
	 */
	public static final String ACTIVATION_COMPLETE_PROPERTY = "activationComplete";

	/**
	 * A property change event for this property name is fired when a content
	 * synchronization, based on a Graph property change, has finished.
	 */
	public static final String SYNC_COMPLETE_PROPERTY = "synchronizationComplete";

	private PropertyChangeListener graphPropertyChangeListener = new PropertyChangeListener() {
		@SuppressWarnings("unchecked")
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (IAttributeStore.ATTRIBUTES_PROPERTY.equals(evt.getPropertyName())) {
				// the layout algorithm might have changed
				refreshVisual();
			} else if (Graph.NODES_PROPERTY.equals(evt.getPropertyName())
					|| Graph.EDGES_PROPERTY.equals(evt.getPropertyName())) {
				// construct new layout context
				getAdapter(GraphLayoutContext.class).setGraph(getContent());
				getAdapter(ContentBehavior.class).synchronizeContentChildren(getContentChildren());

				pcs.firePropertyChange(SYNC_COMPLETE_PROPERTY, false, true);
			}
		}
	};

	@Override
	protected void addChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		getVisual().getChildren().add(index, child.getVisual());
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
		getContent().addPropertyChangeListener(graphPropertyChangeListener);
		pcs.firePropertyChange(ACTIVATION_COMPLETE_PROPERTY, false, true);
		refreshVisual();
	}

	@Override
	protected void doDeactivate() {
		getContent().removePropertyChangeListener(graphPropertyChangeListener);
		super.doDeactivate();
	}

	@Override
	public void doRefreshVisual(Group visual) {
		// set layout algorithm from Graph on the context
		setGraphLayoutAlgorithm();
		// TODO: setGraphStyleSheet();
	}

	@Override
	public Graph getContent() {
		return (Graph) super.getContent();
	}

	@Override
	public SetMultimap<? extends Object, String> getContentAnchorages() {
		return HashMultimap.create();
	}

	@Override
	public List<Object> getContentChildren() {
		List<Object> children = new ArrayList<>();
		children.addAll(getContent().getEdges());
		for (Edge e : getContent().getEdges()) {
			children.add(new Pair<>(e, "LABEL"));
		}
		children.addAll(getContent().getNodes());
		return children;
	}

	@Override
	protected void removeChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		getVisual().getChildren().remove(child.getVisual());
	}

	@Override
	public void setContent(Object content) {
		super.setContent(content);
		getAdapter(GraphLayoutContext.class).setGraph(getContent());
	}

	private void setGraphLayoutAlgorithm() {
		Object algo = getContent().getAttributes().get(ZestProperties.GRAPH_LAYOUT_ALGORITHM);
		if (algo instanceof ILayoutAlgorithm) {
			ILayoutAlgorithm layoutAlgorithm = (ILayoutAlgorithm) algo;
			ILayoutContext layoutContext = getAdapter(GraphLayoutContext.class);
			if (layoutContext != null && layoutContext.getLayoutAlgorithm() != algo) {
				layoutContext.setLayoutAlgorithm(layoutAlgorithm);
			}
		}
	}

}
