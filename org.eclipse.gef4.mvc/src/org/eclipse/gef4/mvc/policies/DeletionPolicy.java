/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;

/**
 * The {@link DeletionPolicy} is an {@link ITransactional}
 * {@link AbstractPolicy} that handles the deletion of existing
 * {@link IContentPart}s via the {@link ContentPolicy}.
 *
 * @author wienand
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public class DeletionPolicy<VR> extends AbstractPolicy<VR> implements
		ITransactional {

	private Set<IContentPart<VR>> partsToDelete;

	@Override
	public IUndoableOperation commit() {
		// unestablish anchor relations
		ReverseUndoCompositeOperation rev = new ReverseUndoCompositeOperation(
				"Unestablish Anchor Relations");
		for (IContentPart<VR> p : partsToDelete) {
			ContentPolicy<VR> policy = p
					.<ContentPolicy<VR>> getAdapter(ContentPolicy.class);
			if (policy != null) {
				policy.init();
				policy.detachAllContentAnchoreds();
				policy.detachFromAllContentAnchorages();
				IUndoableOperation detachOperation = policy.commit();
				if (detachOperation != null) {
					rev.add(detachOperation);
				}
			}
		}

		// remove content from parent
		for (IContentPart<VR> p : partsToDelete) {
			ContentPolicy<VR> policy = p
					.<ContentPolicy<VR>> getAdapter(ContentPolicy.class);
			if (policy != null) {
				policy.init();
				policy.removeFromParent();
				IUndoableOperation removeOperation = policy.commit();
				if (removeOperation != null) {
					rev.add(removeOperation);
				}
			}
		}

		partsToDelete = null;
		return rev.unwrap();
	}

	/**
	 * Marks the given {@link IContentPart}s for deletion.
	 *
	 * @param contentPartsToDelete
	 *            The {@link IContentPart}s to mark for deletion.
	 */
	public void delete(Collection<IContentPart<VR>> contentPartsToDelete) {
		partsToDelete.addAll(contentPartsToDelete);
	}

	/**
	 * Marks the given {@link IContentPart}s for deletion.
	 *
	 * @param contentPartsToDelete
	 *            The {@link IContentPart}s to mark for deletion.
	 */
	public void delete(IContentPart<VR>... contentPartsToDelete) {
		partsToDelete.addAll(Arrays.asList(contentPartsToDelete));
	}

	@Override
	public void init() {
		partsToDelete = new HashSet<IContentPart<VR>>();
	}

}
