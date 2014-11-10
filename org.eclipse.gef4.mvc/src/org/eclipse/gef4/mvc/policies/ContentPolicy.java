/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.policies;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.mvc.operations.DetachFromContentAnchorageOperation;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.operations.RemoveContentChildOperation;
import org.eclipse.gef4.mvc.operations.SynchronizeContentAnchoragesOperation;
import org.eclipse.gef4.mvc.operations.SynchronizeContentChildrenOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * A (transaction) policy to handle content changes, i.e. adding/removing of
 * content children, as well as attaching/detaching to/from content anchorages.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
// TODO: ensure this can only be attached to content parts
public class ContentPolicy<VR> extends AbstractPolicy<VR> implements
		ITransactional {

	private ForwardUndoCompositeOperation commitOperation;

	public void addContentChild(Object child) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public void attachToContentAnchorage(Object anchorage, String role) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public IUndoableOperation commit() {
		IUndoableOperation commit = commitOperation.unwrap();
		commitOperation = null;
		return commit;
	}

	public void deleteContent() {
		ForwardUndoCompositeOperation deleteOps = new ForwardUndoCompositeOperation(
				"Delete Content");
		// detach all anchored
		for (IVisualPart<VR> anchored : getHost().getAnchoreds()) {
			if (anchored instanceof IContentPart) {
				ContentPolicy<VR> policy = anchored
						.<ContentPolicy<VR>> getAdapter(ContentPolicy.class);
				if (policy != null) {
					policy.init();
					for (String role : anchored.getAnchorages().get(getHost())) {
						policy.detachFromContentAnchorage(getHost()
								.getContent(), role);
					}
					IUndoableOperation detachOperation = policy.commit();
					if (detachOperation != null) {
						deleteOps.add(detachOperation);
					}
				}
			}
		}
		// detach from anchorages
		for (IVisualPart<VR> anchorage : getHost().getAnchorages().keySet()) {
			if (anchorage instanceof IContentPart) {
				for (String role : getHost().getAnchorages().get(anchorage)) {
					detachFromContentAnchorage(
							((IContentPart<VR>) anchorage).getContent(), role);
				}
			}
		}
		// remove from parent
		if (getHost().getParent() instanceof IContentPart) {
			ContentPolicy<VR> policy = getHost().getParent()
					.<ContentPolicy<VR>> getAdapter(ContentPolicy.class);
			if (policy != null) {
				policy.init();
				policy.removeContentChild(getHost().getContent());
				IUndoableOperation removeOperation = policy.commit();
				if (removeOperation != null) {
					deleteOps.add(removeOperation);
				}
			}
		}
		commitOperation.add(deleteOps.unwrap());
	}

	public void detachFromContentAnchorage(Object contentAnchorage, String role) {
		// assemble content operations in forward-undo-operations, so that
		// synchronization is always performed after changing the content
		// model (in execute() and undo())
		ForwardUndoCompositeOperation detachOperation = new ForwardUndoCompositeOperation(
				"Detach From Content Anchorage");
		detachOperation.add(new DetachFromContentAnchorageOperation<VR>(
				getHost(), contentAnchorage, role));
		detachOperation.add(new SynchronizeContentAnchoragesOperation<VR>(
				"Synchronize Anchorages", getHost()));
		commitOperation.add(detachOperation);
	}

	@Override
	public IContentPart<VR> getHost() {
		return (IContentPart<VR>) super.getHost();
	}

	@Override
	public void init() {
		commitOperation = new ForwardUndoCompositeOperation("Content Change");
	}

	public void removeContentChild(Object contentChild) {
		ForwardUndoCompositeOperation removeOperation = new ForwardUndoCompositeOperation(
				"Remove Content Child");
		removeOperation.add(new RemoveContentChildOperation<VR>(getHost(),
				contentChild));
		removeOperation.add(new SynchronizeContentChildrenOperation<VR>(
				"Synchronize Children", getHost()));
		commitOperation.add(removeOperation);
	}
}
