/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import java.util.Collections;

import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * The {@link FXFocusAndSelectOnClickPolicy} is an
 * {@link AbstractFXOnClickPolicy} that focuses and selects its
 * {@link #getHost() host} by altering the {@link FocusModel} and the
 * {@link SelectionModel} when the {@link #getHost() host} is clicked by the
 * mouse.
 *
 * @author anyssen
 *
 */
public class FXFocusAndSelectOnClickPolicy extends AbstractFXOnClickPolicy {

	@Override
	public void click(MouseEvent e) {
		// focus and select are only done on single click
		if (e.getClickCount() > 1) {
			return;
		}

		IVisualPart<Node, ? extends Node> host = getHost();
		FocusModel<Node> focusModel = host.getRoot().getViewer()
				.<FocusModel<Node>> getAdapter(FocusModel.class);
		SelectionModel<Node> selectionModel = getHost().getRoot().getViewer()
				.<SelectionModel<Node>> getAdapter(SelectionModel.class);

		if (host instanceof IContentPart) {
			focusModel.setFocused((IContentPart<Node, ? extends Node>) host);

			boolean append = e.isControlDown();
			if (selectionModel
					.isSelected((IContentPart<Node, ? extends Node>) host)) {
				if (append) {
					// deselect the target edit part (ensure we get a new
					// primary selection)
					selectionModel.deselect(Collections.singleton(
							(IContentPart<Node, ? extends Node>) host));
				}
			} else {
				if (append) {
					// append to current selection (as new primary)
					selectionModel.select(Collections.singletonList(
							(IContentPart<Node, ? extends Node>) host));
				} else {
					// clear old selection, target should become the only
					// selected
					selectionModel.updateSelection(Collections.singletonList(
							(IContentPart<Node, ? extends Node>) host));
				}
			}
		} else if (host instanceof IRootPart) {
			// check if click on background (either one of the root visuals, or
			// an unregistered visual)
			IVisualPart<Node, ? extends Node> targetPart = getHost().getRoot()
					.getViewer().getVisualPartMap().get(e.getTarget());
			if (targetPart == null || targetPart == host) {
				// unset focus
				focusModel.setFocused(null);
				// remove all selected
				selectionModel.deselectAll();
			}
		}
	}
}
