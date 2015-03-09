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
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;

import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.layout.ILayoutContext;
import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.models.LayoutModel;

public abstract class AbstractLayoutBehavior extends AbstractBehavior<Node> {

	private ILayoutContext glc;

	private PropertyChangeListener layoutContextListener = new PropertyChangeListener() {
		@SuppressWarnings("unchecked")
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (LayoutModel.LAYOUT_CONTEXT_PROPERTY.equals(evt
					.getPropertyName())) {
				Map.Entry<Graph, ILayoutContext> oldContext = (Map.Entry<Graph, ILayoutContext>) evt
						.getOldValue();
				Map.Entry<Graph, ILayoutContext> newContext = (Map.Entry<Graph, ILayoutContext>) evt
						.getNewValue();
				onLayoutContextChange(oldContext.getKey(),
						(GraphLayoutContext) oldContext.getValue(),
						(GraphLayoutContext) newContext.getValue());
			}
		}
	};

	private ChangeListener<? super Bounds> layoutBoundsListener = new ChangeListener<Bounds>() {
		@Override
		public void changed(ObservableValue<? extends Bounds> observable,
				Bounds oldBounds, Bounds newBounds) {
			onBoundsChange(oldBounds, newBounds);
		}
	};

	private Runnable onFlushChanges = new Runnable() {
		@Override
		public void run() {
			onFlushChanges();
		}
	};

	@Override
	public void activate() {
		super.activate();
		// register listeners
		getDomainAdapter(LayoutModel.class).addPropertyChangeListener(
				layoutContextListener);
		getHost().getVisual().layoutBoundsProperty()
				.addListener(layoutBoundsListener);
		// check if a layout context is already available
		ILayoutContext layoutContext = getDomainAdapter(LayoutModel.class)
				.getLayoutContext(getGraph());
		if (layoutContext instanceof GraphLayoutContext) {
			onLayoutContextChange(getGraph(), null,
					(GraphLayoutContext) layoutContext);
		}
	}

	@Override
	public void deactivate() {
		getHost().getVisual().layoutBoundsProperty()
				.removeListener(layoutBoundsListener);
		getDomainAdapter(LayoutModel.class).removePropertyChangeListener(
				layoutContextListener);
		super.deactivate();
	}

	public <T> T getDomainAdapter(Class<T> key) {
		return getHost().getRoot().getViewer().getDomain().getAdapter(key);
	}

	protected abstract Graph getGraph();

	/**
	 * Called when the GLC changes.
	 *
	 * @param glc
	 */
	protected abstract void initializeLayout(GraphLayoutContext glc);

	/**
	 * Called when the visual bounds of the host visual (e.g. a node) are
	 * changed.
	 *
	 * @param oldBounds
	 * @param newBounds
	 */
	protected abstract void onBoundsChange(Bounds oldBounds, Bounds newBounds);

	/**
	 * Called after a layout pass.
	 */
	protected abstract void onFlushChanges();

	protected void onLayoutContextChange(Graph key, GraphLayoutContext oldGlc,
			GraphLayoutContext newGlc) {
		if (getGraph() != key) {
			return;
		}

		if (oldGlc != null && oldGlc == glc) {
			oldGlc.unscheduleFromFlushChanges(onFlushChanges);
			glc = null;
		}
		if (newGlc != null) {
			glc = newGlc;
			newGlc.scheduleForFlushChanges(onFlushChanges);
			initializeLayout(newGlc);
		}
	}

}
