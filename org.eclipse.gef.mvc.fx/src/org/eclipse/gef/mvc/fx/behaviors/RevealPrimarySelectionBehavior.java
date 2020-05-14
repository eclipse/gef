/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.behaviors;

import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 * The {@link RevealPrimarySelectionBehavior} observes the
 * {@link SelectionModel} that is registered at the {@link IViewer} of its
 * {@link #getHost() host} and
 * {@link IViewer#reveal(org.eclipse.gef.mvc.fx.parts.IVisualPart) reveals} the
 * primary selection, i.e. the first element of the
 * {@link SelectionModel#selectionUnmodifiableProperty()} when the selection
 * changes.
 *
 * @author wienand
 *
 */
public class RevealPrimarySelectionBehavior extends AbstractBehavior {

	private SelectionModel selectionModel;
	private IContentPart<? extends Node> previousPrimarySelection = null;
	private ListChangeListener<IContentPart<? extends Node>> selectionObserver = new ListChangeListener<IContentPart<? extends Node>>() {
		@Override
		public void onChanged(
				ListChangeListener.Change<? extends IContentPart<? extends Node>> c) {
			// determine primary selection
			IContentPart<? extends Node> currentPrimarySelection = null;
			if (c.getList().size() > 0) {
				currentPrimarySelection = c.getList().get(0);
			}
			if (currentPrimarySelection != previousPrimarySelection) {
				onPrimarySelectionChanged(previousPrimarySelection,
						currentPrimarySelection);
				previousPrimarySelection = currentPrimarySelection;
			}
		}
	};

	@Override
	protected void doActivate() {
		super.doActivate();
		// determine selection model
		IViewer viewer = getHost().getRoot().getViewer();
		selectionModel = viewer.getAdapter(SelectionModel.class);
		// observe selection
		selectionModel.selectionUnmodifiableProperty()
				.addListener(selectionObserver);
		// reveal initially
		previousPrimarySelection = null;
		ObservableList<IContentPart<? extends Node>> selection = selectionModel
				.getSelectionUnmodifiable();
		if (selection.size() > 0) {
			previousPrimarySelection = selection.get(0);
			onPrimarySelectionChanged(null, previousPrimarySelection);
		}
	}

	@Override
	protected void doDeactivate() {
		// remove selection observer
		selectionModel.selectionUnmodifiableProperty()
				.removeListener(selectionObserver);
		super.doDeactivate();
	}

	/**
	 * This method is called when the primary selection (i.e. the first element
	 * of the {@link SelectionModel#selectionUnmodifiableProperty()}) is
	 * changed.
	 *
	 * @param previousPrimarySelection
	 *            The previous primary selection, may be <code>null</code> in
	 *            case there was no previous selection.
	 * @param currentPrimarySelection
	 *            The current primary selection, may be <code>null</code> in
	 *            case there is no selection.
	 */
	protected void onPrimarySelectionChanged(
			IContentPart<? extends Node> previousPrimarySelection,
			IContentPart<? extends Node> currentPrimarySelection) {
		if (currentPrimarySelection != null) {
			// reveal current primary selection
			getHost().getRoot().getViewer().reveal(currentPrimarySelection);
		}
	}

}
