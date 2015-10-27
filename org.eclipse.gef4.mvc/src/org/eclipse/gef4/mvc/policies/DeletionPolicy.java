/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.policies;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * The {@link DeletionPolicy} is an {@link ITransactional}
 * {@link AbstractPolicy} that handles the deletion of existing
 * {@link IContentPart}s via the {@link ContentPolicy}.
 *
 * @author mwienand
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
	private Set<IContentPart<VR, ? extends VR>> partsToDelete;

	@Override
	public ITransactionalOperation commit() {
		if (!initialized) {
			return null;
		}

		ReverseUndoCompositeOperation rev = new ReverseUndoCompositeOperation(
				"Delete");
		// detach all content anchoreds
		for (IContentPart<VR, ? extends VR> p : partsToDelete) {
			for (IVisualPart<VR, ? extends VR> anchored : p.getAnchoreds()) {
				if (anchored instanceof IContentPart) {
					ContentPolicy<VR> policy = anchored
							.<ContentPolicy<VR>> getAdapter(
									ContentPolicy.class);
					if (policy != null) {
						policy.init();
						for (String role : anchored.getAnchorages().get(p)) {
							policy.detachFromContentAnchorage(p.getContent(),
									role);
						}
						ITransactionalOperation detachOperation = policy
								.commit();
						if (detachOperation != null) {
							rev.add(detachOperation);
						}
					}
				}
			}
		}

		// detach from all content anchorages
		for (IContentPart<VR, ? extends VR> p : partsToDelete) {
			ContentPolicy<VR> policy = p
					.<ContentPolicy<VR>> getAdapter(ContentPolicy.class);
			if (policy != null) {
				policy.init();
				for (IVisualPart<VR, ? extends VR> anchorage : getHost()
						.getAnchorages().keySet()) {
					if (anchorage instanceof IContentPart) {
						for (String role : p.getAnchorages().get(anchorage)) {
							policy.detachFromContentAnchorage(
									((IContentPart<VR, ? extends VR>) anchorage)
											.getContent(),
									role);
						}
					}
				}
				ITransactionalOperation detachOperation = policy.commit();
				if (detachOperation != null) {
					rev.add(detachOperation);
				}
			}
		}

		// remove from content parent
		for (IContentPart<VR, ? extends VR> p : partsToDelete) {
			ContentPolicy<VR> parentContentPolicy = p.getParent()
					.<ContentPolicy<VR>> getAdapter(ContentPolicy.class);
			if (parentContentPolicy != null) {
				parentContentPolicy.init();
				parentContentPolicy.removeContentChild(p.getContent());
				ITransactionalOperation removeOperation = parentContentPolicy
						.commit();
				if (removeOperation != null) {
					rev.add(removeOperation);
				}
			}
		}

		// after commit, we need to be re-initialized
		initialized = false;
		partsToDelete = null;

		return rev.unwrap(true);
	}

	/**
	 * Marks the given {@link IContentPart}s for deletion.
	 *
	 * @param contentPartsToDelete
	 *            The {@link IContentPart}s to mark for deletion.
	 */
	public void delete(
			Collection<? extends IContentPart<VR, ? extends VR>> contentPartsToDelete) {
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}
		partsToDelete.addAll(contentPartsToDelete);
	}

	/**
	 * Marks the given {@link IContentPart}s for deletion.
	 *
	 * @param contentPartToDelete
	 *            The {@link IContentPart}s to mark for deletion.
	 */
	public void delete(IContentPart<VR, ? extends VR> contentPartToDelete) {
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}
		partsToDelete.add(contentPartToDelete);
	}

	@Override
	public void init() {
		partsToDelete = new HashSet<IContentPart<VR, ? extends VR>>();
		initialized = true;
	}

}
