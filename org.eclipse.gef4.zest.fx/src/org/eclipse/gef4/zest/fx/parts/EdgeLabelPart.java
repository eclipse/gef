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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.fx.listeners.VisualChangeListener;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.parts.AbstractVisualPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.ZestProperties;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.util.Pair;

/**
 * The {@link EdgeLabelPart} is an {@link AbstractVisualPart} that is used to
 * display the label of an edge. It is created be an {@link EdgeContentPart}
 * upon activation. By separating the {@link EdgeLabelPart} from the
 * {@link EdgeContentPart}, bindings can be defined individually for both types
 * of parts.
 *
 * @author mwienand
 *
 */
public class EdgeLabelPart extends AbstractFXContentPart<Text> {

	/**
	 * The CSS class that is assigned to the visualization of the
	 * {@link EdgeLabelPart} of this {@link EdgeContentPart}.
	 */
	public static final String CSS_CLASS_LABEL = "label";

	private VisualChangeListener vcl = new VisualChangeListener() {
		@Override
		protected void boundsInLocalChanged(Bounds oldBounds, Bounds newBounds) {
			refreshVisual();
		}

		@Override
		protected void localToParentTransformChanged(Node observed, Transform oldTransform, Transform newTransform) {
			refreshVisual();
		}
	};
	private Translate translate;

	@Override
	protected void attachToAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
		vcl.register(anchorage.getVisual(), getVisual());
	}

	@Override
	protected Text createVisual() {
		Text text = new Text();
		text.setTextOrigin(VPos.TOP);
		text.setManaged(false);
		text.setPickOnBounds(true);
		// add translation transform to the Text
		translate = new Translate();
		text.getTransforms().add(translate);
		// add css class
		text.getStyleClass().add(CSS_CLASS_LABEL);
		return text;
	}

	@Override
	protected void detachFromAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
		vcl.unregister();
	}

	@Override
	protected void doRefreshVisual(Text visual) {
		Edge edge = getContent().getKey();
		Map<String, Object> attrs = edge.getAttrs();

		if (attrs.containsKey(ZestProperties.EDGE_LABEL_CSS_STYLE)) {
			String textCssStyle = ZestProperties.getEdgeLabelCssStyle(edge);
			getVisual().setStyle(textCssStyle);
		}

		// label
		Object label = attrs.get(ZestProperties.ELEMENT_LABEL);
		if (label instanceof String) {
			getVisual().setText((String) label);
		}

		EdgeContentPart edgeContentPart = getHost();
		if (edgeContentPart == null) {
			return;
		}
		// determine bounds of anchorage visual
		Rectangle bounds = edgeContentPart.getVisual().getCurveNode().getGeometry().getBounds();
		// determine text bounds
		Bounds textBounds = getVisual().getLayoutBounds();
		// compute label position
		visual.setTranslateX(bounds.getX() + bounds.getWidth() / 2 - textBounds.getWidth() / 2);
		visual.setTranslateY(bounds.getY() + bounds.getHeight() / 2 - textBounds.getHeight());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pair<Edge, String> getContent() {
		return (Pair<Edge, String>) super.getContent();
	}

	@Override
	public SetMultimap<? extends Object, String> getContentAnchorages() {
		SetMultimap<Object, String> contentAnchorages = HashMultimap.create();
		contentAnchorages.put(getContent().getKey(), getContent().getValue());
		return contentAnchorages;
	}

	@Override
	public List<? extends Object> getContentChildren() {
		return Collections.emptyList();
	}

	/**
	 * Returns the {@link EdgeContentPart} for which this {@link EdgeLabelPart}
	 * displays the label.
	 *
	 * @return The {@link EdgeContentPart} for which this {@link EdgeLabelPart}
	 *         displays the label.
	 */
	public EdgeContentPart getHost() {
		return getAnchorages().isEmpty() ? null : (EdgeContentPart) getAnchorages().keys().iterator().next();
	}

	/**
	 * Returns the offset that is currently applied to the label.
	 *
	 * @return The offset that is currently applied to the label.
	 */
	public Translate getOffset() {
		return translate;
	}

}