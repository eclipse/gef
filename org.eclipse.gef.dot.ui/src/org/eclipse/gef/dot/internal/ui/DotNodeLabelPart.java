/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Zoey Gerrit Prigge (itemis AG) - initial API and implementation (bug #541056)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui;

import java.util.Map;

import org.eclipse.gef.graph.Node;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.gef.zest.fx.parts.NodeLabelPart;

import javafx.scene.Group;

public class DotNodeLabelPart extends NodeLabelPart {
	/**
	 * The implementation of this class is mainly taken from the
	 * org.eclipse.gef.zest.fx.parts.NodeLabelPart java class.
	 *
	 * Modification added: applying the external label css style on the Text
	 * widget instead of its parent Group.
	 */
	@Override
	protected void doRefreshVisual(Group visual) {
		Node node = getContent().getKey();
		Map<String, Object> attrs = node.attributesProperty();

		if (attrs.containsKey(ZestProperties.EXTERNAL_LABEL_CSS_STYLE__NE)) {
			String textCssStyle = ZestProperties.getExternalLabelCssStyle(node);
			getText().setStyle(textCssStyle);
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
}
