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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.gef4.common.notify.IPropertyChangeNotifier;
import org.eclipse.gef4.mvc.parts.IContentPart;

/**
 * The {@link SelectionModel} is used to store the current viewer's
 * {@link IContentPart} selection. A selection tool is used to update the
 * {@link SelectionModel} as the result of input events.
 *
 * @author anyssen
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

	private List<IContentPart<VR>> selectionList = new ArrayList<IContentPart<VR>>();
	private Set<IContentPart<VR>> selectionSet = new HashSet<IContentPart<VR>>();

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * Appends the given {@link IContentPart}s to the current selection, i.e.
	 * inserts them at the end of the selection.
	 *
	 * @param contentParts
	 *            The {@link IContentPart}s which are appended to the selection.
	 */
	public void appendSelection(List<IContentPart<VR>> contentParts) {
		List<IContentPart<VR>> oldSelection = getSelectionCopy();
		for (IContentPart<VR> p : contentParts) {
			if (!selectionSet.contains(p)) {
				selectionList.add(p);
				selectionSet.add(p);
			}
		}
		propertyChangeSupport.firePropertyChange(SELECTION_PROPERTY,
				oldSelection, getSelected());
	}

	/**
	 * Removes the given {@link IContentPart}s from the current selection.
	 *
	 * @param contentParts
	 *            The {@link IContentPart}s which are removed from the
	 *            selection.
	 */
	public void deselect(Collection<IContentPart<VR>> contentParts) {
		List<IContentPart<VR>> oldSelection = getSelectionCopy();
		selectionList.removeAll(contentParts);
		selectionSet.removeAll(contentParts);
		propertyChangeSupport.firePropertyChange(SELECTION_PROPERTY,
				oldSelection, getSelected());
	}

	/**
	 * Clears the current selection.
	 */
	public void deselectAll() {
		List<IContentPart<VR>> oldSelection = getSelectionCopy();
		selectionList.clear();
		selectionSet.clear();
		propertyChangeSupport.firePropertyChange(SELECTION_PROPERTY,
				oldSelection, getSelected());
	}

	/**
	 * Returns an unmodifiable list of the currently selected
	 * {@link IContentPart}s.
	 *
	 * @return An unmodifiable list of the currently selected
	 *         {@link IContentPart}s.
	 */
	public List<IContentPart<VR>> getSelected() {
		return Collections.unmodifiableList(selectionList);
	}

	/**
	 * Returns a modifiable list of the currently selected {@link IContentPart}
	 * s.
	 *
	 * @return A modifiable list of the currently selected {@link IContentPart}
	 *         s.
	 */
	private List<IContentPart<VR>> getSelectionCopy() {
		return new ArrayList<IContentPart<VR>>(selectionList);
	}

	/**
	 * Returns <code>true</code> if the given {@link IContentPart} is part of
	 * the current selection.
	 *
	 * @param contentPart
	 *            The {@link IContentPart} which is checked for containment.
	 * @return <code>true</code> if the {@link IContentPart} is contained by the
	 *         current selection.
	 */
	public boolean isSelected(IContentPart<VR> contentPart) {
		return selectionSet.contains(contentPart);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * Selects the given list of {@link IContentPart}s. The parts are inserted
	 * at the beginning of the selection list.
	 *
	 * @param newlySelected
	 *            The {@link IContentPart}s to select.
	 */
	public void select(List<IContentPart<VR>> newlySelected) {
		List<IContentPart<VR>> oldSelection = getSelectionCopy();
		selectionList.removeAll(newlySelected);
		selectionSet.removeAll(newlySelected);
		int i = 0;
		for (IContentPart<VR> p : newlySelected) {
			if (!selectionSet.contains(p)) {
				selectionList.add(i++, p);
				selectionSet.add(p);
			}
		}
		propertyChangeSupport.firePropertyChange(SELECTION_PROPERTY,
				oldSelection, getSelected());
	}

	/**
	 * Replaces the whole selection with the given list of {@link IContentPart}
	 * s.
	 *
	 * @param newSelection
	 *            The list of {@link IContentPart}s constituting the new
	 *            selection.
	 */
	public void updateSelection(List<IContentPart<VR>> newSelection) {
		selectionList.clear();
		selectionSet.clear();
		select(newSelection);
	}

}
