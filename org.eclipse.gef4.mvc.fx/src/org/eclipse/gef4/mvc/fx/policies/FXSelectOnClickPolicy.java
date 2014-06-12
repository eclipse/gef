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

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class FXSelectOnClickPolicy extends AbstractFXClickPolicy {

	@Override
	public void click(MouseEvent e) {
		boolean append = e.isControlDown();
		IVisualPart<Node> host = getHost();
		if (host instanceof IRootPart) {
			select(null, append);
		} else if (host instanceof IContentPart) {
			select((IContentPart<Node>) host, append);
		}
	}

	protected ISelectionModel<Node> getSelectionModel() {
		return getHost().getRoot().getViewer().getSelectionModel();
	}

	/**
	 *
	 * @param targetPart
	 * @param append
	 * @return <code>true</code> on selection change, otherwise
	 *         <code>false</code>
	 */
	// TODO: move this into selection policy
	@SuppressWarnings("unchecked")
	public boolean select(IContentPart<Node> targetPart, boolean append) {
		// TODO: extract into tool policy
		boolean changed = true;

		ISelectionModel<Node> selectionModel = getSelectionModel();
		// retrieve old selection
		List<IContentPart<Node>> oldSelection = new ArrayList<IContentPart<Node>>(
				selectionModel.getSelected());
		// determine new selection
		if (targetPart == null) {
			// remove all selected
			selectionModel.deselectAll();
		} else {
			if (oldSelection.contains(targetPart)) {
				if (append) {
					// deselect the target edit part (ensure we get a new
					// primary selection)
					selectionModel.deselect(targetPart);
				} else {
					// target should become the new primary selection
					// selectionModel.select(targetEditPart);
					changed = false;
				}
			} else {
				if (append) {
					// append to current selection (as new primary)
					selectionModel.select(targetPart);
				} else {
					// clear old selection, target should become the only
					// selected
					selectionModel.deselectAll();
					selectionModel.select(targetPart);
				}
			}
		}
		return changed;
	}

}
