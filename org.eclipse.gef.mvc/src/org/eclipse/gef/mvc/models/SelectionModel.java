/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Camille Letavernier (camille.letavernier@cea.fr) - fix for bug #475399
 *
 * Note: Parts of this class have been transferred from org.eclipse.gef.SelectionManager.
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.common.beans.property.ReadOnlyListWrapperEx;
import org.eclipse.gef.common.collections.CollectionUtils;
import org.eclipse.gef.common.dispose.IDisposable;
import org.eclipse.gef.mvc.parts.IContentPart;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * The {@link SelectionModel} is used to store the current viewer's selection.
 * It represents the selection as an ordered list of {@link IContentPart}s.
 * Thereby, it supports a multi-selection and allows to identify a primary
 * selection (the head element of the list) that may be treated specially.
 *
 * @author anyssen
 * @author mwienand
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 *
 */
// TODO: We could expose the selection as modifiable collection and modifiable
// read-only property if we could use an ordered set. As we use a list, we have
// to ensure it does not contain duplicates.
public class SelectionModel<VR> implements IDisposable {

	/**
	 * Name of the {@link #selectionUnmodifiableProperty()}.
	 */
	public static final String SELECTION_PROPERTY = "selection";

	private ObservableList<IContentPart<VR, ? extends VR>> selection = CollectionUtils
			.observableArrayList();
	private ObservableList<IContentPart<VR, ? extends VR>> selectionUnmodifiable = FXCollections
			.unmodifiableObservableList(selection);
	private ReadOnlyListWrapper<IContentPart<VR, ? extends VR>> selectionUnmodifiableProperty = new ReadOnlyListWrapperEx<>(
			this, SELECTION_PROPERTY, selectionUnmodifiable);

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
	public void appendToSelection(IContentPart<VR, ? extends VR> toBeAppended) {
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
			List<? extends IContentPart<VR, ? extends VR>> toBeAppended) {
		List<IContentPart<VR, ? extends VR>> newSelection = getSelectionCopy();
		newSelection.removeAll(toBeAppended);
		for (IContentPart<VR, ? extends VR> p : toBeAppended) {
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
		// XXX With JavaFX 2.2, a change would be fired when the selection is
		// cleared,even if it was already empty. With JavaFX 8 this is not the
		// case. We ensure same behavior for listeners by ensuring clear is only
		// called if needed.
		if (!selection.isEmpty()) {
			selection.clear();
		}
	}

	/**
	 * @since 1.1
	 */
	@Override
	public void dispose() {
		selection.clear();
	}

	/**
	 * Returns a modifiable list of the currently selected {@link IContentPart}
	 * s.
	 *
	 * @return A modifiable list of the currently selected {@link IContentPart}
	 *         s.
	 */
	private List<IContentPart<VR, ? extends VR>> getSelectionCopy() {
		return new ArrayList<>(selection);
	}

	/**
	 * Returns an unmodifiable observable list of the currently selected
	 * {@link IContentPart}s.
	 *
	 * @return An unmodifiable observable list of the currently selected
	 *         {@link IContentPart}s.
	 */
	public ObservableList<IContentPart<VR, ? extends VR>> getSelectionUnmodifiable() {
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
	public boolean isSelected(IContentPart<VR, ? extends VR> contentPart) {
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
	public void prependToSelection(
			IContentPart<VR, ? extends VR> toBePrepended) {
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
			List<? extends IContentPart<VR, ? extends VR>> toBePrepended) {
		List<IContentPart<VR, ? extends VR>> newSelection = getSelectionCopy();
		newSelection.removeAll(toBePrepended);
		int i = 0;
		for (IContentPart<VR, ? extends VR> p : toBePrepended) {
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
			Collection<? extends IContentPart<VR, ? extends VR>> contentParts) {
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
	public void removeFromSelection(
			IContentPart<VR, ? extends VR> contentPart) {
		selection.remove(contentPart);
	}

	/**
	 * Returns an unmodifiable read-only list property that represents the
	 * current selection.
	 *
	 * @return An unmodifiable read-only property named
	 *         {@link #SELECTION_PROPERTY}.
	 */
	public ReadOnlyListProperty<IContentPart<VR, ? extends VR>> selectionUnmodifiableProperty() {
		return selectionUnmodifiableProperty.getReadOnlyProperty();
	}

	/**
	 * Replaces the current selection with the given {@link IContentPart}.
	 *
	 * @param newSelection
	 *            The {@link IContentPart} constituting the new selection.
	 */
	public void setSelection(IContentPart<VR, ? extends VR> newSelection) {
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
			List<? extends IContentPart<VR, ? extends VR>> selection) {
		List<IContentPart<VR, ? extends VR>> newSelection = new ArrayList<>();
		int i = 0;
		for (IContentPart<VR, ? extends VR> p : selection) {
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
