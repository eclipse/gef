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
			// forward selection changes to selection provider (in case
			// there is any)
			ISelection oldSelectedContent = getSelection();
			if (c.getList().isEmpty()) {
				if (oldSelectedContent == null
						|| !oldSelectedContent.isEmpty()) {
					setSelection(StructuredSelection.EMPTY);
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
					setSelection(newSelectedContent);
				}
			}
		}
	}

	private final SelectionObserver selectionObserver = new SelectionObserver();
	private List<ISelectionChangedListener> selectionChangedListeners = new ArrayList<>();
	private SelectionModel<VR> selectionModel;
	private ISelection selection;

	/**
	 * Creates a new {@link ContentSelectionProvider} for the given
	 * {@link SelectionModel}.
	 *
	 * @param selectionModel
	 *            The {@link SelectionModel} part to associate this
	 *            {@link ContentSelectionProvider} with.
	 */
	public ContentSelectionProvider(SelectionModel<VR> selectionModel) {
		this.selectionModel = selectionModel;
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
		return selection;
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		final SelectionChangedEvent e = new SelectionChangedEvent(this,
				selection);
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