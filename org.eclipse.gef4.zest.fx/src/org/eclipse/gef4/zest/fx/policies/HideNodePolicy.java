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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.operations.ChangeFocusOperation;
import org.eclipse.gef4.mvc.operations.ChangeSelectionOperation;
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
public class HideNodePolicy extends AbstractPolicy<Node> {

	/**
	 * Returns an {@link ITransactionalOperation} that removes the given
	 * {@link IContentPart} from the {@link FocusModel} and from the
	 * {@link SelectionModel} of the corresponding {@link IViewer}.
	 *
	 * @param part
	 *            The {@link IContentPart} that is removed from the viewer
	 *            models.
	 * @return An {@link ITransactionalOperation} that changes the viewer
	 *         models.
	 */
	protected ITransactionalOperation createClearViewerModelsOperation(IContentPart<Node, ? extends Node> part) {
		ReverseUndoCompositeOperation clearOp = new ReverseUndoCompositeOperation("ClearViewerModels()");
		IViewer<Node> viewer = part.getRoot().getViewer();
		// remove from focus model
		FocusModel<Node> focusModel = viewer.<FocusModel<Node>> getAdapter(FocusModel.class);
		if (focusModel != null && focusModel.getFocused() == part) {
			clearOp.add(new ChangeFocusOperation<Node>(viewer, null));
		}
		// remove from selection model
		SelectionModel<Node> selectionModel = viewer.<SelectionModel<Node>> getAdapter(SelectionModel.class);
		if (selectionModel != null) {
			List<IContentPart<Node, ? extends Node>> selected = selectionModel.getSelected();
			if (selected.contains(part)) {
				List<IContentPart<Node, ? extends Node>> newSelection = new ArrayList<IContentPart<Node, ? extends Node>>(
						selected);
				newSelection.remove(part);
				clearOp.add(new ChangeSelectionOperation<Node>(viewer, newSelection));
			}
		}
		return clearOp.unwrap(true);
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
		revOp.add(createClearViewerModelsOperation(getHost()));
		revOp.add(HideOperation.hide(getHost()));
		getHost().getRoot().getViewer().getDomain().execute(revOp);
	}

	/**
	 * Executes an operation on the history that shows the {@link #getHost()
	 * host} {@link NodeContentPart}.
	 */
	public void show() {
		getHost().getRoot().getViewer().getDomain().execute(HideOperation.show(getHost()));
	}

}
