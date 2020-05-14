/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.mvc.fx.operations.SelectOperation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * The {@link SelectAllOnTypeHandler} is an {@link AbstractHandler} that selects
 * all {@link #getSelectableContentParts() selectable parts} within the
 * {@link #getHost() host's} {@link IViewer} when Ctrl-A is pressed.
 *
 * @author wienand
 *
 */
public class SelectAllOnTypeHandler extends AbstractHandler
		implements IOnTypeHandler {

	/**
	 * Returns a list containing the {@link IContentPart}s that should be
	 * selected by this action handler at the point of time this method is
	 * called.
	 * <p>
	 * Per default, all active and selectable parts within the content-part-map
	 * of the current viewer are returned.
	 *
	 * @return A list containing the {@link IContentPart}s that should be
	 *         selected by this action handler at the point of time this method
	 *         is called.
	 */
	protected List<? extends IContentPart<? extends Node>> getSelectableContentParts() {
		if (getHost().getViewer() == null) {
			return Collections.emptyList();
		}
		ArrayList<IContentPart<? extends Node>> parts = new ArrayList<>(
				getHost().getViewer().getContentPartMap().values());
		parts.removeIf(p -> !p.isSelectable());
		return parts;
	}

	/**
	 * Returns <code>true</code> if the given {@link KeyEvent} should trigger
	 * "SelectAll". Otherwise returns <code>false</code>.
	 *
	 * @param keyEvent
	 *            The {@link KeyEvent}.
	 * @param pressedKeys
	 *            The set of pressed {@link KeyCode}s.
	 * @return <code>true</code> if the given {@link KeyEvent} should trigger
	 *         "SelectAll", <code>false</code> otherwise.
	 */
	protected boolean isSelectAll(KeyEvent keyEvent, Set<KeyCode> pressedKeys) {
		return keyEvent.isControlDown() && pressedKeys.contains(KeyCode.A);
	}

	@Override
	public void type(KeyEvent keyEvent, Set<KeyCode> pressedKeys) {
		if (isSelectAll(keyEvent, pressedKeys)) {
			SelectOperation selectOperation = new SelectOperation(
					getHost().getViewer(), getSelectableContentParts());
			try {
				getHost().getViewer().getDomain().execute(selectOperation,
						null);
			} catch (ExecutionException e) {
				throw new IllegalStateException(e);
			}
		}
	}
}
