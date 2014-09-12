/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.behaviors;

import javafx.scene.Node;
import javafx.scene.transform.Scale;

import org.eclipse.gef4.mvc.behaviors.AbstractZoomBehavior;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.parts.IRootPart;

public class FXZoomBehavior extends AbstractZoomBehavior<Node> {

	@Override
	protected void applyZoom(double zoomFactor) {
		if (zoomFactor <= 0) {
			throw new IllegalArgumentException(
					"Expected: positive double. Given: <" + zoomFactor + ">.");
		}

		IRootPart<Node> root = getHost().getRoot();
		if (root instanceof FXRootPart) {
			FXRootPart fxRootPart = (FXRootPart) root;
			fxRootPart.zoomProperty().set(new Scale(zoomFactor, zoomFactor));
			// TODO: set zoom on content layer rather than via zoomProperty() on
			// FXRootPart
			// also set it on grid layer in case zoom grid is enabled??
		}
	}
}
