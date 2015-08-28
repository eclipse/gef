/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.policies.DeletionPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class FXDeleteSelectedOnTypePolicy extends AbstractFXOnTypePolicy {

	protected boolean isDelete(KeyEvent event) {
		// only delete on <DELETE> key
		if (event.getCode() != KeyCode.DELETE) {
			return false;
		}

		// prevent deletion when other drag policies are running
		FXClickDragTool tool = getHost().getRoot().getViewer().getDomain()
				.getAdapter(FXClickDragTool.class);
		if (tool != null && getHost().getRoot().getViewer().getDomain()
				.isExecutionTransactionOpen(tool)) {
			return false;
		}

		return true;
	}

	@Override
	public void pressed(KeyEvent event) {
		if (!isDelete(event)) {
			return;
		}

		// get current selection
		IViewer<Node> viewer = getHost().getRoot().getViewer();
		List<IContentPart<Node, ? extends Node>> selected = viewer
				.<SelectionModel<Node>> getAdapter(SelectionModel.class)
				.getSelected();

		// if no parts are selected, we do not delete anything
		if (selected.isEmpty()) {
			return;
		}

		DeletionPolicy<Node> deletionPolicy = getHost()
				.<DeletionPolicy<Node>> getAdapter(DeletionPolicy.class);
		if (deletionPolicy == null) {
			// ignore this event when no DeletionPolicy is available
			return;
		}

		// delete part
		deletionPolicy.init();
		deletionPolicy.delete(selected);
		IUndoableOperation deleteOperation = deletionPolicy.commit();

		// execute on stack
		getHost().getRoot().getViewer().getDomain().execute(deleteOperation);
	}

	@Override
	public void released(KeyEvent event) {
	}

}
