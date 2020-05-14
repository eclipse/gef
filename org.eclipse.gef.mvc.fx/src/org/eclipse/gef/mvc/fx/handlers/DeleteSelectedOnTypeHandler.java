/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.mvc.fx.gestures.ClickDragGesture;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.policies.DeletionPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * The {@link DeleteSelectedOnTypeHandler} is an {@link IOnTypeHandler} that
 * performs content deletion upon the press of a key.
 *
 * @author mwienand
 *
 */
public class DeleteSelectedOnTypeHandler extends AbstractHandler
		implements IOnStrokeHandler {

	@Override
	public void abortPress() {
	}

	@Override
	public void finalRelease(KeyEvent event) {
	}

	@Override
	public void initialPress(KeyEvent event) {
		if (!isDelete(event)) {
			return;
		}

		// get current selection
		IViewer viewer = getHost().getRoot().getViewer();
		List<IContentPart<? extends Node>> selected = new ArrayList<>(viewer
				.getAdapter(SelectionModel.class).getSelectionUnmodifiable());

		// if no parts are selected, we do not delete anything
		if (selected.isEmpty()) {
			return;
		}

		// delete selected parts
		DeletionPolicy deletionPolicy = getHost().getRoot()
				.getAdapter(DeletionPolicy.class);
		init(deletionPolicy);
		for (IContentPart<? extends Node> s : selected) {
			deletionPolicy.delete(s);
		}
		commit(deletionPolicy);
	}

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
		ClickDragGesture tool = getHost().getRoot().getViewer().getDomain()
				.getAdapter(ClickDragGesture.class);
		if (tool != null && getHost().getRoot().getViewer().getDomain()
				.isExecutionTransactionOpen(tool)) {
			return false;
		}

		return true;
	}

	@Override
	public void press(KeyEvent event) {
	}

	@Override
	public void release(KeyEvent event) {
	}

}
