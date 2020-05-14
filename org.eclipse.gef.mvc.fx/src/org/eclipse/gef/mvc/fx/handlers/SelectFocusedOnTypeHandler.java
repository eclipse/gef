/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
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

import java.util.Collections;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.operations.ChangeSelectionOperation;
import org.eclipse.gef.mvc.fx.operations.DeselectOperation;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.operations.SelectOperation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * The {@link SelectFocusedOnTypeHandler} implements (de-)selecting a focused
 * part via the keyboard.
 *
 * @author mwienand
 *
 */
public class SelectFocusedOnTypeHandler extends AbstractHandler
		implements IOnStrokeHandler {

	@Override
	public void abortPress() {
	}

	@Override
	public void finalRelease(KeyEvent event) {
	}

	@Override
	public void initialPress(KeyEvent event) {
		// only react to events fired directly at our host
		if (isRegistered(event.getTarget())
				&& !isRegisteredForHost(event.getTarget())) {
			return;
		}

		// only react to the SPACE key
		if (!isSelect(event)) {
			return;
		}

		IVisualPart<? extends Node> host = getHost();
		IViewer viewer = host.getRoot().getViewer();
		SelectionModel selectionModel = viewer.getAdapter(SelectionModel.class);

		ITransactionalOperation op = null;
		if (host instanceof IRootPart) {
			// clear the selection if on the root part/background
			op = new DeselectOperation(viewer,
					selectionModel.getSelectionUnmodifiable());
		} else if (host instanceof IContentPart) {
			IContentPart<? extends Node> contentPart = (IContentPart<? extends Node>) host;
			// depending on modifier, append or set the selection
			if (event.isControlDown()) {
				// append selection
				if (selectionModel.isSelected(contentPart)) {
					op = new DeselectOperation(viewer,
							Collections.singletonList(contentPart));
				} else {
					op = new SelectOperation(viewer,
							Collections.singletonList(contentPart));
				}
			} else {
				// set selection
				op = new ChangeSelectionOperation(viewer,
						Collections.singletonList(contentPart));
			}
		}

		// execute on stack
		if (op != null) {
			try {
				viewer.getDomain().execute(op, new NullProgressMonitor());
			} catch (ExecutionException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	/**
	 * Returns <code>true</code> if the given {@link KeyEvent} should trigger
	 * selection. Otherwise returns <code>false</code>. Per default returns
	 * <code>true</code> if <code>&lt;Space&gt;</code> is pressed.
	 *
	 * @param event
	 *            The {@link KeyEvent} in question.
	 * @return <code>true</code> if the given {@link KeyEvent} should trigger
	 *         zooming, otherwise <code>false</code>.
	 */
	protected boolean isSelect(KeyEvent event) {
		return KeyCode.SPACE.equals(event.getCode());
	}

	@Override
	public void press(KeyEvent event) {
	}

	@Override
	public void release(KeyEvent event) {
	}

}
