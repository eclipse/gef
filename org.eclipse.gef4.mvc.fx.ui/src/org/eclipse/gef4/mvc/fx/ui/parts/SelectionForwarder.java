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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.common.reflect.Types;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

import javafx.collections.ListChangeListener;

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
public class SelectionForwarder<VR> implements ISelectionChangedListener {

	private class SelectionObserver
			implements ListChangeListener<IContentPart<VR, ? extends VR>> {

		@Override
		public void onChanged(
				ListChangeListener.Change<? extends IContentPart<VR, ? extends VR>> c) {
			// forward selection changes to selection provider (in case
			// there is any)
			ISelection oldSelectedContent = selectionProvider.getSelection();
			if (c.getList().isEmpty()) {
				if (oldSelectedContent == null
						|| !oldSelectedContent.isEmpty()) {
					selectionProvider.setSelection(StructuredSelection.EMPTY);
				}
			} else {
				// extract content elements of selected parts
				List<? extends IContentPart<VR, ? extends VR>> selectedContentParts = c
						.getList();
				List<Object> selectedContentElements = new ArrayList<>(
						selectedContentParts.size());
				for (IContentPart<VR, ? extends VR> cp : selectedContentParts) {
					selectedContentElements.add(cp.getContent());
				}
				// set the content elements as the new selection on the
				// selection provider
				StructuredSelection newSelectedContent = new StructuredSelection(
						selectedContentElements);
				if (!newSelectedContent.equals(oldSelectedContent)) {
					selectionProvider.setSelection(newSelectedContent);
				}
			}
		}
	}

	private final SelectionObserver selectionObserver = new SelectionObserver();
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
	public SelectionForwarder(final ISelectionProvider selectionProvider,
			IViewer<VR> viewer) {
		if (viewer == null) {
			throw new IllegalArgumentException(
					"The given IViewer<VR> may not be null.");
		}
		this.selectionProvider = selectionProvider;
		this.viewer = viewer;
		this.selectionModel = getSelectionModel();

		// register listeners
		if (selectionProvider != null) {
			selectionProvider.addSelectionChangedListener(this);
		}
		selectionModel.getSelectionUnmodifiable()
				.addListener(selectionObserver);
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
			selectionModel.getSelectionUnmodifiable()
					.removeListener(selectionObserver);
		}
	}

	/**
	 * Returns the {@link SelectionModel} attached to the viewer.
	 *
	 * @return The {@link SelectionModel} that should be used.
	 */
	@SuppressWarnings("serial")
	protected SelectionModel<VR> getSelectionModel() {
		SelectionModel<VR> selectionModel = viewer
				.getAdapter(new TypeToken<SelectionModel<VR>>() {
				}.where(new TypeParameter<VR>() {
				}, Types.<VR> argumentOf(viewer.getClass())));
		return selectionModel;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		if (selection.isEmpty()) {
			if (!selectionModel.getSelectionUnmodifiable().isEmpty()) {
				selectionModel.clearSelection();
			}
		} else if (selection instanceof StructuredSelection) {
			// find the content parts associated with the selection
			Object[] selected = ((StructuredSelection) selection).toArray();
			List<IContentPart<VR, ? extends VR>> parts = new ArrayList<>(
					selected.length);
			for (Object content : selected) {
				IContentPart<VR, ? extends VR> part = viewer.getContentPartMap()
						.get(content);
				if (part != null) {
					parts.add(part);
				}
			}
			// set the content parts as the new selection on the
			// SelectionModel
			if (!selectionModel.getSelectionUnmodifiable().equals(parts)) {
				selectionModel.setSelection(parts);
			}
		}
	}
}
