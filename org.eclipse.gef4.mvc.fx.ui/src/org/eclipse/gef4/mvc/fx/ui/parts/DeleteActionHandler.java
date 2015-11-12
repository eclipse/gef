/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.ui.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.policies.DeletionPolicy;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.ActionFactory;

import javafx.scene.Node;

/**
 * An {@link Action} to handle deletion of selection elements in an
 * {@link FXViewer}.
 *
 * @author anyssen
 *
 */
public class DeleteActionHandler extends Action {

	private FXViewer viewer = null;
	private PropertyChangeListener selectionListener = new PropertyChangeListener() {

		@SuppressWarnings("unchecked")
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			updateEnabledState((SelectionModel<Node>) evt.getSource());
		}
	};

	/**
	 * Creates a new {@link DeleteActionHandler}.
	 */
	public DeleteActionHandler() {
		super("Delete");
		setId(ActionFactory.DELETE.getId());
		setEnabled(false);
	}

	private SelectionModel<Node> getSelectionModel() {
		if (viewer == null) {
			return null;
		}
		return viewer.<SelectionModel<Node>> getAdapter(SelectionModel.class);
	}

	/**
	 * Binds this {@link DeleteActionHandler} to the given viewer.
	 *
	 * @param viewer
	 *            The {@link FXViewer} to bind this {@link Action} to. May be
	 *            <code>null</code> to unbind this action.
	 */
	public void init(FXViewer viewer) {
		SelectionModel<Node> oldSelectionModel = getSelectionModel();
		SelectionModel<Node> newSelectionModel = null;
		this.viewer = viewer;
		if (viewer != null) {
			newSelectionModel = viewer
					.<SelectionModel<Node>> getAdapter(SelectionModel.class);
		}
		// register listeners to update enabled state
		if (oldSelectionModel != null
				&& oldSelectionModel != newSelectionModel) {
			oldSelectionModel.removePropertyChangeListener(selectionListener);
		}
		if (newSelectionModel != null
				&& oldSelectionModel != newSelectionModel) {
			newSelectionModel.addPropertyChangeListener(selectionListener);
		}
		updateEnabledState(newSelectionModel);
	}

	@Override
	public void runWithEvent(Event event) {
		// delete selected parts
		DeletionPolicy<Node> deletionPolicy = viewer.getRootPart()
				.<DeletionPolicy<Node>> getAdapter(DeletionPolicy.class);
		if (deletionPolicy == null) {
			throw new IllegalStateException(
					"DeleteActionHandler requires a DeletionPolicy to be registered at the viewer's root part.");
		}
		deletionPolicy.init();
		for (IContentPart<Node, ? extends Node> s : new ArrayList<IContentPart<Node, ? extends Node>>(
				getSelectionModel().getSelection())) {
			deletionPolicy.delete(s);
		}
		IUndoableOperation deleteOperation = deletionPolicy.commit();
		if (deleteOperation != null) {
			viewer.getDomain().execute(deleteOperation);
		}
	}

	/**
	 * Updates the enabled state of this {@link Action} dependent on the
	 * selection state of the {@link SelectionModel}.
	 *
	 * @param selectionModel
	 *            The {@link SelectionModel} to obtain the selection from.
	 */
	protected void updateEnabledState(SelectionModel<Node> selectionModel) {
		if (selectionModel == null) {
			setEnabled(false);
		} else {
			setEnabled(!selectionModel.getSelection().isEmpty());
		}
	}
}