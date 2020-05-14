/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.parts;

import org.eclipse.gef.mvc.fx.models.GridModel;
import org.eclipse.gef.mvc.fx.parts.LayeredRootPart;

import javafx.scene.Scene;

/**
 * The {@link ZestFxRootPart} is an extension to {@link LayeredRootPart} that hides
 * the grid (see {@link GridModel#setShowGrid(boolean)}) and adds a stylesheet
 * to the {@link Scene} (see {@link Scene#getStylesheets()}).
 *
 * @author mwienand
 *
 */
public class ZestFxRootPart extends LayeredRootPart {

	/**
	 * The url to the stylesheet that is added to the {@link Scene}.
	 */
	public static final String STYLES_CSS_FILE = ZestFxRootPart.class.getResource("styles.css").toExternalForm();

	@Override
	protected void doActivate() {
		super.doActivate();
		// hide grid
		getViewer().getAdapter(GridModel.class).setShowGrid(false);
		// load stylesheet
		getVisual().getScene().getStylesheets().add(STYLES_CSS_FILE);
	}

	@Override
	protected void doDeactivate() {
		super.doDeactivate();
		// un-load stylesheet
		getVisual().getScene().getStylesheets().remove(STYLES_CSS_FILE);
	}

}
