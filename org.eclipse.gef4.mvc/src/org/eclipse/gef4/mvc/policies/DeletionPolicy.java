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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;

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

		// unestablish anchor relations
		ReverseUndoCompositeOperation rev = new ReverseUndoCompositeOperation(
				"Unestablish Anchor Relations");
		for (IContentPart<VR, ? extends VR> p : partsToDelete) {
			ContentPolicy<VR> policy = p
					.<ContentPolicy<VR>> getAdapter(ContentPolicy.class);
			if (policy != null) {
				policy.init();
				policy.detachAllContentAnchoreds();
				policy.detachFromAllContentAnchorages();
				ITransactionalOperation detachOperation = policy.commit();
				if (detachOperation != null) {
					rev.add(detachOperation);
				}
			}
		}

		// remove content from parent
		for (IContentPart<VR, ? extends VR> p : partsToDelete) {
			ContentPolicy<VR> policy = p
					.<ContentPolicy<VR>> getAdapter(ContentPolicy.class);
			if (policy != null) {
				policy.init();
				policy.removeFromParent();
				ITransactionalOperation removeOperation = policy.commit();
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
			Collection<IContentPart<VR, ? extends VR>> contentPartsToDelete) {
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}
		partsToDelete.addAll(contentPartsToDelete);
	}

	/**
	 * Marks the given {@link IContentPart}s for deletion.
	 *
	 * @param contentPartsToDelete
	 *            The {@link IContentPart}s to mark for deletion.
	 */
	public void delete(IContentPart<VR, ? extends VR>... contentPartsToDelete) {
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}
		partsToDelete.addAll(Arrays.asList(contentPartsToDelete));
	}

	@Override
	public void init() {
		partsToDelete = new HashSet<IContentPart<VR, ? extends VR>>();
		initialized = true;
	}

}
