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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;

public class DotNodeContentPart extends NodeContentPart {

	private static final String SCRIPT_GET_WIDTH = "document.getElementById('content').offsetWidth";
	private static final String SCRIPT_GET_HEIGHT = "document.getElementById('content').offsetHeight";
	private static final double VERTICAL_PADDING = 10;
	private static final double HORIZONTAL_PADDING = 10;
	private static final String NO_SCROLLBARS_STYLE = "document.body.style.overflow = 'hidden';"; //$NON-NLS-1$
	private static final String PRE_HTML = "<html><head><title></title></head><body><div id=\"content\" style=\"float:left;\">";
	private static final String POST_HTML = "</div></body></html>";

	private Group labelGroup = new Group();
	private WebView labelWebView = new WebView();

	{
		// ensure no scrollbars are shown in the web view
		labelWebView.getEngine().getLoadWorker().stateProperty()
				.addListener(new ChangeListener<State>() {
					public void changed(ObservableValue<? extends State> o,
							State old, final State state) {
						if (state == State.RUNNING
								|| state == State.SUCCEEDED) {
							labelWebView.getEngine()
									.executeScript(NO_SCROLLBARS_STYLE);
						}
						if (state == State.SUCCEEDED) {
							Object width = labelWebView.getEngine()
									.executeScript(SCRIPT_GET_WIDTH);
							Object height = labelWebView.getEngine()
									.executeScript(SCRIPT_GET_HEIGHT);
							// FIXME: compute real padding
							labelWebView.setPrefSize(
									HORIZONTAL_PADDING + (Integer) width,
									VERTICAL_PADDING + (Integer) height);
						}
					}
				});
		// ensure web view can be resized to any size
		labelWebView.setMinSize(0, 0);
		labelWebView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	}

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
		labelGroup.getChildren().addAll(text, labelWebView);
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
			labelWebView.setVisible(true);
			// supply HTML to web engine
			labelWebView.getEngine().loadContent(PRE_HTML
					+ DotProperties.getLabel(getContent()) + POST_HTML);
		} else {
			getLabelText().setVisible(true);
			labelWebView.setVisible(false);
		}
	}

}
