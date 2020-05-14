/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.policies;

import java.util.Collections;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.mvc.fx.behaviors.HoverBehavior;
import org.eclipse.gef.mvc.fx.models.FocusModel;
import org.eclipse.gef.mvc.fx.models.HoverModel;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.operations.AbstractCompositeOperation;
import org.eclipse.gef.mvc.fx.operations.ChangeFocusOperation;
import org.eclipse.gef.mvc.fx.operations.DeselectOperation;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.policies.AbstractPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.zest.fx.models.HidingModel;
import org.eclipse.gef.zest.fx.operations.HideOperation;
import org.eclipse.gef.zest.fx.parts.NodePart;

import com.google.common.reflect.TypeToken;

import javafx.scene.Node;

/**
 * The {@link HidePolicy} can be installed on {@link NodePart} to hide the
 * contents.
 *
 * @author mwienand
 *
 */
// TODO: only applicable for NodePart (override #getHost)
public class HidePolicy extends AbstractPolicy {

	/**
	 * Clears the hover handles for the given part upon execution of this
	 * operation.
	 */
	public class ClearHoverHandlesOperation extends AbstractOperation implements ITransactionalOperation {
		private HoverModel hoverModel;
		private HoverBehavior hoverBehavior;
		private IVisualPart<? extends Node> hoveredPart;

		/**
		 * @param part
		 *            The part for which to clear the hover handles.
		 */
		public ClearHoverHandlesOperation(IContentPart<? extends Node> part) {
			super("ClearHoverHandles");
			hoveredPart = part;
			hoverModel = part.getViewer().getAdapter(HoverModel.class);
			hoverBehavior = part.getRoot().getAdapter(HoverBehavior.class);
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			if (hoverModel.getHover() == hoveredPart) {
				hoverModel.clearHover();
				hoverBehavior.deactivate();
				hoverBehavior.activate();
			}
			return Status.OK_STATUS;
		}

		@Override
		public boolean isContentRelevant() {
			return false;
		}

		@Override
		public boolean isNoOp() {
			return false;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return execute(monitor, info);
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return Status.OK_STATUS;
		}
	}

	/**
	 * Returns an {@link ITransactionalOperation} that removes the given
	 * {@link NodePart} from the {@link SelectionModel} of the corresponding
	 * {@link IViewer}.
	 *
	 * @param part
	 *            The {@link NodePart} that is to be removed from the
	 *            {@link SelectionModel}.
	 * @return An {@link ITransactionalOperation} that removes the given
	 *         {@link NodePart} from the {@link SelectionModel}.
	 */
	protected ITransactionalOperation createDeselectOperation(NodePart part) {
		return new DeselectOperation(part.getRoot().getViewer(), Collections.singletonList(part));
	}

	/**
	 * Returns an {@link ITransactionalOperation} that adds the given
	 * {@link NodePart} to the {@link HidingModel} of its {@link IViewer} .
	 *
	 * @param part
	 *            The {@link NodePart} that is to be hidden.
	 * @return An {@link ITransactionalOperation} that adds the given
	 *         {@link NodePart} to the {@link HidingModel} of its
	 *         {@link IViewer}.
	 */
	protected ITransactionalOperation createHideOperation(NodePart part) {
		return new HideOperation(part.getRoot().getViewer(), part);
	}

	@Override
	protected ITransactionalOperation createOperation() {
		return new ReverseUndoCompositeOperation("Hide");
	}

	/**
	 * Returns an {@link ITransactionalOperation} that removes the given
	 * {@link NodePart} from the {@link FocusModel} of the corresponding
	 * {@link IViewer}.
	 *
	 * @param part
	 *            The {@link NodePart} that is to be removed.
	 * @return An {@link ITransactionalOperation} that removes the given
	 *         {@link NodePart} from the {@link FocusModel}.
	 */
	@SuppressWarnings("serial")
	protected ITransactionalOperation createUnfocusOperation(NodePart part) {
		IViewer viewer = part.getRoot().getViewer();

		FocusModel focusModel = viewer.getAdapter(new TypeToken<FocusModel>() {
		});
		if (focusModel != null) {
			if (focusModel.getFocus() == part) {
				return new ChangeFocusOperation(viewer, null);

			}
		}
		return null;
	}

	@Override
	public NodePart getHost() {
		return (NodePart) super.getHost();
	}

	/**
	 * Executes an operation on the history that hides the {@link #getHost()
	 * host} {@link NodePart} (and removes it from the {@link FocusModel} and
	 * from the {@link SelectionModel}).
	 */
	public void hide() {
		checkInitialized();
		AbstractCompositeOperation revOp = (AbstractCompositeOperation) getOperation();
		ITransactionalOperation unfocusOperation = createUnfocusOperation(getHost());
		if (unfocusOperation != null) {
			revOp.add(unfocusOperation);
		}
		ITransactionalOperation deselectOperation = createDeselectOperation(getHost());
		if (deselectOperation != null) {
			revOp.add(deselectOperation);
		}
		revOp.add(new ClearHoverHandlesOperation(getHost()));
		ITransactionalOperation hideOperation = createHideOperation(getHost());
		if (hideOperation != null) {
			revOp.add(hideOperation);
		}
		locallyExecuteOperation();
	}
}
