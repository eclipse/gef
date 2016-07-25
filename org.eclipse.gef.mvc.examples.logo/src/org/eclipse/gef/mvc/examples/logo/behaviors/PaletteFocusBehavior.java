/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.behaviors;

import org.eclipse.gef.mvc.fx.behaviors.FXFocusBehavior;
import org.eclipse.gef.mvc.fx.viewer.FXViewer;
import org.eclipse.gef.mvc.viewer.IViewer;

import javafx.scene.Node;

public class PaletteFocusBehavior extends FXFocusBehavior {

	public static final String FOCUSED_STYLE = "-fx-background-insets: 0; -fx-padding: 0; -fx-background-color: rgba(128,128,128,0.75); -fx-border-color: #8ec0fc; -fx-border-width: 2;";
	public static final String DEFAULT_STYLE = "-fx-background-insets: 0; -fx-padding: 0; -fx-background-color: rgba(128,128,128,0.75); -fx-border-color: rgba(128,128,128,1); -fx-border-width: 2;";

	@Override
	protected void addViewerFocusedFeedback() {
		// XXX: super call is necessary so that state is correctly maintained.
		super.addViewerFocusedFeedback();
		IViewer<Node> viewer = getHost().getRoot().getViewer();
		if (viewer instanceof FXViewer) {
			((FXViewer) viewer).getCanvas().setStyle(FOCUSED_STYLE);
		}
	}

	@Override
	protected void removeViewerFocusedFeedback() {
		// XXX: super call is necessary so that state is correctly maintained.
		super.removeViewerFocusedFeedback();
		IViewer<Node> viewer = getHost().getRoot().getViewer();
		if (viewer instanceof FXViewer) {
			((FXViewer) viewer).getCanvas().setStyle(DEFAULT_STYLE);
		}
	}

}
