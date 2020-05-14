/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.parts;

import java.util.Map;

import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.mvc.fx.parts.AbstractVisualPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.zest.fx.ZestProperties;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.text.Text;
import javafx.util.Pair;

/**
 * The {@link NodeLabelPart} is an {@link AbstractVisualPart} that is used to
 * display the external label of a node.
 *
 * @author mwienand
 *
 */
public class NodeLabelPart extends AbstractLabelPart {

	@Override
	public Point computeLabelPosition() {
		IVisualPart<? extends javafx.scene.Node> firstAnchorage = getFirstAnchorage();
		// determine bounds of anchorage visual
		Rectangle anchorageBounds = NodeUtils
				.sceneToLocal(getVisual().getParent(), NodeUtils.localToScene(firstAnchorage.getVisual(),
						FX2Geometry.toRectangle(firstAnchorage.getVisual().getLayoutBounds())))
				.getBounds();
		// determine text bounds
		Bounds textBounds = getVisual().getLayoutBounds();
		// TODO: compute better label position
		return new Point(anchorageBounds.getX() + anchorageBounds.getWidth() / 2 - textBounds.getWidth() / 2,
				anchorageBounds.getY() + anchorageBounds.getHeight());
	}

	@Override
	protected Group doCreateVisual() {
		Text text = createText();
		Group g = new Group();
		g.getStyleClass().add(NodePart.CSS_CLASS);
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

		if (attrs.containsKey(ZestProperties.EXTERNAL_LABEL_CSS_STYLE__NE)) {
			String textCssStyle = ZestProperties.getExternalLabelCssStyle(node);
			getVisual().setStyle(textCssStyle);
		}

		String label = ZestProperties.getExternalLabel(node);
		if (label != null) {
			getText().setText(label);
		}

		IVisualPart<? extends javafx.scene.Node> firstAnchorage = getFirstAnchorage();
		if (firstAnchorage == null) {
			return;
		}

		refreshPosition(getVisual(), getLabelPosition());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pair<Node, String> getContent() {
		return (Pair<Node, String>) super.getContent();
	}

	/**
	 * Returns the {@link NodePart} for which this {@link NodeLabelPart}
	 * displays the label.
	 *
	 * @return The {@link NodePart} for which this {@link NodeLabelPart}
	 *         displays the label.
	 */
	public IVisualPart<? extends javafx.scene.Node> getFirstAnchorage() {
		return getAnchoragesUnmodifiable().isEmpty() ? null
				: (IVisualPart<? extends javafx.scene.Node>) getAnchoragesUnmodifiable().keys().iterator().next();
	}

}