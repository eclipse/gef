/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen (itemis AG)  - refactorings
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.policies;

import java.util.Collections;

import org.eclipse.gef4.common.reflect.Types;
import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.operations.AbstractCompositeOperation;
import org.eclipse.gef4.mvc.operations.ChangeFocusOperation;
import org.eclipse.gef4.mvc.operations.DeselectOperation;
import org.eclipse.gef4.mvc.operations.DetachFromContentAnchorageOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.operations.RemoveContentChildOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.collect.HashMultiset;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

/**
 * The {@link DeletionPolicy} is an {@link AbstractTransactionPolicy} that
 * handles the deletion of content.
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
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public class DeletionPolicy<VR> extends AbstractTransactionPolicy<VR> {

	@SuppressWarnings("serial")
	@Override
	protected ITransactionalOperation createOperation() {
		ReverseUndoCompositeOperation commit = new ReverseUndoCompositeOperation(
				"Delete Content");
		IViewer<VR> viewer = getHost().getRoot().getViewer();
		// unfocus
		// TODO: this should be different-> use unfocus and initialize with null
		// or empty list
		commit.add(new ChangeFocusOperation<VR>(viewer,
				viewer.getAdapter(new TypeToken<FocusModel<VR>>() {
				}.where(new TypeParameter<VR>() {
				}, Types.<VR> argumentOf(
						getHost().getRoot().getViewer().getClass())))
						.getFocused()));
		// deselect
		commit.add(new DeselectOperation<VR>(viewer,
				Collections.<IContentPart<VR, ? extends VR>> emptyList()));
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
	// this will break if being called one after anothe without commit
	@SuppressWarnings("serial")
	public void delete(IContentPart<VR, ? extends VR> contentPartToDelete) {
		checkInitialized();

		getDeselectOperation().getToBeDeselected().add(contentPartToDelete);

		// TODO: use unfocus for this
		FocusModel<VR> focusModel = getHost().getRoot().getViewer()
				.getAdapter(new TypeToken<FocusModel<VR>>() {
				}.where(new TypeParameter<VR>() {
				}, Types.<VR> argumentOf(
						getHost().getRoot().getViewer().getClass())));
		if (focusModel != null) {
			if (focusModel.getFocused() == contentPartToDelete) {
				getUnfocusOperation().setNewFocused(null);
			}
		}

		// detach all content anchoreds
		for (IVisualPart<VR, ? extends VR> anchored : HashMultiset
				.create(contentPartToDelete.getAnchoreds())) {
			if (anchored instanceof IContentPart) {
				ContentPolicy<VR> anchoredContentPolicy = anchored
						.getAdapter(new TypeToken<ContentPolicy<VR>>() {
						}.where(new TypeParameter<VR>() {
						}, Types.<VR> argumentOf(
								getHost().getRoot().getViewer().getClass())));
				if (anchoredContentPolicy != null) {
					anchoredContentPolicy.init();
					for (String role : anchored.getAnchorages()
							.get(contentPartToDelete)) {
						anchoredContentPolicy.detachFromContentAnchorage(
								contentPartToDelete.getContent(), role);
					}
					ITransactionalOperation detachAnchoredOperation = anchoredContentPolicy
							.commit();
					if (detachAnchoredOperation != null
							&& !detachAnchoredOperation.isNoOp()) {
						getDetachAnchoragesOperation()
								.add(detachAnchoredOperation);
					}
				}
			}
		}

		// remove from content parent
		ContentPolicy<VR> parentContentPolicy = contentPartToDelete.getParent()
				.getAdapter(new TypeToken<ContentPolicy<VR>>() {
				}.where(new TypeParameter<VR>() {
				}, Types.<VR> argumentOf(
						getHost().getRoot().getViewer().getClass())));
		if (parentContentPolicy != null) {
			parentContentPolicy.init();
			parentContentPolicy
					.removeContentChild(contentPartToDelete.getContent());
			ITransactionalOperation removeFromParentOperation = parentContentPolicy
					.commit();
			if (removeFromParentOperation != null
					&& !removeFromParentOperation.isNoOp()) {
				getRemoveChildrenOperation().add(removeFromParentOperation);
			}
		}
		locallyExecuteOperation();
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
	@SuppressWarnings("unchecked")
	protected DeselectOperation<VR> getDeselectOperation() {
		return (DeselectOperation<VR>) getCompositeOperation().getOperations()
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
	protected AbstractCompositeOperation getDetachAnchoragesOperation() {
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
	protected AbstractCompositeOperation getRemoveChildrenOperation() {
		return (AbstractCompositeOperation) getCompositeOperation()
				.getOperations().get(3);
	}

	/**
	 * Returns the {@link ChangeFocusOperation} used by this
	 * {@link DeletionPolicy} to unfocus the to be deleted parts. .
	 *
	 * @return The {@link ChangeFocusOperation} that is used.
	 */
	@SuppressWarnings("unchecked")
	protected ChangeFocusOperation<VR> getUnfocusOperation() {
		return (ChangeFocusOperation<VR>) getCompositeOperation()
				.getOperations().get(0);
	}
}
