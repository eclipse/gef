/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Camille Letavernier (camille.letavernier@cea.fr) - fix for bug #475399
 *
 * Note: Parts of this class have been transferred from org.eclipse.gef.SelectionManager.
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.common.beans.property.ReadOnlyListWrapperEx;
import org.eclipse.gef.common.collections.CollectionUtils;
import org.eclipse.gef.common.dispose.IDisposable;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 * The {@link SelectionModel} is used to store the current viewer's selection.
 * It represents the selection as an ordered list of {@link IContentPart}s.
 * Thereby, it supports a multi-selection and allows to identify a primary
 * selection (the head element of the list) that may be treated specially.
 *
 * @author anyssen
 * @author mwienand
 *
 */
// TODO: We could expose the selection as modifiable collection and modifiable
// read-only property if we could use an ordered set. As we use a list, we have
// to ensure it does not contain duplicates.
public class SelectionModel
		extends org.eclipse.gef.common.adapt.IAdaptable.Bound.Impl<IViewer>
		implements IDisposable {

	/**
	 * Name of the {@link #selectionUnmodifiableProperty()}.
	 */
	public static final String SELECTION_PROPERTY = "selection";

	private ObservableList<IContentPart<? extends Node>> selection = CollectionUtils
			.observableArrayList();

	private ObservableList<IContentPart<? extends Node>> selectionUnmodifiable = FXCollections
			.unmodifiableObservableList(selection);
	private ReadOnlyListWrapper<IContentPart<? extends Node>> selectionUnmodifiableProperty = new ReadOnlyListWrapperEx<>(
			this, SELECTION_PROPERTY, selectionUnmodifiable);

	private MapChangeListener<Node, IVisualPart<? extends Node>> visualPartMapListener = new MapChangeListener<Node, IVisualPart<? extends Node>>() {
		@Override
		public void onChanged(
				javafx.collections.MapChangeListener.Change<? extends Node, ? extends IVisualPart<? extends Node>> change) {
			// keep model in sync with part hierarchy
			if (change.wasRemoved()) {
				IVisualPart<? extends Node> valueRemoved = change
						.getValueRemoved();
				if (selection.contains(valueRemoved)) {
					selection.remove(valueRemoved);
				}
			}
		}
	};

	/**
	 * Updates the current selection by adding the given {@link IContentPart} to
	 * it, preserving already selected elements.
	 * <p>
	 * If the given content part is not already selected, it will be added to
	 * the back of the given selection, otherwise it will be moved to the back.
	 * A member of the current selection that is not contained in the given
	 * list, will remain selected.
	 *
	 * @param toBeAppended
	 *            The {@link IContentPart} to add to/move to the back of the
	 *            current selection.
	 */
	public void appendToSelection(IContentPart<? extends Node> toBeAppended) {
		appendToSelection(Collections.singletonList(toBeAppended));
	}

	/**
	 * Updates the current selection by adding the given {@link IContentPart}s
	 * to it, preserving already selected elements.
	 * <p>
	 * A member of the given list that is not contained in the current
	 * selection, will be added to it. A member of the current selection that is
	 * not contained in the given list, will remain selected.
	 * <p>
	 * The selection order will be adjusted, so that the members of the given
	 * list are added at the back (in the order they are given), preceded by the
	 * already selected elements not contained in the given list (preserving
	 * their relative order).
	 *
	 * @param toBeAppended
	 *            The {@link IContentPart}s to add to/move to the back of the
	 *            current selection.
	 */
	public void appendToSelection(
			List<? extends IContentPart<? extends Node>> toBeAppended) {
		List<IContentPart<? extends Node>> newSelection = getSelectionCopy();
		newSelection.removeAll(toBeAppended);
		for (IContentPart<? extends Node> p : toBeAppended) {
			if (newSelection.contains(p)) {
				throw new IllegalArgumentException("The content part " + p
						+ " is provided more than once in the given list.");
			}
			newSelection.add(p);
		}
		// XXX: ObservableList.setAll() is not properly guarded against not
		// having an effect (and will always notify attached listeners)
		if (!selection.equals(newSelection)) {
			selection.setAll(newSelection);
		}
	}

	/**
	 * Clears the current selection.
	 */
	public void clearSelection() {
		selection.clear();
	}

	/**
	 * @since 1.1
	 */
	@Override
	public void dispose() {
		// setAdaptable() already clears the selection
	}

	/**
	 * Returns a modifiable list of the currently selected {@link IContentPart}
	 * s.
	 *
	 * @return A modifiable list of the currently selected {@link IContentPart}
	 *         s.
	 */
	private List<IContentPart<? extends Node>> getSelectionCopy() {
		return new ArrayList<>(selection);
	}

	/**
	 * Returns an unmodifiable observable list of the currently selected
	 * {@link IContentPart}s.
	 *
	 * @return An unmodifiable observable list of the currently selected
	 *         {@link IContentPart}s.
	 */
	public ObservableList<IContentPart<? extends Node>> getSelectionUnmodifiable() {
		return selectionUnmodifiable;
	}

	/**
	 * Returns whether the given {@link IContentPart} is part of the current
	 * selection.
	 *
	 * @param contentPart
	 *            The {@link IContentPart} which is checked for containment.
	 * @return <code>true</code> if the {@link IContentPart} is contained by the
	 *         current selection.
	 */
	public boolean isSelected(IContentPart<? extends Node> contentPart) {
		return selection.contains(contentPart);
	}

	/**
	 * Updates the current selection by adding the given {@link IContentPart} to
	 * it, preserving already selected elements.
	 * <p>
	 * If the given content part is not already selected, it will be added to
	 * the front of the given selection, otherwise it will be moved to the
	 * front. A member of the current selection that is not contained in the
	 * given list, will remain selected.
	 *
	 * @param toBePrepended
	 *            The {@link IContentPart} to add to/move to the front of the
	 *            current selection.
	 */
	public void prependToSelection(IContentPart<? extends Node> toBePrepended) {
		prependToSelection(Collections.singletonList(toBePrepended));
	}

	/**
	 * Updates the current selection by adding the given {@link IContentPart}s
	 * to it, preserving already selected elements.
	 * <p>
	 * A member of the given list that is not contained in the current
	 * selection, will be added to it. A member of the current selection that is
	 * not contained in the given list, will remain selected.
	 * <p>
	 * The selection order will be adjusted, so that the members of the given
	 * list are added in front (in the order they are given), followed by the
	 * already selected elements not contained in the given list (preserving
	 * their relative order).
	 *
	 * @param toBePrepended
	 *            The {@link IContentPart}s to add to/move to the front of the
	 *            current selection.
	 */
	public void prependToSelection(
			List<? extends IContentPart<? extends Node>> toBePrepended) {
		List<IContentPart<? extends Node>> newSelection = getSelectionCopy();
		newSelection.removeAll(toBePrepended);
		int i = 0;
		for (IContentPart<? extends Node> p : toBePrepended) {
			if (newSelection.contains(p)) {
				throw new IllegalArgumentException("The content part " + p
						+ " is provided more than once in the given list.");
			}
			newSelection.add(i++, p);
		}
		if (!selection.equals(newSelection)) {
			selection.setAll(newSelection);
		}
	}

	/**
	 * Removes the given {@link IContentPart}s from the current selection if
	 * they are contained. Ignores those that are not part of the current
	 * selection.
	 *
	 * @param contentParts
	 *            The {@link IContentPart}s which are removed from the
	 *            selection.
	 */
	public void removeFromSelection(
			Collection<? extends IContentPart<? extends Node>> contentParts) {
		selection.removeAll(contentParts);
	}

	/**
	 * Removes the given {@link IContentPart} from the current selection if it
	 * is currently selected. Will not change the current selection otherwise.
	 *
	 * @param contentPart
	 *            The {@link IContentPart} that is to be removed from the
	 *            selection.
	 */
	public void removeFromSelection(IContentPart<? extends Node> contentPart) {
		selection.remove(contentPart);
	}

	/**
	 * Returns an unmodifiable read-only list property that represents the
	 * current selection.
	 *
	 * @return An unmodifiable read-only property named
	 *         {@link #SELECTION_PROPERTY}.
	 */
	public ReadOnlyListProperty<IContentPart<? extends Node>> selectionUnmodifiableProperty() {
		return selectionUnmodifiableProperty.getReadOnlyProperty();
	}

	@Override
	public void setAdaptable(IViewer adaptable) {
		if (getAdaptable() != null) {
			// unregister visual-part-map listener
			getAdaptable().visualPartMapProperty()
					.removeListener(visualPartMapListener);
		}
		super.setAdaptable(adaptable);
		if (adaptable != null) {
			// register for visual-part-map changes
			adaptable.visualPartMapProperty()
					.addListener(visualPartMapListener);
		}
		// start with a clean SelectionModel
		clearSelection();
	}

	/**
	 * Replaces the current selection with the given {@link IContentPart}.
	 *
	 * @param newSelection
	 *            The {@link IContentPart} constituting the new selection.
	 */
	public void setSelection(IContentPart<? extends Node> newSelection) {
		setSelection(Collections.singletonList(newSelection));
	}

	/**
	 * Replaces the current selection with the given list of
	 * {@link IContentPart} s.
	 *
	 * @param selection
	 *            The list of {@link IContentPart}s constituting the new
	 *            selection.
	 */
	public void setSelection(
			List<? extends IContentPart<? extends Node>> selection) {
		List<IContentPart<? extends Node>> newSelection = new ArrayList<>();
		int i = 0;
		for (IContentPart<? extends Node> p : selection) {
			if (newSelection.contains(p)) {
				throw new IllegalArgumentException("The content part " + p
						+ " is provided more than once in the given list.");
			}
			newSelection.add(i++, p);
		}
		if (!this.selection.equals(newSelection)) {
			this.selection.setAll(newSelection);
		}
	}
}
