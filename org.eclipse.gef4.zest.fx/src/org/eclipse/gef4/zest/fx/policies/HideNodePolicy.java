/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.policies;

import java.util.Collections;

import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.operations.ChangeFocusOperation;
import org.eclipse.gef4.mvc.operations.DeselectOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.gef4.zest.fx.operations.HideOperation;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

import javafx.scene.Node;

/**
 * The {@link HideNodePolicy} can be installed on {@link NodeContentPart}. It
 * provides two methods:
 * <ul>
 * <li>{@link #hide()} to hide its host {@link NodeContentPart}
 * <li>{@link #show()} to show its host {@link NodeContentPart}
 * </ul>
 *
 * @author mwienand
 *
 */
// TODO: only applicable for NodeContentPart (override #getHost)
// TODO: Migrate into transactional policy
public class HideNodePolicy extends AbstractPolicy<Node> {

	/**
	 * Returns an {@link ITransactionalOperation} that removes the given
	 * {@link IContentPart} from the {@link SelectionModel} of the corresponding
	 * {@link IViewer}.
	 *
	 * @param part
	 *            The {@link IContentPart} that is removed from the
	 *            {@link SelectionModel}.
	 * @return An {@link ITransactionalOperation} that changes the
	 *         {@link SelectionModel}.
	 */
	protected ITransactionalOperation createDeselectOperation(IContentPart<Node, ? extends Node> part) {
		IViewer<Node> viewer = part.getRoot().getViewer();

		return new DeselectOperation<Node>(viewer, Collections.singletonList(part));
	}

	/**
	 * Returns an {@link ITransactionalOperation} that removes the given
	 * {@link IContentPart} from the {@link FocusModel} of the corresponding
	 * {@link IViewer}.
	 *
	 * @param part
	 *            The {@link IContentPart} that is removed from the
	 *            {@link FocusModel}.
	 * @return An {@link ITransactionalOperation} that changes the
	 *         {@link FocusModel}.
	 */
	protected ITransactionalOperation createUnfocusOperation(IContentPart<Node, ? extends Node> part) {
		IViewer<Node> viewer = part.getRoot().getViewer();

		FocusModel<Node> focusModel = viewer.<FocusModel<Node>> getAdapter(FocusModel.class);
		if (focusModel != null) {
			if (focusModel.getFocused() == part) {
				return new ChangeFocusOperation<Node>(viewer, null);

			}
		}
		return null;
	}

	@Override
	public NodeContentPart getHost() {
		return (NodeContentPart) super.getHost();
	}

	/**
	 * Executes an operation on the history that hides the {@link #getHost()
	 * host} {@link NodeContentPart} (and removes it from the {@link FocusModel}
	 * and from the {@link SelectionModel}).
	 */
	public void hide() {
		ReverseUndoCompositeOperation revOp = new ReverseUndoCompositeOperation("Hide()");
		ITransactionalOperation unfocusOperation = createUnfocusOperation(getHost());
		if (unfocusOperation != null) {
			revOp.add(unfocusOperation);
		}
		ITransactionalOperation deselectOperation = createDeselectOperation(getHost());
		if (deselectOperation != null) {
			revOp.add(deselectOperation);
		}
		HideOperation hideOperation = HideOperation.hide(getHost());
		if (hideOperation != null) {
			revOp.add(hideOperation);
		}
		// TODO: re-layout
		getHost().getRoot().getViewer().getDomain().execute(revOp);
	}

	/**
	 * Executes an operation on the history that shows the {@link #getHost()
	 * host} {@link NodeContentPart}.
	 */
	public void show() {
		HideOperation showOperation = HideOperation.show(getHost());
		// TODO: re-layout
		if (showOperation != null) {
			getHost().getRoot().getViewer().getDomain().execute(showOperation);
		}
	}

}
