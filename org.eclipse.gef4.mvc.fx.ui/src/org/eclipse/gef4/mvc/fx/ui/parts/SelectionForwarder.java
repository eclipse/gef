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
package org.eclipse.gef4.mvc.fx.ui.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

import com.google.common.reflect.TypeToken;

/**
 * The {@link SelectionForwarder} can be used to propagate selections from the
 * Eclipse workbench to the MVC application and vice versa.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit, e.g. javafx.scene.Node in
 *            case of JavaFX.
 */
public class SelectionForwarder<VR>
		implements PropertyChangeListener, ISelectionChangedListener {

	private final ISelectionProvider selectionProvider;
	private final IViewer<VR> viewer;
	private final SelectionModel<VR> selectionModel;

	/**
	 * Creates a new {@link SelectionForwarder} that registers listeners on the
	 * given {@link ISelectionProvider} and the {@link SelectionModel} of the
	 * given {@link IViewer} to propagate selections from the Eclipse workbench
	 * to the viewer, and vice versa.
	 *
	 * @param selectionProvider
	 *            The {@link ISelectionProvider} of the Eclipse workbench
	 * @param viewer
	 *            The {@link IViewer} of which the {@link SelectionModel} should
	 *            be held in sync with the Eclipse workbench selection.
	 */
	@SuppressWarnings("serial")
	public SelectionForwarder(final ISelectionProvider selectionProvider,
			IViewer<VR> viewer) {
		if (viewer == null) {
			throw new IllegalArgumentException(
					"The given IViewer<VR> may not be null.");
		}
		this.selectionProvider = selectionProvider;
		this.viewer = viewer;
		this.selectionModel = viewer
				.getAdapter(new TypeToken<SelectionModel<VR>>(getClass()) {
				});

		// register listeners
		if (selectionProvider != null) {
			selectionProvider.addSelectionChangedListener(this);
		}
		if (selectionModel != null) {
			selectionModel.addPropertyChangeListener(this);
		}
	}

	/**
	 * Unregisters all listeners.
	 */
	public void dispose() {
		// unregister listeners
		if (this.selectionProvider != null) {
			this.selectionProvider.removeSelectionChangedListener(this);
		}
		if (selectionModel != null) {
			selectionModel.removePropertyChangeListener(this);
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (SelectionModel.SELECTION_PROPERTY.equals(event.getPropertyName())) {
			// forward selection changes to selection provider (in case
			// there is any)
			if (event.getNewValue() == null) {
				selectionProvider.setSelection(StructuredSelection.EMPTY);
			} else {
				// extract content elements of selected parts
				@SuppressWarnings("unchecked")
				List<IContentPart<VR, ? extends VR>> selectedParts = (List<IContentPart<VR, ? extends VR>>) event
						.getNewValue();
				List<Object> selectedContentElements = new ArrayList<Object>(
						selectedParts.size());
				for (IContentPart<VR, ? extends VR> cp : selectedParts) {
					selectedContentElements.add(cp.getContent());
				}
				// set the content elements as the new selection on the
				// selection provider
				// TODO: verify no events are fired when the same selection is
				// set again
				selectionProvider.setSelection(
						new StructuredSelection(selectedContentElements));
			}
		}
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		if (selection instanceof StructuredSelection) {
			StructuredSelection sel = (StructuredSelection) selection;
			if (sel.isEmpty()) {
				selectionModel.prependToSelection(Collections
						.<IContentPart<VR, ? extends VR>> emptyList());
			} else {
				// find the content parts associated with the selection
				Object[] selected = sel.toArray();
				List<IContentPart<VR, ? extends VR>> parts = new ArrayList<IContentPart<VR, ? extends VR>>(
						selected.length);
				for (Object content : selected) {
					IContentPart<VR, ? extends VR> part = viewer
							.getContentPartMap().get(content);
					if (part != null) {
						parts.add(part);
					}
				}
				// set the content parts as the new selection on the
				// SelectionModel
				if (!selectionModel.getSelection().equals(parts)) {
					selectionModel.prependToSelection(parts);
				}
			}
		}
	}

}
