/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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

import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.reflect.TypeToken;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * The {@link FXSelectOnTypePolicy} implements (de-)selecting a focused part via
 * the keyboard.
 *
 * @author mwienand
 *
 */
public class FXSelectOnTypePolicy extends AbstractFXInteractionPolicy
		implements IFXOnTypePolicy {

	@Override
	public void pressed(KeyEvent event) {
		// only react to events fired directly at our host
		if (isRegistered(event.getTarget())
				&& !isRegisteredForHost(event.getTarget())) {
			return;
		}

		// only react to the SPACE key
		if (!KeyCode.SPACE.equals(event.getCode())) {
			return;
		}

		IVisualPart<Node, ? extends Node> host = getHost();
		IViewer<Node> viewer = host.getRoot().getViewer();
		@SuppressWarnings("serial")
		SelectionModel<Node> selectionModel = viewer
				.getAdapter(new TypeToken<SelectionModel<Node>>() {
				});

		if (host instanceof IRootPart) {
			// clear the selection if on the root part/background
			selectionModel.clearSelection();
		} else if (host instanceof IContentPart) {
			IContentPart<Node, ? extends Node> contentPart = (IContentPart<Node, ? extends Node>) host;
			// depending on modifier, append or set the selection
			if (event.isControlDown()) {
				// append selection
				if (selectionModel.isSelected(contentPart)) {
					selectionModel.removeFromSelection(contentPart);
				} else {
					selectionModel.prependToSelection(contentPart);
				}
			} else {
				// set selection
				selectionModel.setSelection(contentPart);
			}
		}
	}

	@Override
	public void released(KeyEvent event) {
	}

	@Override
	public void typed(KeyEvent event) {
	}

	@Override
	public void unfocus() {
	}

}
