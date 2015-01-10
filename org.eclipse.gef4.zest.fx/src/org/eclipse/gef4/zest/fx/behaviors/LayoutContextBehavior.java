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
import java.util.List;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.layout.LayoutProperties;
import org.eclipse.gef4.layout.interfaces.LayoutContext;
import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.models.ViewportModel;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.models.LayoutModel;

public class LayoutContextBehavior extends AbstractBehavior<Node> {

	private PropertyChangeListener layoutContextChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			// re-layout in case pruning state or (static) layout algorithm
			// changes
			if ("pruned".equals(evt.getPropertyName())
					|| LayoutContext.STATIC_LAYOUT_ALGORITHM_PROPERTY.equals(evt.getPropertyName())) {
				GraphLayoutContext context = getLayoutContext();
				if (context != null) {
					applyStaticLayout(context);
				}
			}
		}
	};

	private PropertyChangeListener contentsChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (ContentModel.CONTENTS_PROPERTY.equals(evt.getPropertyName())) {
				// create context from content
				Object content = evt.getNewValue();
				final GraphLayoutContext context = createLayoutContext(content);

				// get layout model
				LayoutModel layoutModel = getViewer().getDomain().getAdapter(LayoutModel.class);

				// remove change listener from old context
				LayoutContext oldContext = layoutModel.getLayoutContext();
				if (oldContext instanceof GraphLayoutContext) {
					((GraphLayoutContext) oldContext).removePropertyChangeListener(layoutContextChangeListener);
				}
				// add change listener to new context
				context.addPropertyChangeListener(layoutContextChangeListener);

				// set layout context. other parts listen for the layout model
				// to send in their layout data
				layoutModel.setLayoutContext(context);
				applyStaticLayout(context);
			}
		}
	};

	private PropertyChangeListener viewportChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			String name = evt.getPropertyName();
			if (ViewportModel.VIEWPORT_WIDTH_PROPERTY.equals(name)
					|| ViewportModel.VIEWPORT_HEIGHT_PROPERTY.equals(name)) {
				GraphLayoutContext context = getLayoutContext();
				if (context != null) {
					applyStaticLayout(context);
				}
			}
		}
	};

	@Override
	public void activate() {
		super.activate();
		// register listeners
		getViewer().getAdapter(ContentModel.class).addPropertyChangeListener(contentsChangeListener);
		getViewer().getAdapter(ViewportModel.class).addPropertyChangeListener(viewportChangeListener);
	}

	protected void applyStaticLayout(final GraphLayoutContext context) {
		// get current viewport size
		ViewportModel viewportModel = getViewer().getAdapter(ViewportModel.class);
		double width = viewportModel.getWidth();
		double height = viewportModel.getHeight();
		LayoutProperties.setBounds(context, new Rectangle(0, 0, width, height));

		// apply static layout algorithm
		context.applyStaticLayout(true);
		context.flushChanges(false);
	}

	protected GraphLayoutContext createLayoutContext(Object content) {
		if (!(content instanceof List)) {
			throw new IllegalStateException("Wrong content! Expected <List> but got <" + content + ">.");
		}
		if (((List<?>) content).size() != 1) {
			throw new IllegalStateException("Wrong content! Expected <Graph> but got nothing.");
		}
		content = ((List<?>) content).get(0);
		if (!(content instanceof Graph)) {
			throw new IllegalStateException("Wrong content! Expected <Graph> but got <" + content + ">.");
		}
		// TODO: we should extract this into a factory, so clients can influence
		// the creation process
		final GraphLayoutContext context = new GraphLayoutContext((Graph) content);
		ViewportModel viewport = getViewer().getAdapter(ViewportModel.class);
		LayoutProperties.setBounds(context, new Rectangle(0, 0, viewport.getWidth(), viewport.getHeight()));
		return context;
	}

	@Override
	public void deactivate() {
		super.deactivate();
		getViewer().getAdapter(ContentModel.class).removePropertyChangeListener(contentsChangeListener);
		getViewer().getAdapter(ViewportModel.class).removePropertyChangeListener(viewportChangeListener);
	}

	protected GraphLayoutContext getLayoutContext() {
		LayoutModel layoutModel = getViewer().getDomain().getAdapter(LayoutModel.class);
		if (layoutModel == null) {
			return null;
		}
		return (GraphLayoutContext) layoutModel.getLayoutContext();
	}

	protected IViewer<Node> getViewer() {
		return getHost().getRoot().getViewer();
	}

}
