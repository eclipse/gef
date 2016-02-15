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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.Group;
import javafx.scene.web.WebView;

public class HtmlLabel extends Group {

	private static final String SCRIPT_GET_WIDTH = "document.getElementById('content').offsetWidth"; //$NON-NLS-1$
	private static final String SCRIPT_GET_HEIGHT = "document.getElementById('content').offsetHeight"; //$NON-NLS-1$
	private static final double VERTICAL_PADDING = 10;
	private static final double HORIZONTAL_PADDING = 10;
	private static final String NO_SCROLLBARS_STYLE = "document.body.style.overflow = 'hidden';"; //$NON-NLS-1$
	private static final String PRE_HTML = "<html><head><title></title></head><body><div id=\"content\" style=\"float:left;\">"; //$NON-NLS-1$
	private static final String POST_HTML = "</div></body></html>"; //$NON-NLS-1$

	private WebView webView = new WebView();
	private String loadedContent = null;

	{
		// ensure no scrollbars are shown in the web view
		webView.getEngine().getLoadWorker().stateProperty()
				.addListener(new ChangeListener<State>() {
					public void changed(ObservableValue<? extends State> o,
							State old, final State state) {
						if (state == State.RUNNING
								|| state == State.SUCCEEDED) {
							webView.getEngine()
									.executeScript(NO_SCROLLBARS_STYLE);
						}
						if (state == State.SUCCEEDED) {
							Object width = webView.getEngine()
									.executeScript(SCRIPT_GET_WIDTH);
							Object height = webView.getEngine()
									.executeScript(SCRIPT_GET_HEIGHT);
							// FIXME: compute real padding
							webView.setPrefSize(
									HORIZONTAL_PADDING + (Integer) width,
									VERTICAL_PADDING + (Integer) height);
						}
					}
				});
		// ensure web view can be resized to any size
		webView.setMinSize(0, 0);
		webView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		// add web view to this group
		getChildren().add(webView);
	}

	public void loadContent(String content) {
		if (loadedContent == null || !loadedContent.equals(content)) {
			loadedContent = content;
			webView.getEngine().loadContent(PRE_HTML + content + POST_HTML);
		}
	}

}
