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
package org.eclipse.gef4.mvc.fx.policies;

import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;

import org.eclipse.gef4.mvc.policies.DefaultZoomPolicy;

public class FXZoomOnScrollPolicy extends AbstractFXScrollPolicy {

	@SuppressWarnings("unchecked")
	private DefaultZoomPolicy<Node> getZoomPolicy() {
		return getHost().getAdapter(DefaultZoomPolicy.class);
	}

	@Override
	public void scroll(ScrollEvent event, double deltaY) {
		if (event.isControlDown() || event.isAltDown()) {
			DefaultZoomPolicy<Node> policy = getZoomPolicy();
			if (policy != null) {
				policy.zoomRelative(deltaY > 0 ? 1.25 : 0.8);
			}
		}
	}
}
