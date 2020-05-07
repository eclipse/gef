/************************************************************************************************
 * Copyright (c) 2018, 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #538226)
 *
 ***********************************************************************************************/
package org.eclipse.gef.dot.internal.ui;

import java.util.Map;

import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.gef.zest.fx.parts.EdgeLabelPart;

import javafx.scene.Group;

/**
 * The implementation of this class is mainly taken from the
 * org.eclipse.gef.zest.fx.parts.EdgeLabelPart java class.
 *
 * Modification added: applying the label css style on the Text widget instead
 * of its parent Group.
 */
public class DotEdgeLabelPart extends EdgeLabelPart {

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
				getText().setStyle(textCssStyle);
			}
		} else if (ZestProperties.EXTERNAL_LABEL__NE
				.equals(getContent().getValue())) {
			String label = ZestProperties.getExternalLabel(edge);
			if (label != null) {
				getText().setText(label);
			}
			if (attrs
					.containsKey(ZestProperties.EXTERNAL_LABEL_CSS_STYLE__NE)) {
				String textCssStyle = ZestProperties
						.getExternalLabelCssStyle(edge);
				getText().setStyle(textCssStyle);
			}
		} else if (ZestProperties.SOURCE_LABEL__E
				.equals(getContent().getValue())) {
			String label = ZestProperties.getSourceLabel(edge);
			if (label != null) {
				getText().setText(label);
			}
			if (attrs.containsKey(ZestProperties.SOURCE_LABEL_CSS_STYLE__E)) {
				String textCssStyle = ZestProperties
						.getSourceLabelCssStyle(edge);
				getText().setStyle(textCssStyle);
			}
		} else if (ZestProperties.TARGET_LABEL__E
				.equals(getContent().getValue())) {
			String label = ZestProperties.getTargetLabel(edge);
			if (label != null) {
				getText().setText(label);
			}
			if (attrs.containsKey(ZestProperties.TARGET_LABEL_CSS_STYLE__E)) {
				String textCssStyle = ZestProperties
						.getTargetLabelCssStyle(edge);
				getText().setStyle(textCssStyle);
			}
		}

		// XXX: We may be refreshed before being anchored on the anchorage.
		if (getFirstAnchorage() == null) {
			return;
		}

		refreshPosition(getVisual(), getLabelPosition());

		refreshTooltip();
	}

}
