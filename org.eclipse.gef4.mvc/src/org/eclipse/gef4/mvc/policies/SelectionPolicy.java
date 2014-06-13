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
package org.eclipse.gef4.mvc.policies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class SelectionPolicy<VR> extends AbstractPolicy<VR> {

	public void select(boolean append) {
		IVisualPart<VR> host = getHost();
		
		ISelectionModel<VR> selectionModel = getHost().getRoot().getViewer()
				.getSelectionModel();
		
		// retrieve old selection
		List<IContentPart<VR>> oldSelection = new ArrayList<IContentPart<VR>>(
				selectionModel.getSelected());
		// determine new selection
		if (!(host instanceof IContentPart) || !isSelectable()) {
			// remove all selected
			selectionModel.deselectAll();
		} else {
			if (oldSelection.contains(host)) {
				if (append) {
					// deselect the target edit part (ensure we get a new
					// primary selection)
					selectionModel.deselect((IContentPart<VR>) host);
				} else {
					// target should become the new primary selection
					// selectionModel.select(targetEditPart);
				}
			} else {
				if (append) {
					// append to current selection (as new primary)
					selectionModel.select((IContentPart<VR>) host);
				} else {
					// clear old selection, target should become the only
					// selected
					selectionModel.deselectAll();
					selectionModel.select((IContentPart<VR>) host);
				}
			}
		}
	}

	protected boolean isSelectable() {
		return true;
	}

}
