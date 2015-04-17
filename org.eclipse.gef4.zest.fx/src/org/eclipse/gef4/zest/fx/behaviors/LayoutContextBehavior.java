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
import javafx.scene.layout.Pane;

import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.layout.IConnectionLayout;
import org.eclipse.gef4.layout.ILayoutContext;
import org.eclipse.gef4.layout.ILayoutFilter;
import org.eclipse.gef4.layout.INodeLayout;
import org.eclipse.gef4.layout.LayoutProperties;
import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ViewportModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.layout.GraphEdgeLayout;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.models.HidingModel;
import org.eclipse.gef4.zest.fx.models.LayoutModel;
import org.eclipse.gef4.zest.fx.parts.GraphContentPart;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

/**
 * The LayoutContextBehavior is responsible for initiating layout passes.
 *
 * @author mwienand
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
	private GraphLayoutContext layoutContext;
	private Pane nestingVisual;

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
		// register listener for host property changes
		getHost().addPropertyChangeListener(hostPropertyChangeListener);

		// register listener for bounds changes
		Rectangle initialBounds = new Rectangle();
		if (getHost().getParent() == getHost().getRoot()) {
			/*
			 * Our graph is the root graph, therefore we listen to viewport
			 * changes to update the layout bounds in the context accordingly.
			 */
			ViewportModel viewportModel = getViewportModel();
			viewportModel
					.addPropertyChangeListener(viewportModelPropertyChangeListener);
			// read initial bounds
			FXViewer fxViewer = (FXViewer) getHost().getRoot().getViewer();
			double[] scrollableBounds = fxViewer.getScrollPane()
					.computeScrollableBoundsInLocal();
			initialBounds.setX(scrollableBounds[0]);
			initialBounds.setY(scrollableBounds[1]);
			initialBounds.setWidth(fxViewer.getScrollPane().getWidth());
			initialBounds.setHeight(fxViewer.getScrollPane().getHeight());
		} else if (getHost().getContent().getNestingNode() != null) {
			/*
			 * Our graph is nested inside a node of another graph, therefore we
			 * listen to changes of that node's layout-bounds.
			 */
			nestingVisual = getNestingPart().getNestedChildrenPane();
			nestingVisual.layoutBoundsProperty().addListener(
					nestingVisualLayoutBoundsChangeListener);
			Bounds layoutBounds = nestingVisual.getLayoutBounds();
			// read initial bounds
			initialBounds.setWidth(layoutBounds.getWidth());
			initialBounds.setHeight(layoutBounds.getHeight());
		} else {
			throw new IllegalStateException(
					"Graph is neither nested nor root?!");
		}

		// retrieve layout context
		layoutContext = getLayoutModel();

		// add layout filter for hidden/layout irrelevant elements
		final HidingModel hidingModel = getHost().getRoot().getViewer()
				.getAdapter(HidingModel.class);
		layoutContext.addLayoutFilter(new ILayoutFilter() {
			@Override
			public boolean isLayoutIrrelevant(IConnectionLayout connectionLayout) {
				return ZestProperties.getLayoutIrrelevant(
						((GraphEdgeLayout) connectionLayout).getEdge(), true);
			}

			@Override
			public boolean isLayoutIrrelevant(INodeLayout nodeLayout) {
				org.eclipse.gef4.graph.Node node = (org.eclipse.gef4.graph.Node) nodeLayout
						.getItems()[0];
				return ZestProperties.getLayoutIrrelevant(node, true)
						|| hidingModel.isHidden(node);
			}
		});

		// set initial bounds on the context
		LayoutProperties.setBounds(layoutContext, initialBounds);

		// register listener for layout context property changes after setting
		// the initial layout properties, so that this listener will not be
		// called for the initial layout properties
		layoutContext
				.addPropertyChangeListener(layoutContextPropertyChangeListener);
	}

	/**
	 * Performs one layout pass using the static layout algorithm that is
	 * configured for the layout context.
	 */
	protected void applyStaticLayout() {
		if (!isHostActive) {
			return;
		}
		layoutContext.applyStaticLayout(true);
		layoutContext.flushChanges(false);
	}

	@Override
	public void deactivate() {
		super.deactivate();
		// remove host property change listener
		getHost().removePropertyChangeListener(hostPropertyChangeListener);
		// remove property change listener from context
		if (layoutContext != null) {
			layoutContext
					.removePropertyChangeListener(layoutContextPropertyChangeListener);
		}
		if (nestingVisual == null) {
			// remove change listener from viewport model
			getViewportModel().removePropertyChangeListener(
					viewportModelPropertyChangeListener);
		} else {
			nestingVisual.layoutBoundsProperty().removeListener(
					nestingVisualLayoutBoundsChangeListener);
		}
		// nullify variables
		layoutContext = null;
		nestingVisual = null;
		isHostActive = false;
	}

	@Override
	public GraphContentPart getHost() {
		return (GraphContentPart) super.getHost();
	}

	protected LayoutModel getLayoutModel() {
		return getHost().<LayoutModel> getAdapter(LayoutModel.class);
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
				applyStaticLayout();
			}
		} else if (GraphContentPart.SYNC_COMPLETE_PROPERTY.equals(evt
				.getPropertyName()) && isHostActive) {
			if ((Boolean) evt.getNewValue()) {
				applyStaticLayout();
			}
		}
	}

	/**
	 * Re-layout when certain properties of the LayoutContext change:
	 * <ul>
	 * <li>static layout algorithm
	 * <li>layout bounds
	 * </ul>
	 *
	 * @param evt
	 *            A {@link PropertyChangeEvent} that was fired by the layout
	 *            context.
	 */
	protected void onLayoutContextPropertyChange(PropertyChangeEvent evt) {
		if (ILayoutContext.STATIC_LAYOUT_ALGORITHM_PROPERTY.equals(evt
				.getPropertyName())) {
			applyStaticLayout();
		} else if (LayoutProperties.BOUNDS_PROPERTY.equals(evt
				.getPropertyName())) {
			applyStaticLayout();
		}
	}

	/**
	 * Sets the layout bounds on the layout context for nested graphs.
	 *
	 * @param oldLayoutBounds
	 *            The previous nesting node's bounds.
	 * @param newLayoutBounds
	 *            The current nesting node's bounds.
	 */
	protected void onNestingVisualLayoutBoundsChange(Bounds oldLayoutBounds,
			Bounds newLayoutBounds) {
		// update layout bounds to match the nesting visual layout bounds
		double width = newLayoutBounds.getWidth();
		double height = newLayoutBounds.getHeight();
		Rectangle newBounds = new Rectangle(0, 0, width, height);
		if (!LayoutProperties.getBounds(layoutContext).equals(newBounds)) {
			LayoutProperties.setBounds(layoutContext, newBounds);
		}
	}

	protected void onViewportModelPropertyChange(PropertyChangeEvent evt) {
		if (!ViewportModel.VIEWPORT_WIDTH_PROPERTY
				.equals(evt.getPropertyName())
				&& !ViewportModel.VIEWPORT_HEIGHT_PROPERTY.equals(evt
						.getPropertyName())) {
			// only width and height changes are of interest
			return;
		}
		// update layout bounds to match the viewport bounds
		FXViewer fxViewer = (FXViewer) getHost().getRoot().getViewer();
		double[] scrollableBounds = fxViewer.getScrollPane()
				.computeScrollableBoundsInLocal();
		Rectangle newBounds = new Rectangle(scrollableBounds[0],
				scrollableBounds[1], fxViewer.getScrollPane().getWidth(),
				fxViewer.getScrollPane().getHeight());

		if (!LayoutProperties.getBounds(layoutContext).equals(newBounds)) {
			LayoutProperties.setBounds(layoutContext, newBounds);
		}
	}

}
