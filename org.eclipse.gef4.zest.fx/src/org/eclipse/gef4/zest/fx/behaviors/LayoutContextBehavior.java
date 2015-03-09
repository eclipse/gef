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

import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.layout.LayoutProperties;
import org.eclipse.gef4.layout.interfaces.ConnectionLayout;
import org.eclipse.gef4.layout.interfaces.ILayoutFilter;
import org.eclipse.gef4.layout.interfaces.LayoutContext;
import org.eclipse.gef4.layout.interfaces.NodeLayout;
import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.models.ViewportModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.layout.GraphEdgeLayout;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.models.LayoutModel;
import org.eclipse.gef4.zest.fx.parts.GraphContentPart;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

/**
 * The LayoutContextBehavior is responsible for the creation and distribution of
 * a LayoutContext for a GraphContentPart and for initiating layout passes in
 * this context.
 *
 * @author wienand
 *
 */
// only applicable for GraphContentPart (see #getHost())
public class LayoutContextBehavior extends AbstractBehavior<Node> {

	private PropertyChangeListener layoutContextPropertyChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			onLayoutContextPropertyChange(evt);
		}
	};

	private boolean isHostActive;
	private boolean isRootContent;
	private GraphLayoutContext layoutContext;

	private PropertyChangeListener viewportModelPropertyChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			onViewportModelPropertyChange(evt);
		}
	};

	private ChangeListener<? super Bounds> nestingVisualLayoutBoundsChangeListener = new ChangeListener<Bounds>() {
		@Override
		public void changed(ObservableValue<? extends Bounds> observable,
				Bounds oldLayoutBounds, Bounds newLayoutBounds) {
			onNestingVisualLayoutBoundsChange(oldLayoutBounds, newLayoutBounds);
		}
	};

	private PropertyChangeListener hostPropertyChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			onHostPropertyChange(evt);
		}
	};

	@Override
	public void activate() {
		super.activate();
		getHost().addPropertyChangeListener(hostPropertyChangeListener);
	}

	/**
	 * Performs one layout pass using the static layout algorithm that is
	 * configured for the given context.
	 *
	 * @param context
	 */
	protected void applyStaticLayout(final GraphLayoutContext context) {
		if (!isHostActive) {
			return;
		}
		context.applyStaticLayout(true);
		context.flushChanges(false);
	}

	protected GraphLayoutContext createLayoutContext(Graph content) {
		GraphLayoutContext graphLayoutContext = new GraphLayoutContext(content);
		graphLayoutContext.addLayoutFilter(new ILayoutFilter() {
			@Override
			public boolean isLayoutIrrelevant(ConnectionLayout connectionLayout) {
				return ZestProperties
						.getLayoutIrrelevant(((GraphEdgeLayout) connectionLayout)
								.getEdge());
			}

			@Override
			public boolean isLayoutIrrelevant(NodeLayout nodeLayout) {
				org.eclipse.gef4.graph.Node node = (org.eclipse.gef4.graph.Node) nodeLayout
						.getItems()[0];
				return ZestProperties.getLayoutIrrelevant(node, true)
						|| ZestProperties.getHidden(node, true);
			}
		});
		return graphLayoutContext;
	}

	@Override
	public void deactivate() {
		super.deactivate();
		// remove property change listener from context
		if (layoutContext != null) {
			layoutContext
					.removePropertyChangeListener(layoutContextPropertyChangeListener);
		}
		if (isRootContent) {
			// remove change listener from viewport model
			getViewportModel().removePropertyChangeListener(
					viewportModelPropertyChangeListener);
		} else {
			// remove change listener from layout-bounds-property
			getNestingPart().getVisual().layoutBoundsProperty()
					.removeListener(nestingVisualLayoutBoundsChangeListener);
		}
		// nullify variables
		layoutContext = null;
		isRootContent = false;
		isHostActive = false;
		// remove layout context from model
		getLayoutModel().removeLayoutContext(getHost().getContent());
	}

	private void distributeLayoutContext() {
		ContentModel contentModel = getHost().getRoot().getViewer()
				.getAdapter(ContentModel.class);
		Object content = contentModel.getContents().get(0);
		Rectangle initialBounds = new Rectangle();
		if (getHost().getContent() == content) {
			/*
			 * Our graph is the root graph, therefore we listen to viewport
			 * changes to update the layout bounds in the context accordingly.
			 */
			isRootContent = true;
			ViewportModel viewportModel = getViewportModel();
			viewportModel
					.addPropertyChangeListener(viewportModelPropertyChangeListener);
			// read initial bounds
			initialBounds.setWidth(viewportModel.getWidth());
			initialBounds.setHeight(viewportModel.getHeight());
		} else if (getHost().getContent().getNestingNode() != null) {
			/*
			 * Our graph is nested inside a node of another graph, therefore we
			 * listen to changes of that node's layout-bounds.
			 */
			Node visual = getNestingPart().getNestedChildrenPane();
			visual.layoutBoundsProperty().addListener(
					nestingVisualLayoutBoundsChangeListener);
			Bounds layoutBounds = visual.getLayoutBounds();
			// read initial bounds
			initialBounds.setWidth(layoutBounds.getWidth());
			initialBounds.setHeight(layoutBounds.getHeight());
		} else {
			throw new IllegalStateException(
					"Graph is neither nested nor root?!");
		}

		// create layout context
		layoutContext = createLayoutContext(getHost().getContent());
		Graph graph = layoutContext.getGraph();

		// get layout model
		LayoutModel layoutModel = getLayoutModel();

		// set initial bounds
		LayoutProperties.setBounds(layoutContext, initialBounds);

		// add change listener to new context
		layoutContext
				.addPropertyChangeListener(layoutContextPropertyChangeListener);

		// set layout context. other parts listen for the layout model
		// to send in their layout data
		layoutModel.setLayoutContext(graph, layoutContext);

		// initial layout pass
		applyStaticLayout(layoutContext);
	}

	@Override
	public GraphContentPart getHost() {
		return (GraphContentPart) super.getHost();
	}

	protected LayoutModel getLayoutModel() {
		return getHost().getRoot().getViewer().getDomain()
				.<LayoutModel> getAdapter(LayoutModel.class);
	}

	protected NodeContentPart getNestingPart() {
		org.eclipse.gef4.graph.Node nestingNode = getHost().getContent()
				.getNestingNode();
		IContentPart<Node, ? extends Node> nestingNodePart = getHost()
				.getRoot().getViewer().getContentPartMap().get(nestingNode);
		return (NodeContentPart) nestingNodePart;
	}

	protected ViewportModel getViewportModel() {
		return getHost().getRoot().getViewer()
				.<ViewportModel> getAdapter(ViewportModel.class);
	}

	protected void onHostPropertyChange(PropertyChangeEvent evt) {
		if (GraphContentPart.ACTIVATION_COMPLETE_PROPERTY.equals(evt
				.getPropertyName())) {
			if ((Boolean) evt.getNewValue()) {
				isHostActive = true;
				distributeLayoutContext();
			}
		}
	}

	/**
	 * Relayout when certain properties of the LayoutContext change:
	 * <ul>
	 * <li>static layout algorithm
	 * <li>layout bounds
	 * </ul>
	 *
	 * @param evt
	 */
	protected void onLayoutContextPropertyChange(PropertyChangeEvent evt) {
		if (layoutContext == null) {
			return;
		}
		if (LayoutContext.STATIC_LAYOUT_ALGORITHM_PROPERTY.equals(evt
				.getPropertyName())) {
			applyStaticLayout(layoutContext);
		} else if (LayoutProperties.BOUNDS_PROPERTY.equals(evt
				.getPropertyName())) {
			applyStaticLayout(layoutContext);
		}
	}

	protected void onNestingVisualLayoutBoundsChange(Bounds oldLayoutBounds,
			Bounds newLayoutBounds) {
		if (layoutContext == null) {
			return;
		}
		// update layout bounds to match the nesting visual layout bounds
		double width = newLayoutBounds.getWidth();
		double height = newLayoutBounds.getHeight();
		Rectangle newBounds = new Rectangle(0, 0, width, height);
		if (!LayoutProperties.getBounds(layoutContext).equals(newBounds)) {
			LayoutProperties.setBounds(layoutContext, newBounds);
		}
	}

	protected void onViewportModelPropertyChange(PropertyChangeEvent evt) {
		if (layoutContext == null) {
			return;
		}
		if (!ViewportModel.VIEWPORT_WIDTH_PROPERTY
				.equals(evt.getPropertyName())
				&& !ViewportModel.VIEWPORT_HEIGHT_PROPERTY.equals(evt
						.getPropertyName())) {
			// only width and height changes are of interest
			return;
		}
		// update layout bounds to match the viewport bounds
		double width = getViewportModel().getWidth();
		double height = getViewportModel().getHeight();
		Rectangle newBounds = new Rectangle(0, 0, width, height);
		if (!LayoutProperties.getBounds(layoutContext).equals(newBounds)) {
			LayoutProperties.setBounds(layoutContext, newBounds);
		}
	}

}
