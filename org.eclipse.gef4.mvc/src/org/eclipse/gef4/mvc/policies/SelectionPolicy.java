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
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

//TODO: make ITransactional
public class SelectionPolicy<VR> extends AbstractPolicy<VR> {

	// TODO: use a ChangeSelectionOperation (and provide a hook to decide
	// whether it should be executed on the operation history)
	public void select(boolean append) {
		IVisualPart<VR> host = getHost();

		SelectionModel<VR> selectionModel = getHost().getRoot().getViewer()
				.getAdapter(SelectionModel.class);

		// retrieve old selection
		List<IContentPart<VR>> oldSelection = new ArrayList<IContentPart<VR>>(
				selectionModel.getSelected());
		// determine new selection
		if (!(host instanceof IContentPart)) {
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
					selectionModel.select(Collections
							.singletonList((IContentPart<VR>) host));
				} else {
					// clear old selection, target should become the only
					// selected
					selectionModel.deselectAll();
					selectionModel.select(Collections
							.singletonList((IContentPart<VR>) host));
				}
			}
		}
	}

}
