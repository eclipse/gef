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

import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.mvc.parts.AbstractVisualPart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.zest.fx.ZestProperties;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.util.Pair;

/**
 * The {@link EdgeLabelPart} is an {@link AbstractVisualPart} that is used to
 * display the label of an edge.
 *
 * @author mwienand
 *
 */
public class EdgeLabelPart extends AbstractLabelPart {

	@Override
	public Point computeLabelPosition() {
		Point position = null;
		String value = getContent().getValue();
		if (ZestProperties.LABEL__NE.equals(value)) {
			position = getMidPoint();
		} else if (ZestProperties.EXTERNAL_LABEL__NE.equals(value)) {
			position = getMidPoint();
		} else if (ZestProperties.SOURCE_LABEL__E.equals(value)) {
			position = getStartPoint();
		} else if (ZestProperties.TARGET_LABEL__E.equals(value)) {
			position = getEndPoint();
		}
		return position;
	}

	@Override
	protected Group createVisual() {
		Text text = createText();
		Group g = new Group();
		g.getStyleClass().add(EdgePart.CSS_CLASS);
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
		Edge edge = getContent().getKey();
		Map<String, Object> attrs = edge.attributesProperty();

		// label or external label (depends on which element we control)
		if (ZestProperties.LABEL__NE.equals(getContent().getValue())) {
			String label = ZestProperties.getLabel(edge);
			if (label != null) {
				getText().setText(label);
			}
			if (attrs.containsKey(ZestProperties.LABEL_CSS_STYLE__E)) {
				String textCssStyle = ZestProperties.getLabelCssStyle(edge);
				getVisual().setStyle(textCssStyle);
			}
		} else if (ZestProperties.EXTERNAL_LABEL__NE.equals(getContent().getValue())) {
			String label = ZestProperties.getExternalLabel(edge);
			if (label != null) {
				getText().setText(label);
			}
			if (attrs.containsKey(ZestProperties.EXTERNAL_LABEL_CSS_STYLE__NE)) {
				String textCssStyle = ZestProperties.getExternalLabelCssStyle(edge);
				getVisual().setStyle(textCssStyle);
			}
		} else if (ZestProperties.SOURCE_LABEL__E.equals(getContent().getValue())) {
			String label = ZestProperties.getSourceLabel(edge);
			if (label != null) {
				getText().setText(label);
			}
			if (attrs.containsKey(ZestProperties.SOURCE_LABEL_CSS_STYLE__E)) {
				String textCssStyle = ZestProperties.getSourceLabelCssStyle(edge);
				getVisual().setStyle(textCssStyle);
			}
		} else if (ZestProperties.TARGET_LABEL__E.equals(getContent().getValue())) {
			String label = ZestProperties.getTargetLabel(edge);
			if (label != null) {
				getText().setText(label);
			}
			if (attrs.containsKey(ZestProperties.TARGET_LABEL_CSS_STYLE__E)) {
				String textCssStyle = ZestProperties.getTargetLabelCssStyle(edge);
				getVisual().setStyle(textCssStyle);
			}
		}

		// XXX: We may be refreshed before being anchored on the anchorage.
		if (getFirstAnchorage() == null) {
			return;
		}

		refreshPosition(getVisual(), getStoredLabelPosition());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pair<Edge, String> getContent() {
		return (Pair<Edge, String>) super.getContent();
	}

	private Point getEndPoint() {
		// TODO: compute better label position
		Connection connection = getFirstAnchorage().getVisual();
		Point endPoint = connection.getEndPoint();
		Vector v = new Vector(endPoint, connection.getStartPoint()).getNormalized()
				.getMultiplied(getText().getLayoutBounds().getHeight());
		return NodeUtils.sceneToLocal(getVisual().getParent(),
				NodeUtils.localToScene(connection, endPoint.getTranslated(v.x, v.y)));
	}

	/**
	 * Returns the {@link IContentPart} for which this {@link EdgeLabelPart}
	 * displays the label.
	 *
	 * @return The {@link IContentPart} for which this {@link EdgeLabelPart}
	 *         displays the label.
	 */
	@SuppressWarnings("unchecked")
	protected IContentPart<Node, ? extends Connection> getFirstAnchorage() {
		return getAnchoragesUnmodifiable().isEmpty() ? null
				: (IContentPart<Node, ? extends Connection>) getAnchoragesUnmodifiable().keys().iterator().next();
	}

	private Point getMidPoint() {
		Connection connection = getFirstAnchorage().getVisual();
		Point midPoint = connection.getCenter();
		return NodeUtils.sceneToLocal(getVisual().getParent(), NodeUtils.localToScene(connection, midPoint));
	}

	private Point getStartPoint() {
		// TODO: compute better label position
		Connection connection = getFirstAnchorage().getVisual();
		Point startPoint = connection.getStartPoint();
		Vector v = new Vector(startPoint, connection.getEndPoint()).getNormalized()
				.getMultiplied(getText().getLayoutBounds().getHeight());
		return NodeUtils.sceneToLocal(getVisual().getParent(),
				NodeUtils.localToScene(connection, startPoint.getTranslated(v.x, v.y)));
	}
}