/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.handlers;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.mvc.fx.models.FocusModel;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.operations.ChangeFocusOperation;
import org.eclipse.gef.mvc.fx.operations.ChangeSelectionOperation;
import org.eclipse.gef.mvc.fx.operations.DeselectOperation;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.operations.SelectOperation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.common.reflect.TypeToken;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * The {@link FocusAndSelectOnClickHandler} is an {@link IOnClickHandler} that
 * focuses and selects its {@link #getHost() host} by altering the
 * {@link FocusModel} and the {@link SelectionModel} when the {@link #getHost()
 * host} is clicked by the mouse.
 *
 * @author anyssen
 *
 */
public class FocusAndSelectOnClickHandler extends AbstractHandler
		implements IOnClickHandler {

	@SuppressWarnings("serial")
	@Override
	public void click(MouseEvent e) {
		// focus and select are only done on single click
		if (!isFocusAndSelect(e)) {
			return;
		}

		IVisualPart<? extends Node> host = getHost();
		IViewer viewer = host.getRoot().getViewer();
		SelectionModel selectionModel = viewer.getAdapter(SelectionModel.class);

		// determine if replacing or extending the selection
		boolean append = isAppend(e);

		// perform different changes depending on host type
		if (host instanceof IContentPart) {
			IContentPart<? extends Node> contentPart = (IContentPart<? extends Node>) host;

			// check if the host is the explicit event target
			if (isRegistered(e.getTarget())
					&& !isRegisteredForHost(e.getTarget())) {
				// do not process events for other parts
				return;
			}

			List<IContentPart<? extends Node>> singletonHostList = Collections
					.<IContentPart<? extends Node>> singletonList(contentPart);

			// create selection change operation(s)
			boolean wasDeselected = false;
			ITransactionalOperation selectionChangeOperation = null;
			if (selectionModel.isSelected(contentPart)) {
				if (append) {
					// deselect the host
					selectionChangeOperation = new DeselectOperation(viewer,
							singletonHostList);
					wasDeselected = true;
				}
			} else if (contentPart.isSelectable()) {
				if (append) {
					// prepend host to current selection (as new primary)
					selectionChangeOperation = new SelectOperation(viewer,
							singletonHostList);
				} else {
					// clear old selection, host becomes the only selected
					selectionChangeOperation = new ChangeSelectionOperation(
							viewer, singletonHostList);
				}
			}

			// execute selection changes
			if (selectionChangeOperation != null) {
				try {
					viewer.getDomain().execute(selectionChangeOperation,
							new NullProgressMonitor());
				} catch (ExecutionException e1) {
					throw new IllegalStateException(e1);
				}
			}

			// change focus depending on selection changes
			ChangeFocusOperation changeFocusOperation = null;
			ObservableList<IContentPart<? extends Node>> selection = selectionModel
					.getSelectionUnmodifiable();
			if (wasDeselected && selection.isEmpty()) {
				// unfocus when the only selected part was deselected
				changeFocusOperation = new ChangeFocusOperation(viewer, null);
			} else {
				// focus new primary selection
				IContentPart<? extends Node> primarySelection = selection
						.get(0);
				if (primarySelection.isFocusable()) {
					FocusModel focusModel = viewer
							.getAdapter(new TypeToken<FocusModel>() {
							});
					if (focusModel.getFocus() == primarySelection) {
						primarySelection.getVisual().requestFocus();
					} else {
						changeFocusOperation = new ChangeFocusOperation(viewer,
								primarySelection);
					}
				}
			}

			// execute focus change
			if (changeFocusOperation != null) {
				try {
					viewer.getDomain().execute(changeFocusOperation,
							new NullProgressMonitor());
				} catch (ExecutionException e1) {
					throw new IllegalStateException(e1);
				}
			}
		} else if (host instanceof IRootPart) {
			// check if click on background (either one of the root visuals, or
			// an unregistered visual)
			if (!isRegistered(e.getTarget())
					|| isRegisteredForHost(e.getTarget())) {
				// check if append-modifier is pressed
				if (append) {
					// do nothing
					return;
				}

				// unset focus and clear selection
				try {
					FocusModel focusModel = viewer
							.getAdapter(new TypeToken<FocusModel>() {
							});
					if (focusModel.getFocus() == null) {
						// no focus change needed, only update feedback
						viewer.getRootPart().getVisual().requestFocus();
					} else {
						// change focus, will update feedback via behavior
						viewer.getDomain().execute(
								new ChangeFocusOperation(viewer, null),
								new NullProgressMonitor());
					}
					viewer.getDomain().execute(
							new DeselectOperation(viewer,
									selectionModel.getSelectionUnmodifiable()),
							new NullProgressMonitor());
				} catch (ExecutionException e1) {
					throw new IllegalStateException(e1);
				}
			}
		}
	}

	/**
	 * Returns <code>true</code> if the selection should be extended according
	 * to the given {@link MouseEvent}, <code>false</code> if it should be
	 * replaced.
	 *
	 * @param e
	 *            The {@link MouseEvent} for which to determine if the selection
	 *            is to be replaced or extended.
	 * @return <code>true</code> if the selection should be extended according
	 *         to the given {@link MouseEvent}, <code>false</code> if it should
	 *         be replaced.
	 */
	protected boolean isAppend(MouseEvent e) {
		return e.isShortcutDown();
	}

	/**
	 * Returns <code>true</code> if the given {@link MouseEvent} should trigger
	 * focus and select. Otherwise returns <code>false</code>. Per default
	 * returns <code>true</code> if a single mouse click is performed.
	 *
	 * @param event
	 *            The {@link MouseEvent} in question.
	 * @return <code>true</code> if the given {@link MouseEvent} should trigger
	 *         focus and select, otherwise <code>false</code>.
	 */
	protected boolean isFocusAndSelect(MouseEvent event) {
		return event.getClickCount() <= 1;
	}

}
