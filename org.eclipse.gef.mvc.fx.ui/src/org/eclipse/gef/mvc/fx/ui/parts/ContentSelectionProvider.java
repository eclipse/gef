/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.ui.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.services.IDisposable;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;

/**
 * The {@link ContentSelectionProvider} is an {@link ISelectionProvider}
 * implementation that manages the un-/registration of listeners and their
 * execution upon selection changes.
 *
 * @author anyssen
 *
 */
public class ContentSelectionProvider
		implements ISelectionProvider, IDisposable {

	private class SelectionObserver
			implements ListChangeListener<IContentPart<? extends Node>> {

		@Override
		public void onChanged(
				ListChangeListener.Change<? extends IContentPart<? extends Node>> c) {
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
	private IViewer viewer;
	private SelectionModel selectionModel;

	/**
	 * Creates a new {@link ContentSelectionProvider} for the given
	 * {@link SelectionModel}.
	 *
	 * @param viewer
	 *            The {@link IViewer} to associate this
	 *            {@link ContentSelectionProvider} to.
	 */
	public ContentSelectionProvider(IViewer viewer) {
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
			List<? extends IContentPart<? extends Node>> selectedContentParts = selectionModel
					.getSelectionUnmodifiable();
			List<Object> selectedContentElements = new ArrayList<>(
					selectedContentParts.size());
			for (IContentPart<? extends Node> cp : selectedContentParts) {
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
	protected IViewer getViewer() {
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
			List<IContentPart<? extends Node>> parts = new ArrayList<>(
					selected.length);
			for (Object content : selected) {
				IContentPart<? extends Node> part = viewer.getContentPartMap()
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