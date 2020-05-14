/*******************************************************************************
 * Copyright (c) 2015, 2019 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *     Tamas Miklossy   (itemis AG) - edge tooltip support (bug #530658)
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.parts;

import java.util.Map;

import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.euclidean.Vector;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.zest.fx.ZestProperties;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
import javafx.util.Pair;

/**
 * The {@link EdgeLabelPart} is an {@link AbstractLabelPart} that is used to
 * display the label of an edge.
 *
 * @author mwienand
 *
 */
public class EdgeLabelPart extends AbstractLabelPart {

	private Tooltip[] tooltipNodes = new Tooltip[4];

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
	protected Group doCreateVisual() {
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
			if (attrs.containsKey(ZestProperties.LABEL_CSS_STYLE__NE)) {
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

		refreshPosition(getVisual(), getLabelPosition());

		refreshTooltip();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pair<Edge, String> getContent() {
		return (Pair<Edge, String>) super.getContent();
	}

	/**
	 * Computes the end position for placing a label. The position is interpreted in
	 * the parent coordinate system of this part's visual.
	 *
	 * @return The end position for placing a label.
	 */
	protected Point getEndPoint() {
		Connection connection = getFirstAnchorage().getVisual();
		Point endPoint = connection.getEndPoint();
		Vector v = new Vector(endPoint, connection.getStartPoint());
		if (!v.isNull()) {
			v = v.getNormalized().getMultiplied(getText().getLayoutBounds().getHeight());
		}
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
	protected IContentPart<? extends Connection> getFirstAnchorage() {
		return getAnchoragesUnmodifiable().isEmpty() ? null
				: (IContentPart<? extends Connection>) getAnchoragesUnmodifiable().keys().iterator().next();
	}

	/**
	 * Computes the middle position for placing a label. The position is interpreted
	 * in the parent coordinate system of this part's visual.
	 *
	 * @return The middle position for placing a label.
	 */
	protected Point getMidPoint() {
		Connection connection = getFirstAnchorage().getVisual();
		Point midPoint = connection.getCenter();
		return NodeUtils.sceneToLocal(getVisual().getParent(), NodeUtils.localToScene(connection, midPoint));
	}

	/**
	 * Computes the start position for placing a label. The position is interpreted
	 * in the parent coordinate system of this part's visual.
	 *
	 * @return The start position for placing a label.
	 */
	protected Point getStartPoint() {
		Connection connection = getFirstAnchorage().getVisual();
		Point startPoint = connection.getStartPoint();
		Vector v = new Vector(startPoint, connection.getEndPoint());
		if (!v.isNull()) {
			v = v.getNormalized().getMultiplied(getText().getLayoutBounds().getHeight());
		}
		return NodeUtils.sceneToLocal(getVisual().getParent(),
				NodeUtils.localToScene(connection, startPoint.getTranslated(v.x, v.y)));
	}

	/**
	 * Array containing the {@link Tooltip} nodes of this {@link EdgeLabelPart} in
	 * the following order:
	 * <ul>
	 * <li>[0]: tooltip node on the label of the edge.
	 * <li>[1]: tooltip node on the source label of the edge.
	 * <li>[2]: tootlip node on the target label of the edge.
	 * <li>[3]: tooltip node on the external label of the edge.
	 * </ul>
	 *
	 * @return Array of {@link Tooltip}s.
	 * @since 5.1
	 */
	protected Tooltip[] getTooltipNodes() {
		return tooltipNodes;
	}

	/**
	 * Changes the tooltip of this {@link EdgeLabelPart} to the given value.
	 *
	 * @since 5.1
	 */
	protected void refreshTooltip() {
		Pair<Edge, String> content = getContent();
		Edge edge = content.getKey();
		String zestProperty = content.getValue();
		switch (zestProperty) {
		case ZestProperties.LABEL__NE:
			refreshTooltip(tooltipNodes[0], ZestProperties.getLabelTooltip(edge));
			break;
		case ZestProperties.SOURCE_LABEL__E:
			refreshTooltip(tooltipNodes[1], ZestProperties.getSourceLabelTooltip(edge));
			break;
		case ZestProperties.TARGET_LABEL__E:
			refreshTooltip(tooltipNodes[2], ZestProperties.getTargetLabelTooltip(edge));
			break;
		case ZestProperties.EXTERNAL_LABEL__NE:
			refreshTooltip(tooltipNodes[3], ZestProperties.getExternalLabelTooltip(edge));
			break;
		default:
			break;
		}
	}

	/**
	 * Changes the tooltip of this {@link EdgeLabelPart} to the given value.
	 *
	 * @param tooltipNode
	 *            the tooltip node
	 * @param tooltip
	 *            the tooltip text
	 * @since 5.1
	 */
	protected void refreshTooltip(Tooltip tooltipNode, String tooltip) {
		if (tooltip != null && !tooltip.isEmpty()) {
			if (tooltipNode == null) {
				tooltipNode = new Tooltip(tooltip);
				Tooltip.install(getVisual(), tooltipNode);
			} else {
				tooltipNode.setText(tooltip);
			}
		} else {
			if (tooltipNode != null) {
				Tooltip.uninstall(getVisual(), tooltipNode);
			}
		}
	}
}