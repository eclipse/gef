/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *     Alexander Nyßen (itemis AG)  - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.behaviors;

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.layout.LayoutProperties;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.gef.zest.fx.parts.NodePart;

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

	private Dimension preLayoutSize = null;

	@Override
	public NodePart getHost() {
		return (NodePart) super.getHost();
	}

	@Override
	protected LayoutContext getLayoutContext() {
		IContentPart<? extends Node> graphPart = getHost().getRoot().getViewer().getContentPartMap()
				.get(getHost().getContent().getGraph());
		return graphPart.getAdapter(GraphLayoutBehavior.class).getLayoutContext();
	}

	@Override
	protected void postLayout() {
		org.eclipse.gef.graph.Node content = getHost().getContent();

		// update size
		Dimension postLayoutSize = LayoutProperties.getSize(content);
		if (postLayoutSize != null) {
			ZestProperties.setSize(content, postLayoutSize);
		}

		// location is center, position is top-left
		Point postLayoutLocation = LayoutProperties.getLocation(content);
		if (postLayoutLocation != null) {
			ZestProperties.setPosition(content, postLayoutLocation.getTranslated(
					(postLayoutSize == null ? preLayoutSize : postLayoutSize).getScaled(0.5).getNegated()));
		}

		// refresh our visual
		getHost().refreshVisual();

		// update label positions (from visual locations) as they are not
		// provided by layout
		layoutLabels();
	}

	@Override
	protected void preLayout() {
		org.eclipse.gef.graph.Node content = getHost().getContent();

		Node visual = getHost().getVisual();
		Bounds hostBounds = visual.getLayoutBounds();
		double minx = hostBounds.getMinX();
		double miny = hostBounds.getMinY();
		double maxx = hostBounds.getMaxX();
		double maxy = hostBounds.getMaxY();
		Affine transform = getHost().getVisualTransform();

		// initialize size
		if (ZestProperties.getSize(content) != null) {
			// no model information available yet, use visual location
			preLayoutSize = ZestProperties.getSize(content).getCopy();
		} else {
			preLayoutSize = new Dimension(maxx - minx, maxy - miny);
		}

		// constrain to visual's min-size
		{
			double minWidth = visual.minWidth(-1);
			double minHeight = visual.minHeight(-1);
			if (preLayoutSize.width < minWidth) {
				preLayoutSize.width = minWidth;
			}
			if (preLayoutSize.height < minHeight) {
				preLayoutSize.height = minHeight;
			}
		}

		// System.out.println("pre layout size of " + content + ": " +
		// preLayoutSize);
		LayoutProperties.setSize(content, preLayoutSize.getCopy());

		// initialize location (layout location is center while visual position
		// is top-left)
		if (ZestProperties.getPosition(content) != null) {
			LayoutProperties.setLocation(content,
					ZestProperties.getPosition(content).getTranslated(preLayoutSize.getScaled(0.5)));
		} else {
			// no model information available yet, use visual location
			LayoutProperties.setLocation(content, new Point(transform.getTx() + minx + (maxx - minx) / 2,
					transform.getTy() + miny + (maxy - miny) / 2));
		}

		// additional information inferred from visual
		LayoutProperties.setResizable(content, visual.isResizable());
	}
}