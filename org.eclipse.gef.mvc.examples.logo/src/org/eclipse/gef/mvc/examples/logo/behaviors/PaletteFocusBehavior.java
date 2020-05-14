/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.behaviors;

import org.eclipse.gef.mvc.fx.behaviors.FocusBehavior;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

public class PaletteFocusBehavior extends FocusBehavior {

	public static final String FOCUSED_STYLE = "-fx-background-insets: 0; -fx-padding: 0; -fx-background-color: rgba(128,128,128,0.75); -fx-border-color: #8ec0fc; -fx-border-width: 2;";
	public static final String DEFAULT_STYLE = "-fx-background-insets: 0; -fx-padding: 0; -fx-background-color: rgba(128,128,128,0.75); -fx-border-color: rgba(128,128,128,1); -fx-border-width: 2;";

	@Override
	protected void addViewerFocusedFeedback() {
		// XXX: super call is necessary so that state is correctly maintained.
		super.addViewerFocusedFeedback();
		IViewer viewer = getHost().getRoot().getViewer();
		viewer.getCanvas().setStyle(FOCUSED_STYLE);
	}

	@Override
	protected void removeViewerFocusedFeedback() {
		// XXX: super call is necessary so that state is correctly maintained.
		super.removeViewerFocusedFeedback();
		IViewer viewer = getHost().getRoot().getViewer();
		viewer.getCanvas().setStyle(DEFAULT_STYLE);
	}

}
