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
package org.eclipse.gef4.mvc.fx.policies;

import javafx.scene.input.ScrollEvent;

import org.eclipse.gef4.mvc.models.ViewportModel;

public class FXPanOnScrollPolicy extends AbstractFXScrollPolicy {

	@Override
	public void scroll(ScrollEvent event) {
		// do not scroll when a modifier key is down
		if (event.isAltDown() || event.isControlDown() || event.isMetaDown()
				|| event.isShiftDown()) {
			return;
		}

		// TODO obtain policy to manipulate the viewport model
		double x = event.getDeltaX();
		double y = event.getDeltaY();
		ViewportModel viewportModel = getHost().getRoot().getViewer()
				.getAdapter(ViewportModel.class);
		viewportModel.setTranslateX(viewportModel.getTranslateX() + x);
		viewportModel.setTranslateY(viewportModel.getTranslateY() - y);
	}

}
