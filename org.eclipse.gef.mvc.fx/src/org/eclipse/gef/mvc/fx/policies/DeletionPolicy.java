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
 *     Alexander Nyßen (itemis AG)  - refactorings
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.policies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.mvc.fx.models.FocusModel;
import org.eclipse.gef.mvc.fx.operations.AbstractCompositeOperation;
import org.eclipse.gef.mvc.fx.operations.ChangeContentsOperation;
import org.eclipse.gef.mvc.fx.operations.ChangeFocusOperation;
import org.eclipse.gef.mvc.fx.operations.DeselectOperation;
import org.eclipse.gef.mvc.fx.operations.DetachFromContentAnchorageOperation;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.operations.RemoveContentChildOperation;
import org.eclipse.gef.mvc.fx.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.common.collect.HashMultiset;

import javafx.scene.Node;

/**
 * The {@link DeletionPolicy} is an {@link AbstractPolicy} that handles the
 * deletion of content.
 * <p>
 * It handles the deletion of a {@link IContentPart}'s content by initiating the
 * removal from the content parent via the {@link ContentPolicy} of the parent
 * {@link IContentPart}, as well as the detachment of anchored content elements
 * via the {@link ContentPolicy}s of anchored {@link IContentPart}s.
 * <p>
 * This policy should be registered at an {@link IRootPart}. It depends on
 * {@link ContentPolicy}s being registered on all {@link IContentPart}s that are
 * affected by the deletion.
 *
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class DeletionPolicy extends AbstractPolicy {

	@Override
	protected ITransactionalOperation createOperation() {
		ReverseUndoCompositeOperation commit = new ReverseUndoCompositeOperation(
				"Delete Content");
		// TODO: inline creation of nested operations into createOperation()
		IViewer viewer = getHost().getRoot().getViewer();
		// unfocus
		IContentPart<? extends Node> currentlyFocusedPart = viewer
				.getAdapter(FocusModel.class).getFocus();
		commit.add(new ChangeFocusOperation(viewer, currentlyFocusedPart));
		// deselect
		commit.add(new DeselectOperation(viewer,
				Collections.<IContentPart<? extends Node>> emptyList()));
		// detach anchorages
		commit.add(new ReverseUndoCompositeOperation("Detach anchorages"));
		// remove children
		commit.add(new ReverseUndoCompositeOperation("Remove children"));
		return commit;
	}

	/**
	 * Deletes the given {@link IContentPart} by removing the
	 * {@link IContentPart}'s content from the parent {@link IContentPart}'
	 * content and by detaching the contents of all anchored
	 * {@link IContentPart}s from the {@link IContentPart}'s content.
	 *
	 * @param contentPartToDelete
	 *            The {@link IContentPart} to mark for deletion.
	 */
	// TODO: offer a bulk operation to improve deselect (can remove all in one
	// operation pass)
	// this will break if being called one after another without commit
	public void delete(IContentPart<? extends Node> contentPartToDelete) {
		checkInitialized();

		// clear viewer models so that anchoreds are removed
		IViewer viewer = getHost().getRoot().getViewer();
		getDeselectOperation().getToBeDeselected().add(contentPartToDelete);
		FocusModel focusModel = viewer.getAdapter(FocusModel.class);
		if (focusModel != null) {
			if (focusModel.getFocus() == contentPartToDelete) {
				getUnfocusOperation().setNewFocused(null);
			}
		}

		// XXX: Execute operations for changing the viewer models prior to
		// detaching anchoreds and removing children, so that no link to the
		// viewer is available for the removed part via selection, focus, or
		// hover feedback or handles.
		locallyExecuteOperation();

		// detach all content anchoreds
		// XXX: copy iterated to prevent CME
		for (IVisualPart<? extends Node> anchored : HashMultiset
				.create(contentPartToDelete.getAnchoredsUnmodifiable())) {
			if (anchored instanceof IContentPart) {
				ContentPolicy anchoredContentPolicy = anchored
						.getAdapter(ContentPolicy.class);
				if (anchoredContentPolicy != null) {
					anchoredContentPolicy.init();
					for (String role : new ArrayList<>(
							anchored.getAnchoragesUnmodifiable()
									.get(contentPartToDelete))) {
						anchoredContentPolicy.detachFromContentAnchorage(
								contentPartToDelete.getContent(), role);
					}
					ITransactionalOperation detachFromContentAnchoredOperation = anchoredContentPolicy
							.commit();
					if (detachFromContentAnchoredOperation != null
							&& !detachFromContentAnchoredOperation.isNoOp()) {
						getDetachContentAnchoragesOperation()
								.add(detachFromContentAnchoredOperation);
					}
				}
			}
		}

		if (contentPartToDelete.getParent() instanceof IRootPart) {
			// remove content from viewer contents
			ChangeContentsOperation changeContentsOperation = new ChangeContentsOperation(
					viewer);
			List<Object> newContents = new ArrayList<>(viewer.getContents());
			newContents.remove(contentPartToDelete.getContent());
			changeContentsOperation.setNewContents(newContents);
			getRemoveContentChildrenOperation().add(changeContentsOperation);
		} else {
			// remove from content parent
			ContentPolicy parentContentPolicy = contentPartToDelete.getParent()
					.getAdapter(ContentPolicy.class);
			if (parentContentPolicy != null) {
				parentContentPolicy.init();
				parentContentPolicy
						.removeContentChild(contentPartToDelete.getContent());
				ITransactionalOperation removeFromParentOperation = parentContentPolicy
						.commit();
				if (removeFromParentOperation != null
						&& !removeFromParentOperation.isNoOp()) {
					getRemoveContentChildrenOperation()
							.add(removeFromParentOperation);
				}
			}
		}

		locallyExecuteOperation();

		// verify that all anchoreds were removed
		if (!contentPartToDelete.getAnchoredsUnmodifiable().isEmpty()) {
			throw new IllegalStateException(
					"After deletion of <" + contentPartToDelete
							+ "> there are still anchoreds remaining.");
		}
	}

	/**
	 * Extracts a {@link AbstractCompositeOperation} from the operation created
	 * by {@link #createOperation()}. The composite operation is used to combine
	 * individual content change operations.
	 *
	 * @return The {@link AbstractCompositeOperation} that is used to combine
	 *         the individual content change operations.
	 */
	protected AbstractCompositeOperation getCompositeOperation() {
		return (AbstractCompositeOperation) getOperation();
	}

	/**
	 * Returns the {@link DeselectOperation} used by this {@link DeletionPolicy}
	 * to deselect the to be deleted parts.
	 *
	 * @return The {@link DeselectOperation} that is used.
	 */
	protected DeselectOperation getDeselectOperation() {
		return (DeselectOperation) getCompositeOperation().getOperations()
				.get(1);
	}

	/**
	 * Returns an {@link AbstractCompositeOperation} that comprises all
	 * {@link DetachFromContentAnchorageOperation} returned by the delegate
	 * {@link ContentPolicy}.
	 *
	 * @return The {@link AbstractCompositeOperation} that is used for detaching
	 *         anchorages.
	 */
	private AbstractCompositeOperation getDetachContentAnchoragesOperation() {
		return (AbstractCompositeOperation) getCompositeOperation()
				.getOperations().get(2);
	}

	/**
	 * Returns an {@link AbstractCompositeOperation} that comprises all
	 * {@link RemoveContentChildOperation} returned by the delegate
	 * {@link ContentPolicy}.
	 *
	 * @return The {@link AbstractCompositeOperation} that is used for removing
	 *         children.
	 */
	private AbstractCompositeOperation getRemoveContentChildrenOperation() {
		return (AbstractCompositeOperation) getCompositeOperation()
				.getOperations().get(3);
	}

	/**
	 * Returns the {@link ChangeFocusOperation} used by this
	 * {@link DeletionPolicy} to unfocus the to be deleted parts. .
	 *
	 * @return The {@link ChangeFocusOperation} that is used.
	 */
	protected ChangeFocusOperation getUnfocusOperation() {
		return (ChangeFocusOperation) getCompositeOperation().getOperations()
				.get(0);
	}

}
