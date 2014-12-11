/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.parts;

import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.models.GridModel;

public class GraphRootPart extends FXRootPart {

	public static final String STYLES_CSS_FILE = GraphRootPart.class
			.getResource("styles.css").toExternalForm();

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
