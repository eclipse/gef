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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.policies.DeletionPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * The {@link FXDeleteSelectedOnTypePolicy} is an {@link AbstractFXOnTypePolicy}
 * that performs content deletion upon the press of a key.
 *
 * @author mwienand
 *
 */
public class FXDeleteSelectedOnTypePolicy extends AbstractFXOnTypePolicy {

	/**
	 * Returns <code>true</code> if the given {@link KeyEvent} is a "delete"
	 * event, i.e. the {@link KeyEvent#getCode()} is {@link KeyCode#DELETE} and
	 * no drag policy is currently running. Otherwise returns <code>false</code>
	 * .
	 *
	 * @param event
	 *            The {@link KeyEvent} in question.
	 * @return <code>true</code> if the given {@link KeyEvent} should trigger
	 *         content deletion, otherwise <code>false</code>.
	 */
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
		List<IContentPart<Node, ? extends Node>> selected = new ArrayList<IContentPart<Node, ? extends Node>>(
				viewer.<SelectionModel<Node>> getAdapter(SelectionModel.class)
						.getSelection());

		// if no parts are selected, we do not delete anything
		if (selected.isEmpty()) {
			return;
		}

		// delete selected parts
		DeletionPolicy<Node> deletionPolicy = getHost().getRoot()
				.<DeletionPolicy<Node>> getAdapter(DeletionPolicy.class);
		init(deletionPolicy);
		for (IContentPart<Node, ? extends Node> s : selected) {
			deletionPolicy.delete(s);
		}
		commit(deletionPolicy);
	}

	@Override
	public void released(KeyEvent event) {
	}

	@Override
	public void typed(KeyEvent event) {
	}

}
