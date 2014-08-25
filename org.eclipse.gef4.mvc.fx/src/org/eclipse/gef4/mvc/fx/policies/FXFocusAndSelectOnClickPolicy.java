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

import org.eclipse.gef4.mvc.policies.FocusPolicy;
import org.eclipse.gef4.mvc.policies.SelectionPolicy;

public class FXFocusAndSelectOnClickPolicy extends AbstractFXClickPolicy {

	@Override
	public void click(MouseEvent e) {
		FocusPolicy<Node> focusPolicy = getFocusPolicy();
		if (focusPolicy != null) {
			focusPolicy.focus();
		}

		SelectionPolicy<Node> selectionPolicy = getSelectionPolicy();
		if (selectionPolicy != null) {
			selectionPolicy.select(e.isControlDown());
		}
	}

	@SuppressWarnings("unchecked")
	private FocusPolicy<Node> getFocusPolicy() {
		return getHost().getAdapter(FocusPolicy.class);
	}

	@SuppressWarnings("unchecked")
	private SelectionPolicy<Node> getSelectionPolicy() {
		return getHost().getAdapter(SelectionPolicy.class);
	}

}
