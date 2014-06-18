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

import org.eclipse.gef4.mvc.policies.DefaultSelectionPolicy;

public class FXSelectOnClickPolicy extends AbstractFXClickPolicy {

	@Override
	public void click(MouseEvent e) {
		DefaultSelectionPolicy<Node> policy = getSelectionPolicy();
		if (policy != null) {
			policy.select(e.isControlDown());
		}
	}

	@SuppressWarnings("unchecked")
	private DefaultSelectionPolicy<Node> getSelectionPolicy() {
		return getHost().getAdapter(DefaultSelectionPolicy.class);
	}

}
