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
 *     Alexander Nyßen (itemis AG)  - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.behaviors;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.layout.LayoutContext;
import org.eclipse.gef4.layout.LayoutProperties;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.parts.NodePart;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Affine;

/**
 * The {@link NodeLayoutBehavior} is a {@link NodePart}-specific
 * {@link AbstractLayoutBehavior} implementation.
 *
 * @author mwienand
 * @author anyssen
 *
 */
// only applicable to NodePart (see #getHost())
public class NodeLayoutBehavior extends AbstractLayoutBehavior {

	@Override
	public NodePart getHost() {
		return (NodePart) super.getHost();
	}

	@Override
	protected LayoutContext getLayoutContext() {
		IContentPart<Node, ? extends Node> graphPart = getHost().getRoot().getViewer().getContentPartMap()
				.get(getHost().getContent().getGraph());
		return graphPart.getAdapter(GraphLayoutBehavior.class).getLayoutContext();
	}

	@Override
	protected void postLayout() {
		org.eclipse.gef4.graph.Node content = getHost().getContent();
		Point location = LayoutProperties.getLocation(content);
		Dimension size = LayoutProperties.getSize(content);

		// location is center, position is top-left
		ZestProperties.setPosition(content, location.getTranslated(size.getScaled(0.5).getNegated()));
		ZestProperties.setSize(content, size.getCopy());

		// refresh our visual
		getHost().refreshVisual();

		// update label positions (from visual locations) as they are not
		// provided by layout
		updateLabels();
	}

	@Override
	protected void preLayout() {
		org.eclipse.gef4.graph.Node content = getHost().getContent();
		Node visual = getHost().getVisual();

		Point position = ZestProperties.getPosition(content);
		Dimension size = ZestProperties.getSize(content);

		if (position != null && size != null) {
			// location is center while position is top-left
			LayoutProperties.setLocation(content, position.getTranslated(size.getScaled(0.5)));
			LayoutProperties.setSize(content, size.getCopy());
		} else {
			// no model information available yet, use visual location
			Bounds hostBounds = visual.getLayoutBounds();
			double minx = hostBounds.getMinX();
			double miny = hostBounds.getMinY();
			double maxx = hostBounds.getMaxX();
			double maxy = hostBounds.getMaxY();
			Affine transform = getHost().getAdapter(FXTransformPolicy.TRANSFORM_PROVIDER_KEY).get();
			LayoutProperties.setLocation(content, new Point(transform.getTx() + minx, transform.getTy() + miny));
			LayoutProperties.setSize(content, new Dimension(maxx - minx, maxy - miny));
		}

		// additional information inferred from visual
		LayoutProperties.setResizable(content, visual.isResizable());
	}
}