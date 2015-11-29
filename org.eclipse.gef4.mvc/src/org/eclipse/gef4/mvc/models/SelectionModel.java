/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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
package org.eclipse.gef4.mvc.models;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.common.properties.IPropertyChangeNotifier;
import org.eclipse.gef4.mvc.parts.IContentPart;

/**
 * The {@link SelectionModel} is used to store the current viewer's selection.
 * It represents the selection as an ordered list of {@link IContentPart}s.
 * Thereby, it supports a multi-selection and allows to identify a primary
 * selection (the head element of the list) that may be treated specially.
 * <p>
 * The {@link SelectionModel} is an {@link IPropertyChangeNotifier} and will
 * notify about changes to the selection, using the {@link #SELECTION_PROPERTY}
 * property name.
 *
 * @author anyssen
 * @author mwienand
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 *
 */
public class SelectionModel<VR> implements IPropertyChangeNotifier {

	/**
	 * <pre>
	 * &quot;selection&quot;
	 * </pre>
	 *
	 * The property name which is used for {@link PropertyChangeEvent}s.
	 */
	public static final String SELECTION_PROPERTY = "selection";

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);

	private List<IContentPart<VR, ? extends VR>> selection = new ArrayList<>();

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

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
		List<IContentPart<VR, ? extends VR>> oldSelection = getSelectionCopy();
		selection.removeAll(toBeAppended);
		for (IContentPart<VR, ? extends VR> p : toBeAppended) {
			if (selection.contains(p)) {
				throw new IllegalArgumentException("The content part " + p
						+ " is provided more than once in the given list.");
			}
			selection.add(p);
		}
		propertyChangeSupport.firePropertyChange(SELECTION_PROPERTY,
				oldSelection, getSelection());
	}

	/**
	 * Clears the current selection.
	 */
	public void clearSelection() {
		List<IContentPart<VR, ? extends VR>> oldSelection = getSelectionCopy();
		selection.clear();
		propertyChangeSupport.firePropertyChange(SELECTION_PROPERTY,
				oldSelection, getSelection());
	}

	/**
	 * Returns an unmodifiable list of the currently selected
	 * {@link IContentPart}s.
	 *
	 * @return An unmodifiable list of the currently selected
	 *         {@link IContentPart}s.
	 */
	public List<IContentPart<VR, ? extends VR>> getSelection() {
		return Collections.unmodifiableList(selection);
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
		List<IContentPart<VR, ? extends VR>> oldSelection = getSelectionCopy();
		selection.removeAll(toBePrepended);
		int i = 0;
		for (IContentPart<VR, ? extends VR> p : toBePrepended) {
			if (selection.contains(p)) {
				throw new IllegalArgumentException("The content part " + p
						+ " is provided more than once in the given list.");
			}
			selection.add(i++, p);
		}
		propertyChangeSupport.firePropertyChange(SELECTION_PROPERTY,
				oldSelection, getSelection());
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
		List<IContentPart<VR, ? extends VR>> oldSelection = getSelectionCopy();
		selection.removeAll(contentParts);
		propertyChangeSupport.firePropertyChange(SELECTION_PROPERTY,
				oldSelection, getSelection());
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
		removeFromSelection(Collections.singletonList(contentPart));
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
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
	 * @param newSelection
	 *            The list of {@link IContentPart}s constituting the new
	 *            selection.
	 */
	public void setSelection(
			List<? extends IContentPart<VR, ? extends VR>> newSelection) {
		List<IContentPart<VR, ? extends VR>> oldSelection = getSelectionCopy();
		selection.clear();
		int i = 0;
		for (IContentPart<VR, ? extends VR> p : newSelection) {
			if (selection.contains(p)) {
				throw new IllegalArgumentException("The content part " + p
						+ " is provided more than once in the given list.");
			}
			selection.add(i++, p);
		}
		propertyChangeSupport.firePropertyChange(SELECTION_PROPERTY,
				oldSelection, getSelection());
	}

}
