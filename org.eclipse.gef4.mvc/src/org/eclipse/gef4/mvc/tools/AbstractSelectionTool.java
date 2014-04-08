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
package org.eclipse.gef4.mvc.tools;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.models.IContentModel;
import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.ISelectionPolicy;

/**
 * 
 * @author anyssen
 * @author mwienand
 * 
 * @param <V>
 */
// TODO: marquee selection can by performed by drag policy on root
public abstract class AbstractSelectionTool<V> extends AbstractTool<V>
		implements PropertyChangeListener {

	@Override
	public void setDomain(IDomain<V> domain) {
		super.setDomain(domain);
	}

	@SuppressWarnings("unchecked")
	protected ISelectionPolicy<V> getToolPolicy(IVisualPart<V> visualPart) {
		return visualPart.getBound(ISelectionPolicy.class);
	}

	/**
	 * 
	 * @param targetPart
	 * @param append
	 * @return <code>true</code> on selection change, otherwise
	 *         <code>false</code>
	 */
	@SuppressWarnings("unchecked")
	public boolean select(IContentPart<V> targetPart, boolean append) {
		// TODO: extract into tool policy
		boolean changed = true;

		ISelectionModel<V> selectionModel = getSelectionModel();
		// retrieve old selection
		List<IContentPart<V>> oldSelection = new ArrayList<IContentPart<V>>(
				selectionModel.getSelected());
		// determine new selection
		if (targetPart == null || getToolPolicy(targetPart) == null
				|| !getToolPolicy(targetPart).isSelectable()) {
			// remove all selected
			selectionModel.deselectAll();
		} else {
			if (oldSelection.contains(targetPart)) {
				if (append) {
					// deselect the target edit part (ensure we get a new
					// primary selection)
					selectionModel.deselect(targetPart);
				} else {
					// target should become the new primary selection
					// selectionModel.select(targetEditPart);
					changed = false;
				}
			} else {
				if (append) {
					// append to current selection (as new primary)
					selectionModel.select(targetPart);
				} else {
					// clear old selection, target should become the only
					// selected
					selectionModel.deselectAll();
					selectionModel.select(targetPart);
				}
			}
		}

		return changed;
	}

	protected ISelectionModel<V> getSelectionModel() {
		return getDomain().getViewer().getSelectionModel();
	}

	@Override
	protected void registerListeners() {
		getDomain().getViewer().getContentModel()
				.addPropertyChangeListener(this);
	}

	@Override
	protected void unregisterListeners() {
		getDomain().getViewer().getContentModel()
				.removePropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		/*
		 * TODO: Viewer should flush interaction model data when contents
		 * changes.
		 */
		if (evt.getPropertyName().equals(IContentModel.CONTENTS_PROPERTY)) {
			select(null, false);
		}
	}

}
