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
package org.eclipse.gef4.zest.fx.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.layout.LayoutAlgorithm;
import org.eclipse.gef4.layout.PropertiesHelper;
import org.eclipse.gef4.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.models.GridModel;
import org.eclipse.gef4.mvc.models.ViewportModel;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.models.ILayoutModel;

public class GraphRootPart extends FXRootPart {

	public static final String STYLES_CSS_FILE = GraphRootPart.class
			.getResource("styles.css").toExternalForm();

	public static final LayoutAlgorithm DEFAULT_LAYOUT_ALGORITHM = new SpringLayoutAlgorithm();

	private LayoutAlgorithm layoutAlgorithm = DEFAULT_LAYOUT_ALGORITHM;

	private PropertyChangeListener contentChanged = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (ContentModel.CONTENTS_PROPERTY.equals(evt.getPropertyName())) {
				Object content = evt.getNewValue();
				final GraphLayoutContext context = createLayoutContext(content);

				// set layout algorithm
				context.setStaticLayoutAlgorithm(layoutAlgorithm);

				// set layout context. other parts listen for the layout model
				// to send in their layout data
				getViewer().getDomain().getAdapter(ILayoutModel.class)
						.setLayoutContext(context);
				applyLayout(context);
			}
		}
	};

	private PropertyChangeListener viewportChanged = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			String name = evt.getPropertyName();
			if (ViewportModel.VIEWPORT_WIDTH_PROPERTY.equals(name)
					|| ViewportModel.VIEWPORT_HEIGHT_PROPERTY.equals(name)) {
				GraphLayoutContext context = getLayoutContext();
				if (context != null) {
					applyLayout(context);
				}
			}
		}
	};

	protected void applyLayout(final GraphLayoutContext context) {
		// get current viewport size
		ViewportModel viewportModel = getViewer().getAdapter(
				ViewportModel.class);
		double width = viewportModel.getWidth();
		double height = viewportModel.getHeight();
		PropertiesHelper.setBounds(context, new Rectangle(0, 0, width, height));

		// apply layout algorithm
		context.applyStaticLayout(true);
		context.flushChanges(false);
	}

	protected GraphLayoutContext createLayoutContext(Object content) {
		if (!(content instanceof List)) {
			throw new IllegalStateException(
					"Wrong content! Expected <List> but got <" + content + ">.");
		}
		if (((List<?>) content).size() != 1) {
			throw new IllegalStateException(
					"Wrong content! Expected <Graph> but got nothing.");
		}
		content = ((List<?>) content).get(0);
		if (!(content instanceof Graph)) {
			throw new IllegalStateException(
					"Wrong content! Expected <Graph> but got <" + content
							+ ">.");
		}
		final GraphLayoutContext context = new GraphLayoutContext(
				(Graph) content);
		ViewportModel viewport = getViewer().getAdapter(ViewportModel.class);
		PropertiesHelper.setBounds(context,
				new Rectangle(0, 0, viewport.getWidth(), viewport.getHeight()));
		return context;
	}

	@Override
	protected void doActivate() {
		super.doActivate();
		getViewer().getAdapter(ContentModel.class).addPropertyChangeListener(
				contentChanged);
		getViewer().getAdapter(ViewportModel.class).addPropertyChangeListener(
				viewportChanged);

		getViewer().getAdapter(GridModel.class).setShowGrid(false);

		// load stylesheet
		getVisual().getScene().getStylesheets().add(STYLES_CSS_FILE);
	}

	@Override
	protected void doDeactivate() {
		super.doDeactivate();
		getViewer().getAdapter(ContentModel.class)
				.removePropertyChangeListener(contentChanged);
		getViewer().getAdapter(ViewportModel.class)
				.removePropertyChangeListener(viewportChanged);

		// un-load stylesheet
		getVisual().getScene().getStylesheets().remove(STYLES_CSS_FILE);
	}

	protected GraphLayoutContext getLayoutContext() {
		ILayoutModel layoutModel = getViewer().getDomain().getAdapter(
				ILayoutModel.class);
		if (layoutModel == null) {
			return null;
		}
		return (GraphLayoutContext) layoutModel.getLayoutContext();
	}

	public void setLayoutAlgorithm(LayoutAlgorithm algorithm) {
		layoutAlgorithm = algorithm;
		GraphLayoutContext context = getLayoutContext();
		if (context != null) {
			context.setStaticLayoutAlgorithm(layoutAlgorithm);
			applyLayout(context);
		}
	}

}
