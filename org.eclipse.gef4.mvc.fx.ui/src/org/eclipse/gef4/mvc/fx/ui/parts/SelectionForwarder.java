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
package org.eclipse.gef4.mvc.fx.ui.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;

public class SelectionForwarder implements PropertyChangeListener {

	private ISelectionProvider selectionProvider;

	public SelectionForwarder(ISelectionProvider selectionProvider) {
		this.selectionProvider = selectionProvider;

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (ISelectionModel.SELECTION_PROPERTY.equals(event.getPropertyName())) {
			// forward selection changes to selection provider (in case
			// there is any)
			if (event.getNewValue() == null) {
				selectionProvider.setSelection(StructuredSelection.EMPTY);
			} else {
				selectionProvider.setSelection(new StructuredSelection(
						(List) event.getNewValue()));
			}
		}
	}
}
