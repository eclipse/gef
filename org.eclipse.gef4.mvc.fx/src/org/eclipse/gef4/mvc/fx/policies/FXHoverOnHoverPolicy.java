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

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.mvc.policies.HoverPolicy;

public class FXHoverOnHoverPolicy extends AbstractFXHoverPolicy {

	@SuppressWarnings("unchecked")
	private HoverPolicy<Node> getHoverPolicy() {
		return getHost().getAdapter(HoverPolicy.class);
	}

	@Override
	public void hover(MouseEvent e) {
		HoverPolicy<Node> policy = getHoverPolicy();
		if (policy != null) {
			policy.hover();
		}
	}

}
