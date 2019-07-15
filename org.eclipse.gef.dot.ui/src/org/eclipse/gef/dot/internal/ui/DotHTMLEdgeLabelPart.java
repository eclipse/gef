/************************************************************************************************
 * Copyright (c) 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoey Prigge (itemis AG) - initial API and implementation (bug #321775)
 *
 ***********************************************************************************************/
package org.eclipse.gef.dot.internal.ui;

import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.zest.fx.ZestProperties;

import javafx.scene.Group;
import javafx.scene.Node;

public class DotHTMLEdgeLabelPart extends DotEdgeLabelPart {

	@Override
	protected Group doCreateVisual() {
		createText(); // to avoid NPE
		return new Group();
	}

	@Override
	protected void doRefreshVisual(Group visual) {
		super.doRefreshVisual(visual);
		refreshHtmlLabelNode();
	}

	protected void refreshHtmlLabelNode() {
		Node fx = getHtmlLabelNode();
		if (fx != null && !getVisual().getChildren().contains(fx)) {
			getVisual().getChildren().clear();
			getVisual().getChildren().add(fx);
		}
	}

	protected Node getHtmlLabelNode() {
		Edge edge = getContent().getKey();
		String attributeName = getContent().getValue();
		if (ZestProperties.LABEL__NE.equals(attributeName)) {
			return DotProperties.getHtmlLikeLabel(edge);
		} else if (ZestProperties.EXTERNAL_LABEL__NE.equals(attributeName)) {
			return DotProperties.getHtmlLikeExternalLabel(edge);
		} else if (ZestProperties.SOURCE_LABEL__E.equals(attributeName)) {
			return DotProperties.getHtmlLikeSourceLabel(edge);
		} else if (ZestProperties.TARGET_LABEL__E.equals(attributeName)) {
			return DotProperties.getHtmlLikeTargetLabel(edge);
		}
		return null;
	}
}
