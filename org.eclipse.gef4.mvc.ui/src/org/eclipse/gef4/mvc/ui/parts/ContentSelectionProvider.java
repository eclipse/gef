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
package org.eclipse.gef4.mvc.ui.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.services.IDisposable;

import javafx.collections.ListChangeListener;

/**
 * The {@link ContentSelectionProvider} is an {@link ISelectionProvider}
 * implementation that manages the un-/registration of listeners and their
 * execution upon selection changes.
 *
 * @author anyssen
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 *
 */
public class ContentSelectionProvider<VR>
		implements ISelectionProvider, IDisposable {

	private class SelectionObserver
			implements ListChangeListener<IContentPart<VR, ? extends VR>> {

		@Override
		public void onChanged(
				ListChangeListener.Change<? extends IContentPart<VR, ? extends VR>> c) {
			// notify listeners that selection has changed
			final SelectionChangedEvent e = new SelectionChangedEvent(
					ContentSelectionProvider.this, getSelection());
			for (final ISelectionChangedListener l : selectionChangedListeners) {
				SafeRunner.run(new SafeRunnable() {
					@Override
					public void run() {
						l.selectionChanged(e);
					}
				});
			}
		}
	}

	private final SelectionObserver selectionObserver = new SelectionObserver();
	private List<ISelectionChangedListener> selectionChangedListeners = new ArrayList<>();
	private IViewer<VR> viewer;
	private SelectionModel<VR> selectionModel;

	/**
	 * Creates a new {@link ContentSelectionProvider} for the given
	 * {@link SelectionModel}.
	 *
	 * @param viewer
	 *            The {@link IViewer} to associate this
	 *            {@link ContentSelectionProvider} to.
	 */
	@SuppressWarnings("unchecked")
	public ContentSelectionProvider(IViewer<VR> viewer) {
		this.viewer = viewer;
		this.selectionModel = viewer.getAdapter(SelectionModel.class);
		selectionModel.getSelectionUnmodifiable()
				.addListener(selectionObserver);
	}

	@Override
	public void addSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	@Override
	public void dispose() {
		if (selectionModel != null) {
			selectionModel.getSelectionUnmodifiable()
					.removeListener(selectionObserver);
		}
	}

	@Override
	public ISelection getSelection() {
		ISelection selection = StructuredSelection.EMPTY;
		if (!selectionModel.getSelectionUnmodifiable().isEmpty()) {
			// extract content elements of selected parts
			List<? extends IContentPart<VR, ? extends VR>> selectedContentParts = selectionModel
					.getSelectionUnmodifiable();
			List<Object> selectedContentElements = new ArrayList<>(
					selectedContentParts.size());
			for (IContentPart<VR, ? extends VR> cp : selectedContentParts) {
				selectedContentElements.add(cp.getContent());
			}
			// return the content elements as our selection
			selection = new StructuredSelection(selectedContentElements);
		}
		return selection;
	}

	/**
	 * Returns the {@link IViewer} this {@link ContentSelectionProvider} is
	 * bound to.
	 *
	 * @return The {@link IViewer} this {@link ContentSelectionProvider} is
	 *         bound to.
	 */
	protected IViewer<VR> getViewer() {
		return viewer;
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		// update the selection model, which will lead to an update of our
		// selection and to listener notification.
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
			// set the content parts as the new selection to the SelectionModel
			if (!selectionModel.getSelectionUnmodifiable().equals(parts)) {
				selectionModel.setSelection(parts);
			}
		}
	}
}