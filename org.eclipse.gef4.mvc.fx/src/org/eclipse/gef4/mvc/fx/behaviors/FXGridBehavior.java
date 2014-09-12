/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.behaviors;

import javafx.scene.Node;
import javafx.scene.transform.Scale;

import org.eclipse.gef4.mvc.behaviors.AbstractGridBehavior;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;

public class FXGridBehavior extends AbstractGridBehavior<Node> {

	@Override
	protected void applyGridCellHeight(double height) {
		((FXRootPart) getHost().getRoot()).getGridLayer().setGridHeight(height);
	}

	@Override
	protected void applyGridCellWidth(double width) {
		((FXRootPart) getHost().getRoot()).getGridLayer().setGridWidth(width);
	}

	@Override
	protected void applyShowGrid(boolean showGrid) {
		if (showGrid) {
			((FXRootPart) getHost().getRoot()).getGridLayer().setVisible(true);
			((FXRootPart) getHost().getRoot()).getGridLayer().setManaged(true);
		} else {
			((FXRootPart) getHost().getRoot()).getGridLayer().setVisible(false);
			((FXRootPart) getHost().getRoot()).getGridLayer().setManaged(false);
		}
	}

	@Override
	protected void applyZoomGrid(boolean zoomGrid) {
		// TODO: add listener to zoom model instead and update zoom property
		// accordingly
		if (zoomGrid) {
			// bind grid scale to zoom property
			((FXRootPart) getHost().getRoot()).getGridLayer()
					.gridScaleProperty()
					.bind(((FXRootPart) getHost().getRoot()).zoomProperty());
		} else {
			((FXRootPart) getHost().getRoot()).getGridLayer()
					.gridScaleProperty().unbind();
			((FXRootPart) getHost().getRoot()).getGridLayer()
					.gridScaleProperty().set(new Scale(1, 1));
		}
	}

}
