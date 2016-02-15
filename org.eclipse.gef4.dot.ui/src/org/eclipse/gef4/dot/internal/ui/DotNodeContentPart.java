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
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class DotNodeContentPart extends NodeContentPart {

	private Group labelGroup = new Group();
	private HtmlLabel htmlLabel = new HtmlLabel();

	protected Group createVisual() {
		// super call to create visual
		Group group = super.createVisual();
		// find the label Text
		Text text = getLabelText();
		// replace labelText with labelGroup in the visual hierarchy
		Pane textParent = (Pane) text.getParent();
		int index = textParent.getChildren().indexOf(text);
		textParent.getChildren().remove(text);
		textParent.getChildren().add(index, labelGroup);
		// put label Text and WebView into label Group
		labelGroup.getChildren().addAll(text, htmlLabel);
		// return the modified group
		return group;
	}

	@Override
	public void doRefreshVisual(Group visual) {
		super.doRefreshVisual(visual);
		// check if HTML label
		Boolean isHtmlLabel = DotProperties.isHtmlLabel(getContent());
		if (isHtmlLabel == null) {
			isHtmlLabel = DotProperties.IS_HTML_LABEL_DEFAULT;
		}
		if (isHtmlLabel) {
			getLabelText().setVisible(false);
			htmlLabel.setVisible(true);
			// supply HTML to web engine
			String label = DotProperties.getLabel(getContent());
			htmlLabel.loadContent(label);
		} else {
			getLabelText().setVisible(true);
			htmlLabel.setVisible(false);
		}
	}

}
