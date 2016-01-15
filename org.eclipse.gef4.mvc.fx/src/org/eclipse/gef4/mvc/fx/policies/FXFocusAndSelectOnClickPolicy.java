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

import java.util.Map;

import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractInteractionPolicy;

import com.google.common.reflect.TypeToken;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * The {@link FXFocusAndSelectOnClickPolicy} is an {@link IFXOnClickPolicy} that
 * focuses and selects its {@link #getHost() host} by altering the
 * {@link FocusModel} and the {@link SelectionModel} when the {@link #getHost()
 * host} is clicked by the mouse.
 *
 * @author anyssen
 *
 */
public class FXFocusAndSelectOnClickPolicy
		extends AbstractInteractionPolicy<Node> implements IFXOnClickPolicy {

	@SuppressWarnings("serial")
	@Override
	public void click(MouseEvent e) {
		// focus and select are only done on single click
		if (e.getClickCount() > 1) {
			return;
		}

		IVisualPart<Node, ? extends Node> host = getHost();
		FocusModel<Node> focusModel = host.getRoot().getViewer()
				.getAdapter(new TypeToken<FocusModel<Node>>() {
				});
		SelectionModel<Node> selectionModel = getHost().getRoot().getViewer()
				.getAdapter(new TypeToken<SelectionModel<Node>>() {
				});

		if (host instanceof IContentPart) {
			focusModel.setFocus((IContentPart<Node, ? extends Node>) host);

			boolean append = e.isControlDown();
			if (selectionModel
					.isSelected((IContentPart<Node, ? extends Node>) host)) {
				if (append) {
					// deselect the target edit part (ensure we get a new
					// primary selection)
					selectionModel.removeFromSelection(
							(IContentPart<Node, ? extends Node>) host);
				}
			} else {
				if (append) {
					// prepend to current selection (as new primary)
					selectionModel.prependToSelection(
							(IContentPart<Node, ? extends Node>) host);
				} else {
					// clear old selection, target should become the only
					// selected
					selectionModel.setSelection(
							(IContentPart<Node, ? extends Node>) host);
				}
			}
		} else if (host instanceof IRootPart) {
			// check if click on background (either one of the root visuals, or
			// an unregistered visual)
			Map<Node, IVisualPart<Node, ? extends Node>> visualPartMap = getHost()
					.getRoot().getViewer().getVisualPartMap();
			IVisualPart<Node, ? extends Node> targetPart = visualPartMap
					.get(e.getTarget());
			if (targetPart == null || targetPart == host) {
				// unset focus
				focusModel.setFocus(null);
				// remove all selected
				selectionModel.clearSelection();
			}
		}
	}
}
