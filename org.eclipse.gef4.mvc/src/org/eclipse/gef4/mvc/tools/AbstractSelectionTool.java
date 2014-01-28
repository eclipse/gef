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

import org.eclipse.gef4.mvc.domain.IEditDomain;
import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractSelectionFeedbackPolicy;
import org.eclipse.gef4.mvc.policies.ISelectionToolPolicy;
import org.eclipse.gef4.mvc.viewer.IVisualPartViewer;

/**
 * 
 * @author anyssen
 * @author mwienand
 * 
 * @param <V>
 */
public abstract class AbstractSelectionTool<V> extends AbstractTool<V>
		implements PropertyChangeListener {

	@Override
	public void setDomain(IEditDomain<V> domain) {
		super.setDomain(domain);
	}

	// @SuppressWarnings("unchecked")
	// protected AbstractSelectionFeedbackPolicy<V> getSelectionPolicy(
	// IVisualPart<V> editPart) {
	// return editPart.getEditPolicy(AbstractSelectionFeedbackPolicy.class);
	// }

	protected ISelectionToolPolicy getToolPolicy(IVisualPart<V> visualPart) {
		return visualPart.getEditPolicy(ISelectionToolPolicy.class);
	}

	/**
	 * 
	 * @param targetEditPart
	 * @param append
	 * @return <code>true</code> on selection change, otherwise
	 *         <code>false</code>
	 */
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
	public void activate() {
		super.activate();
		getDomain().getViewer().addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		getDomain().getViewer().removePropertyChangeListener(this);
		super.deactivate();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		/*
		 * TODO: Viewer should flush interaction model data when contents
		 * changes.
		 */
		if (evt.getPropertyName().equals(IVisualPartViewer.CONTENTS_PROPERTY)) {
			select(null, false);
		}
	}

}
