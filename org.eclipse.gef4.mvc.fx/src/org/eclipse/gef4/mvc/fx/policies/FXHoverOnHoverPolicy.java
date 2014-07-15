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

import org.eclipse.gef4.mvc.policies.DefaultHoverPolicy;

public class FXHoverOnHoverPolicy extends AbstractFXHoverPolicy {

	@SuppressWarnings("unchecked")
	private DefaultHoverPolicy<Node> getHoverPolicy() {
		return getHost().getAdapter(DefaultHoverPolicy.class);
	}

	@Override
	public void hover(MouseEvent e) {

		DefaultHoverPolicy<Node> policy = getHoverPolicy();
		if (policy != null) {
			policy.hover();
		}
	}

}
