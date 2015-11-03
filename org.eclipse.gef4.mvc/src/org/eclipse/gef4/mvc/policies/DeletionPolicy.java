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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.operations.ChangeFocusOperation;
import org.eclipse.gef4.mvc.operations.ChangeSelectionOperation;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

/**
 * The {@link DeletionPolicy} is an {@link ITransactional}
 * {@link AbstractPolicy} that handles the deletion of content.
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
public class DeletionPolicy<VR> extends AbstractPolicy<VR>
		implements ITransactional {

	/**
	 * Stores the <i>initialized</i> flag for this policy, i.e.
	 * <code>true</code> after {@link #init()} was called, and
	 * <code>false</code> after {@link #commit()} was called, respectively.
	 */
	protected boolean initialized;
	private Set<IContentPart<VR, ? extends VR>> contentPartsToDelete;
	private ReverseUndoCompositeOperation deleteOperation;

	@Override
	public ITransactionalOperation commit() {
		if (!initialized) {
			return null;
		}

		// IMPORTANT: The content synchronization performed by (ContentBehavior)
		// will dispose the part (synchronizing any children and anchorages) if
		// it does not have a parent and it does not have any anchoreds. As
		// such, its sufficient to remove it from its parent and to detach all
		// anchoreds here via the ContentPolicy, which will trigger
		// synchronization.
		deleteOperation = new ReverseUndoCompositeOperation("Delete Content");

		// remove from viewer models (execute directly and chain for deletion)
		for (IContentPart<VR, ? extends VR> p : contentPartsToDelete) {
			ITransactionalOperation deselectOperation = createDeselectOperation(
					p);
			if (deselectOperation != null) {
				deleteOperation.add(deselectOperation);
				try {
					deselectOperation.execute(null, null);
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
			ITransactionalOperation unfocusOperation = createUnfocusOperation(
					p);
			if (unfocusOperation != null) {
				deleteOperation.add(unfocusOperation);
				try {
					unfocusOperation.execute(null, null);
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		}

		// detach all content anchoreds
		for (IContentPart<VR, ? extends VR> p : contentPartsToDelete) {
			for (IVisualPart<VR, ? extends VR> anchored : p.getAnchoreds()) {
				if (anchored instanceof IContentPart) {
					ContentPolicy<VR> anchoredContentPolicy = anchored
							.<ContentPolicy<VR>> getAdapter(
									ContentPolicy.class);
					if (anchoredContentPolicy != null) {
						anchoredContentPolicy.init();
						for (String role : anchored.getAnchorages().get(p)) {
							anchoredContentPolicy.detachFromContentAnchorage(
									p.getContent(), role);
						}
						ITransactionalOperation detachAnchoredOperation = anchoredContentPolicy
								.commit();
						if (detachAnchoredOperation != null) {
							deleteOperation.add(detachAnchoredOperation);
						}
					}
				}
			}
		}

		// remove from content parent
		for (IContentPart<VR, ? extends VR> p : contentPartsToDelete) {
			ContentPolicy<VR> parentContentPolicy = p.getParent()
					.<ContentPolicy<VR>> getAdapter(ContentPolicy.class);
			if (parentContentPolicy != null) {
				parentContentPolicy.init();
				parentContentPolicy.removeContentChild(p.getContent());
				ITransactionalOperation removeFromParentOperation = parentContentPolicy
						.commit();
				if (removeFromParentOperation != null) {
					deleteOperation.add(removeFromParentOperation);
				}
			}
		}

		// after commit, we need to be re-initialized
		initialized = false;
		contentPartsToDelete = null;

		return deleteOperation.unwrap(true);
	}

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
	protected ITransactionalOperation createDeselectOperation(
			IContentPart<VR, ? extends VR> part) {
		IViewer<VR> viewer = part.getRoot().getViewer();

		SelectionModel<VR> selectionModel = viewer
				.<SelectionModel<VR>> getAdapter(SelectionModel.class);
		if (selectionModel != null) {
			List<IContentPart<VR, ? extends VR>> selected = selectionModel
					.getSelection();
			if (selected.contains(part)) {
				List<IContentPart<VR, ? extends VR>> newSelection = new ArrayList<IContentPart<VR, ? extends VR>>(
						selected);
				newSelection.remove(part);
				return new ChangeSelectionOperation<VR>(viewer, newSelection);
			}
		}
		return null;
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
	protected ITransactionalOperation createUnfocusOperation(
			IContentPart<VR, ? extends VR> part) {
		IViewer<VR> viewer = part.getRoot().getViewer();

		FocusModel<VR> focusModel = viewer
				.<FocusModel<VR>> getAdapter(FocusModel.class);
		if (focusModel != null) {
			if (focusModel.getFocused() == part) {
				return new ChangeFocusOperation<VR>(viewer, null);

			}
		}
		return null;
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
	public void delete(IContentPart<VR, ? extends VR> contentPartToDelete) {
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}
		contentPartsToDelete.add(contentPartToDelete);
	}

	@Override
	public void init() {
		contentPartsToDelete = new HashSet<IContentPart<VR, ? extends VR>>();
		initialized = true;
	}

}
