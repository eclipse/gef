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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.mvc.parts.IContentPart;

/**
 * 
 * @author anyssen
 *
 * @param <V>
 */
public class DefaultSelectionModel<V> implements ISelectionModel<V> {

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);
	private List<IContentPart<V>> selection = new ArrayList<IContentPart<V>>();

	/* (non-Javadoc)
	 * @see org.eclipse.gef4.mvc.partviewer.IContentPartSelection#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef4.mvc.partviewer.IContentPartSelection#appendSelection(org.eclipse.gef4.mvc.parts.IContentPart)
	 */
	@Override
	public void appendSelection(IContentPart<V> editpart) {
		List<IContentPart<V>> oldSelection = getSelectionCopy();
		selection.add(editpart);
		propertyChangeSupport.firePropertyChange(SELECTION_PROPERTY, oldSelection,
				getSelected());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef4.mvc.partviewer.IContentPartSelection#deselect(org.eclipse.gef4.mvc.parts.IContentPart)
	 */
	@Override
	public void deselect(IContentPart<V> editpart) {
		List<IContentPart<V>> oldSelection = getSelectionCopy();
		selection.remove(editpart);
		propertyChangeSupport.firePropertyChange(SELECTION_PROPERTY, oldSelection,
				getSelected());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef4.mvc.partviewer.IContentPartSelection#deselectAll()
	 */
	@Override
	public void deselectAll() {
		List<IContentPart<V>> oldSelection = getSelectionCopy();
		selection.clear();
		propertyChangeSupport.firePropertyChange(SELECTION_PROPERTY, oldSelection,
				getSelected());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef4.mvc.partviewer.IContentPartSelection#getSelection()
	 */
	@Override
	public List<IContentPart<V>> getSelected() {
		return Collections.unmodifiableList(selection);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef4.mvc.partviewer.IContentPartSelection#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef4.mvc.partviewer.IContentPartSelection#select(org.eclipse.gef4.mvc.parts.IContentPart)
	 */
	@Override
	public void select(IContentPart<V>... parts) {
		List<IContentPart<V>> newlySelected = Arrays.asList(parts);
		List<IContentPart<V>> oldSelection = getSelectionCopy();
		selection.removeAll(newlySelected);
		selection.addAll(0, newlySelected);
		propertyChangeSupport.firePropertyChange(SELECTION_PROPERTY, oldSelection,
				getSelected());
	}
	
	private List<IContentPart<V>> getSelectionCopy() {
		List<IContentPart<V>> oldSelection = new ArrayList<IContentPart<V>>(selection);
		return oldSelection;
	}
}
