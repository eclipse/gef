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
import org.eclipse.gef4.geometry.planar.Point;
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
import javafx.scene.Group;
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
public class EdgeLabelPart extends AbstractFXContentPart<Group> {

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

	private Text text;

	@Override
	protected void attachToAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
		vcl.register(anchorage.getVisual(), getVisual());
	}

	@Override
	protected Group createVisual() {
		text = new Text();
		text.setTextOrigin(VPos.TOP);
		text.setManaged(false);
		text.setPickOnBounds(true);
		// add translation transform to the Text
		translate = new Translate();
		text.getTransforms().add(translate);
		// add css class
		text.getStyleClass().add(CSS_CLASS_LABEL);

		Group g = new Group();
		g.getStyleClass().add(EdgeContentPart.CSS_CLASS);
		g.getChildren().add(text);
		return g;
	}

	@Override
	protected void detachFromAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
		vcl.unregister();
	}

	@Override
	protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
		SetMultimap<Object, String> contentAnchorages = HashMultimap.create();
		contentAnchorages.put(getContent().getKey(), getContent().getValue());
		return contentAnchorages;
	}

	@Override
	protected List<? extends Object> doGetContentChildren() {
		return Collections.emptyList();
	}

	@Override
	protected void doRefreshVisual(Group visual) {
		Edge edge = getContent().getKey();
		Map<String, Object> attrs = edge.attributesProperty();

		if (attrs.containsKey(ZestProperties.ELEMENT_LABEL_CSS_STYLE)) {
			String textCssStyle = ZestProperties.getLabelCssStyle(edge);
			getVisual().setStyle(textCssStyle);
		}

		// label or external label (depends on which element we control)
		if (ZestProperties.ELEMENT_LABEL.equals(getContent().getValue())) {
			String label = ZestProperties.getLabel(edge);
			if (label != null) {
				text.setText(label);
			}
		} else if (ZestProperties.ELEMENT_EXTERNAL_LABEL.equals(getContent().getValue())) {
			String label = ZestProperties.getExternalLabel(edge);
			if (label != null) {
				text.setText(label);
			}
		} else if (ZestProperties.EDGE_SOURCE_LABEL.equals(getContent().getValue())) {
			String label = ZestProperties.getSourceLabel(edge);
			if (label != null) {
				text.setText(label);
			}
		} else if (ZestProperties.EDGE_TARGET_LABEL.equals(getContent().getValue())) {
			String label = ZestProperties.getTargetLabel(edge);
			if (label != null) {
				text.setText(label);
			}
		}

		EdgeContentPart edgeContentPart = getHost();
		if (edgeContentPart == null) {
			return;
		}

		Point labelPosition = null;
		if (ZestProperties.ELEMENT_LABEL.equals(getContent().getValue())) {
			labelPosition = ZestProperties.getLabelPosition(edge);
		} else if (ZestProperties.ELEMENT_EXTERNAL_LABEL.equals(getContent().getValue())) {
			labelPosition = ZestProperties.getExternalLabelPosition(edge);
		} else if (ZestProperties.EDGE_SOURCE_LABEL.equals(getContent().getValue())) {
			labelPosition = ZestProperties.getSourceLabelPosition(edge);
		} else if (ZestProperties.EDGE_TARGET_LABEL.equals(getContent().getValue())) {
			labelPosition = ZestProperties.getTargetLabelPosition(edge);
		}

		if (labelPosition != null) {
			refreshPosition(getVisual(), labelPosition);
		} else {
			// determine bounds of anchorage visual
			Rectangle bounds = edgeContentPart.getVisual().getCurveNode().getGeometry().getBounds();
			// determine text bounds
			Bounds textBounds = getVisual().getLayoutBounds();
			// compute label position
			refreshPosition(getVisual(), new Point(bounds.getX() + bounds.getWidth() / 2 - textBounds.getWidth() / 2,
					bounds.getY() + bounds.getHeight() / 2 - textBounds.getHeight()));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Pair<Edge, String> getContent() {
		return (Pair<Edge, String>) super.getContent();
	}

	/**
	 * Returns the {@link EdgeContentPart} for which this {@link EdgeLabelPart}
	 * displays the label.
	 *
	 * @return The {@link EdgeContentPart} for which this {@link EdgeLabelPart}
	 *         displays the label.
	 */
	public EdgeContentPart getHost() {
		return getAnchoragesUnmodifiable().isEmpty() ? null
				: (EdgeContentPart) getAnchoragesUnmodifiable().keys().iterator().next();
	}

	/**
	 * Returns the offset that is currently applied to the label.
	 *
	 * @return The offset that is currently applied to the label.
	 */
	public Translate getOffset() {
		return translate;
	}

	/**
	 * Adjusts the label's position to fit the given {@link Point}.
	 *
	 * @param visual
	 *            This node's visual.
	 * @param position
	 *            This node's position.
	 */
	protected void refreshPosition(Node visual, Point position) {
		if (position != null) {
			// TODO: use transform policy, so positions are persisted properly
			getVisual().setTranslateX(position.x);
			getVisual().setTranslateY(position.y);

			// // translate
			// FXTransformPolicy transformPolicy =
			// getAdapter(FXTransformPolicy.class);
			// transformPolicy.init();
			// transformPolicy.setTransform(new AffineTransform(1, 0, 0, 1,
			// position.x, position.y));
			// try {
			// transformPolicy.commit().execute(null, null);
			// } catch (ExecutionException e) {
			// throw new IllegalStateException(e);
			// }
		}
	}

}