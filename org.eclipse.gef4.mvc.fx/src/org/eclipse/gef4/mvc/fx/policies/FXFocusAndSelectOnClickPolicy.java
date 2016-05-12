/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.operations.ChangeFocusOperation;
import org.eclipse.gef4.mvc.operations.DeselectOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.SelectOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.reflect.TypeToken;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * The {@link FXFocusAndSelectOnClickPolicy} is an {@link IFXOnClickPolicy} that
 * focuses and selects its {@link #getHost() host} by altering the
 * {@link FocusModel} and the {@link SelectionModel} when the {@link #getHost()
 * host} is clicked by the mouse.
 *
 * @author anyssen
 *
 */
public class FXFocusAndSelectOnClickPolicy extends AbstractFXInteractionPolicy
		implements IFXOnClickPolicy {

	@SuppressWarnings("serial")
	@Override
	public void click(MouseEvent e) {
		// focus and select are only done on single click
		if (e.getClickCount() > 1) {
			return;
		}

		IVisualPart<Node, ? extends Node> host = getHost();
		IViewer<Node> viewer = host.getRoot().getViewer();
		SelectionModel<Node> selectionModel = viewer
				.getAdapter(new TypeToken<SelectionModel<Node>>() {
				});

		// query current selection
		ObservableList<IContentPart<Node, ? extends Node>> oldSelection = selectionModel
				.getSelectionUnmodifiable();

		// perform different changes depending on host type
		if (host instanceof IContentPart) {
			IContentPart<Node, ? extends Node> contentPart = (IContentPart<Node, ? extends Node>) host;

			// check if the host is the explicit event target
			if (isRegistered(e.getTarget())
					&& !isRegisteredForHost(e.getTarget())) {
				// do not process events for other parts
				return;
			}

			// determine if replacing or extending the selection
			boolean append = e.isControlDown();
			List<IContentPart<Node, ? extends Node>> singletonHostList = Collections
					.<IContentPart<Node, ? extends Node>> singletonList(
							contentPart);

			// create selection change operation(s)
			boolean wasDeselected = false;
			ITransactionalOperation selectionChangeOperation = null;
			if (selectionModel.isSelected(contentPart)) {
				if (append) {
					// deselect the host
					selectionChangeOperation = new DeselectOperation<>(viewer,
							singletonHostList);
					wasDeselected = true;
				}
			} else if (contentPart.isSelectable()) {
				if (append) {
					// prepend host to current selection (as new primary)
					selectionChangeOperation = new SelectOperation<>(viewer,
							singletonHostList);
				} else {
					// clear old selection, host becomes the only selected
					ReverseUndoCompositeOperation revOp = new ReverseUndoCompositeOperation(
							"SetSelection()");
					revOp.add(new DeselectOperation<>(viewer, oldSelection));
					revOp.add(new SelectOperation<>(viewer, singletonHostList));
					selectionChangeOperation = revOp;
				}
			}

			// execute selection changes
			if (selectionChangeOperation != null) {
				try {
					viewer.getDomain().execute(selectionChangeOperation);
				} catch (ExecutionException e1) {
					throw new IllegalStateException(e1);
				}
			}

			// change focus depending on selection changes
			ChangeFocusOperation<Node> changeFocusOperation = null;
			ObservableList<IContentPart<Node, ? extends Node>> selection = selectionModel
					.getSelectionUnmodifiable();
			if (wasDeselected && selection.isEmpty()) {
				// unfocus when the only selected part was deselected
				changeFocusOperation = new ChangeFocusOperation<>(viewer, null);
			} else {
				// focus new primary selection
				IContentPart<Node, ? extends Node> primarySelection = selection
						.get(0);
				if (primarySelection.isFocusable()) {
					FocusModel<Node> focusModel = viewer
							.getAdapter(new TypeToken<FocusModel<Node>>() {
							});
					if (focusModel.getFocus() == primarySelection) {
						primarySelection.getVisual().requestFocus();
					} else {
						changeFocusOperation = new ChangeFocusOperation<>(
								viewer, primarySelection);
					}
				}
			}

			// execute focus change
			if (changeFocusOperation != null) {
				try {
					viewer.getDomain().execute(changeFocusOperation);
				} catch (ExecutionException e1) {
					throw new IllegalStateException(e1);
				}
			}
		} else if (host instanceof IRootPart) {
			// check if click on background (either one of the root visuals, or
			// an unregistered visual)
			if (!isRegistered(e.getTarget())
					|| isRegisteredForHost(e.getTarget())) {
				// unset focus and clear selection
				try {
					FocusModel<Node> focusModel = viewer
							.getAdapter(new TypeToken<FocusModel<Node>>() {
							});
					if (focusModel.getFocus() == null) {
						// no focus change needed, only update feedback
						viewer.getRootPart().getVisual().requestFocus();
					} else {
						// change focus, will update feedback via behavior
						viewer.getDomain().execute(
								new ChangeFocusOperation<>(viewer, null));
					}
					viewer.getDomain().execute(new DeselectOperation<>(viewer,
							selectionModel.getSelectionUnmodifiable()));
				} catch (ExecutionException e1) {
					throw new IllegalStateException(e1);
				}
			}
		}
	}

}
