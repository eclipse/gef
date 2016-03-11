/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
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

import java.util.Map;

import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.mvc.parts.AbstractVisualPart;
import org.eclipse.gef4.zest.fx.ZestProperties;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.text.Text;
import javafx.util.Pair;

/**
 * The {@link NodeExternalLabelPart} is an {@link AbstractVisualPart} that is
 * used to display the external label of a node.
 *
 * @author mwienand
 *
 */
public class NodeExternalLabelPart extends AbstractLabelPart {

	@Override
	protected Group createVisual() {
		Text text = createText();
		Group g = new Group();
		g.getStyleClass().add(NodeContentPart.CSS_CLASS);
		g.getChildren().add(text);
		return g;
	}

	@Override
	protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
		SetMultimap<Object, String> contentAnchorages = HashMultimap.create();
		contentAnchorages.put(getContent().getKey(), getContent().getValue());
		return contentAnchorages;
	}

	@Override
	protected void doRefreshVisual(Group visual) {
		Node node = getContent().getKey();
		Map<String, Object> attrs = node.attributesProperty();

		if (attrs.containsKey(ZestProperties.ELEMENT_EXTERNAL_LABEL_CSS_STYLE)) {
			String textCssStyle = ZestProperties.getExternalLabelCssStyle(node);
			getVisual().setStyle(textCssStyle);
		}

		String label = ZestProperties.getExternalLabel(node);
		if (label != null) {
			getText().setText(label);
		}

		NodeContentPart NodeContentPart = getHost();
		if (NodeContentPart == null) {
			return;
		}

		Point labelPosition = ZestProperties.getExternalLabelPosition(node);
		if (labelPosition != null) {
			refreshPosition(getVisual(), labelPosition);
		} else {
			// determine bounds of anchorage visual
			Rectangle bounds = FX2Geometry.toRectangle(NodeContentPart.getVisual().getLayoutBounds());
			// determine text bounds
			Bounds textBounds = getVisual().getLayoutBounds();
			// compute label position
			refreshPosition(getVisual(), new Point(bounds.getX() + bounds.getWidth() / 2 - textBounds.getWidth() / 2,
					bounds.getY() + bounds.getHeight() / 2 - textBounds.getHeight()));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pair<Node, String> getContent() {
		return (Pair<Node, String>) super.getContent();
	}

	/**
	 * Returns the {@link NodeContentPart} for which this
	 * {@link NodeExternalLabelPart} displays the label.
	 *
	 * @return The {@link NodeContentPart} for which this
	 *         {@link NodeExternalLabelPart} displays the label.
	 */
	public NodeContentPart getHost() {
		return getAnchoragesUnmodifiable().isEmpty() ? null
				: (NodeContentPart) getAnchoragesUnmodifiable().keys().iterator().next();
	}

}