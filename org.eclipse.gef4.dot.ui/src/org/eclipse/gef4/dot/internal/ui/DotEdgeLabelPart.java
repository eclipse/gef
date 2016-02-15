/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #321775
 *
 *******************************************************************************/
package org.eclipse.gef4.dot.internal.ui;

import org.eclipse.gef4.dot.DotProperties;
import org.eclipse.gef4.zest.fx.parts.EdgeLabelPart;

import javafx.scene.Group;

public class DotEdgeLabelPart extends EdgeLabelPart {

	private HtmlLabel htmlLabel = new HtmlLabel();

	@Override
	protected Group createVisual() {
		Group group = super.createVisual();
		group.getChildren().add(htmlLabel);
		return group;
	}

	protected void doRefreshVisual(Group visual) {
		super.doRefreshVisual(visual);
		// check if HTML label
		Boolean isHtmlLabel = DotProperties.isHtmlLabel(getContent().getKey());
		if (isHtmlLabel == null) {
			isHtmlLabel = DotProperties.IS_HTML_LABEL_DEFAULT;
		}
		if (isHtmlLabel) {
			getText().setVisible(false);
			htmlLabel.setVisible(true);
			// supply HTML to web engine
			String label = DotProperties.getLabel(getContent().getKey());
			htmlLabel.loadContent(label);
		} else {
			getText().setVisible(true);
			htmlLabel.setVisible(false);
		}
	}

}
