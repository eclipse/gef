/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.behaviors;

import org.eclipse.gef.common.reflect.Types;
import org.eclipse.gef.mvc.models.SelectionModel;
import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.mvc.viewer.IViewer;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * The {@link RevealPrimarySelectionBehavior} observes the
 * {@link SelectionModel} that is registered at the {@link IViewer} of its
 * {@link #getHost() host} and
 * {@link IViewer#reveal(org.eclipse.gef.mvc.parts.IVisualPart) reveals} the
 * primary selection, i.e. the first element of the
 * {@link SelectionModel#selectionUnmodifiableProperty()} when the selection
 * changes.
 *
 * @author wienand
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 *
 */
public class RevealPrimarySelectionBehavior<VR> extends AbstractBehavior<VR> {

	private SelectionModel<VR> selectionModel;
	private IContentPart<VR, ? extends VR> previousPrimarySelection = null;
	private ListChangeListener<IContentPart<VR, ? extends VR>> selectionObserver = new ListChangeListener<IContentPart<VR, ? extends VR>>() {
		@Override
		public void onChanged(
				ListChangeListener.Change<? extends IContentPart<VR, ? extends VR>> c) {
			// determine primary selection
			IContentPart<VR, ? extends VR> currentPrimarySelection = null;
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
		IViewer<VR> viewer = getHost().getRoot().getViewer();
		selectionModel = viewer.getAdapter(new TypeToken<SelectionModel<VR>>() {
		}.where(new TypeParameter<VR>() {
		}, Types.<VR> argumentOf(viewer.getClass())));
		// observe selection
		selectionModel.selectionUnmodifiableProperty()
				.addListener(selectionObserver);
		// reveal initially
		previousPrimarySelection = null;
		ObservableList<IContentPart<VR, ? extends VR>> selection = selectionModel
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
			IContentPart<VR, ? extends VR> previousPrimarySelection,
			IContentPart<VR, ? extends VR> currentPrimarySelection) {
		if (currentPrimarySelection != null) {
			// reveal current primary selection
			getHost().getRoot().getViewer().reveal(currentPrimarySelection);
		}
	}

}
