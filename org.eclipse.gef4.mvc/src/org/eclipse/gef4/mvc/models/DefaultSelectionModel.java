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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * 
 * @author anyssen
 * 
 * @param <VR>
 *            The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public class DefaultSelectionModel<VR> implements ISelectionModel<VR> {

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);
	private List<IContentPart<VR>> selection = new ArrayList<IContentPart<VR>>();

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void appendSelection(IContentPart<VR> editpart) {
		List<IContentPart<VR>> oldSelection = getSelectionCopy();
		selection.add(editpart);
		propertyChangeSupport.firePropertyChange(SELECTION_PROPERTY,
				oldSelection, getSelected());
	}

	@Override
	public void deselect(IContentPart<VR> editpart) {
		List<IContentPart<VR>> oldSelection = getSelectionCopy();
		selection.remove(editpart);
		propertyChangeSupport.firePropertyChange(SELECTION_PROPERTY,
				oldSelection, getSelected());
	}

	@Override
	public void deselectAll() {
		List<IContentPart<VR>> oldSelection = getSelectionCopy();
		selection.clear();
		propertyChangeSupport.firePropertyChange(SELECTION_PROPERTY,
				oldSelection, getSelected());
	}

	@Override
	public List<IContentPart<VR>> getSelected() {
		return Collections.unmodifiableList(selection);
	}

	private List<IContentPart<VR>> getSelectionCopy() {
		List<IContentPart<VR>> oldSelection = new ArrayList<IContentPart<VR>>(
				selection);
		return oldSelection;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	@Override
	public void select(List<IContentPart<VR>> newlySelected) {
		List<IContentPart<VR>> oldSelection = getSelectionCopy();
		selection.removeAll(newlySelected);
		selection.addAll(0, newlySelected);
		propertyChangeSupport.firePropertyChange(SELECTION_PROPERTY,
				oldSelection, getSelected());
	}
}
